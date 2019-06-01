package com.gilecode.xmx.smx;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunConfigurationExtension;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

public class XmxAgentConfigExtension extends RunConfigurationExtension {

    private File agentJar;
    private File xmxHomeDir;
    private boolean agentJarFound = false;

    @Override
    public <T extends RunConfigurationBase> void updateJavaParameters(T configuration, JavaParameters params, RunnerSettings runnerSettings) throws ExecutionException {
        if (!isApplicableFor(configuration)) {
            return;
        }
        params.getVMParametersList().add("-javaagent:" + agentJar.getAbsolutePath() + "=" + buildXmxParams());
    }

    private String buildXmxParams() {
        Map<String, Object> params = new TreeMap<>();
        params.put("config", new File(xmxHomeDir, "smx.ini").getAbsolutePath());
        params.put("EmbeddedWebServer.Port", findAvailablePort());
        return toParamsString(params);
    }

    private String toParamsString(Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(entry.getKey()).append('=').append(entry.getValue().toString());
        }
        return sb.toString();
    }

    private String findAvailablePort() {
        // TODO: get port range from config, detect available, maybe mark as busy
        return "8081";
    }

    @Override
    public boolean isApplicableFor(@NotNull RunConfigurationBase<?> configuration) {
        if (!isPluginEnabled()) {
            return false;
        }
        // TODO: check that spring jars are in classpath
        // TODO: check that enabled for the configuration of this type (Run vs Debug)
        ensureAgentJar();
        return agentJarFound;
    }

    private boolean isPluginEnabled() {
        // TODO: check that the plugin is enabled
        return true;
    }

    private void ensureAgentJar() {
        if (agentJar == null) {
            File pluginDir = PluginManager.getPlugin(PluginId.getId(Constants.PLUGIN_ID)).getPath();
            xmxHomeDir = new File(pluginDir, "lib//xmx");
            agentJar = new File(xmxHomeDir, "bin//xmx-agent.jar");
            agentJarFound = agentJar.isFile();
        }
    }
}
