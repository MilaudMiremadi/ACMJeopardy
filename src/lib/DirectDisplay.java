package lib;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public final class DirectDisplay {

	private BufferedImage image;
	public int[] memory;

	public int width;
	public int height;

	public DirectDisplay(int w, int h) {
		width = w;
		height = h;

		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device = env.getDefaultScreenDevice();
		GraphicsConfiguration config = device.getDefaultConfiguration();

		image = config.createCompatibleImage(w, h);
		memory = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	}

	public Image getImage() {
		return image;
	}

}