package resources;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

/**
 * Light / dark palette for the hex editor.
 *
 * The colors are only ever pushed onto this plugin's own component tree - the
 * shared UIManager defaults are deliberately left alone so that toggling the
 * theme here cannot repaint the rest of Ghidra.
 *
 * The choice is remembered between runs through java.util.prefs.
 */
public class Theme {

    private static final String PREF_KEY = "darkMode";

    // --- window chrome: menus, panels, labels, fields -------------------
    private static final Color LIGHT_BG      = new Color(238, 238, 238);
    private static final Color LIGHT_FG      = new Color( 30,  30,  30);
    private static final Color LIGHT_FIELD   = new Color(255, 255, 255);
    private static final Color LIGHT_SELECT  = new Color(184, 207, 229);
    private static final Color LIGHT_ACCENT  = new Color(  0,   0, 255);
    private static final Color LIGHT_ERROR   = new Color(200,   0,   0);

    private static final Color DARK_BG       = new Color( 43,  43,  43);
    private static final Color DARK_FG       = new Color(216, 216, 216);
    private static final Color DARK_FIELD    = new Color( 30,  30,  30);
    private static final Color DARK_SELECT   = new Color( 62,  85, 110);
    private static final Color DARK_ACCENT   = new Color(102, 178, 255);
    private static final Color DARK_ERROR    = new Color(255, 110, 110);

    // --- hex canvas ----------------------------------------------------
    private static final Color LIGHT_CANVAS         = Color.WHITE;
    private static final Color LIGHT_CANVAS_TEXT    = Color.BLACK;
    private static final Color LIGHT_CANVAS_OFFSET  = Color.BLUE;
    private static final Color LIGHT_CANVAS_CHANGED = Color.RED;
    private static final Color LIGHT_CANVAS_SELECT  = Color.CYAN;
    private static final Color LIGHT_CANVAS_CARET   = Color.MAGENTA;
    private static final Color LIGHT_CANVAS_MARK    = Color.GREEN.darker();

    // the tint behind an unmodified byte is shared by both themes: in dark mode it
    // is drawn in XOR mode, where anything brighter than this turns the byte grid
    // into a row of solid blocks
    private static final Color CANVAS_MODIFIED      = new Color( 50,  50,  50, 40);

    private static final Color DARK_CANVAS          = new Color( 18,  19,  22);
    private static final Color DARK_CANVAS_TEXT     = new Color(220, 220, 220);
    private static final Color DARK_CANVAS_OFFSET   = new Color(120, 170, 255);
    private static final Color DARK_CANVAS_CHANGED  = new Color(255, 120, 120);
    private static final Color DARK_CANVAS_SELECT   = new Color( 38,  92, 112);
    private static final Color DARK_CANVAS_CARET    = new Color(255, 120, 255);
    private static final Color DARK_CANVAS_MARK     = new Color( 90, 200, 120);

    // structure inside the byte grid: banding, rules, and the washed out color
    // that a 0x00 byte or an unprintable character is drawn in
    private static final Color LIGHT_CANVAS_BAND    = new Color(  0,   0,   0,  10);
    private static final Color LIGHT_CANVAS_ROW     = new Color(  0,  90, 255,  20);
    private static final Color LIGHT_CANVAS_RULE    = new Color(  0,   0,   0,  38);
    private static final Color LIGHT_CANVAS_FADED   = new Color(  0,   0,   0,  75);

    private static final Color DARK_CANVAS_BAND     = new Color(255, 255, 255,  10);
    private static final Color DARK_CANVAS_ROW      = new Color(120, 170, 255,  28);
    private static final Color DARK_CANVAS_RULE     = new Color(255, 255, 255,  30);
    private static final Color DARK_CANVAS_FADED    = new Color(220, 220, 220,  85);

    private static final Color LIGHT_DIVIDER = new Color(  0,   0,   0,  42);
    private static final Color DARK_DIVIDER  = new Color(255, 255, 255,  32);

    private static final Color TRANSPARENT = new Color(0, 0, 0, 0);

    // null until the user picks a side for themselves, at which point we stop
    // taking the host's word for it
    private static Boolean chosen = loadPreference();
    private static boolean dark = chosen != null && chosen.booleanValue();

    // borrowed from the host look and feel while we are still following it
    private static Color hostBg;
    private static Color hostFg;
    private static Color hostField;
    private static Color hostFieldFg;

    private Theme() {
    }

    /**
     * Falls in behind whatever theme the host is using.
     *
     * Ghidra ships light and dark themes of its own, and an editor docked in the
     * tool that ignores them looks pasted on. Rather than depend on a particular
     * Ghidra release's theme API, this reads the current look and feel: the
     * luminance of the panel color decides light or dark, and the actual colors
     * are borrowed so the editor's chrome matches its neighbours exactly.
     *
     * A theme the user chose by hand always wins.
     */
    public static void followHost() {
        if(chosen != null) {
            dark = chosen.booleanValue();
            releaseHostColors();
            return;
        }

        hostBg = UIManager.getColor("Panel.background");
        hostFg = UIManager.getColor("Panel.foreground");
        hostField = UIManager.getColor("TextField.background");
        hostFieldFg = UIManager.getColor("TextField.foreground");

        Color probe = hostBg != null ? hostBg : UIManager.getColor("control");
        dark = probe != null && luminance(probe) < 0.5D;
    }

    private static void releaseHostColors() {
        hostBg = null;
        hostFg = null;
        hostField = null;
        hostFieldFg = null;
    }

    private static double luminance(Color c) {
        return (0.2126D * c.getRed() + 0.7152D * c.getGreen() + 0.0722D * c.getBlue()) / 255.0D;
    }

    public static boolean isDark() {
        return dark;
    }

    public static void setDark(boolean b) {
        chosen = Boolean.valueOf(b);
        dark = b;
        releaseHostColors();
        savePreference();
    }

    /** Flips the theme and returns the new state. */
    public static boolean toggle() {
        setDark(!dark);
        return dark;
    }

    public static Color background()      { return hostBg != null ? hostBg : (dark ? DARK_BG : LIGHT_BG); }
    public static Color foreground()      { return hostFg != null ? hostFg : (dark ? DARK_FG : LIGHT_FG); }
    public static Color fieldBackground() { return hostField != null ? hostField : (dark ? DARK_FIELD : LIGHT_FIELD); }
    public static Color fieldForeground() { return hostFieldFg != null ? hostFieldFg : foreground(); }
    public static Color selection()       { return dark ? DARK_SELECT : LIGHT_SELECT; }
    public static Color accent()          { return dark ? DARK_ACCENT : LIGHT_ACCENT; }
    public static Color error()           { return dark ? DARK_ERROR  : LIGHT_ERROR;  }
    /** Hairline used to separate the bars of chrome from each other. */
    public static Color divider()         { return dark ? DARK_DIVIDER : LIGHT_DIVIDER; }

    public static Color canvasBackground()  { return dark ? DARK_CANVAS         : LIGHT_CANVAS;         }
    public static Color canvasText()        { return dark ? DARK_CANVAS_TEXT    : LIGHT_CANVAS_TEXT;    }
    public static Color canvasOffset()      { return dark ? DARK_CANVAS_OFFSET  : LIGHT_CANVAS_OFFSET;  }
    public static Color canvasChangedByte() { return dark ? DARK_CANVAS_CHANGED : LIGHT_CANVAS_CHANGED; }
    public static Color canvasSelection()   { return dark ? DARK_CANVAS_SELECT  : LIGHT_CANVAS_SELECT;  }
    public static Color canvasCaret()       { return dark ? DARK_CANVAS_CARET   : LIGHT_CANVAS_CARET;   }
    public static Color canvasMark()        { return dark ? DARK_CANVAS_MARK    : LIGHT_CANVAS_MARK;    }
    public static Color canvasModified()    { return CANVAS_MODIFIED; }
    public static Color canvasBand()        { return dark ? DARK_CANVAS_BAND    : LIGHT_CANVAS_BAND;    }
    public static Color canvasCurrentRow()  { return dark ? DARK_CANVAS_ROW     : LIGHT_CANVAS_ROW;     }
    public static Color canvasRule()        { return dark ? DARK_CANVAS_RULE    : LIGHT_CANVAS_RULE;    }
    public static Color canvasFaded()       { return dark ? DARK_CANVAS_FADED   : LIGHT_CANVAS_FADED;   }
    public static Color canvasWarning()     { return error(); }
    public static Color transparent()       { return TRANSPARENT; }

    private static String monoFamily;

    /**
     * The first modern monospaced family that is actually installed, falling back
     * to Java's logical "Monospaced". That logical family resolves to Courier on
     * several platforms, and a Courier byte grid is most of why this window reads
     * as a 1990s terminal.
     */
    public static Font monoFont(int style, int size) {
        if(monoFamily == null) {
            String[] wanted = {"JetBrains Mono", "SF Mono", "Menlo", "Cascadia Mono",
                               "Consolas", "DejaVu Sans Mono", "Liberation Mono",
                               "Noto Sans Mono", "Ubuntu Mono", "Courier New"};
            List installed = Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
            monoFamily = "Monospaced";
            for(int i = 0; i < wanted.length; ++i) {
                if(installed.contains(wanted[i])) {
                    monoFamily = wanted[i];
                    break;
                }
            }
        }
        return new Font(monoFamily, style, size);
    }

    /**
     * Turns on antialiasing for a canvas we paint ourselves. Without this the
     * byte grid is rendered with hard aliased glyphs, which no other panel in a
     * current desktop application still does.
     */
    public static void applyTextHints(Graphics g) {
        if(!(g instanceof Graphics2D)) {
            return;
        }
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        // the desktop hints carry the platform's subpixel setting when there is one
        Object desktop = Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
        if(desktop instanceof Map) {
            g2.addRenderingHints((Map)desktop);
        }
        Object text = g2.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
        if(text == null || RenderingHints.VALUE_TEXT_ANTIALIAS_OFF.equals(text)
                || RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT.equals(text)) {
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
    }

    /**
     * Recolors a component and everything below it. Safe to call repeatedly and
     * safe to call on components that are currently detached from the frame,
     * which is how the find bar and the help pane get themed while hidden.
     */
    public static void apply(Component c) {
        if(c == null) {
            return;
        }

        recolor(c);

        // a JMenu keeps its items in a popup that is not part of getComponents()
        if(c instanceof JMenu) {
            JMenu menu = (JMenu)c;
            apply(menu.getPopupMenu());
            for(int i = 0; i < menu.getItemCount(); ++i) {
                apply(menu.getItem(i)); // null for separators, handled above
            }
        }

        if(c instanceof Container) {
            Component[] children = ((Container)c).getComponents();
            for(int i = 0; i < children.length; ++i) {
                apply(children[i]);
            }
        }
    }

    private static void recolor(Component c) {
        c.setBackground(background());
        c.setForeground(foreground());

        if(c instanceof JTextComponent) {
            JTextComponent t = (JTextComponent)c;
            t.setBackground(fieldBackground());
            t.setForeground(fieldForeground());
            t.setCaretColor(fieldForeground());
            t.setSelectionColor(selection());
            t.setSelectedTextColor(fieldForeground());
            t.setDisabledTextColor(fieldForeground());
        }

        // several look and feels only honor setBackground() on a component that
        // declares itself opaque - menus in particular paint their own fill
        if(c instanceof JMenuBar || c instanceof JMenuItem || c instanceof JPopupMenu
                || c instanceof JPanel || c instanceof JLabel) {
            ((JComponent)c).setOpaque(true);
        }

        if(c instanceof JComponent) {
            Border b = ((JComponent)c).getBorder();
            if(b instanceof TitledBorder) {
                ((TitledBorder)b).setTitleColor(accent());
            }
        }
    }

    private static Boolean loadPreference() {
        try {
            String stored = Preferences.userNodeForPackage(Theme.class).get(PREF_KEY, null);
            return stored == null ? null : Boolean.valueOf(stored);
        } catch (Exception e) {
            return null; // no preference store available, follow the host
        }
    }

    private static void savePreference() {
        try {
            Preferences.userNodeForPackage(Theme.class).put(PREF_KEY, String.valueOf(dark));
        } catch (Exception e) {
            // remembering the theme is a convenience, never a reason to fail
        }
    }
}
