package frames;

import global.GConstants;
import shapes.GDrawingState;
import shapes.GShape;
import shapes.GShapeList;
import transformer.*;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTreeUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Vector;
import shapes.GText;

public class GDrawingPanel extends JPanel {

    //declaration
    private enum EDrawingState {
        eIdle,
        eTransforming,
        eSelecting
    }

    //attributes
    private EDrawingState eDrawingState;
    private int pasteOffset = 20;

    //components
    private final GShapeList shapes;
    private final GShapeList clipboard;
    private final GDrawingState drawingState;
    private BufferedImage bufferImage;
    private GTransformer transformer;
    private GPopupMenu popupMenu;
    private GLassoUI lassoUI;


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
        this.setFocusable(true);


        //components list
        this.shapes = new GShapeList();
        this.clipboard = new GShapeList();
        this.drawingState = new GDrawingState();
        this.popupMenu = new GPopupMenu();
        this.lassoUI = new GLassoUI();
        this.bufferImage = null;
        this.transformer = null;

        MouseHandler mouseHandler = new MouseHandler();
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
        KeyHandler keyHandler = new KeyHandler();
        this.addKeyListener(keyHandler);

        this.popupMenu.associateWith(this);
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
        if (eDrawingState == EDrawingState.eSelecting) {
            lassoUI.draw(panelGraphics);
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

    // 🌟 다이어트 성공! 이제 이 메서드는 길안내(Routing) 역할만 합니다.
    private void startTransform(MouseEvent e) {
        if (toolBar.getShapeType() == GConstants.EShapeType.eSelect) {
            initiateSelection(e); // 선택/변형 로직으로 분기
        } else {
            initiateDrawing(e);   // 새로 그리기 로직으로 분기
        }
        this.prepareDrawing();
    }

    // 🌟 선택 및 다중 변형을 준비하는 로직 (기존의 뚱뚱했던 부분 상단)
    private void initiateSelection(MouseEvent e) {
        int x = e.getX(), y = e.getY();
        boolean isCtrl = e.isControlDown() || e.isMetaDown();

        GShape clickedShape = onShape(x, y);
        GShape.EAnchor clickedAnchor = null;

        if (clickedShape == null) {
            startLasso(x, y);
            return;
        }

        clickedAnchor = clickedShape.onShape(x, y);

        // Ctrl 토글 로직
        if (isCtrl) {
            clickedShape.setSelected(!clickedShape.isSelected());
        } else {
            if (!clickedShape.isSelected()) {
                for (GShape shape : shapes.getShapes()) shape.setSelected(false);
                clickedShape.setSelected(true);
            }
        }

        Vector<GShape> selectedShapes = new Vector<>();
        for (GShape shape : shapes.getShapes()) {
            if (shape.isSelected()) selectedShapes.add(shape);
        }

        if (selectedShapes.isEmpty()) {
            this.transformer = null;
            drawAllShapes();
            return;
        }

        GShape targetShape = clickedShape;
        if (selectedShapes.size() > 1) {
            shapes.GGroup tempGroup = new shapes.GGroup();
            for (GShape s : selectedShapes) tempGroup.addShape(s);
            targetShape = tempGroup;
        } else {
            targetShape = selectedShapes.get(0);
        }

        // 트랜스포머 할당
        if (clickedAnchor == GShape.EAnchor.eRotate) {
            this.transformer = new GRotater(targetShape);
        } else if (clickedAnchor == GShape.EAnchor.eMove || clickedAnchor == null) {
            this.transformer = new GTranslator(targetShape) {};
        } else {
            this.transformer = new GScale(targetShape, clickedAnchor);
        }
        this.transformer.start(x, y);

        // UI 업데이트
        drawingState.setLineColor(targetShape.getLineColor());
        drawingState.setFillColor(targetShape.getFillColor());
        drawingState.setThickness(targetShape.getThickness());
        drawingState.setTextColor(targetShape.getTextColor());
        if (this.colorBar != null) this.colorBar.updateUIFromState();
        if (this.styleToolBar != null) this.styleToolBar.setPenWidthUI(targetShape.getThickness());
    }

    // 🌟 도형을 새로 그리는 로직 (기존의 else 부분)
    private void initiateDrawing(MouseEvent e) {
        for (GShape shape : shapes.getShapes()) shape.setSelected(false);

        GShape currentShape = toolBar.getShapeType().getShape();
        currentShape.setSelected(false);

        if (this.colorBar != null) {
            currentShape.setLineColor(drawingState.getLineColor());
            currentShape.setFillColor(drawingState.getFillColor());
            currentShape.setTextColor(drawingState.getTextColor());
        }
        if (this.styleToolBar != null) currentShape.setThickness(drawingState.getThickness());

        this.shapes.add(currentShape);
        this.transformer = new GDrawer(currentShape); // 드로어 생성
        this.transformer.start(e.getX(), e.getY());
    }

    private void keepTransform(int x, int y, boolean isShift) {
        if (this.transformer != null) {
            // 다형성을 이용해 새로 그릴 때(GDrawer)만 Shift 상태를 같이 넘겨줍니다.
            if (this.transformer instanceof GDrawer) {
                ((GDrawer) this.transformer).keep(x, y, isShift);
            } else {
                this.transformer.keep(x, y, isShift);
            }
        }
        drawAllShapes();
    }

    private void continueDrawing(int x, int y) {
        if (this.transformer != null) {
            this.transformer.cont(x, y);
        }
    }

    private void finishTransform(int x, int y, boolean isShif) {
        if (this.transformer != null) {
            this.transformer.finish(x, y, isShif);
            this.transformer = null;
        }
    }

    public void clearAll() {
        this.shapes.clear();
        drawAllShapes();
    }

    public boolean isCtrl() {
        return true;
    }
    public boolean isnotCtrl() {
        return false;
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

    // GDrawingPanel.java 내부에 추가
    public void addImageShape(String filePath) {
        // 이미지를 (50, 50) 좌표에 생성
        shapes.GImage newImage = new shapes.GImage(filePath, 50, 50);

        // 새로 추가된 이미지 자동 선택 처리
        for (shapes.GShape shape : shapes.getShapes()) {
            shape.setSelected(false);
        }
        newImage.setSelected(true);

        this.shapes.add(newImage); // 리스트에 추가
        drawAllShapes(); // 화면 다시 그리기
    }
    public void deleteSelectedShapes() {
        this.shapes.deleteSelected();
        drawAllShapes();
    }

    public void copySelectedShapes() {
        this.shapes.copyTo(clipboard);
        this.pasteOffset = 20;
    }

    public void pasteShapes() {
        this.shapes.pasteFrom(clipboard, pasteOffset);
        this.pasteOffset += 20;
        drawAllShapes();
    }

    public void cutSelectedShapes() {
        copySelectedShapes();
        deleteSelectedShapes();
    }

    public void duplicateSelectedShapes() {
        copySelectedShapes();
        pasteShapes();
    }

    // 🌟 1. 올가미 선택 시작
    private void startLasso(int x, int y) {
        // 기존 선택 모두 해제
        for (GShape shape : shapes.getShapes()) {
            shape.setSelected(false);
        }
        eDrawingState = EDrawingState.eSelecting;
        lassoUI.startDrawing(x, y);
        drawAllShapes();
    }

    // 🌟 2. 드래그 중 올가미 크기 갱신
    private void keepLasso(int x, int y) {
        lassoUI.keepDrawing(x, y);
        repaint(); // 화면 즉각 갱신
    }

    // 🌟 3. 마우스 릴리즈 시 올가미 선택 확정
    private void finishLasso() {
        Rectangle bounds = lassoUI.getBounds();
        if (!bounds.isEmpty()) {
            for (GShape shape : shapes.getShapes()) {
                // 완전히 포함되거나 겹치면 선택
                if (bounds.contains(shape.getShape().getBounds()) || bounds.intersects(shape.getShape().getBounds())) {
                    shape.setSelected(true);
                }
            }
        }
        lassoUI.stopDrawing(); // 올가미 데이터 초기화
        eDrawingState = EDrawingState.eIdle; // 상태 복구
        drawAllShapes(); // 최종 선택된 앵커들 다시 그리기
    }
    public void groupSelectedShapes() {
        Vector<GShape> selectedShapes = new Vector<>();

        // 1. 현재 도화지에서 선택된 도형들을 모두 찾습니다.
        for (GShape shape : shapes.getShapes()) {
            if (shape.isSelected()) {
                selectedShapes.add(shape);
            }
        }

        // 2. 선택된 도형이 2개 이상일 때만 그룹화 진행
        if (selectedShapes.size() > 1) {
            shapes.GGroup group = new shapes.GGroup();

            for (GShape shape : selectedShapes) {
                shape.setSelected(false); // 앵커 숨기기
                group.addShape(shape);    // 그룹 객체 내부로 도형 이동
                shapes.getShapes().remove(shape); // 🚨 도화지(GShapeList)에서는 개별 도형들을 지움
            }

            group.setSelected(true); // 이제부터는 그룹 덩어리 자체가 선택된 상태
            shapes.add(group);       // 🚨 도화지에는 그룹 객체 '단 1개'만 새로 등록
            drawAllShapes();
        }
    }

    // 🌟 선택된 그룹을 다시 개별 도형으로 풀기
    public void ungroupSelectedShapes() {
        Vector<GShape> toRemove = new Vector<>();
        Vector<GShape> toAdd = new Vector<>();

        for (GShape shape : shapes.getShapes()) {
            // 선택된 객체이면서, 동시에 GGroup 타입인 경우에만 해제
            if (shape.isSelected() && shape instanceof shapes.GGroup) {
                toRemove.add(shape); // 도화지에서 지워버릴 그룹 덩어리

                // 그룹 안에 갇혀있던 자식 도형들을 구출
                for (GShape child : ((shapes.GGroup) shape).getChildShapes()) {
                    child.setSelected(true); // 다시 앵커가 보이도록 선택 상태로 만듦
                    toAdd.add(child);        // 도화지에 다시 추가할 목록에 넣기
                }
            }
        }

        // 3. 도화지 리스트(GShapeList) 갱신
        if (!toRemove.isEmpty()) {
            for (GShape shape : toRemove) {
                shapes.getShapes().remove(shape); // 그룹 껍데기 삭제
            }
            for (GShape shape : toAdd) {
                shapes.add(shape); // 개별 도형들을 다시 도화지에 복귀
            }
            drawAllShapes();
        }
    }

    private class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(java.awt.event.KeyEvent e) {
            int keyCode = e.getKeyCode();
            boolean isCtrl = e.isControlDown() || e.isMetaDown();
            boolean isShift = e.isShiftDown();
            // Delete 키나 Backspace 키가 눌렸을 때 삭제 메서드 실행
            if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DELETE) {
                deleteSelectedShapes();
            } else if (isCtrl) {
                if (keyCode == KeyEvent.VK_C) copySelectedShapes();
                else if (keyCode == KeyEvent.VK_V) pasteShapes();
                else if (keyCode == KeyEvent.VK_X) cutSelectedShapes();
                else if (keyCode == KeyEvent.VK_D) duplicateSelectedShapes();
                else if (keyCode == KeyEvent.VK_G) {
                    if (isShift) ungroupSelectedShapes();
                    else groupSelectedShapes();
                }
            }
        }
        @Override
        public void keyTyped(KeyEvent e) {
            // 현재 선택된 도형들을 검사
            for (GShape shape : shapes.getShapes()) {
                // 선택된 도형이 GText 타입일 때만 타이핑 적용
                if (shape.isSelected() && shape instanceof GText) {
                    GText textShape = (GText) shape;
                    char c = e.getKeyChar();

                    if (c == '\b') { // 백스페이스 처리
                        String t = textShape.getText();
                        if (!t.isEmpty()) {
                            textShape.setText(t.substring(0, t.length() - 1));
                        }
                    } else if (!Character.isISOControl(c)) { // 일반 문자 입력 처리
                        textShape.setText(textShape.getText() + c);
                    }

                    drawAllShapes(); // 화면 갱신
                    break;
                }
            }
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
                    startTransform(e);
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
                    keepTransform(e.getX(), e.getY(), e.isShiftDown());
                }
            }
        }

        private void mouseLButton2Clicked(MouseEvent e){
            if (toolBar.getShapeType().getDrawingType() == GConstants.EDrawingType.eNPoint) {
                if (eDrawingState == EDrawingState.eTransforming) {
                    finishTransform(e.getX(), e.getY(), e.isShiftDown()                    );
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
                if (eDrawingState == EDrawingState.eIdle) {
                    startTransform(e);

                    // 🚨 핵심 수정 3: startTransform 안에서 허공을 클릭해 올가미 모드(eSelecting)가
                    // 켜졌을 경우에는 상태를 덮어씌우지 않도록 예외 처리를 합니다.
                    if (eDrawingState != EDrawingState.eSelecting) {
                        eDrawingState = EDrawingState.eTransforming;
                    }
                }
            }
        }

        private void mouseRButtonClicked(MouseEvent e) {
            GShape clickedShape = onShape(e.getX(), e.getY());

            if (clickedShape != null) {
                // 🚨 수정된 핵심 로직:
                // 방금 우클릭한 도형이 '이미 선택된 상태'가 아닐 때만 기존 선택을 전부 풉니다.
                // (즉, 올가미로 묶어둔 녀석들 중 하나를 우클릭했다면 선택을 풀지 않고 그대로 유지합니다!)
                if (!clickedShape.isSelected()) {
                    for (GShape shape : shapes.getShapes()) {
                        shape.setSelected(false);
                    }
                    clickedShape.setSelected(true);
                    drawAllShapes(); // 화면 갱신
                }

                // 팝업 메뉴 띄우기
                popupMenu.show(GDrawingPanel.this, e.getX(), e.getY());
            }
        }
        @Override
        public void mouseDragged(MouseEvent e) {
            if (statusBar != null) {
                statusBar.updateCoordinates(e.getX(), e.getY());
            }
            if (eDrawingState == EDrawingState.eSelecting) {
                keepLasso(e.getX(), e.getY());
                return; // 처리 후 바로 리턴
            }
            if (toolBar.getShapeType().getDrawingType() == GConstants.EDrawingType.e2Point) {
                if (eDrawingState == EDrawingState.eTransforming) {
                    keepTransform(e.getX(), e.getY(), e.isShiftDown());
                }
            }


        }
        @Override
        public void mouseReleased(MouseEvent e) {
            if (eDrawingState == EDrawingState.eSelecting) {
                finishLasso();
                return;
            }// 처리 후 바로 리턴
            if (toolBar.getShapeType().getDrawingType() == GConstants.EDrawingType.e2Point) {
                if (eDrawingState == EDrawingState.eTransforming) {
                    finishTransform(e.getX(), e.getY(), e.isShiftDown());
                    eDrawingState = EDrawingState.eIdle;
                }
            }

        }
        @Override public void mouseEntered(MouseEvent e) {}
        @Override public void mouseExited(MouseEvent e) {}
    }


}