package resources;

import javax.swing.JFrame;

import ghidra.app.script.GhidraScript;

public class GhidraSrc extends GhidraScript{
    public MainWindow mw;
    public String currentDirectory;
    public String name;
    public JFrame frame;
    private boolean DEBUG = false;

    public GhidraSrc(){
        this.frame = new JFrame();
    }

    protected void run() throws Exception {
    }

    public void dbprint(String s){
        if(DEBUG){
            print(s);
        }
    }
    
}