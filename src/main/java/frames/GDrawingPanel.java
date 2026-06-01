package frames;

import global.GConstants;
import shape.GOval;
import shape.GShape;
import shape.GRectangle;
import transformer.GDrawer;
import transformer.GTransformer;
import transformer.GTranslator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Vector;

public class GDrawingPanel extends JPanel {

    //declaration
    private enum EDrawingState {
        eIdle,
        eTransforming
    }

    //attributes
    private EDrawingState eDrawingState;

    //components
    private final Vector<GShape> shapes;
    private BufferedImage bufferImage;
    private GTransformer transformer;

    //associations
    private GShapeToolBar toolBar;


    //constructors
    public GDrawingPanel() {

        //attributes
        this.setBackground(Color.WHITE);
        this.eDrawingState = EDrawingState.eIdle;

        //components list
        this.shapes = new Vector<GShape>();
        this.bufferImage = null;
        this.transformer = null;

        MouseHandler mouseHandler = new MouseHandler();
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
    }

    //setters and getters
        //association을 부를 때 메서드를 통해 불러야 함
    public void associateWith(GShapeToolBar toolBar) {
        this.toolBar = toolBar;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D panelGraphics = (Graphics2D) g;
        if (this.bufferImage != null) {
            panelGraphics.drawImage(this.bufferImage, 0, 0, null);
        }
    }

    private void prepareDrawing() {
        if (getWidth() <= 0 || getHeight() <= 0 ) {
            return;
        }

        if (bufferImage == null
                || bufferImage.getWidth() != getWidth()
                || bufferImage.getHeight() != getHeight()) {
            bufferImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D bufferGraphics = bufferImage.createGraphics();
            bufferGraphics.setColor(getBackground());
            bufferGraphics.fillRect(0, 0, getWidth(), getHeight());
            bufferGraphics.dispose();
        }



    }

    private void startTransform(int x, int y) {
        //context check
        if (toolBar.getShapeType() == GConstants.EShapeType.eSelect) { //context
            for (GShape shape : shapes) { //startTransform
                GShape.EAnchor eAnchor = shape.onShape(x, y);
                if (eAnchor != null) {
                    if (eAnchor == GShape.EAnchor.eRotate) {
                        this.transformer = new GDrawer(shape);
                    } else if (eAnchor == GShape.EAnchor.eMove) {
                        this.transformer = new GTranslator(shape) {
                        };
                    } else { //resize
                        this.transformer = new GDrawer(shape);
                    }
                    this.transformer.start(x, y);
                    break;
                    //trainsformer가 null인 경우를 체크해서 해결한다
                }
            }
        } else {
            GShape currentShape = toolBar.getShapeType().getShape();
            this.shapes.add(currentShape);
            this.transformer = new GDrawer(currentShape);
            this.transformer.start(x, y);
        }
        this.prepareDrawing();

    }

    private void keepTransform(int x, int y) {
        Graphics2D bufferGraphics = this.bufferImage.createGraphics();
        bufferGraphics.setColor(this.getBackground());
        bufferGraphics.fillRect(0, 0, this.getWidth(), this.getHeight());
        bufferGraphics.setColor(Color.BLACK);

        this.transformer.keep(x, y);
        //상태에 따라
        //+트랜스폼이 널인 경우를 체크해서 해결해야함

        for (GShape shape : this.shapes) {
            shape.draw(bufferGraphics);
        }

        bufferGraphics.dispose();
        repaint();

    }

    private void continueDrawing(int x, int y) {
        this.transformer.cont(x,y);
    }

    private void finishTransform(int x, int y) {
        this.transformer.finish(x, y);
        this.transformer = null;
    }

    private class MouseHandler implements MouseListener, MouseMotionListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == 1) { // left button
                if (e.getClickCount() == 1) { // single click
                    mouseLButton1Clicked(e);
                } else if (e.getClickCount() == 2) { // double click
                    mouseLButton2Clicked(e);
                }
            }
        }

        private void mouseLButton1Clicked (MouseEvent e){
            if (toolBar.getShapeType().getDrawingType() == GConstants.EDrawingType.eNPoint) {
                if (eDrawingState == EDrawingState.eIdle) {
                    startTransform(e.getX(), e.getY());
                    eDrawingState = EDrawingState.eTransforming;

                }
                else {
                    //addLine
                    continueDrawing(e.getX(), e.getY());
                }
            }

        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (toolBar.getShapeType().getDrawingType() == GConstants.EDrawingType.eNPoint) {
                if (eDrawingState == EDrawingState.eTransforming) {
                    keepTransform(e.getX(), e.getY());
                }
            }
        }

        private void mouseLButton2Clicked(MouseEvent e){
            if (toolBar.getShapeType().getDrawingType() == GConstants.EDrawingType.eNPoint) {
                if (eDrawingState == EDrawingState.eTransforming) {
                    finishTransform(e.getX(), e.getY());
                    eDrawingState = EDrawingState.eIdle;
                }
            }


        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (toolBar.getShapeType().getDrawingType() == GConstants.EDrawingType.e2Point) {
                if (eDrawingState == EDrawingState.eIdle) { //target state
                    startTransform(e.getX(), e.getY());
                    eDrawingState = EDrawingState.eTransforming;
                }
            }
        }
        @Override
        public void mouseDragged(MouseEvent e) {
            if (toolBar.getShapeType().getDrawingType() == GConstants.EDrawingType.e2Point) {
                if (eDrawingState == EDrawingState.eTransforming) {
                    keepTransform(e.getX(), e.getY());
                }
            }

        }
        @Override
        public void mouseReleased(MouseEvent e) {
            if (toolBar.getShapeType().getDrawingType() == GConstants.EDrawingType.e2Point) {
                if (eDrawingState == EDrawingState.eTransforming) {
                    finishTransform(e.getX(), e.getY());
                    eDrawingState = EDrawingState.eIdle;
                }
            }

        }
        @Override public void mouseEntered(MouseEvent e) {}
        @Override public void mouseExited(MouseEvent e) {}
    }


}