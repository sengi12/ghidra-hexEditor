// Ghidra-HexEditor
// @author Michael Sengelmann
// @category Patching Binaries
// @keybinding alt H
// @toolbar img/zero.png

import ghidra.app.script.GhidraScript;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

import javax.swing.*;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import resources.MainWindow;
import resources.GhidraSrc;
import resources.*; 

public class HexEditor extends GhidraSrc {
    public resources.MainWindow mw;
    public String currentDirectory;
    public String name;
    public JFrame frame;

    @Override
    protected void run() throws Exception {
        this.currentDirectory = sourceFile.getAbsolutePath();
        this.currentDirectory = currentDirectory.substring(0, currentDirectory.length()-14);
        this.name = currentProgram.getName();
        this.frame = new JFrame();
        this.frame.setTitle(String.format("Ghidra Hex Editor    :   %s", name));
        String path = currentProgram.getExecutablePath();
        // currentProgram.getExecutablePath() returns path with an extra / at beginning on Windows 10
        // This check will remove that extra character
        if(System.getProperty("os.name").equals("Windows 10") && (path.charAt(0) == '\\' || path.charAt(0) == '/')) {
            path = path.substring(1);
        }
        this.mw = new resources.MainWindow(this, path);
        this.frame.getContentPane().add(mw);
        this.frame.setSize(resources.MainWindow.getWindowWidth(), resources.MainWindow.getWindowHeight());
        this.frame.setMinimumSize(new Dimension(resources.MainWindow.getWindowWidth(), resources.MainWindow.getWindowHeight()));
        this.frame.setVisible(true);
        this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    @Override
    public String getName(){
        return currentProgram.getName();
    }

    @Override
    public void changeTitle(String s){
        this.frame.setTitle(String.format("Ghidra Hex Editor    :   %s",s));
        this.frame.repaint();
    }
}
