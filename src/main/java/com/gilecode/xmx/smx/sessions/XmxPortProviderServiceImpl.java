// Copyright © 2019 Andrey Mogilev. All rights reserved.

package com.gilecode.xmx.smx.sessions;

import com.gilecode.xmx.smx.settings.SmxSettingService;
import org.apache.commons.lang3.tuple.Pair;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.List;
import java.util.Set;

public class XmxPortProviderServiceImpl implements XmxPortProviderService {

    public static final String DEFAULT_PORTS_RANGE = "8081,8181,8281,9011-65535";

    private final XmxSessionWatcherService watcherService;
    private final SmxSettingService settingService;

    private String portRangesStr;
    private Iterable<? extends Pair<Integer, Integer>> portRanges;

    public XmxPortProviderServiceImpl(XmxSessionWatcherService watcherService, SmxSettingService settingService) {
        this.watcherService = watcherService;
        this.settingService = settingService;
    }

    @Override
    public Integer findAndReserveFreeXmxPort() {
        Set<Integer> busyPorts = watcherService.getReservedAndBusyXmxPorts();

        for (Pair<Integer, Integer> range : getPortRanges()) {
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

    private Iterable<? extends Pair<Integer, Integer>> getPortRanges() {
        String curPortsStr = settingService.getState().getPorts();
        if (curPortsStr.equals(portRangesStr)) {
            // use cached
            return portRanges;
        } else {
            portRanges = new PortRangesParser().parsePortRanges(curPortsStr);
            portRangesStr = curPortsStr;
            return portRanges;
        }
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
