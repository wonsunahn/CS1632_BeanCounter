import java.util.Random;

public interface Bean {
	/**
	 * Returns the either BeanImpl or BeanBuggy instance depending on the Config.
	 * 
	 * @param slotCount the number of slots in the machine
	 * @param isLuck whether the bean is in luck mode
	 * @param rand the random number generator 
	 * @return Bean object
	 */
	public static Bean createInstance(int slotCount, boolean isLuck, Random rand) {
		switch (Config.getLogicType()) {
		case IMPL:
			return new BeanImpl(slotCount, isLuck, rand);
		case BUGGY:
			return new BeanBuggy(slotCount, isLuck, rand);
		case SOLUTION:
			return new BeanSolution(slotCount, isLuck, rand);
		default:
			assert(false);
			return null;
		}
	}
}