package com.zach.mapgen.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.zach.mapgen.MapGenerationCore;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = (int) (1+Math.pow(2, 10));
		config.height = (int) (1+Math.pow(2, 10));
		new LwjglApplication(new MapGenerationCore(), config);
	}
}
