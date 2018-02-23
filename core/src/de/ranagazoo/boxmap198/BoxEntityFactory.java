
package de.ranagazoo.boxmap198;

import static de.ranagazoo.boxmap198.Config.CATEGORY_MONSTER;
import static de.ranagazoo.boxmap198.Config.CATEGORY_MSENSOR;
import static de.ranagazoo.boxmap198.Config.CATEGORY_PLAYER;
import static de.ranagazoo.boxmap198.Config.MASK_MONSTER;
import static de.ranagazoo.boxmap198.Config.MASK_MSENSOR;
import static de.ranagazoo.boxmap198.Config.MASK_PLAYER;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Disposable;

public class BoxEntityFactory implements Disposable {
	public static final int BOXTYPE_PLAYER = 1;
	public static final int BOXTYPE_ENEMY = 2;
	public static final int BOXTYPE_OBSTACLE = 3;
	public static final int BOXTYPE_WAYPOINT = 4;

	private BodyDef playerBodyDef, enemyBodyDef, obstacleBodyDef, waypointBodyDef;
	private FixtureDef playerFixtureDef, enemyFixtureDef, enemyFixtureDefSensor, obstacleFixtureDef, waypointFixtureDef;

	private PolygonShape playerPolygonShape, enemyPolygonShape, obstaclePolygonShape;
	private CircleShape waypointCircleShape, enemyCircleShape;

// 1. Camera anpassen
// 2. Obstacle und Waypoint brauch kein render und kein move....
// 3. Waypoint-Polygons statt waypoints

	public BoxEntityFactory () {
		// Player
		playerBodyDef = new BodyDef();
		playerBodyDef.angularDamping = 5f;
		playerBodyDef.fixedRotation = false;
		playerBodyDef.linearDamping = 5f;
		playerBodyDef.type = BodyType.DynamicBody;

		playerFixtureDef = new FixtureDef();
		playerFixtureDef.density = 0.5f;
		playerFixtureDef.friction = 0.4f;
		playerFixtureDef.restitution = 0.1f;
		playerFixtureDef.filter.categoryBits = CATEGORY_PLAYER;
		playerFixtureDef.filter.maskBits = MASK_PLAYER;
		playerFixtureDef.isSensor = false;

		// Enemy
		enemyBodyDef = new BodyDef();
		enemyBodyDef.angularDamping = 2;
		enemyBodyDef.fixedRotation = false;
		enemyBodyDef.linearDamping = 2;
		enemyBodyDef.type = BodyType.DynamicBody;

		enemyFixtureDef = new FixtureDef();
		enemyFixtureDef.density = 0.2f;
		enemyFixtureDef.friction = 0.4f;
		enemyFixtureDef.restitution = 0.1f;
		enemyFixtureDef.filter.categoryBits = CATEGORY_MONSTER;
		enemyFixtureDef.filter.maskBits = MASK_MONSTER;
		enemyFixtureDef.isSensor = false;

		enemyFixtureDefSensor = new FixtureDef();
		enemyFixtureDefSensor.density = 0f;
		enemyFixtureDefSensor.friction = 0.4f;
		enemyFixtureDefSensor.restitution = 0.1f;
		enemyFixtureDefSensor.filter.categoryBits = CATEGORY_MSENSOR;
		enemyFixtureDefSensor.filter.maskBits = MASK_MSENSOR;
		enemyFixtureDefSensor.isSensor = true;

		// Obstacle
		obstacleBodyDef = new BodyDef();
		obstacleBodyDef.angularDamping = 2f;
		obstacleBodyDef.fixedRotation = false;
		obstacleBodyDef.linearDamping = 2f;
		obstacleBodyDef.type = BodyType.StaticBody;

		obstacleFixtureDef = new FixtureDef();
		obstacleFixtureDef.density = 0.5f;
		obstacleFixtureDef.friction = 0.4f;
		obstacleFixtureDef.restitution = 0.1f;
		obstacleFixtureDef.filter.categoryBits = Config.CATEGORY_SCENERY;
		obstacleFixtureDef.filter.maskBits = Config.MASK_SCENERY;
		obstacleFixtureDef.isSensor = false; // UMÄNDERN!!!!

		// Waypoint
		waypointBodyDef = new BodyDef();
		waypointBodyDef.angularDamping = 2f;
		waypointBodyDef.fixedRotation = false;
		waypointBodyDef.linearDamping = 2f;
		waypointBodyDef.type = BodyType.StaticBody;

		waypointFixtureDef = new FixtureDef();
		waypointFixtureDef.density = 0.2f;
		waypointFixtureDef.friction = 0.4f;
		waypointFixtureDef.restitution = 0.1f;
		waypointFixtureDef.isSensor = true;
		waypointFixtureDef.filter.categoryBits = Config.CATEGORY_WAYPOINT;
		waypointFixtureDef.filter.maskBits = Config.MASK_WAYPOINT;

		playerPolygonShape = new PolygonShape();
		playerPolygonShape.set(new float[] {-0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f});
		playerFixtureDef.shape = playerPolygonShape;

		enemyPolygonShape = new PolygonShape();
		enemyPolygonShape.set(new float[] {-0.25f, -0.25f, 0, -1, 0.25f, -0.25f, 0.25f, 0.25f, -0.25f, 0.25f});
		enemyFixtureDef.shape = enemyPolygonShape;

		enemyCircleShape = new CircleShape();
		enemyCircleShape.setRadius(3);
		enemyFixtureDefSensor.shape = enemyCircleShape;

		obstaclePolygonShape = new PolygonShape();
		obstaclePolygonShape.set(new float[] {-2f, -1f, 2f, -1f, 2f, 1f, -2f, 1f});
		obstacleFixtureDef.shape = obstaclePolygonShape;

		waypointCircleShape = new CircleShape();
		waypointCircleShape.setRadius(0.5f);
		waypointFixtureDef.shape = waypointCircleShape;
	}

	public String getTypeFromMapObject (MapObject mapObject) {
		return mapObject.getProperties().get("type", String.class);
	}

	public Vector2 getPositionFromMapObject (MapObject mapObject) {
		float x = mapObject.getProperties().get("x", Float.class);
		float y = mapObject.getProperties().get("y", Float.class);
		float width = mapObject.getProperties().get("width", Float.class);
		float height = mapObject.getProperties().get("height", Float.class);
		Float posX = (x + width / 2.0f) / Config.TS;
		Float posY = (y + height / 2.0f) / Config.TS;
		return new Vector2(posX, posY);
	}

	public Shape getShapeFromMapObject (MapObject mapObject) {
		if (mapObject.getClass().equals(PolygonMapObject.class)) {
			Polygon polygon = ((PolygonMapObject)mapObject).getPolygon();
			polygon.setPosition(0, 0);
			polygon.setScale(1f / 32f, 1f / 32f);

			PolygonShape polygonShape = new PolygonShape();
			polygonShape.set(polygon.getTransformedVertices());

			return polygonShape;
		} else if (mapObject.getClass().equals(RectangleMapObject.class)) {
			Rectangle rectangle = ((RectangleMapObject)mapObject).getRectangle();
			rectangle.setPosition(0, 0);
			float hx = (rectangle.x + rectangle.width / 2f) / 32f;
			float hy = (rectangle.y + rectangle.height / 2f) / 32f;
			PolygonShape polygonShape = new PolygonShape();
			polygonShape.setAsBox(hx, hy);
			return polygonShape;
		}
// else if (mapObject.getClass().equals(TiledMapTileMapObject.class))
		Gdx.app.log("BoxEntityFactory3.getShapeFromMapObject: ",
			"Aktuell nur Obstacles vorgesehen und nur als Rectangle oder Polygon");
		return null;
	}

	public BodyDef getBodyDefFromMapObject (String type) {
		if (type.equals(Config.TYPE_PLAYER1))
			return playerBodyDef;
		else if (type.equals(Config.TYPE_ENEMY1))
			return enemyBodyDef;
		else if (type.equals(Config.TYPE_WAYPOINTGROUP))
			return waypointBodyDef;
		else
			return obstacleBodyDef;
	}

	public FixtureDef getFixtureDefFromMapObject (String type) {
		if (type.equals(Config.TYPE_PLAYER1))
			return playerFixtureDef;
		else if (type.equals(Config.TYPE_ENEMY1))
			return enemyFixtureDef;
		else if (type.equals(Config.TYPE_WAYPOINT))
			return waypointFixtureDef;
		// Könnte richtig, wichtig oder egal sein
		else if (type.equals(Config.TYPE_WAYPOINTGROUP))
			return waypointFixtureDef;
		else
			return obstacleFixtureDef;
	}

	public FixtureDef getFixtureDefSensor () {
		return enemyFixtureDefSensor;
	}

	@Override
	public void dispose () {
		playerPolygonShape.dispose();
		enemyPolygonShape.dispose();
		obstaclePolygonShape.dispose();
		waypointCircleShape.dispose();
	}

}
