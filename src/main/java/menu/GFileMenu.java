package menu;

import frames.GDrawingPanel;
import shapes.GShape;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Vector;

import static javax.swing.JFileChooser.APPROVE_OPTION;
import static javax.swing.JOptionPane.*;

public class GFileMenu extends JMenu {

    //components
    private JMenuItem newItem;
    private JMenuItem openItem;
    private JMenuItem saveItem;
    private JMenuItem insertImageItem;
    private JMenuItem saveImageItem;
    private JFileChooser fileChooser;

    //association
    private GDrawingPanel drawingPanel;

    public GFileMenu(String titile) {
        super(titile);
        FileActionHandler actionHandler = new FileActionHandler();

        fileChooser = new JFileChooser();

        newItem = new JMenuItem("새로 만들기");
        newItem.setActionCommand("New");
        newItem.addActionListener(actionHandler);
        this.add(newItem);

        openItem = new JMenuItem("파일 열기");
        openItem.setActionCommand("Open");
        openItem.addActionListener(actionHandler);
        this.add(openItem);

        saveItem = new JMenuItem("파일 저장");
        saveItem.setActionCommand("Save");
        saveItem.addActionListener(actionHandler);
        this.add(saveItem);

        insertImageItem = new JMenuItem("이미지 삽입");
        insertImageItem.setActionCommand("InsertImage");
        insertImageItem.addActionListener(actionHandler);
        this.add(insertImageItem);

        saveImageItem = new JMenuItem("이미지로 저장");
        saveImageItem.setActionCommand("SaveImage");
        saveImageItem.addActionListener(actionHandler);
        this.add(saveImageItem);
    }

    public void associateWith(GDrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
    }

    private void reset() {
        if (drawingPanel != null) {
            int result = showConfirmDialog(
                    null,
                    "새로 만드시겠습니까?",
                    "새로 만들기",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (result == JOptionPane.YES_OPTION) {
                drawingPanel.clearAll();
            }
        }
    }
    private void save() {
        if (drawingPanel == null) return;

        int returnVal = fileChooser.showSaveDialog(null);

        if (returnVal == APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file == null) {
                return;
            }

            if (!file.getName().endsWith(".grap")) {
                file = new File(file.getAbsolutePath() + ".grap");
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(drawingPanel.getShapes());
                showMessageDialog(null, "저장 완료", "저장 완료", INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                showMessageDialog(null, "저장 중 오류 발생", "에러", ERROR_MESSAGE);
            }
        }
    }
    private void open() {
        if (drawingPanel == null) return;

        // 윈도우 '파일 열기' 창을 화면에 띄웁니다.
        int returnVal = fileChooser.showOpenDialog(null);

        // 사용자가 파일을 선택하고 '열기' 버튼을 눌렀을 경우
        if (returnVal == APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file == null) {
                return;
            }
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                @SuppressWarnings("unchecked")
                Vector<GShape> loadedShapes = (Vector<GShape>) ois.readObject();
                drawingPanel.setShapes(loadedShapes);
            } catch (Exception ex) {
                ex.printStackTrace();
                showMessageDialog(null, "파일을 여는 중 오류 발생", "에러", ERROR_MESSAGE);
            }
        }
    }
    private void export() {
        if (drawingPanel == null) return;

        int returnVal = fileChooser.showSaveDialog(null);
        if (returnVal == APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            if (file == null) return;

            if (!file.getName().toLowerCase().endsWith(".png")) {
                file = new File(file.getAbsolutePath() + ".png");
            }

            drawingPanel.exportToImage(file);
            showMessageDialog(null, "png 저장 완료", "png 저장 완료", INFORMATION_MESSAGE);
        }
    }

    private void insertImage() {
        if (drawingPanel == null) return;
        int returnVal = fileChooser.showOpenDialog(null);
        if (returnVal == APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file != null) {
                drawingPanel.addImage(file.getAbsolutePath());
            }
        }
    }
    private class FileActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (command.equals("New")) {
                reset();
            } else if (command.equals("Open")) {
                open();
            } else if (command.equals("Save")) {
                save();
            } else if (command.equals("SaveImage")) {
                export();
            } else if (command.equals("InsertImage")) {
                insertImage();
            }
        }
    }
}
