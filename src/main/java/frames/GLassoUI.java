package frames;

import java.awt.*;

public class GLassoUI {
    private Point startPoint;
    private Rectangle lassoRect;

    public GLassoUI() {
        this.lassoRect = new Rectangle(0, 0, 0, 0);
    }

    public void start(int x, int y) {
        this.startPoint = new Point(x, y);
        this.lassoRect.setBounds(x, y, 0, 0);
    }

    public void keep(int x, int y) {
        if (startPoint == null) return;
        int w = Math.abs(x - startPoint.x);
        int h = Math.abs(y - startPoint.y);
        int rx = Math.min(x, startPoint.x);
        int ry = Math.min(y, startPoint.y);
        this.lassoRect.setBounds(rx, ry, w, h);
    }

    public void finish() {
        this.startPoint = null;
        this.lassoRect.setBounds(0, 0, 0, 0);
    }


    public Rectangle getBounds() {return this.lassoRect;}

    public void draw(Graphics2D g) {
        if (lassoRect == null || lassoRect.isEmpty()) return;

        g.setColor(new Color(0, 120, 215, 50));
        g.fill(lassoRect);

        g.setColor(new Color(0, 120, 215));
        Stroke oldStroke = g.getStroke();
        Stroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5.0f}, 0);
        g.setStroke(dashed);
        g.draw(lassoRect);

        g.setStroke(oldStroke);
    }
}