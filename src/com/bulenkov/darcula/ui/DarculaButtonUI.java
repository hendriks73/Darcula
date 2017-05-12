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
import com.bulenkov.iconloader.util.SystemInfo;
import com.bulenkov.iconloader.util.GraphicsConfig;
import com.bulenkov.iconloader.util.GraphicsUtil;
import sun.swing.SwingUtilities2;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;
import java.awt.*;
import java.beans.PropertyChangeListener;

import static com.bulenkov.darcula.DarculaUIUtil.getScale;

/**
 * @author Konstantin Bulenkov
 */
public class DarculaButtonUI extends BasicButtonUI {

  private float scale = 1f;
  private PropertyChangeListener sizeVariantListener = evt -> scale = getScale((JComponent) evt.getSource());

  @SuppressWarnings("MethodOverridesStaticMethodOfSuperclass")
  public static ComponentUI createUI(JComponent c) {
    return new DarculaButtonUI();
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

  public static boolean isSquare(Component c) {
    return c instanceof JButton && "square".equals(((JButton)c).getClientProperty("JButton.buttonType"));
  }

  public static boolean isIndeterminate(Component c) {
    return c instanceof JComponent && "indeterminate".equals(((JComponent)c).getClientProperty("JButton.selectedState"));
  }

  @Override
  public Dimension getPreferredSize(final JComponent c) {
    final Dimension d = super.getPreferredSize(c);
    return new Dimension((int)(d.width * scale + 0.5f), (int)(d.height * scale + 0.5f));
  }

  private static Rectangle viewRect = new Rectangle();
  private static Rectangle textRect = new Rectangle();
  private static Rectangle iconRect = new Rectangle();

  private String layout(AbstractButton b, FontMetrics fm,
                        int width, int height) {
    Insets i = b.getInsets();
    viewRect.x = i.left;
    viewRect.y = i.top;
    viewRect.width = width - (i.right + viewRect.x);
    viewRect.height = height - (i.bottom + viewRect.y);

    textRect.x = textRect.y = textRect.width = textRect.height = 0;
    iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;

    // layout the text and icon
    return SwingUtilities.layoutCompoundLabel(
            b, fm, b.getText(), b.getIcon(),
            b.getVerticalAlignment(), b.getHorizontalAlignment(),
            b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
            viewRect, iconRect, textRect,
            b.getText() == null ? 0 : b.getIconTextGap());
  }

  @Override
  public void paint(Graphics g, JComponent c) {
    final Graphics2D g2d = (Graphics2D) g.create();
    g2d.scale(scale, scale);
    final AbstractButton b = (AbstractButton) c;
    final ButtonModel model = b.getModel();
    final Border border = c.getBorder();
    final GraphicsConfig config = GraphicsUtil.setupAAPainting(g2d);
    final boolean square = isSquare(c);
    final int h = (int) (c.getHeight()/scale);
    final int w = (int) (c.getWidth()/scale);
    if (c.isEnabled() && border != null && b.isContentAreaFilled()) {
      final Insets ins = border.getBorderInsets(c);
      final int yOff = (ins.top + ins.bottom) / 4;
      if (!square) {
        if (c instanceof JButton && ((JButton)c).isDefaultButton() || model.isSelected()) {
          g2d.setPaint(new GradientPaint(0, 0, getSelectedButtonColor1(), 0, h, getSelectedButtonColor2()));
        }
        else {
          g2d.setPaint(new GradientPaint(0, 0, getButtonColor1(), 0, h, getButtonColor2()));
        }
      }
      final int scaleFactor = DarculaUIUtil.getScaleFactor();
      final int x = (square ? 2 : 4) * scaleFactor;
      final int width = w - 2 * 4 * scaleFactor;
      final int height = h - 2 * yOff;
      final int arcWidth = (square ? 3 : 5) * scaleFactor;
      final int arcHeight = (square ? 3 : 5) * scaleFactor;
      g2d.fillRoundRect(x, yOff, width, height, arcWidth, arcHeight);
    }
    config.restore();

    String text = layout(b, SwingUtilities2.getFontMetrics(b, g2d), w, h);

    clearTextShiftOffset();

    // perform UI specific press action, e.g. Windows L&F shifts text
    if (model.isArmed() && model.isPressed()) {
      paintButtonPressed(g2d, b);
    }

    // Paint the Icon
    if(b.getIcon() != null) {
      paintIcon(g2d, c, iconRect);
    }

    if (text != null && !text.equals("")){
      View v = (View) c.getClientProperty(BasicHTML.propertyKey);
      if (v != null) {
        v.paint(g2d, textRect);
      } else {
        paintText(g2d, b, textRect, text);
      }
    }

    if (b.isFocusPainted() && b.hasFocus()) {
      // paint UI specific focus
      paintFocus(g2d,b,viewRect,textRect,iconRect);
    }
  }

  protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
    final Graphics2D g2d = (Graphics2D) g.create();
    final AbstractButton button = (AbstractButton)c;
    final ButtonModel model = button.getModel();
    Color fg = button.getForeground();
    if (fg instanceof UIResource && button instanceof JButton && ((JButton)button).isDefaultButton()) {
      final Color selectedFg = UIManager.getColor("Button.darcula.selectedButtonForeground");
      if (selectedFg != null) {
        fg = selectedFg;
      }
    }
    g2d.setColor(fg);

    FontMetrics metrics = SwingUtilities2.getFontMetrics(c, g2d);
    int mnemonicIndex = button.getDisplayedMnemonicIndex();
    if (model.isEnabled()) {

      SwingUtilities2.drawStringUnderlineCharAt(c, g2d, text, mnemonicIndex,
                                                textRect.x + getTextShiftOffset(),
                                                textRect.y + metrics.getAscent() + getTextShiftOffset());
    }
    else {
      g2d.setColor(UIManager.getColor("Button.darcula.disabledText.shadow"));
      SwingUtilities2.drawStringUnderlineCharAt(c, g2d, text, -1,
                                                textRect.x + getTextShiftOffset()+1,
                                                textRect.y + metrics.getAscent() + getTextShiftOffset()+1);
      g2d.setColor(UIManager.getColor("Button.disabledText"));
      SwingUtilities2.drawStringUnderlineCharAt(c, g2d, text, -1,
                                                textRect.x + getTextShiftOffset(),
                                                textRect.y + metrics.getAscent() + getTextShiftOffset());


    }
  }

  @Override
  public void update(Graphics g, JComponent c) {
    super.update(g, c);
    if (c instanceof JButton && ((JButton)c).isDefaultButton() && !SystemInfo.isMac) {
      if (!c.getFont().isBold()) {
       c.setFont(c.getFont().deriveFont(Font.BOLD));
      }
    }
  }

  protected Color getButtonColor1() {
    return UIManager.getColor("Button.darcula.color1");
  }

  protected Color getButtonColor2() {
    return UIManager.getColor("Button.darcula.color2");
  }

  protected Color getSelectedButtonColor1() {
    return UIManager.getColor("Button.darcula.selection.color1");
  }

  protected Color getSelectedButtonColor2() {
    return UIManager.getColor("Button.darcula.selection.color2");
  }
}
