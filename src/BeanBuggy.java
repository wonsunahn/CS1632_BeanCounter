import gov.nasa.jpf.annotation.FilterField;

import java.util.Random;

/**
 * Code by @author Wonsun Ahn
 * 
 * <p>
 * Bean: Each bean is assigned a skill level from 0-9 on creation according to a
 * normal distribution with average SKILL_AVERAGE and standard deviation
 * SKILL_STDEV. The formula to calculate the skill level is:
 * 
 * <p>
 * (int) Math.round(rand.nextGaussian() * SKILL_STDEV + SKILL_AVERAGE)
 * 
 * <p>
 * A skill level of 9 means it always makes the "right" choices (pun intended)
 * when the machine is operating in skill mode ("skill" passed on command line).
 * That means the bean will always go right when a peg is encountered, resulting
 * it falling into slot 9. A skill evel of 0 means that the bean will always go
 * left, resulting it falling into slot 0. For the in-between skill levels, the
 * bean will first go right then left. For example, for a skill level of 7, the
 * bean will go right 7 times then go left twice.
 * 
 * <p>
 * Skill levels are irrelevant when the machine operates in luck mode. In that
 * case, the bean will have a 50/50 chance of going right or left, regardless of
 * skill level. The formula to calculate the direction is: rand.nextInt(2). If
 * the return value is 0, the bean goes left. If the return value is 1, the bean
 * goes right.
 */

public class BeanBuggy implements Bean {
	// TODO: Add member methods and variables as needed

	private static final double SKILL_AVERAGE = 4.5;	// MainPanel.SLOT_COUNT * 0.5;
	private static final double SKILL_STDEV = 1.5;		// Math.sqrt(SLOT_COUNT * 0.5 * (1 - 0.5));
	
	private Random rand;
	private boolean isLuck;
	private int xpos;
	private int rightChoices;
	private int remainingRightChoices;

	/**
	 * Constructor - creates a bean in either luck mode or skill mode.
	 * 
	 * @param isLuck	whether the bean is in luck mode
	 * @param r         the random number generator
	 */
	BeanBuggy(boolean isLuck, Random rand) {
		// TODO: Implement
		this.isLuck = isLuck;
		this.rand = rand;
		rightChoices = (int) Math.round(rand.nextGaussian() * SKILL_STDEV + SKILL_AVERAGE);
		rightChoices = rightChoices > 9 ? 9 : rightChoices;
		rightChoices = rightChoices < 0 ? 0 : rightChoices;
		remainingRightChoices = rightChoices;
		xpos = 0;
	}
	
	public boolean isSkilled() {
		return !isLuck;
	}
	
	public void setXPos(int pos) {
		xpos = pos;
	}

	public int getXPos() {
		return xpos;
	}

	public void reset() {
		remainingRightChoices = rightChoices;
		xpos = 0;
	}

	/**
	 * Chooses left or right randomly (if luck) or according to skill.
	 */
	public void choose() {
		if (isLuck) {
			if (rand.nextInt(2) == 1) {
				xpos++;
			}
		} else {
			if (remainingRightChoices > 0) {
				xpos++;
				remainingRightChoices--;
			}
		}
	}
}
