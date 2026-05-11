import frames.GMainFrame;

public class Main {
    private GMainFrame mainFrame;
    public Main() {
        this.mainFrame = new GMainFrame();
        this.mainFrame.setVisible(true); //그려라! ㅋ 여기서 그리는 이유 나중에 설명
    }

    public static void main(String[] args) {
        Main main = new Main();
    }
}