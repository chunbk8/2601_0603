package shapes;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class GText extends GShape {
    private String text;
    private Font font;

    public GText() {
        this.shape = new Rectangle2D.Double();
        this.text = ""; //default text
        this.font = new Font("맑은 고딕", Font.PLAIN, 20); //default style
    }

    public void setText(String text) { this.text = text; }
    public String getText() { return text; }
    public Font getFont() { return font; }
    public void setFont(Font font) { this.font = font; }

    @Override
    public void setLocation0(int x, int y) {
        Rectangle2D r = (Rectangle2D) shape;
        r.setFrame(x, y, 0, 0);
        this.x0 = x;
        this.y0 = y;
    }

    @Override
    public void setLocation1(int x, int y, boolean isShift) {
        Rectangle2D r = (Rectangle2D) shape;

        int newX = Math.min(this.x0, x);
        int newY = Math.min(this.y0, y);
        int w = Math.abs(x - this.x0);
        int h = Math.abs(y - this.y0);
        r.setFrame(newX, newY, w, h);
        this.rotCx = r.getCenterX();
        this.rotCy = r.getCenterY();
    }

    @Override
    public void draw(Graphics2D g) {

        g.rotate(this.angle, this.rotCx, this.rotCy);

        if (!this.fillColor.equals(global.GConstants.EColor.eTransparent.getColor())) {
            g.setColor(this.fillColor);
            g.fill(this.shape);
        }
        Stroke oldStroke = g.getStroke();
        g.setStroke(new BasicStroke(this.thickness));
        g.setColor(this.lineColor);
        g.draw(this.shape);
        g.setStroke(oldStroke);

        if (text != null && !text.isEmpty()) {
            g.setColor(this.textColor);
            g.setFont(this.font);

            Rectangle2D r = (Rectangle2D) shape;

            FontMetrics fm = g.getFontMetrics(this.font);
            g.drawString(text, (int) r.getX() + 5, (int) r.getY() + fm.getAscent());
        }

        if (this.isSelected) {
            this.drawAnchors(g);
        }

        g.rotate(-this.angle, this.rotCx, this.rotCy);
    }

    @Override
    public GShape clone() {
        GText cloned = (GText) super.clone();
        cloned.shape = (Shape) ((Rectangle2D.Double) this.shape).clone();
        cloned.text = this.text;
        cloned.font = this.font;
        return cloned;
    }

    @Override
    public void translate(int dx, int dy) {
        super.translate(dx, dy);
        Rectangle2D r = (Rectangle2D) shape;
        r.setFrame(r.getX() + dx, r.getY() + dy, r.getWidth(), r.getHeight());
        this.rotCx += dx;
        this.rotCy += dy;


    }

    @Override
    public void scale(double sx, double sy, double tx, double ty) {
        Rectangle2D r = (Rectangle2D) shape;
        double newX1 = tx + (r.getX() - tx) * sx;
        double newY1 = ty + (r.getY() - ty) * sy;
        double newX2 = tx + (r.getX() + r.getWidth() - tx) * sx;
        double newY2 = ty + (r.getY() + r.getHeight() - ty) * sy;

        r.setFrame(
                Math.min(newX1, newX2), Math.min(newY1, newY2),
                Math.abs(newX2 - newX1), Math.abs(newY2 - newY1)
        );
    }
}