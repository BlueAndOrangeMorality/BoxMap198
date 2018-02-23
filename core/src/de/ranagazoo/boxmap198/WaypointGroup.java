
package de.ranagazoo.boxmap198;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/*
 * Is loaded from a map object (of type waypointGroup)
 * Contains a list of waypoints and their order
 */
public class WaypointGroup {
	// Waypoints are doubled here because the rendering expects float[], but it's easier to get the right Vector
	// MAYBE THIS SHOULD BE WAYPOINT-CLASS
	private Array<Body> waypoints = new Array<Body>();
	private float[] waypointsRender = new float[] {};

	// Empty Constructor, das ganze sollte hoffentlich später noch überladen werden.
	// Das Ganze macht nur so lange Sinn, wie es nur eine WaypointGroup gibt, aber das gilt auch für WorldManager
	// Kannst du immer noch aufzwirbeln
	public WaypointGroup () {
	}

	// Waypoint exisitert nicht mehr

	// Hier werden erst alle Punkte um Polygon/Polyline in waypointsRender geschrieben
	// Dann werden daraus Waypoint-Typ-Bodys gemacht, diese in waypoint hinterlegt und jedem this als userObject übergeben
	// So müsste man bei jedem Waypoint an dieses Eltenr-Objekt kommen und umgekehrt

	public WaypointGroup (MapObject mapObject, World world, BoxEntityFactory boxEntityFactory3) {
		float[] waypointsTransformed = new float[] {};
		float[] waypointsTemp = new float[] {};
		if (mapObject.getClass().equals(PolygonMapObject.class)) {
			Polygon polygon = ((PolygonMapObject)mapObject).getPolygon();
			polygon.setScale(1f / 32f, 1f / 32f);
			polygon.setPosition(polygon.getX() / 32f, polygon.getY() / 32f);

			waypointsTransformed = polygon.getTransformedVertices();
			waypointsTemp = polygon.getVertices();
			waypointsRender = new float[waypointsTemp.length];
			// aaaaaaaaaaa Unschön, aber geht
			for (int i = 0; i < waypointsTemp.length; i++) {
				if (i % 2 == 0)
					waypointsRender[i] = waypointsTemp[i] + polygon.getX() * 32f;
				else
					waypointsRender[i] = waypointsTemp[i] + polygon.getY() * 32f;
			}

		} else if (mapObject.getClass().equals(PolylineMapObject.class)) {
// Polyline polyline = ((PolylineMapObject)mapObject).getPolyline();
// Gdx.app.log("Waypointgroup: Ursprungsposition", ""+polyline.getX()+"-"+polyline.getY());
// polyline.setPosition(6f, 25f);
// polyline.setScale(1f/32f, 1f/32f);
// float[] a = polyline.getTransformedVertices();
// float[] d = polyline.getVertices();
// //HIER HIER HIER: Position ist noch nicht richtig, und offenbar macht er aus den Linien auch Vertices
//
//// int remove = 4;
//// float[] b = new float[a.length-remove];
//// float[] c = new float[a.length-remove];
////
//// //remove last numbers: b
//// for(int i=0; i<b.length; i++)
//// {
//// b[i] = a[i];
//// }
////
//// //remove first numbers: a
//// for(int i=0; i<b.length; i++)
//// {
//// c[i] = a[i+remove];
//// }
//// Gdx.app.log("", ""+a.length + " - " + b.length + " - " + c.length);
// Gdx.app.log("", ""+a.length + " - " + d.length);
//
// waypointsRender = a;

		}

		for (int i = 0; i < waypointsTransformed.length - 1; i += 2) {
			BodyDef bodyDef = boxEntityFactory3.getBodyDefFromMapObject(Config.TYPE_WAYPOINT);
			bodyDef.position.set(new Vector2(waypointsTransformed[i], waypointsTransformed[i + 1]));

			// Hat schon seinen Shape
			FixtureDef fixtureDef = boxEntityFactory3.getFixtureDefFromMapObject(Config.TYPE_WAYPOINT);

			Body body = world.createBody(bodyDef);
			body.createFixture(fixtureDef);
			body.setUserData(this);
			waypoints.add(body);
		}
	}

	public Body get (int index) {
		return waypoints.get(index);
	}

	public int getIndex (Body body) {
		return waypoints.indexOf(body, true);
	}

	// TODO: Ist das so richtig?
	public int getNextWaypoint (int lastWaypoint) {
		if (lastWaypoint >= waypoints.size - 1) {
			return 0;
		} else
			return lastWaypoint + 1;
	}

	public Array<Body> getWaypoints () {
		return waypoints;
	}

	public float[] getWaypointsRender () {
		return waypointsRender;
	}
}
