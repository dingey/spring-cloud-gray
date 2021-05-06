package com.github.dingey.gray.ribbon;

import com.github.dingey.gray.GrayContext;
import com.github.dingey.gray.GrayProperties;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 灰度轮询
 *
 * @author d
 */
public class GrayRoundRobinRule extends AbstractLoadBalancerRule {
    private final Logger log = LoggerFactory.getLogger(GrayRoundRobinRule.class);

    @Autowired
    private GrayProperties grayProperties;
    @Autowired
    private ServerFilter serverFilter;

    public GrayRoundRobinRule() {
        this.nextServerCyclicCounter = new AtomicInteger(0);
    }

    public GrayRoundRobinRule(ILoadBalancer lb) {
        this();
        this.setLoadBalancer(lb);
    }

    @Override
    public Server choose(Object o) {
        List<Server> list;
        if (grayProperties.isEnable()) {
            if (GrayContext.isGray()) {
                log.debug("符合灰度策略，版本号是{}", GrayContext.getVersion());
                List<Server> reachableServers = getLoadBalancer().getReachableServers();
                List<Server> allServers = getLoadBalancer().getAllServers();
                int upCount = reachableServers.size();
                int serverCount = allServers.size();

                if ((upCount == 0) || (serverCount == 0)) {
                    log.warn("No up servers available from load balancer: " + getLoadBalancer());
                    return null;
                }
                list = getMatchVersionServers(GrayContext.getVersion());
                log.debug("符合灰度版本的服务实例有{}个", list.size());
            } else {
                list = getMatchVersionServers(null);
                log.debug("非灰度版本的服务实例有{}个", list.size());
            }

            if (list.isEmpty() && !grayProperties.isIsolation()) {
                log.debug("非隔离状态下，没有匹配的版本服务，调用所有服务");
                list = getLoadBalancer().getAllServers();
            }
        } else {
            list = getLoadBalancer().getAllServers();
        }

        return getRoundRobinServer(list);
    }

    private Server getRoundRobinServer(List<Server> allServers) {
        if (getLoadBalancer() == null) {
            log.warn("no load balancer");
            return null;
        } else {
            Server server = null;
            int count = 0;

            while (true) {
                if (count++ < 10) {
                    List<Server> reachableServers = getLoadBalancer().getReachableServers();

                    int upCount = reachableServers.size();
                    int serverCount = allServers.size();
                    if (upCount != 0 && serverCount != 0) {
                        int nextServerIndex = this.incrementAndGetModulo(GrayContext.getVersion(), serverCount);
                        server = allServers.get(nextServerIndex);
                        if (server == null) {
                            Thread.yield();
                        } else {
                            if (server.isAlive() && server.isReadyToServe()) {
                                return server;
                            }

                            server = null;
                        }
                        continue;
                    }

                    log.warn("No up servers available from load balancer: " + getLoadBalancer());
                    return null;
                }

                if (count >= 10) {
                    log.warn("No available alive servers after 10 tries from load balancer: " + getLoadBalancer());
                }

                return server;
            }
        }
    }

    private final AtomicInteger nextServerCyclicCounter;
    private final Map<String, AtomicInteger> nextServerCyclicCounterMap = new WeakHashMap<>();

    private int incrementAndGetModulo(int modulo) {
        int current;
        int next;
        do {
            current = this.nextServerCyclicCounter.get();
            next = (current + 1) % modulo;
        } while (!this.nextServerCyclicCounter.compareAndSet(current, next));

        return next;
    }

    private int incrementAndGetModulo(String version, int modulo) {
        if (StringUtils.isEmpty(version)) {
            return incrementAndGetModulo(modulo);
        }
        AtomicInteger atomicInteger = nextServerCyclicCounterMap.get(version);
        if (atomicInteger == null) {
            atomicInteger = new AtomicInteger(0);
            nextServerCyclicCounterMap.put(version, atomicInteger);
        }
        int current;
        int next;
        do {
            current = atomicInteger.get();
            next = (current + 1) % modulo;
        } while (!atomicInteger.compareAndSet(current, next));

        return next;
    }

    private List<Server> getMatchVersionServers(String version) {
        return serverFilter.filter(getLoadBalancer().getAllServers(), version);
    }

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }
}
