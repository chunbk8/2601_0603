import frames.GMainFrame;

import javax.swing.*;

public class Main {
    private GMainFrame mainFrame;

    public Main() {
        this.mainFrame = new GMainFrame();
        this.mainFrame.setVisible(true);
    }

    public static void main(String[] args) {
        /*try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }*/
            Main main = new Main();

    }
}