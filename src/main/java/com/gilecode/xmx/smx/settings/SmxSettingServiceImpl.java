package com.gilecode.xmx.smx.settings;

import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

@State(name="SpringMX.Settings", storages = @Storage("spring-mx.xml"))
public class SmxSettingServiceImpl implements SmxSettingService {

    private SmxSettingsState state = new SmxSettingsState();

    @NotNull
    @Override
    public SmxSettingsState getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull SmxSettingsState state) {
        this.state = state;
    }
}
