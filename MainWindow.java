import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.border.TitledBorder;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;

public class MainWindow extends JPanel implements ActionListener, ItemListener, CaretListener, MouseListener{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public String path;
    public HexEditor hex;
    public binEdit editor;
    
    public JMenu menu;
    public JMenuItem menuItem;
    public JMenuBar menuBar = new JMenuBar();

    public JComboBox[] viewCBox = new JComboBox[2];
    public JLabel JTsizes = new JLabel("");
    public JTextField JTFile = new JTextField();
    public JTextField JTView = new JTextField();

    public JComponent help;
    public boolean helpFlag = false;

    public JProgressBar savePBar = new JProgressBar(0, 0, 0);
    public JProgressBar findPBar = new JProgressBar(0, 0, 0);

    public JPanel jPBBP = new JPanel(new BorderLayout());
    
    public JPanel stat;
    public JPanel frameFile;

    public byte[] finByte;
    public byte[] finByteU;
    public byte[][][] findChar;
    
    // find bar components
    public JPanel fP0 = new JPanel(new BorderLayout());
    public JPanel fP1;
    public JLabel fJL = new JLabel(" ");
    public JButton[] fJB = new JButton[2];
    public JComboBox[] fJCB = new JComboBox[4];
    public JTextField[] fJTF = new JTextField[2];
    public JCheckBox fJRB = new JCheckBox("Ignore case:");

    JScrollBar jSB = new JScrollBar(1, 0, 1, 0, 1);

    public boolean cp437Available = false;
    public boolean useFindChar = false;
    public boolean hexOffset = true;

    public DecimalFormatSymbols dFS = new DecimalFormatSymbols();
    public DecimalFormat fForm = new DecimalFormat("#.##########E0");
    public DecimalFormat dForm = new DecimalFormat("#.###################E0");


    public MainWindow(HexEditor h, String p) {
        this.path = p;
        this.hex = h;

        this.fP1 = this.findPanel();
        this.help = this.help();

        setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // menu bar instantiation
        createMenuBar();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = getWindowWidth();
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        this.add(this.menuBar, gbc);

        // save progress bar
        this.savePBar.setStringPainted(true);
        this.savePBar.setString("");

        // JTView
        this.JTView.setEditable(false);

        // JTFile
        this.JTFile.setEditable(false);
        ++gbc.gridy;
        this.add(this.JTFile, gbc);
        
        // jPBBP JPanel
        ++gbc.gridy;
        this.add(this.jPBBP, gbc);
        
        // fP0
        ++gbc.gridy;
        this.add(this.fP0, gbc);
        
        // Editor instantiation
        editor = new binEdit(hex, this, path);
        int mainH = getWindowHeight()-200;
        int mainW = getWindowWidth()-50;
        editor.setPreferredSize(new Dimension(mainW, mainH));
        editor.setSize(new Dimension(mainW, mainH));
        ++gbc.gridy;
        gbc.gridheight = mainH;
        gbc.gridwidth = mainW;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(editor, gbc);

        gbc.gridx += (mainW);
        gbc.gridwidth = 1;
        gbc.fill = 1;
        add(jSB, gbc);

        // status bar instantiation
        this.stat = this.status(this.hex);
        gbc.gridy += mainH;
        gbc.fill = 0;
        gbc.gridx = 0;
        add(this.stat, gbc);

        this.dFS.setDecimalSeparator('.');
        this.fForm.setDecimalFormatSymbols(this.dFS);
        this.dForm.setDecimalFormatSymbols(this.dFS);
    }

    public static int getWindowWidth() {
        return 1050;
    }

    public static int getWindowHeight() {
        return 630;
    }

    public JComponent help() {
        jEP editorPane = new jEP((String)null, false);
        // editorPane.setContentType("text/html");
        // String language = Locale.getDefault().getLanguage();
        // language = "ReadMe" + Character.toUpperCase(language.charAt(0)) + language.charAt(1) + ".htm";

        // try {
        //     editorPane.eP.getEditorKit().read(UI.class.getResource(UI.class.getResource(language) == null?"ReadMeEn.htm":language).openStream(), editorPane.eP.getDocument(), 0);
        //     editorPane.eP.getEditorKit().read(UI.class.getResource("shortKey.htm").openStream(), editorPane.eP.getDocument(), editorPane.eP.getDocument().getLength());
        // } catch (Exception e) {
        //     this.hex.dbprint("error opening up help\n");
        // }

        return editorPane;
    }

    private JPanel status(HexEditor hex) {
        String[][] statStrMatrix = new String[][]{{"BE", "LE "}, {"Binary", "Byte, signed/unsigned    ", "Short (16), signed", "Short (16), unsigned", "Int (32), signed", "Int (32), unsigned", "Long (64), signed", "Long (64), unsigned", "Float (32)", "Double (64)", "DOS-US/OEM-US/cp437", "UTF-8", "UTF-16"}, {"<html>Big-Endian (natural order) or little-Endian (Intel order).", "<html>Conversion rule for the data following the caret (shown here after)."}};

        try {
            if(!(this.cp437Available = Charset.isSupported("cp437"))) {
                statStrMatrix[1][10] = "ISO/CEI 8859-1";
            }
        } catch (Exception var6) {
            ;
        }

        JPanel statPanel = new JPanel(new GridBagLayout());
        statPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);

        for(int i = 0; i < this.viewCBox.length; ++i) {
            this.viewCBox[i] = new JComboBox();
            this.viewCBox[i].setPrototypeDisplayValue(statStrMatrix[i][1]);
            this.viewCBox[i].setToolTipText(statStrMatrix[2][i]);

            for(int j = 0; j < statStrMatrix[i].length; ++j) {
                this.viewCBox[i].addItem(statStrMatrix[i][j]);
            }

            ++gbc.gridx;
            statPanel.add(this.viewCBox[i], gbc);
        }

        this.viewCBox[1].setSelectedIndex(1);
        gbc.weightx = 1.0D;
        ++gbc.gridx;
        
        // this.JTView.setPreferredSize(new Dimension(this.JTView.getPreferredSize().width, this.viewCBox[0].getMinimumSize().height));
        this.JTView.setPreferredSize(new Dimension(550/* w */, 10/* h */));
        statPanel.add(this.JTView, gbc);
        gbc.weightx = 0.0D;
        ++gbc.gridx;
        gbc.gridwidth = 100;
        statPanel.add(Box.createHorizontalStrut(3), gbc);
        gbc.gridx+=100;
        statPanel.add(this.JTsizes, gbc);
        ++gbc.gridx;
        statPanel.add(Box.createHorizontalStrut(3), gbc);
        this.viewCBox[0].addItemListener(this);
        this.viewCBox[1].addItemListener(this);
        this.JTsizes.addMouseListener(this);
        return statPanel;
    }

    public void createMenuBar(){
        String[][] menuBarStrMatrix = new String[][]{
            {"File", 
               "Open", 
               "Save as ", 
               "Close file (Q)", 
               "Take Screenshot"
            }, 
            {"Edit", 
               "Select All", 
               "Undo (Z)", 
               "Redo (Y)", 
               "Cut (X)", 
               "Copy", 
               "Paste (V)", 
               "Find", 
               "Insert (before)", 
               "Delete"
            }, 
            {"View", 
               "Goto", 
               "Toggle position Mark", 
               "Down to next mark", 
               "Up to previous mark ", 
               "Toggle caret ", 
               "Higher fontSize", 
               "Lower fontSize", 
               "Toggle Dark Mode"
            }, 
            {"hidden", 
               "Font +", 
               "Font -"
            }, 
            {"Help", 
               "Toggle help"
            }
        };
        int[][] var6 = new int[][]{{70, 79, 83, 81, 80}, {69, 65, 90, 89, 88, 67, 86, 70, 155, 127}, {86, 71, 77, 68, 85, 84, 107, 109, 87}, {178, 521, 45}, {72, 72}};
        int[][] var7 = new int[][]{{70, 79, 83, 81, 80}, {69, 65, 90, 89, 88, 67, 86, 70, 73, 68}, {86, 71, 77, 68, 85, 84, 72, 76, 87}, {72, 43, 45}, {72, 72}};
        int[][] var8 = new int[][]{{0, 2, 2, 2, 2}, {0, 2, 2, 2, 2, 2, 2, 2, 0, 0}, {0, 2, 2, 2, 2, 2, 2, 2, 2}, {0, 2, 2}, {0, 2}};


        for(int i = 0; i < menuBarStrMatrix.length; ++i) {
            (this.menu = new JMenu(menuBarStrMatrix[i][0])).setMnemonic(var7[i][0]);
            for(int j = 1; j < menuBarStrMatrix[i].length; ++j) {
                this.menuItem = new JMenuItem(menuBarStrMatrix[i][j], var7[i][j]);
                if(i != 4) {
                    this.menuItem.setAccelerator(KeyStroke.getKeyStroke(var6[i][j], var8[i][j]));
                }

                this.menuItem.addActionListener(this);
                this.menu.add(this.menuItem);
                if(i == 1 && (j == 1 || j == 3 || j == 6 || j == 7) || i == 2 && (j == 1 || j == 4 || j == 5 || j == 7)) {
                    this.menu.addSeparator();
                }
            }
   
            if(i != 3) {
               // adds menu to menu bar
               this.menuBar.add(this.menu);
            }
        }
    }

    private JPanel findPanel() {
        String[][] var3 = new String[][]{{"BE", "LE"}, {"Signed", "Unsigned"}, {"Short (16)", "Int (32)", "Long (64)", "Float (32)", "Double (64)", "Hexa", "ISO/CEI 8859-1", "UTF-8", "UTF-16"}, {"8 bits", "16 bits", "32 bits", "64 bits", "128 bits"}, {"<html>Big-indian (natural order) or<br>Little-indian (Intel order).", "Only for integer", "Data type", "<html>Select \'64\' if you search a machine instruction for a 64 bits processor.<br>If you don\'t know, left it at \'8\'."}, {"BE", "Unsigned", "ISO/CEI 8859-1", "128 bits"}, {"Next", "Hide"}};
        JPanel findBar = new JPanel(new GridBagLayout());
        JPanel searchBar = new JPanel(new GridBagLayout());
        JPanel findOptions = new JPanel(new GridBagLayout());
        findBar.setBorder(BorderFactory.createTitledBorder("Find:"));
        ((TitledBorder)findBar.getBorder()).setTitleColor(Color.blue);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = 21;
        gbc.gridwidth = getWindowWidth();
        findBar.add(searchBar, gbc);
        gbc.fill = 1;
        ++gbc.gridx;
        findBar.add(findOptions, gbc);
        this.findPBar.setStringPainted(true);
        findBar.add(this.findPBar, gbc);
        searchBar.add(this.fJTF[0] = new JTextField());
        this.fJTF[0].addCaretListener(this);
        searchBar.add(new JLabel("  "));
        searchBar.add(this.fJL);

        int var1;
        for(var1 = 0; var1 < this.fJCB.length; ++var1) {
            this.fJCB[var1] = new JComboBox();
            this.fJCB[var1].setPrototypeDisplayValue(var3[5][var1]);
            this.fJCB[var1].setToolTipText(var3[4][var1]);

            for(int var2 = 0; var2 < var3[var1].length; ++var2) {
                this.fJCB[var1].addItem(var3[var1][var2]);
            }

            this.fJCB[var1].addItemListener(this);
            findOptions.add(this.fJCB[var1]);
        }

        this.fJRB.setHorizontalTextPosition(2);
        this.fJRB.setMargin(new Insets(0, 1, 0, 1));
        this.fJRB.addActionListener(this);
        findOptions.add(this.fJRB);
        this.fJTF[0].setPreferredSize(new Dimension(
            this.fJCB[0].getPreferredSize().width + 
            this.fJCB[1].getPreferredSize().width + 
            this.fJCB[2].getPreferredSize().width + 
            this.fJCB[3].getPreferredSize().width, 
            this.fJTF[0].getPreferredSize().height));
        findOptions.add(Box.createHorizontalGlue());
        findOptions.add(new JLabel("   From:"));
        findOptions.add(this.fJTF[1] = new JTextField(15));

        for(var1 = 0; var1 < this.fJB.length; ++var1) {
            this.fJB[var1] = new JButton(var3[6][var1]);
            this.fJB[var1].setMargin(new Insets(3, 2, 3, 2));
            this.fJB[var1].addActionListener(this);
            findOptions.add(this.fJB[var1]);
        }

        return findBar;
    }

    protected void saveRunning(boolean var1) {
        if(var1) {
            this.jPBBP.add(this.savePBar);
        } else {
            this.jPBBP.removeAll();
        }

        this.savePBar.setValue(0);
        this.validate();
        this.repaint();
    }

    protected void find() {
        this.hex.frame.setSize(MainWindow.getWindowWidth(), MainWindow.getWindowHeight()+60);
        this.hex.frame.setMinimumSize(new Dimension(MainWindow.getWindowWidth(), MainWindow.getWindowHeight()+60));
        this.findPBar.setString("");
        this.fP0.add(this.fP1, "West");
        this.validate();
        this.repaint();
    }

    protected void findRunning(boolean testRun) {
        this.fJB[0].setText(testRun?"Stop":"Next");
        this.fJB[1].setEnabled(!testRun);
        this.findPBar.setValue(0);
        if(!testRun) {
            this.findPBar.setString("");
        }

    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == this.fJRB) {
            this.checkFindEntry();
        } else if(e.getSource() == this.fJB[0] && this.fJB[0].getText() == "Next") {
            this.editor.find1();
        } else if(e.getSource() == this.fJB[0] && this.fJB[0].getText() == "Stop") {
            this.editor.find.interrupt();
        } else if(e.getSource() == this.fJB[1]) { // hide find bar
            this.hex.frame.setMinimumSize(new Dimension(MainWindow.getWindowWidth(), MainWindow.getWindowHeight()));
            this.hex.frame.setSize(MainWindow.getWindowWidth(), MainWindow.getWindowHeight());
            this.fP0.removeAll();
            this.validate();
            this.repaint();
            this.editor.slideScr(-1L, false);
        } else if(e.getSource().getClass().isInstance(new JMenuItem())) { // help
            boolean isToggleHelp = ((JMenuItem)((JMenuItem)e.getSource())).getText().equals("Toggle help");
            if(isToggleHelp || this.helpFlag) {
                this.hex.dbprint("help toggled\n");
                // GridBagConstraints gbc = new GridBagConstraints();
                // gbc.gridwidth = getWindowWidth();
                // gbc.gridheight = getWindowHeight();
                // gbc.gridx = 0;
                // gbc.gridy = 0;
                // gbc.fill = 1;
                // this.removeAll();
                // if(this.helpFlag) {
                //     this.hex.dbprint("helpflag = t\n");
                //     this.add(this.frameFile, gbc);
                // } else {
                //     this.hex.dbprint("helpflag = f\n");
                //     this.add(this.help, gbc);
                // }

                // this.validate();
                // this.repaint();
            }

            this.helpFlag = !this.helpFlag && isToggleHelp;
            if(!isToggleHelp) {
                this.editor.KeyFromMenu(((JMenuItem)e.getSource()).getAccelerator().getKeyCode());
            }
        }

    }

    private void checkFindEntry() {
        // relabel vars to do
        boolean check = false;
        BigDecimal bigDec = null;
        long time = System.currentTimeMillis();
        long[] var17 = new long[2];
        StringBuffer var18 = new StringBuffer(220);
        var18.append("<html>");

        while(System.currentTimeMillis() < time + 50L) {
            ;
        }

        String var19 = this.fJTF[0].getText();
        String var20 = null;
        int var21 = var19.length();
        int var22 = this.fJCB[2].getSelectedIndex();
        boolean var24 = false;
        boolean var25 = false;
        this.useFindChar = this.fJRB.isSelected() && 5 < var22;
        if(var21 == 0) {
            this.fJL.setText(" ");
        } else {
            int var32;
            int var23;
            if(var22 < 5 && !var19.startsWith("0x") && !var19.startsWith("Ox") && !var19.startsWith("ox")) {
                if(var19.charAt(0) == 45 && this.fJCB[1].getSelectedIndex() == 1 && var22 < 3) {
                    this.fJL.setText("<html><FONT color=red>Input must be positive for unsigned integer.</FONT>");
                    return;
                }

                if(var19.equals("-") || var19.equals("+")) {
                    this.fJL.setText(" ");
                    return;
                }

                BigDecimal var4 = var22 == 0?BigDecimal.valueOf(-32768L):(var22 == 1?BigDecimal.valueOf(-2147483648L):(var22 == 2?BigDecimal.valueOf(Long.MIN_VALUE):(var22 == 3?BigDecimal.valueOf(-3.4028234663852886E38D):BigDecimal.valueOf(-1.7976931348623157E308D))));
                BigDecimal var5;
                if(2 < var22) {
                    var5 = var22 == 3?BigDecimal.valueOf(3.4028234663852886E38D):BigDecimal.valueOf(Double.MAX_VALUE);
                } else if(this.fJCB[1].getSelectedIndex() == 0) {
                    var5 = var22 == 0?BigDecimal.valueOf(32767L):(var22 == 1?BigDecimal.valueOf(2147483647L):BigDecimal.valueOf(Long.MAX_VALUE));
                } else {
                    var5 = var22 == 0?BigDecimal.valueOf(65535L):(var22 == 1?BigDecimal.valueOf(4294967295L):new BigDecimal("18446744073709551615"));
                }

                this.finByte = new byte[var22 < 3?2 << var22:(var22 == 3?4:8)];

                while(0 < var21) {
                    try {
                    bigDec = new BigDecimal(var19.substring(0, var21));
                    if(bigDec.compareTo(var4) >= 0 && var5.compareTo(bigDec) >= 0) {
                        break;
                    }

                    throw new Exception("");
                    } catch (Exception var31) {
                    --var21;
                    }
                }

                if(var21 == 0) {
                    this.fJL.setText("<html><FONT color=red>Input must be a number.</FONT>");
                } else if(var22 < 3) {
                    BigInteger var7;
                    try {
                    var7 = bigDec.setScale(0, 7).unscaledValue();
                    } catch (Exception var27) {
                    var7 = bigDec.setScale(0, 5).unscaledValue();
                    check = true;
                    }

                    if(var7.signum() < 0) {
                    time = var7.longValue();
                    if(this.fJCB[0].getSelectedIndex() == 0) {
                        for(var23 = 0; var23 < this.finByte.length; ++var23) {
                            this.finByte[this.finByte.length - var23 - 1] = (byte)((int)(time & 255L));
                            time >>>= 8;
                        }
                    } else {
                        for(var23 = 0; var23 < this.finByte.length; ++var23) {
                            this.finByte[var23] = (byte)((int)(time & 255L));
                            time >>>= 8;
                        }
                    }
                    } else {
                    byte[] var2 = var7.toByteArray();
                    var32 = this.finByte.length < var2.length?this.finByte.length:var2.length;
                    Arrays.fill(this.finByte, (byte)0);
                    if(this.fJCB[0].getSelectedIndex() == 0) {
                        for(var23 = 1; var23 <= var32; ++var23) {
                            this.finByte[this.finByte.length - var23] = var2[var2.length - var23];
                        }
                    } else {
                        for(var23 = 0; var23 < var32; ++var23) {
                            this.finByte[var23] = var2[var2.length - 1 - var23];
                        }
                    }
                    }
                } else {
                    float var13 = bigDec.floatValue();
                    double var9 = bigDec.doubleValue();
                    this.useFindChar = check = 0 != (var32 = bigDec.compareTo(new BigDecimal(var22 == 3?(double)var13:var9)));
                    time = var17[0] = var22 == 3?(long)Float.floatToRawIntBits(var13):Double.doubleToRawLongBits(var9);
                    if(this.fJCB[0].getSelectedIndex() == 0) {
                    for(var23 = 0; var23 < this.finByte.length; ++var23) {
                        this.finByte[this.finByte.length - var23 - 1] = (byte)((int)(time & 255L));
                        time >>>= 8;
                    }
                    } else {
                    for(var23 = 0; var23 < this.finByte.length; ++var23) {
                        this.finByte[var23] = (byte)((int)(time & 255L));
                        time >>>= 8;
                    }
                    }
                    
                    double testD = 0.0D;
                    float testF = 0.0F;
                    if(this.useFindChar) {
                    var17[1] = var22 == 3?(long)Float.floatToRawIntBits(testF = 0 < var32?math.nextUp(var13):math.nextDown(var13)):Double.doubleToRawLongBits(testD = 0 < var32?math.nextUp(var9):math.nextDown(var9));
                    if(var22 == 3) {
                        var18.append(var32 < 0?"&lt; ":"&gt; ").append(this.fForm.format(new BigDecimal((double)var13))).append("<br>" + (var32 < 0?"&gt; ":"&lt; ")).append(this.fForm.format(new BigDecimal((double)testF)));
                    } else {
                        var18.append(var32 < 0?"&lt; ":"&gt; ").append(this.dForm.format(new BigDecimal(var9))).append("<br>" + (var32 < 0?"&gt; ":"&lt; ")).append(this.dForm.format(new BigDecimal(testD)));
                    }

                    this.findChar = new byte[1][2][var22 == 3?4:8];

                    for(var32 = 0; var32 < 2; ++var32) {
                        if(this.fJCB[0].getSelectedIndex() == 0) {
                            for(var23 = 0; var23 < this.findChar[0][var32].length; ++var23) {
                                this.findChar[0][var32][this.finByte.length - var23 - 1] = (byte)((int)(var17[var32] & 255L));
                                var17[var32] >>>= 8;
                            }
                        } else {
                            for(var23 = 0; var23 < this.findChar[0][var32].length; ++var23) {
                                this.findChar[0][var32][var23] = (byte)((int)(var17[var32] & 255L));
                                var17[var32] >>>= 8;
                            }
                        }
                    }
                    }
                }
            } else if(var22 == 5) {
                var19 = var19.trim().replaceAll(" ", "");
                if(var19.startsWith("0x") || var19.startsWith("Ox") || var19.startsWith("ox")) {
                    var19 = var19.substring(2);
                }

                for(var21 = 0; var21 < var19.length() && -1 < "0123456789abcdefABCDEF".indexOf(var19.charAt(var21)); ++var21) {
                    ;
                }

                if(var21 < 2) {
                    this.fJL.setText(var21 == var19.length()?" ":"<html><FONT color=red>Input must be a hexa string.</FONT>");
                    return;
                }

                this.finByte = new byte[var21 >> 1];

                try {
                    for(var23 = 0; var23 < this.finByte.length; ++var23) {
                    this.finByte[var23] = (byte)Integer.parseInt(var19.substring(var23 << 1, var23 * 2 + 2), 16);
                    }
                } catch (Exception var30) {
                    ;
                }
            } else if(var22 == 6) {
                while(0 < var21) {
                    try {
                    var20 = var19.substring(0, var21);
                    this.finByte = var20.getBytes("ISO-8859-1");
                    if(!var20.equals(new String(this.finByte, "ISO-8859-1"))) {
                        throw new Exception("");
                    }
                    break;
                    } catch (Exception var28) {
                    --var21;
                    }
                }

                if(var21 < 1) {
                    this.fJL.setText("<html><FONT color=red>Input must be an ISO-8859-1 string.</FONT>");
                    return;
                }
            } else {
                while(0 < var21) {
                    try {
                    var20 = var19.substring(0, var21);
                    this.finByte = var20.getBytes(var22 == 7?"UTF-8":(this.fJCB[0].getSelectedIndex() == 0?"UTF-16BE":"UTF-16LE"));
                    break;
                    } catch (Exception var29) {
                    --var21;
                    }
                }

                if(var21 < 1) {
                    this.fJL.setText("<html><FONT color=red>Input must be an UTF string.</FONT>");
                    return;
                }
            }

            if(var22 < 3 || 4 < var22 || !check) {
                for(var23 = 0; var23 < this.finByte.length; ++var23) {
                    var32 = this.finByte[var23] & 255;
                    var18.append((var32 < 16?"0":"") + Integer.toHexString(var32).toUpperCase());
                    if((var23 + 1) % 16 == 0) {
                    var18.append("<br>");
                    } else if((var23 + 1) % (1 << this.fJCB[3].getSelectedIndex()) == 0) {
                    var18.append(" ");
                    }
                }
            }

            var19 = var19.toUpperCase();
            if(var21 == var19.length() - 1 && (var22 >= 5 || var19.charAt(var19.length() - 1) != 69)) {
                var18.append("<br><FONT color=red>The last char is invalid.</FONT>");
            } else if(var21 < var19.length() - 1 && (var21 != var19.length() - 2 || var22 >= 5 || var19.charAt(var19.length() - 2) != 69 || var19.charAt(var19.length() - 1) != 43 && var19.charAt(var19.length() - 1) != 45)) {
                var18.append("<br><FONT color=red>The last ").append(var19.length() - var21).append(" caracters are invalid.</FONT>");
            }

            if(check && !this.useFindChar) {
                var18.append("<br><FONT color=red>The binary doesn\'t represent exactly the significand.</FONT>");
            }

            this.fJL.setText(var18.toString());
            if(var20 != null && 0 < var20.length() && this.fJRB.isSelected() && !var20.toUpperCase().equals(var20.toLowerCase())) {
                var20 = var20.toUpperCase();
                this.findChar = new byte[var20.length()][][];

                for(var21 = 0; var21 < var20.length(); ++var21) {
                    char var8 = var20.charAt(var21);
                    var23 = var8 == Character.toLowerCase(var8)?1:2;
                    if(1 < var23) {
                    for(var22 = 0; var22 < accent.s.length; ++var22) {
                        if(-1 < accent.s[var22].indexOf(var8)) {
                            var23 = accent.s[var22].length();
                            break;
                        }
                    }
                    }

                    this.findChar[var21] = new byte[var23][];

                    for(var32 = 0; var32 < var23; ++var32) {
                    if(var23 < 3) {
                        var8 = var32 == 0?var8:Character.toUpperCase(var8);
                    } else if(var22 < accent.s.length) {
                        var8 = accent.s[var22].charAt(var32);
                    }

                    if(var22 == 6) {
                        this.findChar[var21][var32] = new byte[1];
                        this.findChar[var21][var32][0] = var8 < 256?(byte)var8:this.findChar[var21][0][0];
                    } else if(var22 == 8) {
                        this.findChar[var21][var32] = new byte[2];
                        this.findChar[var21][var32][1 - this.fJCB[0].getSelectedIndex()] = (byte)(var8 & 255);
                        this.findChar[var21][var32][this.fJCB[0].getSelectedIndex()] = (byte)(var8 >> 8);
                    } else {
                        this.findChar[var21][var32] = new byte[var8 < 128?1:(var8 < 2048?2:(var8 < 65536?3:4))];
                        if(var8 < 128) {
                            this.findChar[var21][var32][0] = (byte)var8;
                        } else {
                            int var33;
                            for(var33 = this.findChar[var21][var32].length - 1; 0 < var33; --var33) {
                                this.findChar[var21][var32][var33] = (byte)(var8 & 63 | 128);
                                var8 = (char)(var8 >> 6);
                            }

                            var33 = this.findChar[var21][var32].length;
                            this.findChar[var21][var32][0] = (byte)(var8 | (var33 == 2?192:(var33 == 3?224:240)));
                        }
                    }
                    }
                }
            }
        }
    }

    public void itemStateChanged(ItemEvent e) {
        if(e.getSource() != this.viewCBox[0] && e.getSource() != this.viewCBox[1]) {
            if(e.getSource() == this.fJCB[2]) {
                int i = this.fJCB[2].getSelectedIndex();
                this.fJCB[0].setEnabled(i < 5 || i == 8);
                this.fJCB[1].setEnabled(i < 3);
                this.fJRB.setEnabled(5 < i);
            }

            this.checkFindEntry();
        } else {
            this.editor.rePaint();
        }

    }

    public void caretUpdate(CaretEvent var1) {
        this.checkFindEntry();
    }

    public void mouseReleased(MouseEvent var1) {}

    public void mouseEntered(MouseEvent var1) {}

    public void mouseExited(MouseEvent var1) {}

    public void mousePressed(MouseEvent var1) {}

    public void mouseClicked(MouseEvent var1) {
        this.hexOffset = !this.hexOffset;
        this.editor.setStatus();
    }
}