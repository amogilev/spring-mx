// Copyright © 2019 Andrey Mogilev. All rights reserved.

package com.gilecode.xmx.smx.settings;

import com.gilecode.xmx.smx.sessions.XmxPortProviderServiceImpl;
import org.jetbrains.annotations.NotNull;

public class SmxSettingsState {

    private boolean enabled = true;

    @NotNull
    private String ports = XmxPortProviderServiceImpl.DEFAULT_PORTS_RANGE;

    public SmxSettingsState() {
    }

    public SmxSettingsState(boolean enabled, @NotNull String ports) {
        this.enabled = enabled;
        this.ports = ports;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @NotNull
    public String getPorts() {
        return ports;
    }

    public void setPorts(@NotNull String ports) {
        this.ports = ports;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SmxSettingsState that = (SmxSettingsState) o;

        if (enabled != that.enabled) return false;
        return ports.equals(that.ports);

    }

    @Override
    public int hashCode() {
        int result = (enabled ? 1 : 0);
        result = 31 * result + ports.hashCode();
        return result;
    }
}
