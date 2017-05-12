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
import sun.swing.SwingUtilities2;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.IconUIResource;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.metal.MetalCheckBoxUI;
import javax.swing.text.View;
import java.awt.*;
import java.beans.PropertyChangeListener;

import static com.bulenkov.darcula.DarculaUIUtil.getScale;
import static com.bulenkov.darcula.ui.DarculaButtonUI.isIndeterminate;

/**
 * @author Konstantin Bulenkov
 */
public class DarculaCheckBoxUI extends MetalCheckBoxUI {

  private float scale = 1f;
  private PropertyChangeListener sizeVariantListener = evt -> scale = DarculaUIUtil.getScale((JComponent) evt.getSource());

  @SuppressWarnings("MethodOverridesStaticMethodOfSuperclass")
  public static ComponentUI createUI(JComponent c) {
    if (UIUtil.getParentOfType(CellRendererPane.class, c) != null) {
      c.setBorder(null);
    }
    return new DarculaCheckBoxUI();
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

    JCheckBox b = (JCheckBox) c;
    final ButtonModel model = b.getModel();
    final Dimension s = c.getSize();
    final Dimension size = new Dimension((int)(s.width/scale), (int)(s.height/scale));
    final Font font = c.getFont();

    g2d.setFont(font);
    FontMetrics fm = SwingUtilities2.getFontMetrics(c, g2d, font);

    Rectangle viewRect = new Rectangle(size);
    Rectangle iconRect = new Rectangle();
    Rectangle textRect = new Rectangle();

    Insets i = c.getInsets();
    viewRect.x += i.left;
    viewRect.y += i.top;
    viewRect.width -= (i.right + viewRect.x);
    viewRect.height -= (i.bottom + viewRect.y);

    final int scaleFactor = DarculaUIUtil.getScaleFactor();
    String text = SwingUtilities.layoutCompoundLabel(c, fm, b.getText(), getDefaultIcon(),
                                                     b.getVerticalAlignment(), b.getHorizontalAlignment(),
                                                     b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
                                                     viewRect, iconRect, textRect, b.getIconTextGap());

    //background
    if (c.isOpaque()) {
      g2d.setColor(b.getBackground());
      g2d.fillRect(0, 0, size.width, size.height);
    }

    if (b.isSelected() && b.getSelectedIcon() != null) {
      b.getSelectedIcon().paintIcon(b, g2d, iconRect.x + 4, iconRect.y + 2);
    } else if (!b.isSelected() && b.getIcon() != null) {
      b.getIcon().paintIcon(b, g2d, iconRect.x + 4, iconRect.y + 2);
    } else {
      final int x = iconRect.x + 3 * scaleFactor;
      final int y = iconRect.y + 3 * scaleFactor;
      final int w = iconRect.width - 6 * scaleFactor;
      final int h = iconRect.height - 6 * scaleFactor;

      g2d.translate(x, y);
      final Paint paint = new GradientPaint(w / 2, 0, b.getBackground().brighter(),
                                                    w / 2, h, b.getBackground());
      g2d.setPaint(paint);
      g2d.fillRect(1, 1, w - 2, h - 2);

      //setup AA for lines
      final GraphicsConfig config = new GraphicsConfig(g2d);
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);

      final boolean armed = b.getModel().isArmed();

      if (c.hasFocus()) {
        g2d.setPaint(new GradientPaint(w/2, 1, getFocusedBackgroundColor1(armed), w/2, h, getFocusedBackgroundColor2(armed)));
        g2d.fillRoundRect(0, 0, w - 2, h - 2, 4 * scaleFactor, 4 * scaleFactor);

        DarculaUIUtil.paintFocusRing(g2d, 1, 1, w - 2, h - 2);
      } else {
        g2d.setPaint(new GradientPaint(w / 2, 1, getBackgroundColor1(), w / 2, h, getBackgroundColor2()));
        g2d.fillRoundRect(0, 0, w, h - 1 , 4 * scaleFactor, 4 * scaleFactor);

        g2d.setPaint(new GradientPaint(w / 2, 1, getBorderColor1(b.isEnabled()), w / 2, h, getBorderColor2(b.isEnabled())));
        g2d.drawRoundRect(0, (UIUtil.isUnderDarcula() ? 1 : 0), w, h - 1, 4 * scaleFactor, 4 * scaleFactor);

        g2d.setPaint(getInactiveFillColor());
        g2d.drawRoundRect(0, 0, w, h - 1, 4 * scaleFactor, 4 * scaleFactor);
      }

      if (isIndeterminate(c) && b.getModel().isSelected()) {
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setStroke(new BasicStroke(1*2.0f*scaleFactor, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g2d.setPaint(getShadowColor(b.isEnabled()));
        g2d.drawLine(4 * scaleFactor, 8 * scaleFactor, w-4 * scaleFactor, 8 * scaleFactor);
        g2d.setPaint(getCheckSignColor(b.isEnabled()));
        g2d.drawLine(4 * scaleFactor, 6 * scaleFactor, w-4 * scaleFactor, 6 * scaleFactor);
      } else if (b.getModel().isSelected()) {
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setStroke(new BasicStroke(1 *2.0f*scaleFactor, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g2d.setPaint(getShadowColor(b.isEnabled()));
        g2d.drawLine(4 * scaleFactor, 7 * scaleFactor, 7 * scaleFactor, 11 * scaleFactor);
        g2d.drawLine(7 * scaleFactor, 11 * scaleFactor, w, 2 * scaleFactor);
        g2d.setPaint(getCheckSignColor(b.isEnabled()));
        g2d.drawLine(4 * scaleFactor, 5 * scaleFactor, 7 * scaleFactor, 9 * scaleFactor);
        g2d.drawLine(7 * scaleFactor, 9 * scaleFactor, w, 0);
      }
      g2d.translate(-x, -y);
      config.restore();
    }

    //text
    if(text != null) {
      View view = (View) c.getClientProperty(BasicHTML.propertyKey);
      if (view != null) {
        view.paint(g2d, textRect);
      } else {
        g2d.setColor(model.isEnabled() ? b.getForeground() : getDisabledTextColor());
        SwingUtilities2.drawStringUnderlineCharAt(c, g2d, text,
                                                  b.getDisplayedMnemonicIndex(),
                                                  textRect.x,
                                                  textRect.y + fm.getAscent());
      }
    }
  }

  protected Color getInactiveFillColor() {
    return getColor("inactiveFillColor", Gray._40.withAlpha(180));
  }

  protected Color getBorderColor1(boolean enabled) {
    return enabled ? getColor("borderColor1", Gray._120.withAlpha(0x5a))
                   : getColor("disabledBorderColor1", Gray._120.withAlpha(90));
  }

  protected Color getBorderColor2(boolean enabled) {
    return enabled ? getColor("borderColor2", Gray._105.withAlpha(90))
                   : getColor("disabledBorderColor2", Gray._105.withAlpha(90));
  }

  protected Color getBackgroundColor1() {
    return getColor("backgroundColor1", Gray._110);
  }

  protected Color getBackgroundColor2() {
    return getColor("backgroundColor2", Gray._95);
  }

  protected Color getCheckSignColor(boolean enabled) {
    return enabled ? getColor("checkSignColor", Gray._170)
                   : getColor("checkSignColorDisabled", Gray._120);
  }

  protected Color getShadowColor(boolean enabled) {
    return enabled ? getColor("shadowColor", Gray._30)
                   : getColor("shadowColorDisabled", Gray._60);
  }

  protected Color getFocusedBackgroundColor1(boolean armed) {
    return armed ? getColor("focusedArmed.backgroundColor1", Gray._100)
                 : getColor("focused.backgroundColor1", Gray._120);
  }

  protected Color getFocusedBackgroundColor2(boolean armed) {
    return armed ? getColor("focusedArmed.backgroundColor2", Gray._55)
                 : getColor("focused.backgroundColor2", Gray._75);
  }

  protected static Color getColor(String shortPropertyName, Color defaultValue) {
    final Color color = UIManager.getColor("CheckBox.darcula." + shortPropertyName);
    return color == null ? defaultValue : color;
  }

  @Override
  public Icon getDefaultIcon() {
    return new IconUIResource(EmptyIcon.create((int)(20 * scale * DarculaUIUtil.getScaleFactor() + 0.5f)));
  }
}
