
package de.ranagazoo.boxmap198;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

//Avoid Enums, use final variables
//Avoid Iterators, for-loops are faster
//Avoid Global Static classes at all costs, their runtime differs from normal code


public class BoxMap198 extends ApplicationAdapter {


	private CameraManager cameraManager;
	private AssetManager assetManager;
	private RenderingManager renderingManager;
	private WorldManager worldManager;
//	private ShapeRenderer shapeRenderer;

	private TiledMap map;
	private TiledMapRenderer mapRenderer;

	// My Objects
	private Array<BoxEntity> boxEntities;

	@Override
	public void create () {

		cameraManager = new CameraManager();

		assetManager = new AssetManager();
		loadAssets();

		worldManager = new WorldManager();

		renderingManager = new RenderingManager(assetManager, cameraManager);


		map = assetManager.get(Config.MAP_MAP);
		mapRenderer = new OrthogonalTiledMapRenderer(map, 1f / 1f);
		mapRenderer.setView(cameraManager.getCameraSprites());

		// Erzeuge Entities aus der Map
		boxEntities = new Array<BoxEntity>();
		boxEntities.addAll(worldManager.loadEntities(map));
	}

	@Override
	public void render () {
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) Gdx.app.exit();

		Vector2 playerPosition = new Vector2(0, 0);
		
		// Find Player Position, Move all entities
		for (BoxEntity boxEntity : boxEntities) {
			if (boxEntity.getType().equals(Config.TYPE_PLAYER1)) {
				playerPosition = boxEntity.getBody().getPosition();
			}
			boxEntity.move();
		}

		TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(0);

		Vector2 mapSize = new Vector2(layer.getWidth(), layer.getHeight());
		

		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// TODO: Maprenderer auslagern?
		mapRenderer.setView(cameraManager.getCameraSprites());
		mapRenderer.render();

		renderingManager.update();
		
		
		renderingManager.renderBoxEntities(boxEntities);

		renderingManager.renderWaypoints(worldManager.getWaypointGroup().getWaypoints());

		renderingManager.renderLine(worldManager);
		
		
		//TODO: Hier dafür sorgen, dass die Schrift fix als Hud angezeigt wird. Neue Camera?
		renderingManager.renderDebugOutput();
		

		worldManager.update();
		worldManager.render(cameraManager.getCameraBox2dDebug().combined);

		cameraManager.update(playerPosition, mapSize);

	}

	@Override
	public void dispose () {
//		shapeRenderer.dispose();
		assetManager.dispose();
		worldManager.dispose();
		renderingManager.dispose();
	}


	public void loadAssets () {
		assetManager.load(Config.TEXTURE_LIBGDX, Texture.class);
		assetManager.load(Config.TEXTURE_ENTITIES, Texture.class);
		assetManager.load(Config.TEXTURE_MECHA, Texture.class);
		assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		assetManager.load(Config.MAP_MAP, TiledMap.class);
		assetManager.finishLoading();
	}
}
