package com.gilecode.xmx.smx.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SmxConfigurable implements Configurable {

    private SmxSettingsForm form;
    private final SmxSettingService settingService;

    public SmxConfigurable(SmxSettingService settingService) {
        this.settingService = settingService;
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Spring MX";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return getForm().getComponent();
    }

    private SmxSettingsForm getForm() {
        if (form == null) {
            form = new SmxSettingsForm();
        }
        return form;
    }

    @Override
    public boolean isModified() {
        try {
            return !settingService.getState().equals(getForm().getState());
        } catch (ConfigurationException e) {
            return true;
        }
    }

    @Override
    public void apply() throws ConfigurationException {
        settingService.loadState(getForm().getState());
    }

    @Override
    public void reset() {
        getForm().setState(settingService.getState());
    }
}
