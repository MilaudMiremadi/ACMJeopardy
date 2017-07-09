package lib;

import java.awt.AWTEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

public final class EUTInput {

	private static final int MOUSE_BUTTON_NONE = 0;
	private static final int MOUSE_BUTTON_LEFT = 1;
	private static final int MOUSE_BUTTON_MIDDLE = 2;
	private static final int MOUSE_BUTTON_RIGHT = 4;

	private static int pos_x = 0;
	private static int pos_y = 1;
	private static int active_buttons = 2;
	private static int pressed_buttons = 3;

	private EUTInput() {

	}

	public static final void mouse_reset() {
		pressed_buttons = MOUSE_BUTTON_NONE;
	}

	public static final void mouse_reset_active() {
		active_buttons = MOUSE_BUTTON_NONE;
	}

	private static final void mouse_position(int x, int y) {
		pos_x = x;
		pos_y = y;
	}

	private static final void mouse_consume(MouseEvent e) {
		int eventX = e.getX();
		int eventY = e.getY();

		switch (e.getID()) {
			case MouseEvent.MOUSE_MOVED:
			case MouseEvent.MOUSE_DRAGGED:
				mouse_position(eventX, eventY);
				break;
			case MouseEvent.MOUSE_PRESSED:
				active_buttons |= get_flag(e.getButton());
				pressed_buttons |= get_flag(e.getButton());
				mouse_position(eventX, eventY);
				break;
			case MouseEvent.MOUSE_RELEASED:
				active_buttons &= ~get_flag(e.getButton());
				break;
			case MouseEvent.MOUSE_ENTERED:
				break;
			case MouseEvent.MOUSE_EXITED:
				break;
			default:
				break;
		}
		e.consume();
	}

	private static final void component_consume(ComponentEvent e) {
		EUTDevice.update_graphics = true;
		switch (e.getID()) {
			case ComponentEvent.COMPONENT_RESIZED:
			case ComponentEvent.COMPONENT_MOVED:
				EUTDevice.req_resize = true;
				EUTDevice.req_width = EUTWindow.wnd_get_size().width;
				EUTDevice.req_height = EUTWindow.wnd_get_size().height;
				break;
			default:
				break;
		}
	}

	private static final void window_consume(WindowEvent e) {
		switch (e.getID()) {
			case WindowEvent.WINDOW_CLOSING:
				EUTDevice.eut_stop();
				break;
			default:
				break;
		}
	}

	private static final int get_flag(int button) {
		switch (button) {
			case MouseEvent.BUTTON1:
				return MOUSE_BUTTON_LEFT;
			case MouseEvent.BUTTON2:
				return MOUSE_BUTTON_MIDDLE;
			case MouseEvent.BUTTON3:
				return MOUSE_BUTTON_RIGHT;
			default:
				return -1;
		}
	}

	public static final int mouse_x() {
		return pos_x;
	}

	public static final int mouse_y() {
		return pos_y;
	}

	public static final boolean mouse_left_button_active() {
		return (active_buttons & MOUSE_BUTTON_LEFT) == MOUSE_BUTTON_LEFT;
	}

	public static final boolean mouse_middle_button_active() {
		return (active_buttons & MOUSE_BUTTON_MIDDLE) == MOUSE_BUTTON_MIDDLE;
	}

	public static final boolean mouse_right_button_active() {
		return (active_buttons & MOUSE_BUTTON_RIGHT) == MOUSE_BUTTON_RIGHT;
	}

	public static final boolean mouse_left_button_pressed() {
		return (pressed_buttons & MOUSE_BUTTON_LEFT) == MOUSE_BUTTON_LEFT;
	}

	public static final boolean mouse_middle_button_pressed() {
		return (pressed_buttons & MOUSE_BUTTON_MIDDLE) == MOUSE_BUTTON_MIDDLE;
	}

	public static final boolean mouse_right_button_pressed() {
		return (pressed_buttons & MOUSE_BUTTON_RIGHT) == MOUSE_BUTTON_RIGHT;
	}

	static void process_event(AWTEvent e) {
		if (e instanceof MouseEvent) {
			mouse_consume((MouseEvent) e);
		} else if (e instanceof WindowEvent) {
			window_consume((WindowEvent) e);
		} else if (e instanceof ComponentEvent) {
			component_consume((ComponentEvent) e);
		}
	}

}
