package com.gilecode.xmx.smx.sessions;

import com.intellij.ide.browsers.BrowserLauncher;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.popup.*;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.ui.components.JBLabel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class ShowSessionsListAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        List<XmxSession> sessions = XmxSessionWatcherService.getInstance().getActiveSessions();
        if (sessions.isEmpty()) {
            // null stands for "no sessions"
            sessions.add(null);
        }
        ListPopupStep<XmxSession> step = new BaseListPopupStep<XmxSession>("Active SpringMX sessions", sessions) {
            @Nullable
            @Override
            public PopupStep onChosen(XmxSession session, boolean finalChoice) {
                if (session != null) {
                    BrowserLauncher.getInstance().browse("http://localhost:" + session.getPort() + "/smx/", null);
                }
                return super.onChosen(session, finalChoice);
            }

            @NotNull
            @Override
            public String getTextFor(XmxSession session) {
                return session == null ? "<no active sessions>" : session.toString();
            }
        };
        ListPopup listPopup = JBPopupFactory.getInstance().createListPopup(step);
        listPopup.showInBestPositionFor(e.getDataContext());
    }
}
