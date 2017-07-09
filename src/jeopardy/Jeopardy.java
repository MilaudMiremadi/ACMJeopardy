package jeopardy;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JOptionPane;

import lib.DirectDisplay;
import lib.EUTDevice;
import lib.EUTInput;
import lib.EUTProgram;

/**
 * A jeopardy game created for the ACM meeting
 * 
 * @author Milaud Miremadi
 *
 */
public class Jeopardy implements EUTProgram {

	// Screen dimensions
	static final int WD = 768;
	static final int HT = 768;

	private int dailyDoubles = 0;

	private Questions questions;

	private Button nextButton;
	private Button loadButton;

	private boolean questionUp = false;
	private boolean answerUp = false;

	private Button[] categories;
	private GridButton[] gridButtons;

	private DirectDisplay display = new DirectDisplay(WD, HT);

	private QAPair curq;

	private float scale_x, scale_y;

	private boolean redraw = false;

	public static void main(String[] args) {
		EUTDevice.eut_create(WD, HT);
		EUTDevice.eut_start(new Jeopardy());
	}

	@Override
	public void eut_init() {
		EUTDevice.eut_title("Jeopardy");
		nextButton = new Button("Next");
		nextButton.x = 192;
		nextButton.y = HT - 48;
		nextButton.width = 128;
		nextButton.height = 32;
		loadButton = new Button("Load");
		loadButton.x = 448;
		loadButton.y = HT - 48;
		loadButton.width = 128;
		loadButton.height = 32;
		redraw = true;
	}

	@Override
	public void eut_update() {
		int x = (int) (EUTInput.mouse_x() * scale_x);
		int y = (int) (EUTInput.mouse_y() * scale_y);
		boolean down = EUTInput.mouse_left_button_pressed();
		if (gridButtons != null && !(questionUp || answerUp)) {
			for (int i = 0; i < gridButtons.length; i++) {
				GridButton b = gridButtons[i];
				if (b.inside(x, y) && down) {
					if (!b.getText().equals("-")) {
						pick(b);
					} else {
						JOptionPane.showMessageDialog(null, "This question has been answered already.");
					}
				}
			}
		}
		if (loadButton.inside(x, y) && down) {
			if ((questionUp || answerUp)) {
				JOptionPane.showMessageDialog(null, "Please finish the question first.");
			} else {
				String pathToFile = JOptionPane.showInputDialog("Type the relative path to the questions file: ", "questions/questions.txt");

				if (pathToFile != null) {
					questions = Questions.load(pathToFile);

					if (questions != null) {
						createBoard();
					}
				}
			}
		}
		if (nextButton.inside(x, y) && down) {
			if (questionUp && !answerUp) {
				answerUp = true;
			} else if (questionUp && answerUp) {
				questionUp = answerUp = false;
			} else {
				JOptionPane.showMessageDialog(null, "Please select a question first.");
			}
			redraw = true;
		}
	}

	private void createBoard() {
		categories = new Button[Questions.N_CATEGORIES];
		for (int i = 0; i < Questions.N_CATEGORIES; i++) {
			String c = questions.getCategory(i);
			categories[i] = new Button(c);
			Button cat = categories[i];
			cat.setFont(Button.SML);
			cat.x = 64 | (i << 7);
			cat.y = 16;
			cat.width = 128;
			cat.height = 32;
		}

		int value = 1;
		gridButtons = new GridButton[Questions.N_CATEGORIES * Questions.N_QS_PER_COL];

		for (int q = 0; q < Questions.N_QS_PER_COL; q++) {
			for (int c = 0; c < Questions.N_CATEGORIES; c++) {
				int idx = c + q * Questions.N_CATEGORIES;
				gridButtons[idx] = new GridButton(questions.getQuestion(c, q), value);
				Button b = gridButtons[idx];
				b.x = 64 | (c << 7);
				b.y = 64 | (q << 7);
			}
			value <<= 1;
		}
		dailyDoubles = 0;
		loop: while (dailyDoubles != 2) {
			for (int q = 3; q < Questions.N_QS_PER_COL; q++) {
				for (int c = 0; c < Questions.N_CATEGORIES; c++) {
					boolean chance = (Math.random() * 100) > 95;
					if (chance) {
						gridButtons[c + q * Questions.N_CATEGORIES].getQA().setDailyDouble(true);
						dailyDoubles++;
						if (dailyDoubles >= 2) {
							break loop;
						}
					}
				}
			}
		}
		redraw = true;
	}

	private void pick(GridButton button) {
		curq = button.getQA();
		button.setText("-");
		questionUp = true;
		redraw = true;
	}

	@Override
	public void eut_render() {
		// Rendering done through Java Graphics API
		if (redraw) {
			Graphics g = display.getImage().getGraphics();
			g.setFont(Button.NORM);
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, WD, HT);
			if (questionUp) {
				if (curq.isDailyDouble()) {
					g.setColor(Color.decode("#000044"));
					g.fillRect(0, 0, WD, HT);
				}
				g.setColor(Color.YELLOW);
				String question = curq.getQuestion();
				String answer = curq.getAnswer();
				if (curq.isDailyDouble()) {
					g.setFont(Button.BIG);
					g.drawString("DAILY DOUBLE!", (WD >> 1) - (g.getFontMetrics().stringWidth("DAILY DOUBLE!") >> 1), (HT >> 1) - 128);
					g.setFont(Button.NORM);
				}
				int wd = g.getFontMetrics().stringWidth(question);
				if (wd > Jeopardy.WD - 64) {
					String p1 = question.substring(0, question.length() - find_cutoff(question, g));
					String p2 = question.substring(question.length() - find_cutoff(question, g), question.length());
					g.drawString(p1, (WD >> 1) - (g.getFontMetrics().stringWidth(p1) >> 1), (HT >> 1) - 64);
					g.drawString(p2, (WD >> 1) - (g.getFontMetrics().stringWidth(p2) >> 1), (HT >> 1));
				} else {
					g.drawString(question, (WD >> 1) - (wd >> 1), HT >> 1);
				}
				if (answerUp) {
					wd = g.getFontMetrics().stringWidth(answer);
					g.setColor(Color.GREEN);
					if (wd > Jeopardy.WD - 64) {
						String p1 = answer.substring(0, answer.length() - find_cutoff(answer, g));
						String p2 = answer.substring(answer.length() - find_cutoff(answer, g), answer.length());
						g.drawString(p1, (WD >> 1) - (g.getFontMetrics().stringWidth(p1) >> 1), (HT >> 1) + 64);
						g.drawString(p2, (WD >> 1) - (g.getFontMetrics().stringWidth(p2) >> 1), (HT >> 1) + 128);
					} else {
						g.drawString(answer, (WD >> 1) - (wd >> 1), (HT >> 1) + 64);
					}
				}
			} else {
				if (gridButtons != null) {
					for (int i = 0; i < categories.length; i++) {
						Button b = categories[i];
						b.draw(g);
					}
					for (int i = 0; i < gridButtons.length; i++) {
						GridButton b = gridButtons[i];
						b.draw(g);
					}
				}
			}
			nextButton.draw(g);
			loadButton.draw(g);
			EUTDevice.target_image(display.getImage());
		}
		redraw = false;
	}

	private static int find_cutoff(String s, Graphics g) {
		int i = 0;
		while (!s.endsWith(" ") || g.getFontMetrics().stringWidth(s) > Jeopardy.WD) {
			s = s.substring(0, s.length() - 1);
			i++;
		}
		return i;
	}

	@Override
	public void eut_resize(int wnd_width, int wnd_height) {
		scale_x = WD / (float) wnd_width;
		scale_y = HT / (float) wnd_height;
	}

	@Override
	public void eut_exit() {

	}

}
