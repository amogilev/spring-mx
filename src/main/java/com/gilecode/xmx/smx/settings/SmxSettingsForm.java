package com.gilecode.xmx.smx.settings;

import com.gilecode.xmx.smx.sessions.PortRangesParser;
import com.gilecode.xmx.smx.sessions.ShowSessionsListAction;
import com.gilecode.xmx.smx.sessions.XmxSessionWatcherService;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.components.labels.LinkLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SmxSettingsForm {
    private JPanel mainPanel;
    private JCheckBox cbPluginEnabled;
    private JTextField textPorts;
    private JPanel optionsPanel;
    private LinkLabel<Object> lblSessions;

    public SmxSettingsForm() {
        cbPluginEnabled.addActionListener(e -> setOptionsInputsEnabled(cbPluginEnabled.isSelected()));
        lblSessions.setIcon(null);
        lblSessions.setText(XmxSessionWatcherService.getInstance().getActiveSessions().size() + " active session(s)");
        lblSessions.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ActionManager.getInstance().tryToExecute(new ShowSessionsListAction(), e, lblSessions, null, true);
            }
        });
    }

    public JComponent getComponent() {
        return mainPanel;
    }

    private void setOptionsInputsEnabled(boolean enabled) {
        setComponentsEnabled(optionsPanel, enabled);
    }

    private void setComponentsEnabled(Container container, boolean enabled) {
        for (Component c : container.getComponents()) {
            c.setEnabled(enabled);
            if (c instanceof Container) {
                setComponentsEnabled((Container) c, enabled);
            }
        }
    }

    public void setState(SmxSettingsState state) {
        cbPluginEnabled.setSelected(state.isEnabled());
        textPorts.setText(state.getPorts());

        setOptionsInputsEnabled(state.isEnabled());
    }

    public SmxSettingsState getState() throws ConfigurationException {
        String ports = textPorts.getText();
        try {
            new PortRangesParser().parsePortRanges(ports);
        } catch (IllegalArgumentException e) {
            throw new ConfigurationException(e.getMessage(),
                    "Invalid ports range");
        }
        return new SmxSettingsState(cbPluginEnabled.isSelected(), ports);
    }
}
