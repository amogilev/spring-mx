package com.gilecode.xmx.smx;

import com.gilecode.xmx.smx.impl.XmxSessionWatcherServiceImpl;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.JavaProgramPatcher;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.util.PathsList;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class XmxAgentVmParamsPatcherExtension extends JavaProgramPatcher {

    public static final String PATTERN_SPRING_JAR = "\\bspring[\\p{Alnum}.-]*\\.jar$";

    private static final Logger logger = Logger.getInstance(XmxSessionWatcherServiceImpl.class);

    private final Pattern patternSpringJar = Pattern.compile(PATTERN_SPRING_JAR);
    private File agentJar;
    private File xmxHomeDir;
    private boolean agentJarFound = false;

    private final XmxPortProviderService portProvider;

    public XmxAgentVmParamsPatcherExtension(XmxPortProviderService portProvider) {
        this.portProvider = portProvider;
    }

    @Override
    public void patchJavaParameters(Executor executor, RunProfile configuration, JavaParameters javaParameters) {
        if (!isApplicableFor(executor, configuration, javaParameters)) {
            return;
        }

        try {
            javaParameters.getVMParametersList().add("-javaagent:" + agentJar.getAbsolutePath() + "=" + buildXmxParams());
        } catch (SmxRunConfigException e) {
            logger.error("Failed to inject XMX agent", e);
        }
    }

    private String buildXmxParams() throws SmxRunConfigException {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("EmbeddedWebServer.Port", findAvailablePort());
        params.put("config", new File(xmxHomeDir, "smx.ini").getAbsolutePath());

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

    private String findAvailablePort() throws SmxRunConfigException {
        Integer port = portProvider.findAndReserveFreeXmxPort();
        if (port == null) {
            throw new SmxRunConfigException("No available ports found for XMX agent");
        } else {
            return Integer.toString(port);
        }
    }

    private boolean isPluginEnabled() {
        // TODO: check that the plugin is enabled
        return true;
    }

    private void ensureAgentJar() {
        if (agentJar == null) {
            IdeaPluginDescriptor plugin = PluginManager.getPlugin(PluginId.getId(Constants.PLUGIN_ID));
            if (plugin != null) {
                File pluginDir = plugin.getPath();
                xmxHomeDir = new File(pluginDir, "lib//xmx");
                agentJar = new File(xmxHomeDir, "bin//xmx-agent.jar");
                agentJarFound = agentJar.isFile();
            } else {
                logger.error("Failed to obtain IdeaPluginDescriptor");
            }
        }
    }

}
