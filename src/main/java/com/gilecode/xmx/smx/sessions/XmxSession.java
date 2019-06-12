package com.gilecode.xmx.smx.sessions;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;

public class XmxSession {
    private final Integer port;
    private final ExecutionEnvironment env;
    private final ProcessHandler handler;

    public XmxSession(Integer port, ExecutionEnvironment env, ProcessHandler handler) {
        this.port = port;
        this.env = env;
        this.handler = handler;
    }

    public Integer getPort() {
        return port;
    }

    public ExecutionEnvironment getEnv() {
        return env;
    }

    public ProcessHandler getHandler() {
        return handler;
    }
}
