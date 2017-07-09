package jeopardy;

public class GridButton extends Button {

	private QAPair pair;

	public GridButton(QAPair qa, int value) {
		super();
		pair = qa;
		width = 128;
		height = 128;
		text = Integer.toString(value) + (value == 1 ? " bit" : " bits");
	}

	public QAPair getQA() {
		return pair;
	}

}
