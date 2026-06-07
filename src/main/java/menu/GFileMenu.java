package menu;

import frames.GDrawingPanel;
import shapes.GShape;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Vector;

public class GFileMenu extends JMenu {

    //components
    private JMenuItem newItem;
    private JMenuItem openItem;
    private JMenuItem saveItem;
    private JMenuItem exportItem;
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

        openItem = new JMenuItem("열기");
        openItem.setActionCommand("Open");
        openItem.addActionListener(actionHandler);
        this.add(openItem);
        saveItem = new JMenuItem("저장");
        saveItem.setActionCommand("Save");
        saveItem.addActionListener(actionHandler);
        this.add(saveItem);
        exportItem = new JMenuItem("이미지로 내보내기(PNG)");
        exportItem.setActionCommand("Export");        // 🌟 이름표
        exportItem.addActionListener(actionHandler);  // 🌟 핸들러 부착
        this.add(exportItem);
    }

    public void associateWith(GDrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
    }

    private void reset() {
        if (drawingPanel != null) {
            int result = JOptionPane.showConfirmDialog(
                    null,
                    "모든 작업이 지워집니다. 새로 만드시겠습니까?",
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

        // 윈도우 '다른 이름으로 저장' 창을 화면에 띄웁니다.
        int returnVal = fileChooser.showSaveDialog(null);

        // 사용자가 파일명을 적고 '저장' 버튼을 눌렀을 경우
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file == null) {
                return;
            }

            // 사용자가 확장자를 안 적었으면 우리가 만든 전용 확장자 .grap을 강제로 붙여줍니다!
            if (!file.getName().endsWith(".grap")) {
                file = new File(file.getAbsolutePath() + ".grap");
            }

            // ObjectOutputStream을 사용해 도형 리스트를 파일로 압축해서 저장합니다.
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(drawingPanel.getShapes());
                JOptionPane.showMessageDialog(null, "성공적으로 저장되었습니다!", "저장 완료", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "저장 중 오류가 발생했습니다.", "에러", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // =========================================================
    // 🌟 열기(불러오기) 로직
    // =========================================================
    private void open() {
        if (drawingPanel == null) return;

        // 윈도우 '파일 열기' 창을 화면에 띄웁니다.
        int returnVal = fileChooser.showOpenDialog(null);

        // 사용자가 파일을 선택하고 '열기' 버튼을 눌렀을 경우
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file == null) {
                return;
            }

            // ObjectInputStream을 사용해 파일에 압축된 도형 리스트를 풀어서 가져옵니다.
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                @SuppressWarnings("unchecked")
                Vector<GShape> loadedShapes = (Vector<GShape>) ois.readObject();

                // 패널에 읽어온 도형들을 덮어씌웁니다!
                drawingPanel.setShapes(loadedShapes);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "파일을 여는 중 오류가 발생했습니다.", "에러", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void export() {
        if (drawingPanel == null) return;

        // 탐색기 창 띄우기
        int returnVal = fileChooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            if (file == null) return; // 방어 코드

            // 사용자가 .png를 안 썼으면 우리가 강제로 붙여줍니다!
            if (!file.getName().toLowerCase().endsWith(".png")) {
                file = new File(file.getAbsolutePath() + ".png");
            }

            // 🌟 아까 1단계에서 만든 도화지의 이미지 변환 메서드 호출!
            drawingPanel.exportToImage(file);
            JOptionPane.showMessageDialog(null, "멋진 그림이 PNG로 저장되었습니다!", "내보내기 완료", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    private class FileActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 클릭된 버튼의 이름표(ActionCommand)를 가져옵니다.
            String command = e.getActionCommand();

            // 어떤 버튼이 눌렸는지에 따라 분기 처리
            if (command.equals("New")) {
                reset();
            } else if (command.equals("Open")) {
                open();
            } else if (command.equals("Save")) {
                save();
            } else if (command.equals("Export")) {
                export();
            }
        }
    }
}
