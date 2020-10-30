package com.github.dingey.gray.eureka;

import com.github.dingey.gray.GrayContext;
import com.github.dingey.gray.ribbon.ServerFilter;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author d
 */
public class EurekaServerFilter implements ServerFilter {

    @Override
    public List<Server> filter(List<Server> servers, String version) {
        List<Server> list = new ArrayList<>();
        for (Server server : servers) {
            DiscoveryEnabledServer discoveryEnabledServer = (DiscoveryEnabledServer) server;
            String versionMeta = discoveryEnabledServer.getInstanceInfo().getMetadata().get(GrayContext.PREFIX);
            if ((version == null && StringUtils.isEmpty(versionMeta)) || (Objects.equals(version, versionMeta))) {
                list.add(server);
            }
        }
        return list;
    }
}
