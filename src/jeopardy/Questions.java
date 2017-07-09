package jeopardy;

import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;

public class Questions {

	static final int ERROR_EOF = 0;
	static final int ERROR_FNF = 1;

	static final int N_CATEGORIES = 5;
	static final int N_QS_PER_COL = 5;

	private String[] categories;

	private QAPair[] pairs;

	private Questions() {
		categories = new String[N_CATEGORIES];
		pairs = new QAPair[N_QS_PER_COL * N_QS_PER_COL];
	}

	/**
	 * Load a list of categories and questions from a text file
	 * @param name filepath
	 * @return the new Questions object
	 */
	static Questions load(String name) {
		try {
			File file = new File(name);

			Scanner in = new Scanner(file);

			Questions questions = new Questions();

			int cline = 0;
			// read categories
			for (int i = 0; i < N_CATEGORIES; i++) {
				if (in.hasNextLine()) {
					String l = in.nextLine();
					cline++;

					// skip comments
					while (l.startsWith("#")) {
						l = in.nextLine();
						cline++;
					}
					questions.categories[i] = l;
				} else {
					// no more lines? invalid end of file
					error(ERROR_EOF, cline);
					in.close();
					return null;
				}
			}

			// read grid of questions
			for (int q = 0; q < N_QS_PER_COL; q++) {
				for (int c = 0; c < N_CATEGORIES; c++) {
					if (in.hasNextLine()) {
						String an = in.nextLine();
						cline++;
						String qu;

						// skip comments
						while (an.startsWith("#")) {
							an = in.nextLine();
							cline++;
						}

						if (in.hasNextLine()) {
							qu = in.nextLine();
							cline++;

							// skip comments
							while (qu.startsWith("#")) {
								qu = in.nextLine();
								cline++;
							}
						} else {
							// no more lines? invalid end of file
							error(ERROR_EOF, cline);
							in.close();
							return null;
						}

						questions.pairs[c + q * N_CATEGORIES] = new QAPair(an, qu);
					} else {
						// no more lines? invalid end of file
						error(ERROR_EOF, cline);
						in.close();
						return null;
					}
				}
			}

			in.close();
			return questions;
		} catch (FileNotFoundException e) {
			// couldn't find the file
			error(ERROR_FNF, name);
			return null;
		}
	}

	public String getCategory(int col) {
		return categories[col];
	}

	public QAPair getQuestion(int category, int row) {
		return pairs[category + row * N_CATEGORIES];
	}

	private static void error(int code, Object value) {
		switch (code) {
			case ERROR_EOF:
				System.err.println("Error: Early EOF: " + value);
				break;
			case ERROR_FNF:
				System.err.println("Error: File not found: " + value);
				break;
			default:
				break;
		}
	}

}
