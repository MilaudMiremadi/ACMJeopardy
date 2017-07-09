package jeopardy;

/**
 * Represents a question/answer pair
 * 
 * @author Milaud Miremadi
 *
 */
public class QAPair {

	private String question;
	private String answer;

	// Daily double flag
	private boolean dd;

	public QAPair(String question, String answer) {
		this.question = question;
		this.answer = answer;
	}

	public String getQuestion() {
		return question;
	}

	public String getAnswer() {
		return answer;
	}

	public boolean isDailyDouble() {
		return dd;
	}

	public void setDailyDouble(boolean dd) {
		this.dd = dd;
	}

}
