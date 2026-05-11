package frames;

import global.GConstants;
import shape.GOval;
import shape.GShape;
import shape.GRectangle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Vector;

public class GDrawingPanel extends JPanel {

    private GShapeToolBar toolBar;

    public void associateWith(GShapeToolBar toolBar) {
        this.toolBar = toolBar;
    }

    private enum EDrawingState {
        eIdle,
        eDrawing,
        eMoving,
        eResizing,
        eRotating,
        eShearing
    }
    private EDrawingState eDrawingState;

    private BufferedImage bufferImage;
    //그림을 그릴 때 쌓아놓는 용도
    private Vector<GShape> shapes;
    private GShape currentShape;

    public GDrawingPanel() {
        this.setBackground(Color.WHITE);
        this.eDrawingState = EDrawingState.eIdle;
        this.shapes = new Vector<GShape>();

        MouseHandler mouseHandler = new MouseHandler();
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
    }

    @Override
    // ✅ 수정 1: paintComponents → paintComponent
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D panelGraphics = (Graphics2D) g;
        if (this.bufferImage != null) {
            panelGraphics.drawImage(this.bufferImage, 0, 0, null);
        }
    }

    private void startRectangularShape(int x, int y) {
        if (this.eDrawingState == EDrawingState.eIdle) {
            if (this.toolBar.getShapeType() == GConstants.EShapeType.eSelect) { //select
                for (GShape shape : this.shapes) {
                    GShape.EAnchor eAnchor = shape.onShape(x,y);
                    if (eAnchor != null ) {
                        if (eAnchor == GShape.EAnchor.eRotate) {
                            eDrawingState = EDrawingState.eRotating;
                        } else if (eAnchor == GShape.EAnchor.eMove) {
                            eDrawingState = EDrawingState.eMoving;
                        } else  { //resize
                            eDrawingState = EDrawingState.eResizing;
                        }
                        this.currentShape = shape;
                        break;
                    }
                }
            } else { //select가 아니면 다 드로잉
                if (this.toolBar.getShapeType() == GConstants.EShapeType.eOval) {
                    this.currentShape = new GOval(x, y, x, y);
                }
                else if (this.toolBar.getShapeType() == GConstants.EShapeType.eRectangle) {
                    this.currentShape = new GRectangle(x, y, x, y);
                }
                eDrawingState = EDrawingState.eDrawing;
            }

            if (this.getWidth() <= 0 || this.getHeight() <= 0 || this.eDrawingState == EDrawingState.eIdle) { //그림 영역이 0이거나 || 상태가 eIdle일 떄
                return;
            }

            //double buffering
            if (this.bufferImage == null
                    || this.bufferImage.getWidth() != this.getWidth()
                    || this.bufferImage.getHeight() != this.getHeight()) {
                this.bufferImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D bufferGraphics = this.bufferImage.createGraphics();
                bufferGraphics.setColor(this.getBackground());
                bufferGraphics.fillRect(0, 0, this.getWidth(), this.getHeight());
                bufferGraphics.dispose();
            }
        }



    }

    private void keepRectangularShape(int x, int y) {
        if (this.eDrawingState != EDrawingState.eIdle) {
            this.currentShape.setLocation1(x, y);

            Graphics2D bufferGraphics = this.bufferImage.createGraphics();
            bufferGraphics.setColor(this.getBackground());
            bufferGraphics.fillRect(0, 0, this.getWidth(), this.getHeight());
            bufferGraphics.setColor(Color.BLACK);

            if (this.eDrawingState == EDrawingState.eDrawing) {
                this.currentShape.setLocation1(x, y);
                //여긴 새 그림을 그리는 거
                this.currentShape.draw(bufferGraphics);
            } else if (this.eDrawingState == EDrawingState.eMoving) {
                this.currentShape.move(x, y);
            } else if (this.eDrawingState == EDrawingState.eResizing) {
                this.currentShape.resize(x, y);
            }else if (this.eDrawingState == EDrawingState.eRotating) {
                this.currentShape.rotate(x, y);
            }
            for (GShape shape : this.shapes) {
                shape.draw(bufferGraphics);
                //여기서 그리는 건 도형 저장
            }
            //this.currentShape.draw(bufferGraphics);
            bufferGraphics.dispose();
            repaint();

        }

    }

    private void finishRectangularShape() {
        if (this.eDrawingState != EDrawingState.eIdle) {
            if (this.eDrawingState == EDrawingState.eDrawing) {
                this.shapes.add(this.currentShape);
            }
            this.eDrawingState = EDrawingState.eIdle; //상태 원위치
            this.currentShape = null;
        }


    }

    private class MouseHandler implements MouseListener, MouseMotionListener {
        @Override
        public void mouseClicked(MouseEvent e) {}
        @Override
        public void mouseMoved(MouseEvent e) {}
        @Override
        public void mousePressed(MouseEvent e) {
            startRectangularShape(e.getX(), e.getY());

        }
        @Override
        public void mouseDragged(MouseEvent e) {
            keepRectangularShape(e.getX(), e.getY());

        }
        @Override
        public void mouseReleased(MouseEvent e) {
            finishRectangularShape();

        }
        @Override public void mouseEntered(MouseEvent e) {}
        @Override public void mouseExited(MouseEvent e) {}
    }
}