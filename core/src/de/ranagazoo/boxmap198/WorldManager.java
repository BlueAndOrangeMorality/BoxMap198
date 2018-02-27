
package de.ranagazoo.boxmap198;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class WorldManager implements Disposable {
	private World world;
	private Box2DDebugRenderer debugRenderer;
	private BoxEntityFactory boxEntityFactory;
	private WaypointGroup waypointGroup;

	/** Wrapper class for the Box2D World. Also contains a Box2DDebugRenderer which is used when the render Method is called.
	 * 
	 * TODO: Currently also conatins one central waypointGroup for a map which is a horrible idea */
	public WorldManager () {
		world = new World(new Vector2(0, 0), true);
		world.setContactListener(new BoxMapContactListener());
		debugRenderer = new Box2DDebugRenderer();
		boxEntityFactory = new BoxEntityFactory();
		waypointGroup = new WaypointGroup();
	}

	/** Loads all BoxEntity - entities from the TiledMap. Also loads the internal WaypointGroup and adds it to the Enemy-Type
	 * entities
	 * 
	 * @param tiledMap current TiledMap instance
	 * @return Array<BoxEntity> */
	public Array<BoxEntity> loadEntities (TiledMap tiledMap) {
		Array<BoxEntity> boxEntities = new Array<BoxEntity>();
//
// Hier werden die ganzen entities mit world.clearForces();.WorldManager erzeugt
// am Besten werden hier die Entities auch schon eingeteilt und dann an das Spiel zurückgeliefert, damit beim rendern da
// drüberiteriert wird.
// für render und move
//
// quasi den teil hier aus boxmap richtig durchlaufen lassen
//
// tiledMap.getLayers().get(1).getObjects().getByType(PolygonMapObject.class);
// tiledMap.getLayers().get(1).getObjects().getByType(PolylineMapObject.class);
// tiledMap.getLayers().get(1).getObjects().getByType(RectangleMapObject.class);
// tiledMap.getLayers().get(1).getObjects().getByType(TiledMapTileMapObject.class);
//

		for (MapObject mapObject : tiledMap.getLayers().get(1).getObjects()) {
			String type = boxEntityFactory.getTypeFromMapObject(mapObject);
			Vector2 pos = boxEntityFactory.getPositionFromMapObject(mapObject);

			// TODO: Falls WaypontGroup, das hier erstellen!
			if (type.equals(Config.TYPE_WAYPOINTGROUP)) {
				waypointGroup = new WaypointGroup(mapObject, world, boxEntityFactory);
			}
			// Alles was keine WaypointGroup ist
			else {

				// Sinnvoll ist ggf., immer nur ein Shape pro Objekt anzunehmen
				// und per Parameter in der Klasse einfach nur den sensorenradius anzugeben.
				// Das ist zumindestens aktuell sinnvoll, solange entities nur ein fixture haben
				// ---->Das if/else kann entfallen

				BodyDef bodyDef = boxEntityFactory.getBodyDefFromMapObject(type);
				bodyDef.position.set(pos);
				Body body = world.createBody(bodyDef);

				// Sonderfall: Separater Shape gefunden
				// Gegebenenfalls mehrere FixtureDefs als Array holen?
				// Aber jedes braucht ein Shape

				FixtureDef fixtureDef = boxEntityFactory.getFixtureDefFromMapObject(type);
				// TODO Aktuell bekommen nur Obstacles ein Shape ausgelesen
				if (type.equals(Config.TYPE_OBSTACLE)) fixtureDef.shape = boxEntityFactory.getShapeFromMapObject(mapObject);
				body.createFixture(fixtureDef);

				// Sonderfall Enemy: Sensor hinzugefügt
				if (type.equals(Config.TYPE_ENEMY1)) {
					FixtureDef fixtureDefSensor = boxEntityFactory.getFixtureDefSensor();
					body.createFixture(fixtureDefSensor);
				}

				BoxEntity boxEntity2 = new BoxEntity(type, body);

				// Aktuell werden nur Player und Enemies zurückgeliefert, da nur diese move() enthalten
				if (type.equals(Config.TYPE_PLAYER1) || type.equals(Config.TYPE_ENEMY1)) {
					boxEntities.add(boxEntity2);
				}

			}

		}

		// Muss nachträglich passieren, weil WaypointGroup nach dem ersten Entity geladen worden sein könnte.
		for (BoxEntity boxEntity : boxEntities) {
			if (boxEntity.getType().equals(Config.TYPE_ENEMY1)) {
				// Aktuell nur eine WaypointGroup vorgesehen
				boxEntity.setWaypointGroup(waypointGroup);
			}
		}

		// Map Border
		BodyDef bodyDef = boxEntityFactory.getBodyDefFromMapObject(Config.TYPE_BORDER);
		bodyDef.position.set(0, 0);
		Body body = world.createBody(bodyDef);

		FixtureDef fixtureDef = boxEntityFactory.getFixtureDefFromMapObject(Config.TYPE_BORDER);

		// Shape ist aktuell vorgegeben, sollte aber aus der Map gelesen werden NICHT AUS DEM MAPOBJECT
		// fixtureDef.shape = boxEntityFactory.getShapeFromMapObject(mapObject);

		float width = tiledMap.getProperties().get("width", Integer.class).floatValue();
		float height = tiledMap.getProperties().get("height", Integer.class).floatValue();

		ChainShape borderChainShape = new ChainShape();
		// borderChainShape.createChain(new Vector2[]{new Vector2(0f, 0f), new Vector2(width, 0), new Vector2(width, height), new
		// Vector2(0f, height), new Vector2(0, 0)});
		borderChainShape.createChain(new float[] {0f, 0f, width, 0f, width, height, 0f, height, 0f, 0f});
		fixtureDef.shape = borderChainShape;

		body.createFixture(fixtureDef);

		return boxEntities;
	}

	/** Performs a predifined Box2D - World step. This performs collision detection, integration, and constraint solution. */
	public void update () {
		world.step(1 / 60f, 6, 2);
	}

	/** Uses Box2DDebugRenderer to render the entities. Just used for debug purposes.
	 * 
	 * @param combined */
	public void render (Matrix4 combined) {
		debugRenderer.render(world, combined);
	}

	@Override
	public void dispose () {
		world.dispose();
		boxEntityFactory.dispose();
	}

	public WaypointGroup getWaypointGroup () {
		return waypointGroup;
	}
}
