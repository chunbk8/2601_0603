package shapes;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import javax.swing.ImageIcon;

public class GImage extends GShape {
    private ImageIcon imageIcon;

    public GImage(String filePath, int x, int y) {
        this.imageIcon = new ImageIcon(filePath);
        this.shape = new Rectangle2D.Double(x, y, imageIcon.getIconWidth(), imageIcon.getIconHeight());
        this.x0 = x;
        this.y0 = y;
    }

    @Override
    public void draw(Graphics2D g) {
        g.rotate(this.angle, this.rotCx, this.rotCy);

        Rectangle2D r = (Rectangle2D) shape;
        if (imageIcon != null) {
            g.drawImage(imageIcon.getImage(), (int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight(), null);
        }

        if (this.isSelected) {
            this.drawAnchors(g);
        }

        g.rotate(-this.angle, this.rotCx, this.rotCy);
    }

    @Override
    public GShape clone() {
        GImage cloned = (GImage) super.clone();
        cloned.shape = (Shape) ((Rectangle2D.Double) this.shape).clone();

        return cloned;
    }

    @Override
    public void translate(int dx, int dy) {
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