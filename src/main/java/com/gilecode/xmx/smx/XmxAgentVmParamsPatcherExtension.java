package com.gilecode.xmx.smx;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.JavaProgramPatcher;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.util.PathsList;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class XmxAgentVmParamsPatcherExtension extends JavaProgramPatcher {

    public static final String PATTERN_SPRING_JAR = "\\bspring[\\p{Alnum}.-]*\\.jar$";

    private final Pattern patternSpringJar = Pattern.compile(PATTERN_SPRING_JAR);
    private File agentJar;
    private File xmxHomeDir;
    private boolean agentJarFound = false;

    @Override
    public void patchJavaParameters(Executor executor, RunProfile configuration, JavaParameters javaParameters) {
        if (!isApplicableFor(executor, configuration, javaParameters)) {
            return;
        }
        javaParameters.getVMParametersList().add("-javaagent:" + agentJar.getAbsolutePath() + "=" + buildXmxParams());
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

    public boolean isApplicableFor(Executor executor, RunProfile configuration, JavaParameters params) {
        if (!isPluginEnabled()) {
            return false;
        }

        if (!containsSpringJars(params.getClassPath()) && !containsSpringJars(params.getModulePath())) {
            return false;
        }


        // TODO: check that enabled for the configuration of this type (Run vs Debug)
        ensureAgentJar();
        return agentJarFound;
    }

    private boolean containsSpringJars(PathsList classPath) {
        for (String path : classPath.getPathList()) {
            if (isSpringJar(path)) {
                return true;
            }
        }
        return false;
    }

    boolean isSpringJar(String path) {
        return patternSpringJar.matcher(path).find();
    }

    private String findAvailablePort() {
        // TODO: get port range from config, detect available, maybe mark as busy
        return "8081";
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
