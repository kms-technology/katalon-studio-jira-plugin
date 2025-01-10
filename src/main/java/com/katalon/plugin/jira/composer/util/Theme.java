package com.katalon.plugin.jira.composer.util;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.SWT;

public class Theme {
    public static Color getSecondaryColor() {
        return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
    }

    public static Color getErrorColor() {
        return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
    }
}
