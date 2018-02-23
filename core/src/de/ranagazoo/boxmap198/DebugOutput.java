
package de.ranagazoo.boxmap198;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class DebugOutput {

// nur für Debugtest, verwendet auch Spritebatch
	private BitmapFont font;

	public DebugOutput () {
		// nur für Debugtest, verwendet auch Spritebatch
		font = new BitmapFont(Gdx.files.internal("fonts/vdj8pxwhite.fnt"), false);

	}

	public void render (SpriteBatch batch) {

		// in Debug-Ausgabe wird der dann noch in ° geändert, wegen der Lesbarkeit
// float normalizedDifferenceRotationDeg = MathUtils.radiansToDegrees *normalizedDifferenceRotationRad;
//
// font.draw(batch, "totalRotation: "+(MathUtils.radiansToDegrees *totalRotation), 50, 108);
// font.draw(batch, "normalizedDifferenceRotationDeg: "+normalizedDifferenceRotationDeg, 50, 92);
// font.draw(batch, "currentTriangleRotationDeg: "+(MathUtils.radiansToDegrees * enemyBody.getAngle()), 50, 76);
// font.draw(batch, "triangle-inertia: "+enemyBody.getInertia(), 50, 60);
		font.draw(batch, "BoxMap ----- FPS: " + Gdx.graphics.getFramesPerSecond(), 50, 60);
	}
}
