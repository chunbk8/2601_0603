package frames;

import global.GConstants;
import shapes.GDrawingState;
import shapes.GShape;
import shapes.GShapeList;
import transformer.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
    private final GShapeList shapes;
    private final GDrawingState drawingState;
    private BufferedImage bufferImage;
    private GTransformer transformer;
    private GPopupMenu popupMenu;

    //associations
    private GShapeToolBar toolBar;
    private GStyleToolBar styleToolBar;
    private GColorBar colorBar;
    private GStatusBar statusBar;



    //constructors
    public GDrawingPanel() {
        //attributes
        this.setBackground(Color.WHITE);
        this.eDrawingState = EDrawingState.eIdle;
        this.setLayout(null);


        //components list
        this.shapes = new GShapeList();
        this.drawingState = new GDrawingState();
        this.bufferImage = null;
        this.transformer = null;

        MouseHandler mouseHandler = new MouseHandler();
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);

        initPopupMenu();
    }

    //setters and getters
        //association을 부를 때 메서드를 통해 불러야 함
    // GDrawingPanel.java 에서
    public void associateWith(GToolPanel toolPanel) {
        // 상자를 받아서 필요한 부품을 스스로 꺼내 씁니다.
        this.toolBar = toolPanel.getToolBar();
        this.styleToolBar = toolPanel.getStyleToolBar();
        this.colorBar = toolPanel.getColorBar();
    }
    public void associateWith(GStatusBar statusBar) {
        this.statusBar = statusBar;
    }
    public GShapeList getShapes(){
        return this.shapes;
    }
    public GDrawingState getDrawingState() {return drawingState;}
    public void setShapes(Vector<GShape> loadedShapes) {
        this.shapes.setShapes(loadedShapes);

        // 2. 화면 갱신을 위한 버퍼 체크 및 그리기
        if (this.getWidth() > 0 && this.getHeight() > 0) {
            if (this.bufferImage == null) {
                this.bufferImage = new java.awt.image.BufferedImage(
                        this.getWidth(),
                        this.getHeight(),
                        java.awt.image.BufferedImage.TYPE_INT_ARGB
                );
            }
        }

        // 3. 화면 다시 그리기
        drawAllShapes();
    }

    public void exportToImage(java.io.File file) {
        try {
            // 1. 도화지와 똑같은 크기의 '사진 촬영용 빈 캔버스'를 새로 하나 만듭니다. (배경을 투명하게 안 하려면 TYPE_INT_RGB 사용)
            BufferedImage exportImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = exportImage.createGraphics();

            // 2. 배경을 하얗게 칠해줍니다. (안 그러면 배경이 까맣게 나옵니다)
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

            // 3. 사진을 찍을 때는 조절점(앵커)이 나오면 안 되므로, 임시로 선택을 해제하고 그립니다.
            for (shapes.GShape shape : this.shapes.getShapes()) {
                boolean wasSelected = shape.isSelected(); // 원래 선택 상태 기억
                shape.setSelected(false); // 찰칵! 하는 순간만 앵커 숨기기
                shape.draw(g2d);          // 순수 도형만 그리기
                shape.setSelected(wasSelected); // 다시 원래대로 복구
            }
            g2d.dispose(); // 붓 내려놓기

            // 4. 자바의 마법: 완성된 이미지를 PNG 파일로 컴퓨터에 굽습니다!
            javax.imageio.ImageIO.write(exportImage, "png", file);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "이미지 저장 중 오류가 발생했습니다.", "에러", JOptionPane.ERROR_MESSAGE);
        }
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
            drawAllShapes();
        }
    }

    private void drawAllShapes() {
        // 창이 아직 안 떠서 버퍼가 없을 때를 대비한 방어 코드
        if (this.bufferImage == null) return;
        Graphics2D bufferGraphics = this.bufferImage.createGraphics();
        bufferGraphics.setColor(this.getBackground());
        bufferGraphics.fillRect(0, 0, this.getWidth(), this.getHeight());
        this.shapes.drawAll(bufferGraphics);
        bufferGraphics.dispose();
        repaint();
    }

    public void updateSelectedStyle() {
        GDrawingState state = this.drawingState; // 공유된 객체에서 값을 읽음
        for (GShape shape : shapes.getShapes()) {
            if (shape.isSelected()) {
                shape.setStyle(state.getLineColor(), state.getFillColor(), state.getThickness());
            }
        }
        drawAllShapes();
    }

    private void startTransform(int x, int y) {
        //context check
        if (toolBar.getShapeType() == GConstants.EShapeType.eSelect) { //context

            GShape clickedShape = onShape(x,y);
            GShape.EAnchor clickedAnchor = null;
            if (clickedShape != null) {
                clickedAnchor = clickedShape.onShape(x,y);
                // 클릭된 도형 기억
            }


            // 2. 이제 모든 도형의 선택 상태를 갱신 (클릭된 도형만 true, 나머진 false)
            for (GShape shape : shapes.getShapes()) {
                shape.setSelected(shape == clickedShape);
            }

            // 3. 클릭된 도형이 있다면 알맞은 Transformer 쥐어주기
            if (clickedShape != null) {
                if (clickedAnchor == GShape.EAnchor.eRotate || clickedAnchor == GShape.EAnchor.eRotate) {
                    this.transformer = new GRotater(clickedShape);
                } else if (clickedAnchor == GShape.EAnchor.eMove) {
                    this.transformer = new GTranslator(clickedShape) {};
                } else {
                    // eNW, eNN 등 8방향 앵커인 경우 (리사이즈)
                    this.transformer = new GScale(clickedShape, clickedAnchor);
                }
                this.transformer.start(x, y);

                drawingState.setLineColor(clickedShape.getLineColor());
                drawingState.setFillColor(clickedShape.getFillColor());
                drawingState.setThickness(clickedShape.getThickness());

                if (this.colorBar != null) {
                    this.colorBar.updateUIFromState();
                }
                if (this.styleToolBar != null) {
                    this.styleToolBar.setPenWidthUI(clickedShape.getThickness());
                }
            } else {
                // 빈 바탕을 클릭한 경우 transformer 비우기
                this.transformer = null;
            }

        } else {
            // --- 기존에 도형 새로 그리는 로직 유지 ---
            for(GShape shape : shapes.getShapes()) {
                shape.setSelected(false);
            }
            GShape currentShape = toolBar.getShapeType().getShape();
            currentShape.setSelected(false);

            if (this.colorBar != null) {
                currentShape.setLineColor(drawingState.getLineColor());
                currentShape.setFillColor(drawingState.getFillColor());
            }
            if (this.styleToolBar != null) {
                currentShape.setThickness(drawingState.getThickness());
            }
            this.shapes.add(currentShape);
            this.transformer = new GDrawer(currentShape);
            this.transformer.start(x, y);
        }
        this.prepareDrawing();

    }

    private void keepTransform(int x, int y) {
        if (this.transformer != null) {
            this.transformer.keep(x, y);
        }
        drawAllShapes();

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

    public void clearAll() {
        this.shapes.clear();
        drawAllShapes();
    }

    private GShape onShape(int x, int y) {
        // 나중에 그린 도형(맨 위)부터 역순으로 클릭을 검사합니다.
        for (int i = shapes.getShapes().size() - 1; i >= 0; i--) {
            GShape shape = shapes.getShapes().get(i);
            if (shape.onShape(x, y) != null) {
                return shape;
            }
        }
        return null; // 클릭한 곳에 도형이 없으면 null 반환
    }

    // 🌟 팝업 메뉴를 만들고 버튼 이벤트를 연결하는 메서드
    private void initPopupMenu() {
        this.popupMenu = new GPopupMenu();
        this.popupMenu.associateWith(this);
    }

    // 🌟 선택된 도형을 리스트 맨 뒤로(화면 맨 앞으로) 보내는 로직
    public void bringToFront() {
        this.shapes.bringSelectedToFront(); // 모델에게 명령만 내림
        drawAllShapes();
    }

    // 🌟 선택된 도형을 리스트 맨 앞으로(화면 맨 뒤로) 보내는 로직
    public void sendToBack() {
        this.shapes.sendSelectedToBack();
        drawAllShapes();
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
            if (statusBar != null) {
                statusBar.updateCoordinates(e.getX(), e.getY());
            }
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
            GDrawingPanel.this.requestFocus();
            if (javax.swing.SwingUtilities.isRightMouseButton(e)) {
                mouseRButtonClicked(e);
                return;
            }

            if (toolBar.getShapeType().getDrawingType() == GConstants.EDrawingType.e2Point) {
                if (eDrawingState == EDrawingState.eIdle) { //target state
                    startTransform(e.getX(), e.getY());
                    eDrawingState = EDrawingState.eTransforming;
                }
            }

        }

        private void mouseRButtonClicked(MouseEvent e) {
            GShape clickedShape = onShape(e.getX(), e.getY());

            if (clickedShape != null) {
                // 기존 선택 해제 및 우클릭한 도형만 선택
                for (GShape shape : shapes.getShapes()) {
                    shape.setSelected(false);
                }
                clickedShape.setSelected(true);
                drawAllShapes(); // 화면 갱신

                // 팝업 메뉴 띄우기
                popupMenu.show(GDrawingPanel.this, e.getX(), e.getY());
            }
        }
        @Override
        public void mouseDragged(MouseEvent e) {
            if (statusBar != null) {
                statusBar.updateCoordinates(e.getX(), e.getY());
            }
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