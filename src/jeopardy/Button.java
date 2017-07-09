package jeopardy;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/**
 * Represents a UI Button on the game board
 * 
 * @author Milaud Miremadi
 *
 */
public class Button {

	static final Font BIG = new Font("arial", Font.BOLD, 32);
	static final Font NORM = new Font("arial", Font.BOLD, 24);
	static final Font SML = new Font("arial", Font.BOLD, 16);

	protected int x, y;
	protected int width, height;

	protected String text;

	protected Font font = NORM;

	private boolean recalc = true;
	private Font goodFont;

	public Button() {

	}

	public Button(String text) {
		this.text = text;
	}

	public void setText(String s) {
		text = s;
	}

	public String getText() {
		return text;
	}

	public void setFont(Font f) {
		font = f;
	}

	/**
	 * Default draw function
	 * @param g
	 */
	public void draw(Graphics g) {
		g.setColor(Color.decode("#000088"));
		g.fillRect(x, y, width, height);
		g.setColor(Color.YELLOW);
		g.drawRect(x, y, width, height);
		Font prev = g.getFont();
		int wd = g.getFontMetrics().stringWidth(text);
		int size = SML.getSize();
		if (recalc) {
			// Simple loop that resizes the font until it's small enough
			// to fit into the button
			while (wd > width) {
				goodFont = new Font("arial", Font.BOLD, size--);
				g.setFont(goodFont);
				wd = g.getFontMetrics().stringWidth(text);
			}
			recalc = false;
		} else {
			g.setFont(goodFont);
			wd = g.getFontMetrics().stringWidth(text);
		}
		g.drawString(text, x + (width >> 1) - (wd >> 1), y + (height >> 1) - 4 + (g.getFontMetrics().getHeight() >> 1));
		g.setFont(prev);
	}

	/**
	 * Used to check if the mouse cursor is within the bounds of the button
	 * @param mx mouse x
	 * @param my mouse y
	 * @return if the given (x,y) coordinates are in the button
	 */
	public boolean inside(int mx, int my) {
		return (mx >= x && mx <= (x + width)) && (my >= y && my <= (y + height));
	}

}
