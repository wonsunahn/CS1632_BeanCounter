package edu.pitt.cs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Arrays;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Code by @author Wonsun Ahn
 *
 * <p>MainPanel: This is the display for the machine. Most internal state is
 * encapsulated inside _logic. This class mainly handles the display of that
 * internal state animations between frames.
 */

public class MainPanel extends JPanel {

	private final BeanCounterLogic logic;	// The core logic of the program
	private final Bean[] beans;				// The beans in the machine
	private final int slotCount;			// The number of slots in the machine
	private final boolean debugEnabled;

	private Point[] currentBeanPositions;		// Bean coordinates currently shown; updated as they move
	private Point[] animationStartBeanPositions;	// Snapshot of bean coordinates at the start of a step
	private Point[] animationEndBeanPositions;	// Bean coordinates after the step completes

	private boolean isRunning;			// Is the bean counter running now?

	private int barHeight;				// The height reserved for bar graphs in the panel
	private final Timer animationTimer;
	private int frameDelay;
	private int stepDelay;
	private int animationStride;

	private static final int SLOW_FRAME_DELAY = 10;
	private static final int FAST_FRAME_DELAY = 1;
	private static final int SLOW_STEP_DELAY = 100;
	private static final int FAST_STEP_DELAY = 1;
	private static final int SLOW_ANIMATION_STRIDE = 1;
	private static final int FAST_ANIMATION_STRIDE = 20;
	public static final int PEG_SIZE = 10;
	public static final int BEAN_SIZE = 10;
	public static final int TOP_MARGIN = 30;
	public static final int BOTTOM_MARGIN = 30;
	public static final int BAR_TEXT_HEIGHT = 20;

	/**
	 * Constructor - creates the main animation panel for the machine.
	 * 
	 * @param slotCount number of slots in the machine
	 * @param beanCount number of beans in the machine
	 * @param isLuck    whether beans progress through pure luck (or skill)
	 */
	public MainPanel(InstanceType type, int slotCount, int beanCount, boolean isLuck) {
		this(type, slotCount, beanCount, isLuck, false);
	}

	public MainPanel(InstanceType type, int slotCount, int beanCount, boolean isLuck, boolean debugEnabled) {
		super();
		this.slotCount = slotCount;
		this.debugEnabled = debugEnabled;
		frameDelay = SLOW_FRAME_DELAY;
		animationTimer = new Timer(SLOW_FRAME_DELAY, event -> advanceAnimation());
		animationTimer.setRepeats(true);
		
		// Create the internal logic
		logic = BeanCounterLogic.createInstance(type, slotCount);
		// Create the beans
		beans = new Bean[beanCount];
		for (int i = 0; i < beanCount; i++) {
			beans[i] = Bean.createInstance(type, slotCount, isLuck, new Random());
		}
		// Initialize the logic with the beans
		logic.reset(beans);
		// Set some display related parameters
		stepDelay = SLOW_STEP_DELAY;
		animationStride = SLOW_ANIMATION_STRIDE;
		// Heuristically maximum height of a bell curve with some headroom
		barHeight = (int) Math.round(beanCount * 0.27) + 10; 
		setBackground(Color.WHITE);
	}

	static boolean moveAlongLine(Point current, Point start, Point target) {
		return moveAlongLine(current, start, target, 1);
	}

	static boolean moveAlongLine(Point current, Point start, Point target, int stride) {
		int deltaX = target.x - start.x;
		int deltaY = target.y - start.y;
		int steps = Math.max(Math.abs(deltaX), Math.abs(deltaY));
		if (steps == 0) {
			return true;
		}

		if (stride <= 0) {
			throw new IllegalArgumentException("Animation stride must be positive");
		}
		int progress = Math.max(Math.abs(current.x - start.x), Math.abs(current.y - start.y));
		int nextProgress = Math.min(progress + stride, steps);
		current.x = start.x + (int) Math.round((double) deltaX * nextProgress / steps);
		current.y = start.y + (int) Math.round((double) deltaY * nextProgress / steps);
		return nextProgress == steps;
	}

	static int getBeanXCenteredOnLabel(int labelX, int labelWidth) {
		return labelX + labelWidth / 2 - BEAN_SIZE / 2;
	}

	private Point getFinalRowBeanPosition(int slotIndex) {
		Point position = logicalToPhysical(new Point(slotIndex, slotCount - 1));
		int barWidth = getWidth() / (slotCount + 1);
		int labelX = logicalToPhysical(new Point(slotIndex, slotCount)).x + barWidth / 2;
		int labelWidth = getFontMetrics(getFont()).stringWidth(String.valueOf(slotIndex));
		position.x = getBeanXCenteredOnLabel(labelX, labelWidth);
		position.y -= PEG_SIZE + 5;
		return position;
	}

	/**
	 * Returns the physical coordinate for logical coordinate p. The logical
	 * coordinate is the coordinate system used by BeanCounterLogic (see
	 * BeanCounterLogic for details). The physical coordinate is the actual
	 * coordinate in the MainPanel.
	 * 
	 * @param p the logical coordinate
	 * @return the translated physical coordinate.
	 */
	private Point logicalToPhysical(Point p) {
		Dimension d = getSize();
		int initialX = d.width / 2;
		int initialY = TOP_MARGIN;
		int pegXSpacing = d.width / slotCount;
		int pegYSpacing = (d.height - TOP_MARGIN - BOTTOM_MARGIN - barHeight - BAR_TEXT_HEIGHT)
				/ Math.max(1, slotCount - 1);
		int physicalX = initialX - pegXSpacing / 2 * p.y + p.x * pegXSpacing;
		int physicalY = initialY + p.y * pegYSpacing;
		return new Point(physicalX, physicalY);
	}

	/**
	 * Calculates all the in-flight bean positions (in physical coordinates) from
	 * _logic.
	 * 
	 * @return an array of in-flight bean positions
	 */
	private Point[] getBeanPositions() {
		Point[] positions = new Point[slotCount];
		for (int yPos = 0; yPos < slotCount; yPos++) {
			int xPos = logic.getInFlightBeanXPos(yPos);
			if (xPos != BeanCounterLogic.NO_BEAN_IN_YPOS) {
				if (yPos == slotCount - 1) {
					positions[yPos] = getFinalRowBeanPosition(xPos);
				} else {
					positions[yPos] = logicalToPhysical(new Point(xPos, yPos));
					positions[yPos].y -= PEG_SIZE + 5;
				}
			} else {
				positions[yPos] = null;
			}
		}
		return positions;
	}

	private Point[] copyPositions(Point[] positions) {
		Point[] copies = new Point[positions.length];
		for (int i = 0; i < positions.length; i++) {
			copies[i] = positions[i] == null ? null : new Point(positions[i]);
		}
		return copies;
	}

	/**
	 * Advance the machine one step.
	 */
	private void runOneStep() {
		// Get current positions
		currentBeanPositions = getBeanPositions();
		animationStartBeanPositions = copyPositions(currentBeanPositions);
		currentBeanPositions[slotCount - 1] = null;
		// Advance one step
		try {
			boolean changed = logic.advanceStep();
			if (debugEnabled && changed) {
				printDebugState();
			}
		} catch (BeanOutOfBoundsException ex) {
			System.err.println("Error: bean has gone out of bounds.");
		}
		// Get new positions
		animationEndBeanPositions = getBeanPositions();
		animationTimer.setDelay(frameDelay);
		animationTimer.setInitialDelay(frameDelay);
		if (!animationTimer.isRunning()) {
			animationTimer.start();
		}
		repaint();
	}

	private void printDebugState() {
		System.out.println(BeanCounterTextUI.renderMachine(logic));
	}

	private void advanceAnimation() {
		if (animationEndBeanPositions == null) {
			if (isRunning) {
				runOneStep();
			} else {
				animationTimer.stop();
			}
			return;
		}

		boolean allArrived = true;
		for (int i = 0; i < slotCount - 1; i++) {
			if (currentBeanPositions[i] != null && animationEndBeanPositions[i + 1] != null) {
				moveAlongLine(currentBeanPositions[i], animationStartBeanPositions[i], animationEndBeanPositions[i + 1],
						animationStride);
				allArrived &= currentBeanPositions[i].equals(animationEndBeanPositions[i + 1]);
			}
		}
		repaint();
		if (!allArrived) {
			return;
		}

		currentBeanPositions = getBeanPositions();
		animationEndBeanPositions = null;
		if (!isRunning || Arrays.stream(currentBeanPositions).noneMatch(position -> position != null)) {
			isRunning = false;
			animationTimer.stop();
			return;
		}
		animationTimer.setDelay(stepDelay);
	}

	/**
	 * Advance the machine one step. Stop the machine if running continuously.
	 */
	public void step() {
		isRunning = false;
		animationTimer.stop();
		frameDelay = SLOW_FRAME_DELAY;
		animationStride = SLOW_ANIMATION_STRIDE;
		runOneStep();
	}

	/**
	 * Run the machine continuously in slow mode.
	 */
	public void runSlow() {
		startContinuousRun(SLOW_FRAME_DELAY, SLOW_STEP_DELAY, SLOW_ANIMATION_STRIDE);
	}

	/**
	 * Run the machine continuously in fast mode.
	 */
	public void runFast() {
		startContinuousRun(FAST_FRAME_DELAY, FAST_STEP_DELAY, FAST_ANIMATION_STRIDE);
	}

	private void startContinuousRun(int frameDelay, int delayBetweenSteps, int animationStride) {
		isRunning = true;
		this.frameDelay = frameDelay;
		stepDelay = delayBetweenSteps;
		this.animationStride = animationStride;
		animationTimer.setDelay(frameDelay);
		animationTimer.setInitialDelay(frameDelay);
		animationTimer.stop();
		runOneStep();
	}

	public void stop() {
		isRunning = false;
		if (animationEndBeanPositions == null) {
			animationTimer.stop();
		}
	}

	public void lowerHalf() {
		logic.lowerHalf();
		repaint();
	}

	public void upperHalf() {
		logic.upperHalf();
		repaint();
	}

	/**
	 * Stops the machine and collects existing beans. If "lower half" or "upper
	 * half" was pressed previously, only that half of the beans is collected.
	 */
	public void collect() {
		isRunning = false;
		animationTimer.stop();
		logic.collect();
		animationEndBeanPositions = null;
		// collect() clears machine of in-flight beans, so refresh positions.
		currentBeanPositions = getBeanPositions();
		repaint();
	}

	/**
	 * Stop the machine reset with the original beans.
	 */
	public void reset() {
		isRunning = false;
		animationTimer.stop();
		logic.reset(beans);
		animationEndBeanPositions = null;
		// reset() clears machine of in-flight beans, so refresh positions.
		currentBeanPositions = getBeanPositions();
		repaint();
	}

	/**
	 * Draw the pegs in the machine.
	 * 
	 * @param g the graphics object
	 */
	public void drawPegs(Graphics g) {
		g.setColor(Color.BLACK);
		for (int i = 0; i < slotCount - 1; i++) {
			for (int j = 0; j <= i; j++) {
				Point p = logicalToPhysical(new Point(j, i));
				g.drawRect(p.x, p.y, PEG_SIZE, PEG_SIZE);
			}
		}
	}

	/**
	 * Draw the in-flight beans in the machine.
	 * 
	 * @param g the graphics object
	 */
	public void drawBeans(Graphics g) {
		g.setColor(Color.BLACK);
		for (int i = 0; i < slotCount; i++) {
			if (currentBeanPositions[i] != null) {
				g.fillOval(currentBeanPositions[i].x, currentBeanPositions[i].y, BEAN_SIZE, BEAN_SIZE);
			}
		}
	}

	/**
	 * Draw the bars that represent the number of beans in each slot.
	 * 
	 * @param g the graphics object
	 */
	public void drawBars(Graphics g) {
		g.setColor(Color.BLACK);
		for (int i = 0; i < slotCount; i++) {
			Dimension d = getSize();
			Point p = logicalToPhysical(new Point(i, slotCount));
			int barWidth = d.width / (slotCount + 1);
			int beanCount = logic.getSlotBeanCount(i);
			g.fillRect(p.x, d.height - BOTTOM_MARGIN - BAR_TEXT_HEIGHT - beanCount, barWidth, beanCount);
			g.drawString(String.valueOf(i), p.x + barWidth / 2, d.height - BOTTOM_MARGIN);
		}
	}

	/**
	 * Display some statistics such as the current average slot value and the
	 * remaining number of beans.
	 * 
	 * @param g the graphics object
	 */
	public void drawStats(Graphics g) {
		Dimension d = getSize();
		g.setFont(new Font("Courier", Font.PLAIN, 20));
		String average = "Average = " + BeanCounterTextUI.formatAverage(logic.getAverageSlotBeanCount());
		g.drawString(average, d.width - 200, TOP_MARGIN);
		String remaining = "Remaining = " + logic.getRemainingBeanCount();
		g.drawString(remaining, d.width - 200, TOP_MARGIN + 30);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (currentBeanPositions == null) {
			currentBeanPositions = getBeanPositions();
		}
		drawPegs(g);
		drawBeans(g);
		drawBars(g);
		drawStats(g);
	}

}
