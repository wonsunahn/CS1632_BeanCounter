import java.util.Random;

public interface Bean {
	/**
	 * Returns the either BeanImpl or BeanBuggy instance depending on the Config.
	 * 
	 * @param isLuck whether the bean is in luck mode
	 * @param rand the random number generator 
	 * @return Bean object
	 */
	public static Bean createInstance(boolean isLuck, Random rand) {
		if (Config.getBuggyBeanCounterLogic()) {
			return new BeanBuggy(isLuck, rand);
		} else {
			return new BeanImpl(isLuck, rand);
		}
	}
}