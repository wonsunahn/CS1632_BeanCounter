import java.util.Random;

/**
 * Code by @author Wonsun Ahn
 * 
 * <p>Bean: Each bean is assigned a skill level from 0-9 on creation according to
 * a normal distribution with average SKILL_AVERAGE and standard deviation
 * SKILL_STDEV. A skill level of 9 means it always makes the "right" choices
 * (pun intended) when the machine is operating in skill mode ("skill" passed on
 * command kine). That means the bean will always go right when a peg is
 * encountered, resulting it falling into slot 9. A skill evel of 0 means that
 * the bean will always go left, resulting it falling into slot 0. For the
 * in-between skill levels, the bean will first go right then left. For example,
 * for a skill level of 7, the bean will go right 7 times then go left twice.
 * 
 * <p>Skill levels are irrelevant when the machine operates in luck mode. In that
 * case, the bean will have a 50/50 chance of going right or left, regardless of
 * skill level.
 */

public class Bean {
	// TODO: Add member methods and variables as needed 
	
	private static final double SKILL_AVERAGE = 4.5;	// MainPanel.SLOT_COUNT * 0.5;
	private static final double SKILL_STDEV = 1.5;		// Math.sqrt(SLOT_COUNT * 0.5 * (1 - 0.5));
	
	/**
	 * Constructor - creates a bean in either luck mode or skill mode.
	 * 
	 * @param isLuck	whether the bean is in luck mode
	 * @param rand      the random number generator
	 */
	Bean(boolean isLuck, Random rand) {
		// TODO: Implement
	}
}
