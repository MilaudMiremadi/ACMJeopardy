package lib;

import java.awt.AWTEvent;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.font.TextHitInfo;
import java.awt.im.InputMethodRequests;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

final class EUTWindow {

	static final int MIN_DIMENSION = 0x80;

	static EUTCanvas canvas;
	static EUTFrame wind;

	private static int width;
	private static int height;

	private static boolean closed;

	private EUTWindow() {

	}

	private static class EUTFrame extends Frame {

		private static final long serialVersionUID = 6481015990683298541L;

		EUTFrame() {
			super();
			enableEvents(AWTEvent.WINDOW_EVENT_MASK);
			enableEvents(AWTEvent.WINDOW_FOCUS_EVENT_MASK);
			enableEvents(AWTEvent.WINDOW_STATE_EVENT_MASK);
			enableEvents(AWTEvent.COMPONENT_EVENT_MASK);
		}

		@Override
		public void processEvent(AWTEvent e) {
			EUTInput.process_event(e);
			super.processEvent(e);
		}

		void destroy() {
			disableEvents(AWTEvent.WINDOW_EVENT_MASK);
			disableEvents(AWTEvent.WINDOW_FOCUS_EVENT_MASK);
			disableEvents(AWTEvent.WINDOW_STATE_EVENT_MASK);
			disableEvents(AWTEvent.COMPONENT_EVENT_MASK);
			dispose();
		}

	}

	private static class EUTCanvas extends Canvas implements InputMethodRequests {

		private static final long serialVersionUID = -4952262459876661617L;

		private final InputMethodRequests inputMethodRequests;

		public EUTCanvas() {
			super();
			if (System.getProperty("os.name").toLowerCase().startsWith("mac")) {
				inputMethodRequests = this;
			} else {
				inputMethodRequests = null;
				enableInputMethods(false);
			}
			enableEvents(AWTEvent.COMPONENT_EVENT_MASK);
			enableEvents(AWTEvent.MOUSE_EVENT_MASK);
			enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
		}

		@Override
		public void processEvent(AWTEvent e) {
			EUTInput.process_event(e);
			super.processEvent(e);
		}

		void destroy() {
			disableEvents(AWTEvent.COMPONENT_EVENT_MASK);
			disableEvents(AWTEvent.MOUSE_EVENT_MASK);
			disableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
		}

		@Override
		public InputMethodRequests getInputMethodRequests() {
			return inputMethodRequests;
		}

		@Override
		public Rectangle getTextLocation(TextHitInfo textHitInfo) {
			return new Rectangle(-0x8000, -0x8000, 0, 0);
		}

		@Override
		public TextHitInfo getLocationOffset(int x, int y) {
			return null;
		}

		@Override
		public int getInsertPositionOffset() {
			return 0;
		}

		@Override
		public AttributedCharacterIterator getCommittedText(int beginIndex, int endIndex, AttributedCharacterIterator.Attribute[] attributes) {
			return null;
		}

		@Override
		public int getCommittedTextLength() {
			return 0;
		}

		@Override
		public AttributedCharacterIterator cancelLatestCommittedText(AttributedCharacterIterator.Attribute[] attributes) {
			return null;
		}

		private static final AttributedCharacterIterator EMPTY_TEXT = (new AttributedString("")).getIterator();

		@Override
		public AttributedCharacterIterator getSelectedText(AttributedCharacterIterator.Attribute[] attributes) {
			return EMPTY_TEXT;
		}
	}

	static void wnd_create(int width, int height) {
		canvas = new EUTCanvas();
		EUTWindow.width = width;
		EUTWindow.height = height;
		wnd_set_size(width, height);
		canvas.setMinimumSize(new Dimension(MIN_DIMENSION, MIN_DIMENSION));
		canvas.setIgnoreRepaint(true);
		wind = new EUTFrame();
		wnd_set_title(EUTDevice.lib_name);
		wind.setUndecorated(false);
		wnd_set_resizable(true);
		canvas.setFocusTraversalKeysEnabled(false);
		canvas.setFocusable(true);
		Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
		wind.setLocation(center.x - (width >> 1), center.y - (height >> 1));
		canvas.setVisible(true);
		wind.setVisible(true);
		wind.toFront();
		wind.add(canvas);
		wind.pack();
		canvas.requestFocus();
		canvas.requestFocusInWindow();
	}

	static final void wnd_sync_display() {
		Toolkit.getDefaultToolkit().sync();
	}

	static final void wnd_set_title(String t) {
		wind.setTitle(t);
	}

	static final Graphics wnd_get_graphics() {
		return canvas.getGraphics();
	}

	static final int wnd_get_x() {
		return canvas.getX();
	}

	static final int wnd_get_y() {
		return canvas.getY();
	}

	static final int wnd_get_width() {
		return width;
	}

	static final int wnd_get_height() {
		return height;
	}

	static final Dimension wnd_get_size() {
		return canvas.getSize();
	}

	static final void wnd_set_dimensions(int width, int height) {
		EUTWindow.width = width;
		EUTWindow.height = height;
	}

	static final void wnd_set_size(int width, int height) {
		EUTWindow.width = width;
		EUTWindow.height = height;
		canvas.setSize(width, height);
	}

	static final void wnd_set_resizable(boolean resizable) {
		wind.setResizable(resizable);
	}

	static void wnd_destroy() {
		if (!closed) {
			canvas.destroy();
			wind.destroy();
			closed = true;
		}
	}

}
