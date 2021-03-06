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
import com.bulenkov.iconloader.util.EmptyIcon;
import com.bulenkov.iconloader.util.GraphicsConfig;
import com.bulenkov.iconloader.util.Gray;
import com.bulenkov.iconloader.util.UIUtil;
import sun.swing.MenuItemLayoutHelper;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.IconUIResource;
import java.awt.*;

/**
 * @author Konstantin Bulenkov
 */
public class DarculaCheckBoxMenuItemUI extends DarculaMenuItemUIBase {

  @SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass", "UnusedDeclaration"})
  public static ComponentUI createUI(JComponent c) {
    return new DarculaCheckBoxMenuItemUI();
  }

  protected String getPropertyPrefix() {
      return "CheckBoxMenuItem";
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

    g.translate(lr.getCheckRect().x+2, lr.getCheckRect().y+2);

    final int scaleFactor = DarculaUIUtil.getScaleFactor();
    final int sz = 13 * scaleFactor;
    g.setPaint(new GradientPaint(sz / 2, 1, Gray._110, sz / 2, sz, Gray._95));
    g.fillRoundRect(0, 0, sz, sz - 1 , 4 * scaleFactor, 4 * scaleFactor);

    g.setPaint(new GradientPaint(sz / 2, 1, Gray._120.withAlpha(0x5a), sz / 2, sz, Gray._105.withAlpha(90)));
    g.drawRoundRect(0, (UIUtil.isUnderDarcula() ? 1 : 0), sz, sz - 1, 4 * scaleFactor, 4 * scaleFactor);

    g.setPaint(Gray._40.withAlpha(180));
    g.drawRoundRect(0, 0, sz, sz - 1, 4 * scaleFactor, 4 * scaleFactor);


    if (lh.getMenuItem().isSelected()) {
      g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
      g.setStroke(new BasicStroke(2.0f * scaleFactor, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
      g.setPaint(Gray._30);
      g.drawLine(4 * scaleFactor, 7 * scaleFactor, 7 * scaleFactor, 10 * scaleFactor);
      g.drawLine(7 * scaleFactor, 10 * scaleFactor, sz, 2 * scaleFactor);
      g.setPaint(Gray._170);
      g.drawLine(4 * scaleFactor, 5 * scaleFactor, 7 * scaleFactor, 8 * scaleFactor);
      g.drawLine(7 * scaleFactor, 8 * scaleFactor, sz, 0);
    }

    g.translate(-lr.getCheckRect().x-2, -lr.getCheckRect().y-2);
    config.restore();
    g.setColor(foreground);
  }
}
