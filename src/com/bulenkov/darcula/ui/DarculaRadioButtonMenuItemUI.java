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
import com.bulenkov.iconloader.util.ColorUtil;
import com.bulenkov.iconloader.util.EmptyIcon;
import com.bulenkov.iconloader.util.GraphicsConfig;
import com.bulenkov.iconloader.util.Gray;
import sun.swing.MenuItemLayoutHelper;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.IconUIResource;
import java.awt.*;

/**
 * @author Konstantin Bulenkov
 */
public class DarculaRadioButtonMenuItemUI extends DarculaMenuItemUIBase {
  @SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass", "UnusedDeclaration"})
  public static ComponentUI createUI(JComponent c) {
    return new DarculaRadioButtonMenuItemUI();
  }

  protected String getPropertyPrefix() {
      return "RadioButtonMenuItem";
  }

  @Override
  protected void paintMenuItem(final Graphics g, final JComponent c, final Icon checkIcon, final Icon arrowIcon, final Color background, final Color foreground, final int defaultTextIconGap) {
    super.paintMenuItem(g, c, new IconUIResource(EmptyIcon.create((int)(14 * DarculaUIUtil.getScaleFactor() + 0.5f))), arrowIcon, background, foreground, defaultTextIconGap);
  }

  @Override
  protected void paintCheckIcon(Graphics g2, MenuItemLayoutHelper lh, MenuItemLayoutHelper.LayoutResult lr, Color holdc, Color foreground) {
    Graphics2D g = (Graphics2D) g2;
    final GraphicsConfig config = new GraphicsConfig(g);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);

    g.translate(lr.getCheckRect().x+1, lr.getCheckRect().y+1);

    final int scaleFactor = DarculaUIUtil.getScaleFactor();
    int rad = 5 * scaleFactor;

    final int x = 0;
    final int y = 0;
    final int w = 13 * scaleFactor;
    final int h = 13 * scaleFactor;

    g.translate(x, y);

    //setup AA for lines
    Color bg = lh.getMenuItem().getBackground();
    g.setPaint(new GradientPaint(0, 0, ColorUtil.shift(bg, 1.5),
        0, 16, ColorUtil.shift(bg, 1.2)));

    g.fillOval(0, 1, w - 1, h - 1);

    g.setPaint(new GradientPaint(w / 2, 1, Gray._160.withAlpha(90), w / 2, h, Gray._100.withAlpha(90)));
    g.drawOval(0, 2, w - 1, h - 1);

    g.setPaint(Gray._40.withAlpha(200));
    g.drawOval(0, 1, w - 1, h - 1);

    if (lh.getMenuItem().isSelected()) {
      final boolean enabled = lh.getMenuItem().isEnabled();
      g.setColor(UIManager.getColor(enabled ? "RadioButton.darcula.selectionEnabledShadowColor" : "RadioButton.darcula.selectionDisabledShadowColor"));
      g.fillOval((w - rad)/2 , h/2 - rad/2 + 1, rad, rad);
      g.setColor(UIManager.getColor(enabled ? "RadioButton.darcula.selectionEnabledColor" : "RadioButton.darcula.selectionDisabledColor"));
      g.fillOval((w - rad)/2 , h/2 - rad/2, rad, rad);
    }
    config.restore();
    g.translate(-x, -y);


    g.translate(-lr.getCheckRect().x-1, -lr.getCheckRect().y-1);
    config.restore();
  }
}
