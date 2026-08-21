// Ghidra-HexEditor
// @author Michael Sengelmann
// @category Patching Binaries
// @keybinding alt H
// @toolbar img/zero.png

import ghidra.app.script.GhidraScript;
import ghidra.framework.plugintool.PluginTool;

import javax.imageio.ImageIO;

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

    // mw / currentDirectory / name / frame are inherited from GhidraSrc. They used
    // to be redeclared here, which shadowed the inherited fields: MainWindow reads
    // hex.frame through its GhidraSrc reference, so it was resizing a JFrame that
    // was never shown and the window no longer grew when the find bar opened.

    public resources.HexEditorProvider provider;

    @Override
    protected void run() throws Exception {
        this.currentDirectory = sourceFile.getAbsolutePath();
        this.currentDirectory = currentDirectory.substring(0, currentDirectory.length()-14);
        this.name = currentProgram.getName();
        String path = normalizePath(currentProgram.getExecutablePath());

        // match whatever theme the tool is wearing before any widget is built
        resources.Theme.followHost();
        this.mw = new resources.MainWindow(this, path);

        PluginTool tool = state.getTool();
        if(tool != null) {
            try {
                this.provider = resources.HexEditorProvider.open(tool, this.name, this.mw);
                return;
            } catch (Throwable t) {
                // the docking API is the one thing here that varies between Ghidra
                // releases. If it is not shaped the way this script expects, open a
                // window rather than leaving the user with no editor at all.
                dbprint("could not dock the editor, opening a window instead: " + t + "\n");
            }
        }
        openInWindow();
    }

    /**
     * Fallback for a script run outside a tool, where there is no docking
     * framework to hand the editor to.
     */
    private void openInWindow() {
        this.frame = new JFrame();
        this.frame.setTitle(String.format("Ghidra Hex Editor    :   %s", name));
        this.frame.getContentPane().add(this.mw);
        this.frame.pack();
        this.frame.setMinimumSize(new Dimension(560, 320));
        resources.Theme.apply(this.frame);
        setFrameIcon();
        this.frame.setLocationRelativeTo(null); // centered, rather than pinned to the corner
        this.frame.setVisible(true);
        this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void setFrameIcon() {
        try {
            File icon = new File(this.currentDirectory, "img" + File.separator + "zero.png");
            if(icon.exists()) {
                this.frame.setIconImage(ImageIO.read(icon));
            }
        } catch (Exception e) {
            dbprint("could not load the window icon\n"); // cosmetic only
        }
    }

    /**
     * On Windows, currentProgram.getExecutablePath() hands back the path with a
     * leading separator - "/C:/dir/file.exe" - which java.nio.file.Paths.get()
     * rejects with InvalidPathException. Strip that separator whenever what
     * follows is a drive letter.
     *
     * The previous version of this check compared System.getProperty("os.name")
     * against the literal "Windows 10", so it stopped firing the moment users
     * moved to Windows 11 (issues #2 and #3). Matching on the shape of the path
     * instead keeps working on every past and future Windows release.
     */
    public static String normalizePath(String path) {
        if(path == null || path.length() < 3) {
            return path;
        }
        char first = path.charAt(0);
        boolean leadingSeparator = (first == '/' || first == '\\');
        boolean driveLetterFollows = Character.isLetter(path.charAt(1)) && path.charAt(2) == ':';
        if(leadingSeparator && driveLetterFollows) {
            return path.substring(1);
        }
        return path;
    }

    @Override
    public String getName(){
        return currentProgram.getName();
    }

    @Override
    public void changeTitle(String s){
        if(this.provider != null) {
            this.provider.setTitle("Hex Editor - " + s);
        } else if(this.frame != null) {
            this.frame.setTitle(String.format("Ghidra Hex Editor    :   %s",s));
            this.frame.repaint();
        }
    }
}
