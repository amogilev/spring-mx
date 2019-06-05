package com.gilecode.xmx.smx;

import com.intellij.openapi.components.ServiceManager;

public interface XmxPortProviderService {

    static XmxPortProviderService getInstance() {
        return ServiceManager.getService(XmxPortProviderService.class);
    }

    /**
     * Selects next free port for XMX agent from a configured range of ports and reserves it for the next XMX session.
     *
     * @return the reserved port, or {@code null} if there are no more free ports
     */
    Integer findAndReserveFreeXmxPort();
}
