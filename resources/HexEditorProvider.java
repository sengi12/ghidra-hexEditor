package resources;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import ghidra.framework.plugintool.ComponentProviderAdapter;
import ghidra.framework.plugintool.PluginTool;

/**
 * Hosts the editor as a dockable Ghidra window instead of a floating JFrame.
 *
 * Docked, it tabs and splits alongside the Listing and the Decompiler, Ghidra
 * remembers where the user parked it between sessions, and it stops reading as
 * a separate application that happens to have been launched from Ghidra.
 *
 * Only the long stable part of the docking API is touched - construct, add,
 * remove - because a script is compiled against whichever Ghidra is running it.
 */
public class HexEditorProvider extends ComponentProviderAdapter {

    /** The tool owner string every provider from this script registers under. */
    public static final String OWNER = "HexEditor";

    // one editor per program: re-running the script replaces its own window
    // rather than stacking up another tab beside it
    private static final Map OPEN = new HashMap();

    private final JComponent component;

    private HexEditorProvider(PluginTool tool, String name, JComponent component) {
        super(tool, name, OWNER);
        this.component = component;
        setTransient();
    }

    /**
     * Shows the editor for one program, replacing an editor this script already
     * opened for the same program.
     */
    public static HexEditorProvider open(PluginTool tool, String programName, JComponent component) {
        HexEditorProvider previous = (HexEditorProvider)OPEN.remove(programName);
        if(previous != null) {
            tool.removeComponentProvider(previous);
        }

        HexEditorProvider provider = new HexEditorProvider(tool, "Hex Editor - " + programName, component);
        OPEN.put(programName, provider);
        tool.addComponentProvider(provider, true);
        return provider;
    }

    public JComponent getComponent() {
        return this.component;
    }
}
