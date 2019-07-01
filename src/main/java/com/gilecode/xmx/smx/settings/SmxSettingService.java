// Copyright © 2019 Andrey Mogilev. All rights reserved.

package com.gilecode.xmx.smx.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import org.jetbrains.annotations.NotNull;

public interface SmxSettingService extends PersistentStateComponent<SmxSettingsState> {

    static SmxSettingService getInstance() {
        return ServiceManager.getService(SmxSettingService.class);
    }

    @NotNull
    @Override
    SmxSettingsState getState();
}
