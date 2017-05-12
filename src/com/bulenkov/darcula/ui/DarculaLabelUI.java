/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bulenkov.darcula.ui;

import com.bulenkov.darcula.DarculaUIUtil;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalLabelUI;
import java.awt.*;
import java.beans.PropertyChangeListener;

import static com.bulenkov.darcula.DarculaUIUtil.getScale;

/**
 * DarculaLabelUI.
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 */
public class DarculaLabelUI extends MetalLabelUI {

    private float scale = 1f;
    private PropertyChangeListener sizeVariantListener = evt -> scale = DarculaUIUtil.getScale((JComponent) evt.getSource());

    @SuppressWarnings("MethodOverridesStaticMethodOfSuperclass")
    public static ComponentUI createUI(JComponent c) {
        return new DarculaLabelUI();
    }

    protected void installListeners(final JLabel b) {
        super.installListeners(b);
        b.addPropertyChangeListener("JComponent.sizeVariant", sizeVariantListener);
        this.scale = getScale(b);
    }

    protected void uninstallListeners(final JLabel b) {
        b.removePropertyChangeListener("JComponent.sizeVariant", sizeVariantListener);
        super.uninstallListeners(b);
    }

    @Override
    public void paint(final Graphics g, final JComponent c) {
        Graphics2D g2d = (Graphics2D)g.create();
        g2d.scale(scale, scale);
        super.paint(g2d, c);
        g2d.dispose();
    }
}
