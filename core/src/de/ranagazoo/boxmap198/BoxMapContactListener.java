
package de.ranagazoo.boxmap198;

import static de.ranagazoo.boxmap198.Config.CATEGORY_MONSTER;
import static de.ranagazoo.boxmap198.Config.CATEGORY_MSENSOR;
import static de.ranagazoo.boxmap198.Config.CATEGORY_PLAYER;
import static de.ranagazoo.boxmap198.Config.CATEGORY_WAYPOINT;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class BoxMapContactListener implements ContactListener {
	short aCatBits, bCatBits;

	@Override
	public void beginContact (Contact contact) {
		// MonsterSensor trifft auf player: angriff
		if (checkCategoryBits(contact, CATEGORY_PLAYER, CATEGORY_MSENSOR)) {
			// BoxEntity2 player = (BoxEntity2)contact.getFixtureA().getBody().getUserData();
			BoxEntity enemy = (BoxEntity)contact.getFixtureB().getBody().getUserData();

			enemy.setStatus(Config.STATUS_ATTACK);
			enemy.setTargetBody(contact.getFixtureA().getBody());
			// ((Enemy)contact.getFixtureB().getBody().getUserData()).setStatus(Enemy.STATUS_ATTACK);
		}

		// Monster hat Spieler erwischt
		if (checkCategoryBits(contact, CATEGORY_PLAYER, CATEGORY_MONSTER)) {
			BoxEntity enemy = (BoxEntity)contact.getFixtureB().getBody().getUserData();

			enemy.setStatus(Config.STATUS_NEW_TARGET);
			enemy.resetTargetBodyToWaypoint();

// Gdx.app.log("Spieler erwischt", "Aua-aua-aua");
// ((Enemy)contact.getFixtureB().getBody().getUserData()).setStatus(Enemy.STATUS_NEW_TARGET);
		}

		// Monster hat waypoint erreicht (NICHT MonsterSensor)
		if (checkCategoryBits(contact, CATEGORY_MONSTER, CATEGORY_WAYPOINT)) {
			BoxEntity enemy = (BoxEntity)contact.getFixtureA().getBody().getUserData();

			// Will den neuen Wegpunkt anhand des aktuellen setzen
			Body waypointBody = contact.getFixtureB().getBody();
			WaypointGroup wpg = (WaypointGroup)waypointBody.getUserData();
			// wpg.getWaypoints().indexOf(waypointBody, false);
			enemy.setNextWaypoint(wpg.getWaypoints().indexOf(waypointBody, false));

			enemy.setStatus(Config.STATUS_NEW_TARGET);
			// enemy.setNextWaypoint();

// Enemy enemy = (Enemy)contact.getFixtureA().getBody().getUserData();
// Integer waypointIndexInTheArray = (Integer)contact.getFixtureB().getBody().getUserData();
// if(enemy.getCurrentTargetIndex() == waypointIndexInTheArray.intValue())
// enemy.setStatus(Enemy.STATUS_NEW_TARGET);
		}
	}

	@Override
	public void endContact (Contact contact) {
		// player ist aus monstersensor-reichweite: neuer waypoint
		if (checkCategoryBits(contact, CATEGORY_PLAYER, CATEGORY_MSENSOR)) {
			BoxEntity enemy = (BoxEntity)contact.getFixtureB().getBody().getUserData();
			enemy.setStatus(Config.STATUS_NEW_TARGET);
			enemy.resetTargetBodyToWaypoint();

// Enemy enemy = (Enemy)contact.getFixtureB().getBody().getUserData();
// enemy.setStatus(Enemy.STATUS_NEW_TARGET);
		}

		if (checkCategoryBits(contact, CATEGORY_PLAYER, CATEGORY_MONSTER)) {
			BoxEntity enemy = (BoxEntity)contact.getFixtureB().getBody().getUserData();
			enemy.setStatus(Config.STATUS_NEW_TARGET);
			enemy.resetTargetBodyToWaypoint();
// ((Enemy)contact.getFixtureB().getBody().getUserData()).setStatus(Enemy.STATUS_NEW_TARGET);
		}
	}

	public boolean checkCategoryBits (Contact contact, short a, short b) {
		return (contact.getFixtureA().getFilterData().categoryBits == a && contact.getFixtureB().getFilterData().categoryBits == b);
	}

	@Override
	public void preSolve (Contact contact, Manifold oldManifold) {
	}

	@Override
	public void postSolve (Contact contact, ContactImpulse impulse) {
	}

}
