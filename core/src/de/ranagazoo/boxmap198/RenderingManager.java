
package de.ranagazoo.boxmap198;

import static de.ranagazoo.boxmap198.Config.TS;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;

public class RenderingManager {

	private Sprite playerSprite;
	private Sprite debugSprite;
	private Animation<TextureRegion> animation;
	private SpriteBatch batch;
	
	public RenderingManager (AssetManager assetManager, SpriteBatch batch) {

		this.batch = batch;
		
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
		
	}
	
	//TODO: Eine Funktion für alle, die Type auch noch entgegennimmt
	
	public void renderPlayerSprite(Vector2 position, float angle){
		playerSprite.setPosition((position.x - 0.5f) * TS, (position.y - 0.5f) * TS);
		playerSprite.setRotation(MathUtils.radiansToDegrees * angle);
		playerSprite.draw(batch);
	}
	
	public void renderEnemy(Vector2 position, float angle, float stateTime){
		TextureRegion currentFrame = (TextureRegion)animation.getKeyFrame(stateTime, true);
		Sprite s = new Sprite(currentFrame);
		s.setPosition((position.x - 1) * TS, (position.y - 1) * TS);
		s.setRotation(MathUtils.radiansToDegrees * angle + 180);
		s.draw(batch);
	}
	
	public void renderDebugSprite(Vector2 position, float angle){
		debugSprite.setPosition((position.x - 0.5f) * TS, (position.y - 0.5f) * TS);
		debugSprite.setRotation(MathUtils.radiansToDegrees * angle);
		debugSprite.draw(batch);
	}
	
}
