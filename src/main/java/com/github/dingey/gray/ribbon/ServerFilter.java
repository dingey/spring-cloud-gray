package com.github.dingey.gray.ribbon;

import com.netflix.loadbalancer.Server;

import java.util.List;

/**
 * @author d
 */
public interface ServerFilter {
    List<Server> filter(List<Server> servers, String version);
}
