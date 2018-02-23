package de.ranagazoo.boxmap198.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import de.ranagazoo.boxmap198.BoxMap198;

public class BoxMap198DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
	    config.title = "BoxMap";
	    config.width = 1024;
	    config.height = 768;
	    new LwjglApplication(new BoxMap198(), config);
	}
}
