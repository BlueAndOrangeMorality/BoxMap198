
package de.ranagazoo.boxmap198;

//import static de.ranagazoo.box.Box2dBuilder.createBody;
//import static de.ranagazoo.box.Box2dBuilder.createChainShape;
//import static de.ranagazoo.box.Box2dBuilder.createFixtureDef;
//import static de.ranagazoo.box.Box2dBuilder.createStaticBodyDef;

import static de.ranagazoo.boxmap198.Config.TS;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
//import com.badlogic.gdx.physics.box2d.FixtureDef;
//import com.badlogic.gdx.physics.box2d.Shape;

public class BoxMap198 extends ApplicationAdapter {

	public static final String TEXTURE_LIBGDX = "data/libgdx.png";
	public static final String TEXTURE_ENTITIES = "data/entities-big.png";
	public static final String TEXTURE_MECHA = "data/mecha32.png";
	public static final String MAP_MAP = "data/map.tmx";

	private OrthographicCamera cameraSprites;
	private OrthographicCamera cameraBox2dDebug;

	private AssetManager assetManager;
	private SpriteBatch batch;

	private Sprite playerSprite;
	private Sprite debugSprite;
	private Animation<TextureRegion> animation;

	private float stateTime;

	private WorldManager worldManager;
	private ShapeRenderer shapeRenderer;

	private TiledMap map;
	private TiledMapRenderer mapRenderer;

	// My Objects
	private Array<BoxEntity> boxEntities;

	private DebugOutput debugOutput;

	@Override
	public void create () {

		assetManager = new AssetManager();
		loadAssets();

		worldManager = new WorldManager();

		cameraSprites = new OrthographicCamera();
		cameraSprites.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cameraSprites.update();

		cameraBox2dDebug = new OrthographicCamera();
		cameraBox2dDebug.setToOrtho(false, Gdx.graphics.getWidth() / TS, Gdx.graphics.getHeight() / TS);
		cameraBox2dDebug.update();

		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		Texture entitiesBigTexture = assetManager.get(TEXTURE_ENTITIES);
		entitiesBigTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		playerSprite = new Sprite(new TextureRegion(entitiesBigTexture, 8 * TS, 1 * TS, TS, TS));
		playerSprite.setSize(TS, TS);
		playerSprite.setOrigin(TS / 2, TS / 2);

		debugSprite = new Sprite(new TextureRegion(entitiesBigTexture, 2 * TS, 0 * TS, TS, TS));
		debugSprite.setSize(TS, TS);
		debugSprite.setOrigin(TS / 2, TS / 2);

		Texture mechaTexture = assetManager.get(TEXTURE_MECHA);
		mechaTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		TextureRegion[] animationFrames = new TextureRegion[5];
		animationFrames[0] = new TextureRegion(mechaTexture, 0, 384, 64, 64);
		animationFrames[1] = new TextureRegion(mechaTexture, 64, 384, 64, 64);
		animationFrames[2] = new TextureRegion(mechaTexture, 128, 384, 64, 64);
		animationFrames[3] = new TextureRegion(mechaTexture, 192, 384, 64, 64);
		animationFrames[4] = new TextureRegion(mechaTexture, 256, 384, 64, 64);
		animation = new Animation<TextureRegion>(0.1f, animationFrames);

		debugOutput = new DebugOutput();

		stateTime = 0f;

		map = assetManager.get(MAP_MAP);
		mapRenderer = new OrthogonalTiledMapRenderer(map, 1f / 1f);
		mapRenderer.setView(cameraSprites);

		// Erzeuge Entities aus der Map
		boxEntities = new Array<BoxEntity>();
		boxEntities.addAll(worldManager.loadEntities(map));

		// TODO Box2dChainShape funktioniert nicht mit 1.9.7

		// Border, könnte man auch noch auslagern
// Shape tempShape;
// FixtureDef tempFixtureDef = null;
//
// borderBody = createBody(world, createStaticBodyDef(2, false, 2, new
// Vector2(16, 2)), "userData");
// tempShape = createChainShape(new Vector2[]{new Vector2(-15f, -1f), new
// Vector2(15f, -1f), new Vector2(15f, 21f), new Vector2(-15f, 21f), new
// Vector2(-15f, -1f)});

		// tempFixtureDef = createFixtureDef(0.0f, 0.4f, 0.1f, tempShape,
		// CATEGORY_SCENERY, MASK_SCENERY);
		// borderBody.createFixture(tempFixtureDef);
		// tempShape.dispose();

	}

	@Override
	public void render () {
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) Gdx.app.exit();

		// Move all entities
		for (BoxEntity boxEntity : boxEntities) {
			boxEntity.move();
		}

		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		mapRenderer.render();

		batch.setProjectionMatrix(cameraSprites.combined);
		batch.begin();

		// Aktuell werden nur Player und Enemies gerendert
// BoxEntities rendern sich nicht mehr selber!
// Sie liefern Position, Drehung, Typ(Player/Entity) und Status(Attack/Idle) zurück.
// Gerendert wird hier anhand der Werte

		for (BoxEntity boxEntity : boxEntities) {
			Vector2 position = boxEntity.getBody().getPosition();
			float angle = boxEntity.getBody().getAngle();
			String type = boxEntity.getType();

			if (type.equals(Config.TYPE_PLAYER1)) {
				playerSprite.setPosition((position.x - 0.5f) * TS, (position.y - 0.5f) * TS);
				playerSprite.setRotation(MathUtils.radiansToDegrees * angle);
				playerSprite.draw(batch);
			} else if (type.equals(Config.TYPE_ENEMY1)) {
				stateTime += Gdx.graphics.getDeltaTime();
				TextureRegion currentFrame = (TextureRegion)animation.getKeyFrame(stateTime, true);
				Sprite s = new Sprite(currentFrame);
				s.setPosition((position.x - 1) * TS, (position.y - 1) * TS);
				s.setRotation(MathUtils.radiansToDegrees * angle + 180);
				s.draw(batch);
			}
		}

		Array<Body> waypoints = worldManager.getWaypointGroup().getWaypoints();

		for (Body body : waypoints) {
			Vector2 position = body.getPosition();
			float angle = body.getAngle();

			debugSprite.setPosition((position.x - 0.5f) * TS, (position.y - 0.5f) * TS);
			debugSprite.setRotation(MathUtils.radiansToDegrees * angle);
			debugSprite.draw(batch);
		}

		// Render debug messages
		debugOutput.render(batch);

		batch.end();

		shapeRenderer.setProjectionMatrix(cameraSprites.combined);
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.polygon(worldManager.getWaypointGroup().getWaypointsRender());
		shapeRenderer.end();

		worldManager.step();
		worldManager.render(cameraBox2dDebug.combined);

		// debugRenderer.render(world, cameraBox2dDebug.combined);
	}

	@Override
	public void dispose () {
		batch.dispose();
		shapeRenderer.dispose();
		assetManager.dispose();
		worldManager.dispose();
	}

// Avoid Enums, use final variables
//
// Avoid Iterators, for-loops are faster
//
// Avoid Global Static classes at all costs, their runtime differs from normal code
//
//

// public Waypoint getWaypoint(int parameter)
// {
// return waypoints.get(parameter);
// }

	public void loadAssets () {
		assetManager.load(TEXTURE_LIBGDX, Texture.class);
		assetManager.load(TEXTURE_ENTITIES, Texture.class);
		assetManager.load(TEXTURE_MECHA, Texture.class);
		assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		assetManager.load(MAP_MAP, TiledMap.class);
		assetManager.finishLoading();
	}

	public AssetManager getAssetManager () {
		return assetManager;
	}

	public Animation<TextureRegion> getAnimation () {
		return animation;
	}
}
