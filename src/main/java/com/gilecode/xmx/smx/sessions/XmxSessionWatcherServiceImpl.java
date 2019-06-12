package com.gilecode.xmx.smx.sessions;

import com.gilecode.xmx.smx.run.XmxAgentVmParamsPatcherExtension;
import com.intellij.execution.ExecutionListener;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.process.BaseProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class XmxSessionWatcherServiceImpl implements XmxSessionWatcherService {

    private static final String XMX_PORT_PREFIX = "xmx-agent.jar=EmbeddedWebServer.Port=";
    private static final long RESERVED_PORTS_TIMEOUT_MS = 60_000;

    private static final Logger logger = Logger.getInstance(XmxSessionWatcherServiceImpl.class);

    private Map<Integer, Long> reservedPortsWithTimestamps = new HashMap<>();
    private Map<Integer, XmxSession> activeSessionsByPort = new HashMap<>();

    public XmxSessionWatcherServiceImpl() {
        subscribeToExecutionEvents();
    }

    @Override
    public Set<Integer> getReservedAndBusyXmxPorts() {
        freeTimedOutReservedPorts();

        Set<Integer> ports = new HashSet<>();
        ports.addAll(reservedPortsWithTimestamps.keySet());
        ports.addAll(activeSessionsByPort.keySet());
        return ports;
    }

    @Override
    public boolean reserveXmxPort(int port) {
        freeTimedOutReservedPorts();

        if (reservedPortsWithTimestamps.containsKey(port) || activeSessionsByPort.containsKey(port)) {
            return false;
        }

        reservedPortsWithTimestamps.put(port, System.currentTimeMillis());
        return true;
    }

    @Override
    public List<XmxSession> getActiveSessions() {
        return new ArrayList<>(activeSessionsByPort.values());
    }

    /**
     * Shall be periodically invoked to free reserved ports for which no process was actually started,
     * and thus which were not moved to "busy" ones.
     */
    private void freeTimedOutReservedPorts() {
        if (!reservedPortsWithTimestamps.isEmpty()) {
            long now = System.currentTimeMillis();
            Iterator<Map.Entry<Integer, Long>> it = reservedPortsWithTimestamps.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, Long> entry = it.next();
                int port = entry.getKey();
                long ts = entry.getValue();
                if (now - ts > RESERVED_PORTS_TIMEOUT_MS) {
                    it.remove();
                    logger.warn("Freeing reserved XMX port " + port + " by timeout");
                }
            }
        }
    }

    private void subscribeToExecutionEvents() {
        ApplicationManager.getApplication().getMessageBus().connect().subscribe(ExecutionManager.EXECUTION_TOPIC, new ExecutionListener() {
            @Override
            public void processStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler) {
                Integer port = extractXmxPort(handler);
                if (port != null) {
                    reservedPortsWithTimestamps.remove(port);
                    activeSessionsByPort.put(port, new XmxSession(port, env, handler));
                }
            }

            @Override
            public void processTerminated(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler, int exitCode) {
                Integer port = extractXmxPort(handler);
                if (port != null) {
                    reservedPortsWithTimestamps.remove(port);
                    activeSessionsByPort.remove(port);
                }
            }
        });
    }

    /**
     * Tries to find and extract XMX port from the command line of the specified process.
     * Returns {@code null} if no XMX agent was found.
     * <p/>
     * NOTE: this algorithm relies on the format of the XMX javaagent command format used
     * in {@link XmxAgentVmParamsPatcherExtension#patchJavaParameters(Executor, RunProfile, JavaParameters)}
     */
    private Integer extractXmxPort(ProcessHandler handler) {
        if (handler instanceof BaseProcessHandler) {
            String cmdLine = ((BaseProcessHandler) handler).getCommandLine();
            int idx = cmdLine.indexOf(XMX_PORT_PREFIX);
            if (idx >= 0) {
                int portStart = idx + XMX_PORT_PREFIX.length();
                int portEnd = cmdLine.indexOf(',', portStart);
                return Integer.parseInt(cmdLine.substring(portStart, portEnd));
            }
        }
        return null;
    }
}
