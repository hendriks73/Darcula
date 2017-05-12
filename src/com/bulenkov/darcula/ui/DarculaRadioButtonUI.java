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
import sun.swing.SwingUtilities2;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.IconUIResource;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.metal.MetalRadioButtonUI;
import javax.swing.text.View;
import java.awt.*;
import java.beans.PropertyChangeListener;

import static com.bulenkov.darcula.DarculaUIUtil.getScale;

/**
 * @author Konstantin Bulenkov
 */
public class DarculaRadioButtonUI extends MetalRadioButtonUI {

  private float scale = 1f;
  private PropertyChangeListener sizeVariantListener = evt -> scale = DarculaUIUtil.getScale((JComponent) evt.getSource());

  @SuppressWarnings("MethodOverridesStaticMethodOfSuperclass")
  public static ComponentUI createUI(JComponent c) {
    return new DarculaRadioButtonUI();
  }

  @Override
  protected void installListeners(final AbstractButton b) {
    super.installListeners(b);
    b.addPropertyChangeListener("JComponent.sizeVariant", sizeVariantListener);
    this.scale = getScale(b);
  }

  @Override
  protected void uninstallListeners(final AbstractButton b) {
    b.removePropertyChangeListener("JComponent.sizeVariant", sizeVariantListener);
    super.uninstallListeners(b);
  }

  @Override
  public synchronized void paint(Graphics g, JComponent c) {
    Graphics2D g2d = (Graphics2D)g.create();
    g2d.scale(scale, scale);

    AbstractButton b = (AbstractButton) c;
    ButtonModel model = b.getModel();

    final Dimension s = c.getSize();
    final Dimension size = new Dimension((int)(s.width/scale), (int)(s.height/scale));
    Font f = c.getFont();
    g2d.setFont(f);
    FontMetrics fm = SwingUtilities2.getFontMetrics(c, g2d, f);

    Rectangle viewRect = new Rectangle(size);
    Rectangle iconRect = new Rectangle();
    Rectangle textRect = new Rectangle();

    Insets i = c.getInsets();
    viewRect.x += i.left;
    viewRect.y += i.top;
    viewRect.width -= (i.right + viewRect.x);
    viewRect.height -= (i.bottom + viewRect.y);


    String text = SwingUtilities.layoutCompoundLabel(
      c, fm, b.getText(), getDefaultIcon(),
      b.getVerticalAlignment(), b.getHorizontalAlignment(),
      b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
      viewRect, iconRect, textRect, b.getIconTextGap());

    // fill background
    if(c.isOpaque()) {
      g2d.setColor(b.getBackground());
      g2d.fillRect(0,0, size.width, size.height);
    }

    final int scaleFactor = DarculaUIUtil.getScaleFactor();
    int rad = 5 * scaleFactor;

    // Paint the radio button
    final int x = iconRect.x + (rad-1)/2;
    final int y = iconRect.y + (rad-1)/2;
    final int w = iconRect.width - (rad + 5 * scaleFactor) / 2;
    final int h = iconRect.height - (rad + 5 * scaleFactor) / 2;

    g2d.translate(x, y);

    //setup AA for lines
    final GraphicsConfig config = GraphicsUtil.setupAAPainting(g2d);
    final boolean focus = b.hasFocus();
    g2d.setPaint(new GradientPaint(0, 0, ColorUtil.shift(c.getBackground(), 1.5),
        0, c.getHeight(), ColorUtil.shift(c.getBackground(), 1.2)));
    if (focus) {
      g2d.fillOval(0, 1, w, h);
    } else {
      g2d.fillOval(0, 1, w - 1, h - 1);
    }

    if (focus) {
      if (UIUtil.isRetina()) {
        DarculaUIUtil.paintFocusOval(g2d, 1, 2, w - 2, h - 2);
      } else {
        DarculaUIUtil.paintFocusOval(g2d, 0, 1, w, h);
      }
    } else {
      if (UIUtil.isUnderDarcula()) {
        g2d.setPaint(new GradientPaint(w / 2, 1, Gray._160.withAlpha(90), w / 2, h, Gray._100.withAlpha(90)));
        g2d.drawOval(0, 2, w - 1, h - 1);

        g2d.setPaint(Gray._40.withAlpha(200));
        g2d.drawOval(0, 1, w - 1, h - 1);
      } else {
        g2d.setPaint(b.isEnabled() ? Gray._30 : Gray._130);
        g2d.drawOval(0, 1, w - 1, h - 1);
      }
    }

    if (b.isSelected()) {
      final boolean enabled = b.isEnabled();
      g2d.setColor(UIManager.getColor(enabled ? "RadioButton.darcula.selectionEnabledShadowColor" : "RadioButton.darcula.selectionDisabledShadowColor"));// ? Gray._30 : Gray._60);
      g2d.fillOval(w/2 - rad/2, h/2 - rad/2 + 1, rad, rad);
      g2d.setColor(UIManager.getColor(enabled ? "RadioButton.darcula.selectionEnabledColor" : "RadioButton.darcula.selectionDisabledColor")); //Gray._170 : Gray._120);
      g2d.fillOval(w/2 - rad/2, h/2 - rad/2, rad, rad);
    }
    config.restore();
    g2d.translate(-x, -y);

    // Draw the Text
    if(text != null) {
      View v = (View) c.getClientProperty(BasicHTML.propertyKey);
      if (v != null) {
        v.paint(g2d, textRect);
      } else {
        int mnemIndex = b.getDisplayedMnemonicIndex();
        if(model.isEnabled()) {
          // *** paint the text normally
          g2d.setColor(b.getForeground());
        } else {
          // *** paint the text disabled
          g2d.setColor(getDisabledTextColor());
        }
        SwingUtilities2.drawStringUnderlineCharAt(c, g2d, text,
                                                  mnemIndex, textRect.x, textRect.y + fm.getAscent());
      }
    }
  }

  @Override
  public Icon getDefaultIcon() {
    return new IconUIResource(EmptyIcon.create((int)(20 * scale * DarculaUIUtil.getScaleFactor() + 0.5f)));
  }
}
