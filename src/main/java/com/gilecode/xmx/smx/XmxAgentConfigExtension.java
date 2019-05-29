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

public class XmxAgentConfigExtension extends RunConfigurationExtension {

    private File agentJar;
    private boolean agentJarFound = false;

    @Override
    public <T extends RunConfigurationBase> void updateJavaParameters(T configuration, JavaParameters params, RunnerSettings runnerSettings) throws ExecutionException {
        if (agentJarFound) {
            // TODO: add custom parameters (ini file, port)
            params.getVMParametersList().add("-javaagent:" + agentJar.getAbsolutePath());
        }
        // TODO: remove after debugging
        System.out.println("updateJavaParameters: new VMParams= " + params.getVMParametersList());
    }

    @Override
    public boolean isApplicableFor(@NotNull RunConfigurationBase<?> configuration) {
        if (!isPluginEnabled()) {
            return false;
        }
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
            agentJar = new File(pluginDir, "lib//xmx//bin//xmx-agent.jar");
            agentJarFound = agentJar.isFile();
        }
    }
}
