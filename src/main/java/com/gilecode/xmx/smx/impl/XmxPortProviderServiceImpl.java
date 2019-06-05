package com.gilecode.xmx.smx.impl;

import com.gilecode.xmx.smx.XmxPortProviderService;
import com.gilecode.xmx.smx.XmxSessionWatcherService;
import org.apache.commons.lang3.tuple.Pair;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.List;
import java.util.Set;

public class XmxPortProviderServiceImpl implements XmxPortProviderService {

    public static final String DEFAULT_PORTS_RANGE = "8081,8181,8281,9011-65535";

    private final List<Pair<Integer, Integer>> portRanges;
    private final XmxSessionWatcherService watcherService;

    public XmxPortProviderServiceImpl(XmxSessionWatcherService watcherService) {
        this.watcherService = watcherService;
        portRanges = new PortRangesParser().parsePortRanges(DEFAULT_PORTS_RANGE);
    }

    @Override
    public Integer findAndReserveFreeXmxPort() {
        Set<Integer> busyPorts = watcherService.getReservedAndBusyXmxPorts();

        for (Pair<Integer, Integer> range : portRanges) {
            int lo = range.getLeft();
            int hi = range.getLeft();
            for (int port = lo; port <= hi; port++) {
                if (!busyPorts.contains(port)) {
                    if (watcherService.reserveXmxPort(port) && !isExternallyBound(port)) {
                        return port;
                    }
                }
            }
        }
        return null;
    }

    private boolean isExternallyBound(int port) {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(false);
            serverSocket.bind(new InetSocketAddress(InetAddress.getByName("localhost"), port), 1);
            return false;
        } catch (Exception ex) {
            return true;
        }
    }
}
