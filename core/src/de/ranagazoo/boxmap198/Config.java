
package de.ranagazoo.boxmap198;

public class Config {

	public static final short CATEGORY_PLAYER = 0x0001; // 0000000000000001 in binary
	public static final short CATEGORY_MONSTER = 0x0002; // 0000000000000010 in binary
	public static final short CATEGORY_MSENSOR = 0x0008; // 0000000000001000 in binary
	public static final short CATEGORY_SCENERY = 0x0004; // 0000000000000100 in binary
	public static final short CATEGORY_NONE = 0x0032; // 0000000000100000 in binary ???
	public static final short CATEGORY_WAYPOINT = 0x0064; // 0000000001000000 in binary ???

	public static final short MASK_PLAYER = CATEGORY_MONSTER | CATEGORY_SCENERY | CATEGORY_MSENSOR; // or ~CATEGORY_PLAYER
	public static final short MASK_MONSTER = CATEGORY_PLAYER | CATEGORY_SCENERY | CATEGORY_WAYPOINT; // or ~CATEGORY_MONSTER

	// Monstersensor, reagiert nur auf player
	public static final short MASK_MSENSOR = CATEGORY_PLAYER; // or ~CATEGORY_MONSTER
	public static final short MASK_SCENERY = -1;
	public static final short MASK_NONE = CATEGORY_SCENERY;
	public static final short MASK_WAYPOINT = CATEGORY_MONSTER;

	public static final int TS = 32;

	public static final String TYPE_PLAYER1 = "player1";
	public static final String TYPE_ENEMY1 = "enemy1";
	public static final String TYPE_OBSTACLE = "obstacle";
	public static final String TYPE_WAYPOINT = "waypoint";
	public static final String TYPE_WAYPOINTGROUP = "waypointGroup";

	public static final int STATUS_IDLE = 1;
	public static final int STATUS_ATTACK = 2;
	public static final int STATUS_NEW_TARGET = 3;
}
