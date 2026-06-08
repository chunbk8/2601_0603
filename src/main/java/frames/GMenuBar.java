package frames;

import menu.GFileMenu;

import javax.swing.*;

public class GMenuBar extends JMenuBar {

    private GFileMenu fileMenu;

    public GMenuBar() {
        this.fileMenu = new GFileMenu("File");
        this.add(fileMenu);
    }

    public GFileMenu getFileMenu() {return fileMenu;}


}