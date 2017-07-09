package lib;

import java.awt.Graphics;
import java.awt.Image;

public final class EUTDevice {

	private static final int DEFAULT_REFRESH_RATE = 70;
	private static final int MIN_SLEEP_TIME = 1;
	private static final int SECOND_MILLIS = 1000;

	static String lib_name = "EUT-LE";

	static String version = "2.3";

	private static EUTProgram program;

	private static Image target_image;
	static boolean update_graphics;

	private static int fps;
	private static int frame_time;

	static boolean req_resize = false;
	static int req_width = 0;
	static int req_height = 0;

	private static boolean created = false;

	private static boolean running = false;

	private static int refresh_rate = DEFAULT_REFRESH_RATE;
	private static int period = (int) ((1000f / refresh_rate) + 0.5f);

	private EUTDevice() {

	}

	public static final void eut_create(int wnd_width, int wnd_height) {
		if (created) {
			System.out.println(lib_name + " [EUTDevice]: EUTDevice already created");
			System.exit(0);
			return;
		}
		EUTWindow.wnd_create(wnd_width, wnd_height);
		System.out.println(lib_name + " [EUTDevice]: EUTDevice created");
		created = true;
	}

	public static final void eut_start(EUTProgram program) {
		if (!created) {
			System.out.println(lib_name + " [EUTDevice]: EUTDevice not created");
			System.exit(0);
			return;
		}
		EUTDevice.program = program;
		System.out.println(lib_name + " [EUTDevice]: starting v" + version);
		run();
	}

	private static final void run() {
		Graphics g = EUTWindow.wnd_get_graphics();
		program.eut_init();

		int frames = 0;
		long next_fps_upd = uclock() + SECOND_MILLIS;
		long time;

		long last_time = uclock();

		int updates;

		running = true;
		try {
			EUTInput.mouse_reset();
			EUTInput.mouse_reset_active();
			while (running) {
				long dt = last_time - uclock();

				if (MIN_SLEEP_TIME > dt) {
					dt = MIN_SLEEP_TIME;
				}

				updates = 0;
				sleep(dt);

				time = uclock();
				while (updates < 10 && (updates < 1 || (time > last_time))) {
					updates++;
					last_time += period;
				}

				if (time > last_time) {
					last_time = time;
				}

				if (req_resize) {
					int width = req_width;
					int height = req_height;
					if (width < EUTWindow.MIN_DIMENSION) {
						width = EUTWindow.MIN_DIMENSION;
					}
					if (height < EUTWindow.MIN_DIMENSION) {
						height = EUTWindow.MIN_DIMENSION;
					}
					EUTWindow.wnd_set_dimensions(width, height);
					program.eut_resize(width, height);
					req_resize = false;
					continue;
				}

				if (update_graphics) {
					g = EUTWindow.wnd_get_graphics();
					update_graphics = false;
				}

				for (int i = 0; i < updates; i++) {
					program.eut_update();
					EUTInput.mouse_reset();
				}

				time = uclock();

				long start_frame = uclock();

				program.eut_render();

				blit(g);

				frame_time += (uclock() - start_frame);
				frame_time >>= 1;

				frames++;

				if (time >= next_fps_upd) {
					fps = frames;
					frames = 0;
					next_fps_upd = time + SECOND_MILLIS;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		terminate();
	}

	private static final void blit(Graphics g) {
		g.drawImage(target_image, 0, 0, EUTWindow.wnd_get_width(), EUTWindow.wnd_get_height(), null);
		EUTWindow.wnd_sync_display();
	}

	public static final void eut_stop() {
		if (!created) {
			System.out.println(lib_name + " [EUTDevice]: EUTDevice not created");
			System.exit(0);
			return;
		}
		running = false;
	}

	private static final void terminate() {
		System.out.println(lib_name + " [EUTDevice]: closing");

		program.eut_exit();

		sleep(SECOND_MILLIS);

		EUTWindow.wnd_destroy();

		System.out.println(lib_name + " [EUTDevice]: exited");

		created = false;
	}

	public static final void eut_refresh_rate(int hz) {
		if (!created) {
			System.out.println(lib_name + " [EUTDevice]: EUTDevice not created");
			System.exit(0);
			return;
		}
		refresh_rate = hz;
		period = (int) ((1000f / hz) + 0.5f);
	}

	public static final int eut_width() {
		if (!created) {
			System.out.println(lib_name + " [EUTDevice]: EUTDevice not created");
			System.exit(0);
			return -1;
		}
		return EUTWindow.wnd_get_width();
	}

	public static final int eut_height() {
		if (!created) {
			System.out.println(lib_name + " [EUTDevice]: EUTDevice not created");
			System.exit(0);
			return -1;
		}
		return EUTWindow.wnd_get_height();
	}

	public static final int eut_fps() {
		if (!created) {
			System.out.println(lib_name + " [EUTDevice]: EUTDevice not created");
			System.exit(0);
			return -1;
		}
		return fps;
	}

	public static final int eut_frame_time() {
		if (!created) {
			System.out.println(lib_name + " [EUTDevice]: EUTDevice not created");
			System.exit(0);
			return -1;
		}
		return frame_time;
	}

	public static final void eut_resizable(boolean resizable) {
		if (!created) {
			System.out.println(lib_name + " [EUTDevice]: EUTDevice not created");
			System.exit(0);
			return;
		}
		EUTWindow.wnd_set_resizable(resizable);
	}

	public static final void eut_title(String title) {
		if (!created) {
			System.out.println(lib_name + " [EUTDevice]: EUTDevice not created");
			System.exit(0);
			return;
		}
		EUTWindow.wnd_set_title(title);
	}

	public static final boolean eut_created() {
		return created;
	}

	public static final void target_image(Image target) {
		target_image = target;
	}

	private static final long uclock() {
		return System.currentTimeMillis();
	}

	private static final void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}