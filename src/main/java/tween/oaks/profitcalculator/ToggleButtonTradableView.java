package tween.oaks.profitcalculator;

import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import java.awt.*;

public class ToggleButtonTradableView extends JToggleButton {
    private String text;
    private String selectedText;
    private final int HEIGHT = 28;
    private final int HORIZONTAL_PADDING = 10;
    private final int ARCH = 10;

    private Color rightBackgroundColor1 = new Color(0x015EAC);
    private Color rightBackgroundColor2 = new Color(0x298DE2);

    private Color leftBackgroundColor1 = new Color(0x9E0101);
    private Color leftBackgroundColor2 = new Color(0xD22929);

    private final Color selectedTextColor = new Color(0xFFFFFF);
    private final Color unselectedTextColor = new Color(0xC6C6C6);
    private final Color unselectedHoveredTextColor = new Color(0x808080);

    private final Color backgroundColor1 = new Color(0xFFFFFF);
    private final Color backgroundColor2 = new Color(0xF2F2F2);

    private final Color hoveredBackgroundColor1 = new Color(0xFFFFFF);
    private final Color hoveredBackgroundColor2 = new Color(0xF9F9F9);

    private final Color borderColor = new Color(0xD6D6D6);

    public ToggleButtonTradableView(String text, String selectedText) {
        this.text = text;
        this.selectedText = selectedText;
        setOpaque(false);
        setRolloverEnabled(true);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        RoundRectangle2D roundRectangle = new RoundRectangle2D.Float(0, 0,
                getWidth() - 1,
                getHeight() - 1, ARCH, ARCH);
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(borderColor);
        g2.draw(roundRectangle);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setStroke(new BasicStroke(1f));
        Font font = getCurrentFont();
        FontMetrics metrics = g2.getFontMetrics(font);
        int textHeight = metrics.getHeight();
        int textWidth = metrics.stringWidth(text);
        g2.setFont(getCurrentFont());
        drawBackground(g2);
        int y = (getHeight() + textHeight / 2) / 2;
        if (!isEnabled()) {
            drawLeftTextUnselected(g2, y);
            drawRightTextUnselected(g2, textWidth, y);
            return;
        }
        if (isSelected()) {
            drawRightTextSelected(g2, textWidth, y);
            drawLeftTextUnselected(g2, y);
        } else {
            drawLeftTextSelected(g2, textWidth, y);
            drawRightTextUnselected(g2, textWidth, y);
        }
    }

    private Font getCurrentFont() {
        Font defaultFont = UIManager.getFont("Label.font");
        return new Font(defaultFont.getFontName(), Font.BOLD, defaultFont.getSize() + 4);
    }

    private void drawBackground(Graphics2D g2) {
        setBackgroundPaint(g2);
        RoundRectangle2D roundRectangle = new RoundRectangle2D.Float(1, 1,
                getWidth() - 1,
                getHeight() - 1, ARCH, ARCH);
        g2.fill(roundRectangle);
    }

    private void drawRightTextSelected(Graphics2D g2, int textWidth, int y) {
        fillRightRoundRectangle(g2, textWidth);
        g2.setColor(Color.WHITE);
        int x = getRightTextX(textWidth);
        g2.drawString(selectedText, x, y);
    }

    private void fillRightRoundRectangle(Graphics2D g2, int textWidth) {
        setRightRectanglePaint(g2);
        int x = getRightButtonX(textWidth);
        RoundRectangle2D selectedPart = new RoundRectangle2D.Float(x, 1,
                getWidth() - 1 - x, getHeight() - 1, ARCH, ARCH);
        g2.fill(selectedPart);
        g2.fillRect(x, 1, getWidth() - 1 - x - ARCH, getHeight());
    }

    private void fillLeftRoundRectangle(Graphics2D g2, int textWidth) {
        setLeftRectanglePaint(g2);
        int x = getRightButtonX(textWidth);
        RoundRectangle2D selectedPart = new RoundRectangle2D.Float(1, 1, x,
                getHeight() - 1, ARCH, ARCH);
        g2.fill(selectedPart);
        g2.fillRect(1 + ARCH, 1, x - ARCH, getHeight() - 1);
    }

    private void setBackgroundPaint(Graphics2D g2) {
        GradientPaint gp;
        if (!model.isRollover()) {
            gp = new GradientPaint(0, 0, backgroundColor1, 0,
                    getHeight(), backgroundColor2);
        } else {
            gp = new GradientPaint(0, 0, hoveredBackgroundColor1,
                    0, getHeight(), hoveredBackgroundColor2);
        }
        g2.setPaint(gp);
    }

    private void setRightRectanglePaint(Graphics2D g2) {
        GradientPaint gp = new GradientPaint(0, 0, rightBackgroundColor1, 0,
                getHeight(), rightBackgroundColor2);
        g2.setPaint(gp);
    }

    private void setLeftRectanglePaint(Graphics2D g2) {
        GradientPaint gp = new GradientPaint(0, 0, leftBackgroundColor1, 0,
                getHeight(), leftBackgroundColor2);
        g2.setPaint(gp);
    }

    private void drawLeftTextUnselected(Graphics2D g2, int y) {
        setUnselectedTextColor(g2);
        int x = HORIZONTAL_PADDING;
        g2.drawString(text, x, y);
    }

    private void setUnselectedTextColor(Graphics2D g2) {
        if (model.isRollover()) {
            g2.setColor(unselectedHoveredTextColor);
        } else {
            g2.setColor(unselectedTextColor);
        }
    }

    private int getRightTextX(int textWidth) {
        return 3 * HORIZONTAL_PADDING + textWidth;
    }

    private int getRightButtonX(int textWidth) {
        return 2 * HORIZONTAL_PADDING + textWidth;
    }

    private void drawRightTextUnselected(Graphics2D g2, int textWidth, int y) {
        int x = getRightTextX(textWidth);
        setUnselectedTextColor(g2);
        g2.drawString(selectedText, x, y);
    }

    private void drawLeftTextSelected(Graphics2D g2, int textWidth, int y) {
        fillLeftRoundRectangle(g2, textWidth);
        g2.setColor(selectedTextColor);
        int x = HORIZONTAL_PADDING;
        g2.drawString(text, x, y);
    }

    @Override
    public Dimension getPreferredSize() {
        Font font = getCurrentFont();
        FontMetrics fm = getFontMetrics(font);
        return new Dimension(
                fm.stringWidth(text + selectedText) + 4 * HORIZONTAL_PADDING,
                HEIGHT);
    }
}

