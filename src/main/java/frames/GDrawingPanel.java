package frames;

import global.GConstants;
import shape.GShape;
import transformer.*;

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

            GShape clickedShape = null;
            GShape.EAnchor clickedAnchor = null;

            // 1. 역순 탐색 (위에 겹쳐진 도형부터 마우스 클릭을 감지하기 위함)
            for (int i = shapes.size() - 1; i >= 0; i--) {
                GShape shape = shapes.get(i);
                clickedAnchor = shape.onShape(x, y); // 현재 isSelected 상태를 기반으로 클릭 감지

                if (clickedAnchor != null) {
                    clickedShape = shape; // 클릭된 도형 기억
                    break;
                }
            }

            // 2. 이제 모든 도형의 선택 상태를 갱신 (클릭된 도형만 true, 나머진 false)
            for (GShape shape : shapes) {
                shape.setSelected(shape == clickedShape);
            }

            // 3. 클릭된 도형이 있다면 알맞은 Transformer 쥐어주기
            if (clickedShape != null) {
                // 수정됨: onShape가 반환하는 eRR을 회전으로 인식하도록 조건 추가!
                if (clickedAnchor == GShape.EAnchor.eRotate || clickedAnchor == GShape.EAnchor.eRotate) {
                    this.transformer = new GRotater(clickedShape);
                } else if (clickedAnchor == GShape.EAnchor.eMove) {
                    this.transformer = new GTranslator(clickedShape) {};
                } else {
                    // eNW, eNN 등 8방향 앵커인 경우 (리사이즈)
                    this.transformer = new GScale(clickedShape, clickedAnchor);
                }
                this.transformer.start(x, y);
            } else {
                // 빈 바탕을 클릭한 경우 transformer 비우기
                this.transformer = null;
            }

        } else {
            // --- 기존에 도형 새로 그리는 로직 유지 ---
            for(GShape shape : shapes) {
                shape.setSelected(false);
            }
            GShape currentShape = toolBar.getShapeType().getShape();
            currentShape.setSelected(true);

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

        if (this.transformer != null) {
            this.transformer.keep(x, y);
        }

        for (GShape shape : this.shapes) {
            shape.draw(bufferGraphics);
        }

        bufferGraphics.dispose();
        repaint();

    }

    private void continueDrawing(int x, int y) {
        if (this.transformer != null) {
            this.transformer.cont(x, y);
        }
    }

    private void finishTransform(int x, int y) {
        if (this.transformer != null) {
            this.transformer.finish(x, y);
            this.transformer = null;
        }
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