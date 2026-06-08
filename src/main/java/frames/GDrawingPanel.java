package frames;

import global.GConstants;
import shapes.*;
import transformer.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Vector;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

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
    private GPopupMenu popupMenu;



    //associations
    private GShapeToolBar toolBar;
    private GStyleToolBar styleToolBar;
    private GColorBar colorBar;
    private GStatusBar statusBar;
    private GTransformer transformer;
    private GLassoUI lassoUI;
    private GInlineEditor inlineEditor;

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
        this.lassoUI = new GLassoUI();
        this.inlineEditor = new GInlineEditor(this);
        this.bufferImage = null;
        this.transformer = null;

        MouseHandler mouseHandler = new MouseHandler();
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
        KeyHandler keyHandler = new KeyHandler();
        this.addKeyListener(keyHandler);


    }


    public void associateWith(GToolPanel toolPanel) {
        this.toolBar = toolPanel.getToolBar();
        this.styleToolBar = toolPanel.getStyleToolBar();
        this.colorBar = toolPanel.getColorBar();
    }
    public void associateWith(GStatusBar statusBar) {
        this.statusBar = statusBar;
    }
    public void associateWith(GPopupMenu popupMenu){
        this.popupMenu = popupMenu;
    }
    public GShapeList getShapes(){return this.shapes;}
    public GDrawingState getDrawingState() {return drawingState;}
    public void setShapes(Vector<GShape> loadedShapes) {
        this.shapes.setShapes(loadedShapes);
        if (this.getWidth() > 0 && this.getHeight() > 0) {
            if (this.bufferImage == null) {
                this.bufferImage = new BufferedImage(
                        this.getWidth(),
                        this.getHeight(),
                        BufferedImage.TYPE_INT_ARGB
                );
            }
        }
        drawAllShapes();
    }

    public void exportToImage(File file) {
        try {
            BufferedImage exportImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = exportImage.createGraphics();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

            for (shapes.GShape shape : this.shapes.getShapes()) {
                boolean wasSelected = shape.isSelected();
                shape.setSelected(false);
                shape.draw(g2d);
                shape.setSelected(wasSelected);
            }
            g2d.dispose();
            ImageIO.write(exportImage, "png", file);

        } catch (Exception e) {
            e.printStackTrace();
            showMessageDialog(null, "이미지 저장 중 오류 발생", "에러", ERROR_MESSAGE);
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
        if (this.bufferImage == null) return;
        Graphics2D bufferGraphics = this.bufferImage.createGraphics();
        bufferGraphics.setColor(this.getBackground());
        bufferGraphics.fillRect(0, 0, this.getWidth(), this.getHeight());
        this.shapes.drawAll(bufferGraphics);
        bufferGraphics.dispose();
        repaint();
    }

    public void updateSelectedStyle() {
        GDrawingState state = this.drawingState;
        for (GShape shape : shapes.getShapes()) {
            if (shape.isSelected()) {
                shape.setStyle(state.getLineColor(), state.getFillColor(), state.getThickness());
                shape.setTextColor(state.getTextColor());
            }
        }
        drawAllShapes();
    }


    private void startTransform(MouseEvent e) {
        if (toolBar.getShapeType() == GConstants.EShapeType.eSelect) {
            initiateSelection(e);
        } else {
            initiateDrawing(e);
        }
        this.prepareDrawing();
    }

    private void initiateSelection(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        boolean isCtrl = (e.isControlDown() || e.isMetaDown());
        GShape clickedShape = onShape(x, y);
        GShape.EAnchor clickedAnchor = null;

        if (clickedShape == null) {
            startLasso(x, y);
            return;
        }

        clickedAnchor = clickedShape.onShape(x, y);

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

        if (clickedAnchor == GShape.EAnchor.eRotate) {
            this.transformer = new GRotater(targetShape);
        } else if (clickedAnchor == GShape.EAnchor.eMove || clickedAnchor == null) {
            this.transformer = new GTranslator(targetShape);
        } else {
            this.transformer = new GScale(targetShape, clickedAnchor);
        }
        this.transformer.start(x, y);

        drawingState.setLineColor(targetShape.getLineColor());
        drawingState.setFillColor(targetShape.getFillColor());
        drawingState.setThickness(targetShape.getThickness());
        drawingState.setTextColor(targetShape.getTextColor());
        if (this.colorBar != null) this.colorBar.updateUIFromState();
        if (this.styleToolBar != null) this.styleToolBar.setPenWidthUI(targetShape.getThickness());
    }

    private void initiateDrawing(MouseEvent e) {
        for (GShape shape : shapes.getShapes()) shape.setSelected(false);

        GShape currentShape = toolBar.getShapeType().getShape();
        currentShape.setSelected(false);

        if (this.colorBar != null) {
            currentShape.setLineColor(drawingState.getLineColor());
            currentShape.setFillColor(drawingState.getFillColor());
            currentShape.setTextColor(drawingState.getTextColor());
        }
        if (this.styleToolBar != null) {
            currentShape.setThickness(drawingState.getThickness());
            if (currentShape instanceof shapes.GText) {
                ((shapes.GText) currentShape).setFont(new Font(drawingState.getFontFamily(), Font.PLAIN, drawingState.getFontSize()));
            }
        }

        this.shapes.add(currentShape);
        this.transformer = new GDrawer(currentShape);
        this.transformer.start(e.getX(), e.getY());
    }

    private void keepTransform(int x, int y, boolean isShift) {
        if (this.transformer != null) {
            if (this.transformer instanceof GDrawer) {
                this.transformer.keep(x, y, isShift);
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

            if (toolBar.getShapeType() == GConstants.EShapeType.eText) {
                if (!shapes.getShapes().isEmpty()) {
                    autoSelectIfText(shapes.getShapes().lastElement());
                }
            }
            this.transformer = null;
        }
        drawAllShapes();
    }


    public void clearAll() {
        this.shapes.clear();
        drawAllShapes();
    }

    private GShape onShape(int x, int y) {
        //역순 참조
        for (int i = shapes.getShapes().size() - 1; i >= 0; i--) {
            GShape shape = shapes.getShapes().get(i);
            if (shape.onShape(x, y) != null) {
                return shape;
            }
        }
        return null;
    }

    public void bringToFront() {
        this.shapes.bringSelectedToFront();
        drawAllShapes();
    }

    public void sendToBack() {
        this.shapes.sendSelectedToBack();
        drawAllShapes();
    }

    public void addImage(String filePath) {
        GImage newImage = new GImage(filePath, 0, 0);

        for (GShape shape : shapes.getShapes()) {
            shape.setSelected(false);
        }
        newImage.setSelected(true);

        this.shapes.add(newImage);
        drawAllShapes();
    }
    public void delete() {
        this.shapes.deleteSelected();
        drawAllShapes();
    }

    public void copy() {
        this.shapes.copyTo(clipboard);
        this.pasteOffset = 20;
    }


    public void paste() {
        this.shapes.pasteFrom(clipboard, pasteOffset);
        this.pasteOffset += 20;
        drawAllShapes();
    }

    public void cut() {
        copy();
        delete();
    }

    public void duplicate() {
        copy();
        paste();
    }


    private void startLasso(int x, int y) {
        for (GShape shape : shapes.getShapes()) {
            shape.setSelected(false);
        }
        eDrawingState = EDrawingState.eSelecting;
        lassoUI.start(x, y);
        drawAllShapes();
    }


    private void keepLasso(int x, int y) {
        lassoUI.keep(x, y);
        repaint();
    }


    private void finishLasso() {
        Rectangle bounds = lassoUI.getBounds();
        if (!bounds.isEmpty()) {
            for (GShape shape : shapes.getShapes()) {
                if (bounds.contains(shape.getShape().getBounds()) || bounds.intersects(shape.getShape().getBounds())) {
                    shape.setSelected(true);
                }
            }
        }
        lassoUI.finish();
        eDrawingState = EDrawingState.eIdle;
        drawAllShapes();
    }
    public void group() {
        Vector<GShape> selectedShapes = new Vector<>();

        for (GShape shape : shapes.getShapes()) {
            if (shape.isSelected()) {
                selectedShapes.add(shape);
            }
        }

        if (selectedShapes.size() > 1) {
            GGroup group = new shapes.GGroup();

            for (GShape shape : selectedShapes) {
                shape.setSelected(false);
                group.addShape(shape);
                shapes.getShapes().remove(shape);
            }

            group.setSelected(true);
            shapes.add(group);
            drawAllShapes();
        }
    }

    public void ungroup() {
        Vector<GShape> toRemove = new Vector<>();
        Vector<GShape> toAdd = new Vector<>();

        for (GShape shape : shapes.getShapes()) {
            if (shape.isSelected() && shape instanceof GGroup) {
                toRemove.add(shape);

                for (GShape child : ((GGroup) shape).getChildShapes()) {
                    child.setSelected(true);
                    toAdd.add(child);
                }
            }
        }
        if (!toRemove.isEmpty()) {
            for (GShape shape : toRemove) {
                shapes.getShapes().remove(shape);
            }
            for (GShape shape : toAdd) {
                shapes.add(shape);
            }
            drawAllShapes();
        }
    }
    private void autoSelectIfText(GShape drawnShape) {
        if (drawnShape instanceof shapes.GText) {
            for (GShape shape : shapes.getShapes()) shape.setSelected(false);
            drawnShape.setSelected(true);

            inlineEditor.startEditing((shapes.GText) drawnShape);
        }
    }

    public GText getSelectedTextShape() {
        for (GShape shape : shapes.getShapes()) {
            if (shape.isSelected() && shape instanceof GText) {
                return (GText) shape;
            }
        }
        return null;
    }

    public void applyFontToSelectedText(String fontName, int fontSize) {
        for (shapes.GShape shape : shapes.getShapes()) {
            if (shape.isSelected() && shape instanceof shapes.GText) {
                ((shapes.GText) shape).setFont(new Font(fontName, Font.PLAIN, fontSize));
                drawAllShapes();
            }
        }
    }


    private class KeyHandler extends KeyAdapter {

        @Override
        public void keyPressed(java.awt.event.KeyEvent e) {
            int keyCode = e.getKeyCode();
            boolean isCtrl = e.isControlDown() || e.isMetaDown();
            boolean isShift = e.isShiftDown();

            if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DELETE) {
                delete();
            } else if (isCtrl) {
                if (keyCode == KeyEvent.VK_C) copy();
                else if (keyCode == KeyEvent.VK_V) paste();
                else if (keyCode == KeyEvent.VK_X) cut();
                else if (keyCode == KeyEvent.VK_D) duplicate();
                else if (keyCode == KeyEvent.VK_G) {
                    if (isShift) ungroup();
                    else group();
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
            } else if (e.getButton() == 3) { //right button
                mouseRButtonClicked(e);
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
                    finishTransform(e.getX(), e.getY(), e.isShiftDown());
                    eDrawingState = EDrawingState.eIdle;
                }
            }
        }

        private void mouseRButtonClicked(MouseEvent e) {
            GShape clickedShape = onShape(e.getX(), e.getY());
            if (clickedShape != null) {
                if (!clickedShape.isSelected()) {
                    for (GShape shape : shapes.getShapes()) {
                        shape.setSelected(false);
                    }
                    clickedShape.setSelected(true);
                    drawAllShapes();
                }

                popupMenu.show(GDrawingPanel.this, e.getX(), e.getY());
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            GDrawingPanel.this.requestFocus();

            if (toolBar.getShapeType().getDrawingType() == GConstants.EDrawingType.e2Point) {
                if (eDrawingState == EDrawingState.eIdle) {
                    startTransform(e);

                    if (eDrawingState != EDrawingState.eSelecting) {
                        eDrawingState = EDrawingState.eTransforming;
                    }
                }
            }
        }


        @Override
        public void mouseDragged(MouseEvent e) {
            if (statusBar != null) {
                statusBar.updateCoordinates(e.getX(), e.getY());
            }
            if (eDrawingState == EDrawingState.eSelecting) {
                keepLasso(e.getX(), e.getY());
                return;
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
            }
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