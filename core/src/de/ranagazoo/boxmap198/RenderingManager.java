
package de.ranagazoo.boxmap198;

import static de.ranagazoo.boxmap198.Config.TS;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class RenderingManager implements Disposable {

	private SpriteBatch batch;
	private float stateTime;
	private BitmapFont font;
	
	private Sprite playerSprite;
	private Sprite debugSprite;
	private Animation<TextureRegion> animation;

	private ShapeRenderer shapeRenderer;
	private CameraManager cameraManager;

	public RenderingManager (AssetManager assetManager, CameraManager cameraManager) {

		batch = new SpriteBatch();
		stateTime = 0f;
		font = new BitmapFont(Gdx.files.internal("fonts/vdj8pxwhite.fnt"), false);
		this.cameraManager = cameraManager;
		
		Texture entitiesBigTexture = assetManager.get(Config.TEXTURE_ENTITIES);
		entitiesBigTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		playerSprite = new Sprite(new TextureRegion(entitiesBigTexture, 8 * TS, 1 * TS, TS, TS));
		playerSprite.setSize(TS, TS);
		playerSprite.setOrigin(TS / 2, TS / 2);

		debugSprite = new Sprite(new TextureRegion(entitiesBigTexture, 2 * TS, 0 * TS, TS, TS));
		debugSprite.setSize(TS, TS);
		debugSprite.setOrigin(TS / 2, TS / 2);

		Texture mechaTexture = assetManager.get(Config.TEXTURE_MECHA);
		mechaTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		TextureRegion[] animationFrames = new TextureRegion[5];
		animationFrames[0] = new TextureRegion(mechaTexture, 0, 384, 64, 64);
		animationFrames[1] = new TextureRegion(mechaTexture, 64, 384, 64, 64);
		animationFrames[2] = new TextureRegion(mechaTexture, 128, 384, 64, 64);
		animationFrames[3] = new TextureRegion(mechaTexture, 192, 384, 64, 64);
		animationFrames[4] = new TextureRegion(mechaTexture, 256, 384, 64, 64);
		animation = new Animation<TextureRegion>(0.1f, animationFrames);
		
		shapeRenderer = new ShapeRenderer();
	}

//	//Matrix4 projectionMatrix
//	public void setProjectionMatrix () {
//		//MUSS immer wieder in render aufgeruden werden
//		//https://stackoverflow.com/questions/33703663/understanding-the-libgdx-projection-matrix
//		batch.setProjectionMatrix(cameraManager.getCameraSprites().combined);
//		shapeRenderer.setProjectionMatrix(cameraManager.getCameraSprites().combined);
//	}

	public void begin () {
		batch.begin();
	}

	public void update () {
		stateTime += Gdx.graphics.getDeltaTime();

		//setProjectionMatrix MUSS immer wieder in render aufgeruden werden
		//https://stackoverflow.com/questions/33703663/understanding-the-libgdx-projection-matrix
		batch.setProjectionMatrix(cameraManager.getCameraSprites().combined);
		shapeRenderer.setProjectionMatrix(cameraManager.getCameraSprites().combined);
	}

	public void end () {
		batch.end();
	}

	public void renderDebugOutput () {
		batch.begin();
		
		//debugOutput.render(batch);
   //in Debug-Ausgabe wird der dann noch in ° geändert, wegen der Lesbarkeit
	// float normalizedDifferenceRotationDeg = MathUtils.radiansToDegrees *normalizedDifferenceRotationRad;
	//
	// font.draw(batch, "totalRotation: "+(MathUtils.radiansToDegrees *totalRotation), 50, 108);
	// font.draw(batch, "normalizedDifferenceRotationDeg: "+normalizedDifferenceRotationDeg, 50, 92);
	// font.draw(batch, "currentTriangleRotationDeg: "+(MathUtils.radiansToDegrees * enemyBody.getAngle()), 50, 76);
	// font.draw(batch, "triangle-inertia: "+enemyBody.getInertia(), 50, 60);
			font.draw(batch, "BoxMap ----- FPS: " + Gdx.graphics.getFramesPerSecond(), 50, 60);
		batch.end();
	}

	// Aktuell werden nur Player und Enemies gerendert
// BoxEntities rendern sich nicht mehr selber!
// Sie liefern Position, Drehung, Typ(Player/Entity) und Status(Attack/Idle) zurück.
// Gerendert wird hier anhand der Werte
	public void renderBoxEntities(Array<BoxEntity> boxEntities){
		batch.begin();
		
		for (BoxEntity boxEntity : boxEntities) {
			Vector2 position = boxEntity.getBody().getPosition();
			float angle = boxEntity.getBody().getAngle();
			String type = boxEntity.getType();

			if (type.equals(Config.TYPE_PLAYER1)) {
				renderPlayerSprite(position, angle);
//				playerPosition = position;
			} else if (type.equals(Config.TYPE_ENEMY1)) {
				renderEnemy(position, angle);
			}
		}		
      batch.end();
	}
	
	public void renderWaypoints(Array<Body> waypoints){
		batch.begin();
		
		for (Body body : waypoints) {
			Vector2 position = body.getPosition();
			float angle = body.getAngle();

			renderDebugSprite(position, angle);
		}

      batch.end();
	}
	
	
	
	
	// TODO: Eine Funktion für alle, die Type auch noch entgegennimmt

	public void renderPlayerSprite (Vector2 position, float angle) {
		playerSprite.setPosition((position.x - 0.5f) * TS, (position.y - 0.5f) * TS);
		playerSprite.setRotation(MathUtils.radiansToDegrees * angle);
		playerSprite.draw(batch);
	}

	public void renderEnemy (Vector2 position, float angle) {
		TextureRegion currentFrame = (TextureRegion)animation.getKeyFrame(stateTime, true);
		Sprite s = new Sprite(currentFrame);
		s.setPosition((position.x - 1) * TS, (position.y - 1) * TS);
		s.setRotation(MathUtils.radiansToDegrees * angle + 180);
		s.draw(batch);
	}

	public void renderDebugSprite (Vector2 position, float angle) {
		debugSprite.setPosition((position.x - 0.5f) * TS, (position.y - 0.5f) * TS);
		debugSprite.setRotation(MathUtils.radiansToDegrees * angle);
		debugSprite.draw(batch);
	}

	public void renderLine(WorldManager worldManager)
	{
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.polygon(worldManager.getWaypointGroup().getWaypointsRender());
		shapeRenderer.end();
	}
	
	
	
	@Override
	public void dispose () {
		batch.dispose();
		shapeRenderer.dispose();
	}

}
