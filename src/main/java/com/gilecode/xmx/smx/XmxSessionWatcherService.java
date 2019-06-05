package com.gilecode.xmx.smx;

import com.intellij.openapi.components.ServiceManager;

import java.util.List;
import java.util.Set;

/**
 * A service which watches for starts and stops of processes with XMX agent.
 */
public interface XmxSessionWatcherService {

    static XmxSessionWatcherService getInstance() {
        return ServiceManager.getService(XmxSessionWatcherService.class);
    }

    /**
     * Returns a list of currently active XMX sessions.
     */
    List<XmxSession> getActiveSessions();

    /**
     * Tries to reserve the given port for the next XMX session, if it is not currently reserved or busy
     * for another XMX session.
     * <p/>
     * It is expected that a process with XMX agent on the reserved port will be started soon;
     * otherwise, the port may be returned to a free pool by timeout.
     *
     * @param port the port to reserve
     *
     * @return whether the port is successfully reserved and can be used for starting a new XMX session;
     *  otherwise another port needs to be tried
     */
    boolean reserveXmxPort(int port);

    /**
     * Returns a set of ports which are currently busy for active XMX sessions or reserved for
     * XMX sessions currently being started.
     */
    Set<Integer> getReservedAndBusyXmxPorts();

}
