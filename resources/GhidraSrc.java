package resources;

import javax.swing.JFrame;
import java.io.File;
import java.io.IOException;
import java.rmi.server.ExportException;

import ghidra.app.script.GhidraScript;
import ghidra.app.util.exporter.BinaryExporter;
import ghidra.app.util.exporter.ExporterException;
import ghidra.util.task.TaskMonitor;
import ghidra.program.model.mem.Memory;
import ghidra.program.model.listing.Program;

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

    public String getName() {
        return "";
    }

    public void dbprint(String s){
        if(DEBUG){
            print(s);
        }
    }
    
    public void createFile( File f ){
        BinaryExporter bexp = new BinaryExporter();
        Memory memory = currentProgram.getMemory();
        TaskMonitor monitor = getMonitor();
        Program domainObj = currentProgram;
        try{
            bexp.export(f, domainObj, memory, monitor);
        } catch (ExporterException e){
            dbprint("ERROR Saving File Locally\n"+e.toString());
        } catch (IOException e){
            dbprint("ERROR Saving File Locally\n"+e.toString());
        }
    }

    public void changeTitle(String s){}
}