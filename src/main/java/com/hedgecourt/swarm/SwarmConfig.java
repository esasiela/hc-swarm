package com.hedgecourt.swarm;

public class SwarmConfig {
	// public static int SWARM_PANEL_WIDTH = 401;
	// public static int SWARM_PANEL_HEIGHT = 400;

	// public static int SWARM_PANEL_WIDTH = 1501;
	// public static int SWARM_PANEL_HEIGHT = 950;

	// public static int SWARM_PANEL_WIDTH = 1001;
	// public static int SWARM_PANEL_HEIGHT = 950;

	public static int SWARM_PANEL_WIDTH = 1341;
	public static int SWARM_PANEL_HEIGHT = 1005;

	public static int SPECK_DEFAULT_COUNT = 2500;

	public static String GUI_WINDOW_TITLE = "Swarm Specks";

	public static int SPECK_RADIUS_X = 2;
	public static int SPECK_RADIUS_Y = 2;

	public static boolean SHOW_SPECK_ID = false;
	public static boolean SHOW_SPECK_HEAD = false;

	public static boolean SWARM_HARDCODE_INIT = false;

	public static double FIELD_OUT_OF_BOUNDS_OFFSET = 0.0;

	public static long CONTROL_THREAD_SLEEP_MILLIS = 80;
	public static boolean CONTROL_THREAD_ADJUST_TIME = true;

	public static int EXECUTION_THREAD_COUNT = 50;

	public static int PERFORMANCE_TIMER_MODULUS = 100;

	// public static String SWARM_IMPL_CLASSNAME = "com.hedgecourt.swarm.impl2.UpDownSwarm";
	// public static String SWARM_IMPL_CLASSNAME = "com.hedgecourt.swarm.impl2.WallBounceSwarm";
	// public static String SWARM_IMPL_CLASSNAME = "com.hedgecourt.swarm.impl2.BoidsSeparationSwarm";
	// public static String SWARM_IMPL_CLASSNAME = "com.hedgecourt.swarm.impl2.BoidsCohesionSwarm";
	// public static String SWARM_IMPL_CLASSNAME = "com.hedgecourt.swarm.impl2.BoidsAlignmentSwarm";
	public static String SWARM_IMPL_CLASSNAME = "com.hedgecourt.swarm.impl.RulesBasedSwarm";

	public static boolean FORCE_CONSTANT_SPEED = true;

	public static double WALLFLOWER_BOOT_VELOCITY = 1;

	public static double SPECK_DEFAULT_MASS = 100;
	public static double SPECK_DEFAULT_MAX_SPEED = 7;
	// set NEIGHBORHOOD_ANGLE to 0 to disable any angle checking
	public static double SPECK_DEFAULT_NEIGHBORHOOD_ANGLE = 2 * Math.PI / 3;
	// public static double SPECK_DEFAULT_NEIGHBORHOOD_ANGLE = 0;

	public static double SPECK_DEFAULT_MAX_TURN_RATE = 0.3925;
	// public static double SPECK_DEFAULT_MAX_TURN_RATE = 0.3725;
	// public static double SPECK_DEFAULT_MAX_TURN_RATE = 0.1325;

	public static double SPECK_DEFAULT_SEPARATION_DISTANCE = 5;

	public static double SPECK_DEFAULT_NEIGHBORHOOD_RADIUS = 35;

	public static int RULE_WEIGHT_WALL_COLLISION = -1;
	public static int RULE_WEIGHT_MOOT = 0;

	// a fun config is sep=5, co=1, ali=3

	public static int RULE_WEIGHT_SEPARATION = 5;
	public static int RULE_WEIGHT_COHESION = 1;
	public static int RULE_WEIGHT_ALIGNMENT = 3;
	public static int RULE_WEIGHT_SEPARATE_PREDATOR = 100;
	public static int RULE_WEIGHT_DISALIGN_PREDATOR = 100;

}
