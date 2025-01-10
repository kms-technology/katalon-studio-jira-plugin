package com.katalon.plugin.jira.composer.util;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import java.util.Objects;

public class ControlUtil {
    // A key for the flag to ignore the object org.eclipse.swt.widgets.Widget when the method recursiveSetEnabled() proceeds them
    // Because the key is a flag, so non-null value (whatever type) is true and otherwise is false
    public static final String IGNORE_SET_ENABLED_DATA_KEY = "ignoreSetEnabled";

    public static void recursiveSetEnabled(Control ctrl, boolean enabled) {
        if (ctrl instanceof Composite) {
            Composite comp = (Composite) ctrl;
            for (Control c : comp.getChildren()) {
                if (c instanceof Composite) {
                    recursiveSetEnabled(c, enabled);
                }
                else if (Objects.isNull(c.getData(IGNORE_SET_ENABLED_DATA_KEY))) {
                    c.setEnabled(enabled);
                }
            }
        } else {
            if (Objects.isNull(ctrl.getData(IGNORE_SET_ENABLED_DATA_KEY))) {
                ctrl.setEnabled(enabled);
            }
        }
    }
}
