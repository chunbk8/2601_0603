package shapes;

import global.GConstants;

import java.awt.*;

public class GDrawingState {
    private Color lineColor;
    private Color fillColor;
    private int thickness;
    private Color textColor;
    private String fontFamily;
    private int fontSize;

    public GDrawingState() {
        this.lineColor = GConstants.EColor.eBlack.getColor();
        this.fillColor = GConstants.EColor.eTransparent.getColor();
        this.textColor = GConstants.EColor.eBlack.getColor();
        this.thickness = 1;
        this.fontFamily = "맑은 고딕";
        this.fontSize = 20;
    }

    // Getter/Setter
    public Color getLineColor() { return lineColor; }
    public void setLineColor(Color c) { this.lineColor = c; }
    public Color getFillColor() { return fillColor; }
    public void setFillColor(Color c) { this.fillColor = c; }
    public int getThickness() { return thickness; }
    public void setThickness(int t) { this.thickness = t; }
    public Color getTextColor() {return textColor;}
    public void setTextColor(Color textColor) {this.textColor = textColor;}
    public String getFontFamily() { return fontFamily; }
    public void setFontFamily(String fontFamily) { this.fontFamily = fontFamily; }
    public int getFontSize() { return fontSize; }
    public void setFontSize(int fontSize) { this.fontSize = fontSize; }
}
