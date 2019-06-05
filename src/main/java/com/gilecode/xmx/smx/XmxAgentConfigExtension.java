package com.gilecode.xmx.smx;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunConfigurationExtension;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.openapi.project.ProjectManager;
import org.jetbrains.annotations.NotNull;

public class XmxAgentConfigExtension extends RunConfigurationExtension {

    @Override
    public <T extends RunConfigurationBase> void updateJavaParameters(T configuration, JavaParameters params, RunnerSettings runnerSettings) throws ExecutionException {
//        configuration.getProject().getName();
//        ProjectManager.getInstance().
//        params.getEnv()
    }

    @Override
    public boolean isApplicableFor(@NotNull RunConfigurationBase<?> configuration) {
        return false;
    }
}
