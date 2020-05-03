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

public class HexEditor extends GhidraScript {
    public MainWindow mw;
    public String currentDirectory;
    public String name;
    public JFrame frame;
    private boolean DEBUG = false;

    @Override
    protected void run() throws Exception {
        currentDirectory = sourceFile.getAbsolutePath();
        currentDirectory = currentDirectory.substring(0, currentDirectory.length()-14);
        name = currentProgram.getName();
        writeBinLocation();
        this.frame = new JFrame(String.format("Ghidra Hex Editor    :   %s", name));
        String path = currentProgram.getExecutablePath();
        mw = new MainWindow(this, path);
        frame.getContentPane().add(mw);
        frame.setSize(MainWindow.getWindowWidth(), MainWindow.getWindowHeight());
        frame.setMinimumSize(new Dimension(MainWindow.getWindowWidth(), MainWindow.getWindowHeight()));
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public void dbprint(String s){
        if(DEBUG){
            print(s);
        }
    }

    public void writeBinLocation(){ // run python cleanup.py to recompile program
        GhidraProvider mp = new GhidraProvider();
        String path = mp.getClass(sourceFile, "Cantordust").getAbsolutePath();
        path = path.substring(0, path.length()-16);
        String fileName = currentDirectory+"ghidra_bin_location.txt";
        try{
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8);
            writer.write(path);
            writer.close();
        }catch(IOException e){}
    }
}
