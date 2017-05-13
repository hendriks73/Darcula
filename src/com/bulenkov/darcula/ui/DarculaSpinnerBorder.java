/*
 * Copyright 2000-2014 JetBrains s.r.o.
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
import com.bulenkov.iconloader.util.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.UIResource;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

/**
 * @author Konstantin Bulenkov
 */
public class DarculaSpinnerBorder implements Border, UIResource {

  @Override
  public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
    final JSpinner spinner = (JSpinner)c;
    final JFormattedTextField editor = UIUtil.findComponentOfType(spinner, JFormattedTextField.class);
    final int scaleFactor = DarculaUIUtil.getScaleFactor();
    final int x1 = x + 1*scaleFactor;
    final int y1 = y + 3*scaleFactor;
    final int width1 = width - 2*scaleFactor;
    final int height1 = height - 6*scaleFactor;
    final boolean focused = c.isEnabled() && c.isVisible() && editor != null && editor.hasFocus();
    final GraphicsConfig config = GraphicsUtil.setupAAPainting(g);

    if (c.isOpaque()) {
      g.setColor(UIUtil.getPanelBackground());
      g.fillRect(x, y, width, height);
    }

    g.setColor(UIUtil.getTextFieldBackground());
    g.fillRoundRect(x1, y1, width1, height1, 5*scaleFactor, 5*scaleFactor);
    g.setColor(UIManager.getColor(spinner.isEnabled() ? "Spinner.darcula.enabledButtonColor" : "Spinner.darcula.disabledButtonColor"));
    if (editor != null) {
      final int off = editor.getBounds().x + editor.getWidth() + ((JSpinner)c).getInsets().left + 1*scaleFactor;
      final Area rect = new Area(new RoundRectangle2D.Double(x1, y1, width1, height1, 5*scaleFactor, 5*scaleFactor));
      final Area blueRect = new Area(new Rectangle(off, y1, 22*scaleFactor, height1));
      rect.intersect(blueRect);
      ((Graphics2D)g).fill(rect);
      if (UIUtil.isUnderDarcula()) {
        g.setColor(Gray._100);
        g.drawLine(off, y1, off, height1 + 2*scaleFactor);
      }
    }

    if (!c.isEnabled()) {
      ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
    }

    if (focused) {
      DarculaUIUtil.paintFocusRing(g, x1 + 2*scaleFactor, y1, width1 - 3*scaleFactor, height1);
    } else {
      g.setColor(new DoubleColor(Gray._149,Gray._100));
      g.drawRoundRect(x1, y1, width1, height1, 5*scaleFactor, 5*scaleFactor);
    }
    config.restore();
  }

  @Override
  public Insets getBorderInsets(Component c) {
    final int scaleFactor = DarculaUIUtil.getScaleFactor();
    return new InsetsUIResource(5*scaleFactor, 7*scaleFactor, 5*scaleFactor, 7*scaleFactor);
  }

  @Override
  public boolean isBorderOpaque() {
    return true;
  }
}
