package lib;

public interface EUTProgram {

	void eut_init();

	void eut_update();

	void eut_render();

	void eut_resize(int wnd_width, int wnd_height);

	void eut_exit();

}