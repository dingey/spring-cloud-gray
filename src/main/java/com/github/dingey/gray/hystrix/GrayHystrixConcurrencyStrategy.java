package com.github.dingey.gray.hystrix;

import com.github.dingey.gray.GrayContext;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Callable;

/**
 * @author d
 */
@Configuration
@ConditionalOnProperty(value = "gray.enable")
@ConditionalOnClass(HystrixConcurrencyStrategy.class)
public class GrayHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {
    private final Logger log = LoggerFactory.getLogger(GrayHystrixConcurrencyStrategy.class);

    private HystrixConcurrencyStrategy delegate;

    public GrayHystrixConcurrencyStrategy() {
        try {
            this.delegate = HystrixPlugins.getInstance().getConcurrencyStrategy();
            if (this.delegate instanceof GrayHystrixConcurrencyStrategy) {
                return;
            }
            HystrixCommandExecutionHook commandExecutionHook =
                    HystrixPlugins.getInstance().getCommandExecutionHook();
            HystrixEventNotifier eventNotifier = HystrixPlugins.getInstance().getEventNotifier();
            HystrixMetricsPublisher metricsPublisher = HystrixPlugins.getInstance().getMetricsPublisher();
            HystrixPropertiesStrategy propertiesStrategy =
                    HystrixPlugins.getInstance().getPropertiesStrategy();
            this.logCurrentStateOfHystrixPlugins(eventNotifier, metricsPublisher, propertiesStrategy);
            HystrixPlugins.reset();
            HystrixPlugins.getInstance().registerConcurrencyStrategy(this);
            HystrixPlugins.getInstance().registerCommandExecutionHook(commandExecutionHook);
            HystrixPlugins.getInstance().registerEventNotifier(eventNotifier);
            HystrixPlugins.getInstance().registerMetricsPublisher(metricsPublisher);
            HystrixPlugins.getInstance().registerPropertiesStrategy(propertiesStrategy);
            if (log.isDebugEnabled()) {
                log.debug("Initializing GrayContext Feign Hystrix Strategy support");
            }
        } catch (Exception e) {
            log.error("Failed to register GrayContext Hystrix Strategy support", e);
        }
    }

    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        String version = GrayContext.getVersion();
        return new WrappedCallable<>(callable, version);
    }

    static class WrappedCallable<T> implements Callable<T> {
        private final Callable<T> target;
        private final String version;

        public WrappedCallable(Callable<T> target, String version) {
            this.target = target;
            this.version = version;
        }

        @Override
        public T call() throws Exception {
            try {
                GrayContext.setVersion(version);
                return target.call();
            } finally {
                GrayContext.removeVersion();
            }
        }
    }

    private void logCurrentStateOfHystrixPlugins(HystrixEventNotifier eventNotifier,
                                                 HystrixMetricsPublisher metricsPublisher, HystrixPropertiesStrategy propertiesStrategy) {
        if (log.isDebugEnabled()) {
            log.debug("Current Hystrix plugins configuration is [" + "concurrencyStrategy ["
                    + this.delegate + "]," + "eventNotifier [" + eventNotifier + "]," + "metricPublisher ["
                    + metricsPublisher + "]," + "propertiesStrategy [" + propertiesStrategy + "]," + "]");
            log.debug("Registering Gray Hystrix Concurrency Strategy.");
        }
    }
}
