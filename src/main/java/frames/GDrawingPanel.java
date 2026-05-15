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
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D panelGraphics = (Graphics2D) g;
        if (this.bufferImage != null) {
            panelGraphics.drawImage(this.bufferImage, 0, 0, null);
        }
    }



    private void startDrawing(int x, int y) {
//상태나 이벤트에 대한 판단에 향하는 걸 이벤트 핸들러로 뺌
        //여긴 정말 해야하는 액셔남ㄴ 작성한 것
        //double buffering 준비하는 과정
        if (getWidth() <= 0 || getHeight() <= 0 ) { //그림 영역이 0이거나 || 상태가 eIdle일 떄
            return;
        }

        //double buffering
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
    private void startNewShape(int x, int y) {
        //새로운 쉐입을 만드려고 준비하는 함수

        currentShape = toolBar.getShapeType().getShape();
        /*2포인트에만 사용하는 방법이라 뺄 거임
        currentShape.setLocation0(x, y);
        currentShape.setLocation1(x, y);*/

    }

    private void startTransform(int x, int y) {
        this.currentShape.setLocation1(x, y);
        for (GShape shape : shapes) { //startTransform
            GShape.EAnchor eAnchor = shape.onShape(x,y);
            if (eAnchor != null ) {
                if (eAnchor == GShape.EAnchor.eRotate) {
                    eDrawingState = EDrawingState.eRotating;
                } else if (eAnchor == GShape.EAnchor.eMove) {
                    eDrawingState = EDrawingState.eMoving;
                } else  { //resize
                    eDrawingState = EDrawingState.eResizing;
                }
                currentShape = shape;
                break;
            }
        }

    }

    private void keepTransform(int x, int y) {
        //double buffering 을 위한 graphics 도구 설정 (위로 뺴기)
        Graphics2D bufferGraphics = this.bufferImage.createGraphics();
        bufferGraphics.setColor(this.getBackground());
        bufferGraphics.fillRect(0, 0, this.getWidth(), this.getHeight());
        bufferGraphics.setColor(Color.BLACK);


        //상태에 따라
        if (this.eDrawingState == EDrawingState.eDrawing) {
            this.currentShape.setLocation1(x, y);
            //여긴 새 그림을 그리는 거
            this.currentShape.draw(bufferGraphics);
        } else if (this.eDrawingState == EDrawingState.eMoving) {
            //다이렉트로 에넘에 뭘 집어넣고 그냥 했으면 좋겠다는데? 뭔 개소리지. 이프엘스를 하지말고
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

    private void continueDrawing(int x, int y) {
        // n개의 라인을 그려서 만드는 도형이 일반화되어 여러 다각형을 그리게끔 기능이 추가될 수도 있음
        //그래서 몇개의 점으로 만들어지는 도형인지 컨텍스트를 추가하는게 좋아.
    }

    private void finishTransform(int x, int y) {
        if (this.eDrawingState == EDrawingState.eDrawing) {
            this.shapes.add(this.currentShape);
            this.currentShape = null;
        }


    }

    private class MouseHandler implements MouseListener, MouseMotionListener {
        private void mouseLButton1Clicked (MouseEvent e){
            if (eDrawingState == EDrawingState.eIdle) { //target state
                if (toolBar.getShapeType().getDrawingType() == GConstants.EDrawingType.eNPoint) { //context
                    startNewShape(e.getX(), e.getY());
                    startDrawing(e.getX(), e.getY());//prepare for double buffering
                }

            } else {
                if (toolBar.getShapeType().getDrawingType() == GConstants.EDrawingType.eNPoint) {
                    //addLine
                    continueDrawing(e.getX(), e.getY());
                }

            }



        }
        private void mouseLButton2Clicked(MouseEvent e){
            if (eDrawingState != EDrawingState.eIdle) {
                if (toolBar.getShapeType().getDrawingType() == GConstants.EDrawingType.eNPoint) {
                    finishTransform(e.getX(), e.getY());
                    eDrawingState = EDrawingState.eIdle;
                }
            }


        }
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
        @Override
        public void mouseMoved(MouseEvent e) {
            if (eDrawingState != EDrawingState.eIdle) {
                if (toolBar.getShapeType().getDrawingType() == GConstants.EDrawingType.eNPoint) {
                    keepTransform(e.getX(), e.getY());
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            //startRectangularShape(e.getX(),e.getY());
            if (eDrawingState == EDrawingState.eIdle) { //target state
                if (toolBar.getShapeType().getDrawingType() == GConstants.EDrawingType.e2Point) {
                    if (toolBar.getShapeType() == GConstants.EShapeType.eSelect) { //context
                        //select
                        startTransform(e.getX(),e.getY());
                    } else { //select가 아니면 다 드로잉
                        // drawing
                        startNewShape(e.getX(),e.getY());
                        eDrawingState = EDrawingState.eDrawing;
                    }
                    startDrawing(e.getX(),e.getY()); //prepare for double buffering
                }

            }

        }
        @Override
        public void mouseDragged(MouseEvent e) {
            if (eDrawingState != EDrawingState.eIdle) {
                keepTransform(e.getX(), e.getY());
            }

        }
        @Override
        public void mouseReleased(MouseEvent e) {
            if (eDrawingState != EDrawingState.eIdle) {
                finishTransform(e.getX(), e.getY());
                eDrawingState = EDrawingState.eIdle;
            }

        }
        @Override public void mouseEntered(MouseEvent e) {}
        @Override public void mouseExited(MouseEvent e) {}
    }


}