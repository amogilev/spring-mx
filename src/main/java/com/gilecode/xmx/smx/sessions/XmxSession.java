package com.gilecode.xmx.smx.sessions;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class XmxSession {
    private final Integer port;
    private final ExecutionEnvironment env;
    private final ProcessHandler handler;
    private final LocalDateTime started;

    public XmxSession(Integer port, ExecutionEnvironment env, ProcessHandler handler) {
        this.port = port;
        this.env = env;
        this.handler = handler;
        this.started = LocalDateTime.now();
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

    @Override
    public String toString() {
        return env.toString() + "; started " + getStartedFormatted() + ", port " + port;
    }

    private String getStartedFormatted() {
        if (started.toLocalDate().equals(LocalDate.now())) {
            return DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(started);
        } else {
            return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(started);
        }
    }

}
