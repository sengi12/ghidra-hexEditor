package resources;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * The column ruler above the byte grid: 00 .. 0F across the hex pane, a caption
 * over the decoded text, and the column the caret sits in picked out in the
 * accent color.
 *
 * It is a sibling of binEdit rather than an extra row inside it because binEdit
 * turns a mouse Y straight into a row index - an extra row painted inside the
 * canvas would put every click one line out.
 */
public class HexHeader extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final char[] HEX = "0123456789ABCDEF".toCharArray();

    private final binEdit editor;

    public HexHeader(binEdit editor) {
        this.editor = editor;
        setOpaque(true);
    }

    public Dimension getPreferredSize() {
        return new Dimension(10, (0 < this.editor.hChar ? this.editor.hChar : 16) + 6);
    }

    protected void paintComponent(Graphics g) {
        Theme.applyTextHints(g);
        g.setColor(Theme.canvasBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        if(this.editor.wChar < 1 || this.editor.font == null) {
            return;
        }

        g.setFont(this.editor.font);
        int baseline = this.editor.hMargin + 2;
        int caretColumn = (int)(this.editor.lastPos & 15L);

        for(int i = 0; i < 16; ++i) {
            g.setColor(i == caretColumn ? Theme.accent() : Theme.canvasFaded());
            g.drawString("0", this.editor.cShift['0'] + this.editor.wChar * this.editor.xNib[i * 2], baseline);
            g.drawString("" + HEX[i], this.editor.cShift[HEX[i]] + this.editor.wChar * this.editor.xNib[i * 2 + 1], baseline);
        }

        g.setColor(Theme.canvasFaded());
        g.drawString("Decoded text", this.editor.wChar * this.editor.xTxt[0], baseline);

        // the same hairlines the canvas draws, so the two line up exactly
        g.setColor(Theme.canvasRule());
        g.fillRect(this.editor.wChar * (this.editor.xNib[0] - 3), 0, 1, getHeight());
        g.fillRect(this.editor.wChar * (this.editor.xTxt[0] - 3), 0, 1, getHeight());
        g.fillRect(0, getHeight() - 1, getWidth(), 1);
    }
}
