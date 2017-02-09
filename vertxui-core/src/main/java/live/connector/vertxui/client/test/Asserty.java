package live.connector.vertxui.client.test;

import java.util.Map;

/**
 * A small junit port, leaving about a handfull or assertions which need
 * external dependencies, inlining some methods to reduce the length of the
 * stacktrace, not allowing testing without a first argument which describes the
 * test (because GWT does not show _which_ assertion went wrong), and with a
 * javascript callable (window.asserty()).
 * 
 * @author Niels Gorisse
 *
 */
public class Asserty {

	/**
	 * Give a method that runs all your testcases and throws an exception when
	 * something goes wrong.
	 */
	public final native static String asserty(Map<Integer, Runnable> map)/*-{
																			$wnd.asserty = function(which) {
																			try{ @live.connector.vertxui.client.test.Asserty::doit(Ljava/lang/Integer;Ljava/util/Map;)
																			(which,map); 
																			} catch(e) { 
																			return e.message+"\n"+e.stack;
																			};
																			return null;
																			}
																			}-*/;

	private static void doit(Integer which, Map<Integer, Runnable> map) throws Exception {
		for (Integer key : map.keySet()) {
			if ((key + "").endsWith(which + "")) {
				map.get(key).run();
				return;
			}
		}
		throw new IllegalArgumentException("Method '" + which + "' was not defined.");
	}

	public static void failNoMessage() {
		throw new AssertionError(
				"Please extend this method with a message as first argument, otherwise you will not see in the stacktrace where it went wrong");
	}

	/**
	 * Asserts that a condition is true. If it isn't it throws an
	 * {@link AssertionError} with the given message.
	 *
	 * @param message
	 *            the identifying message for the {@link AssertionError}
	 *            (<code>null</code> okay)
	 * @param condition
	 *            condition to be checked
	 */
	static public void assertTrue(String message, boolean condition) {
		if (!condition) {
			if (message == null) {
				failNoMessage();
			}
			throw new AssertionError(message);
		}
	}

	/**
	 * Asserts that a condition is true. If it isn't it throws an
	 * {@link AssertionError} without a message.
	 *
	 * @param condition
	 *            condition to be checked
	 */
	static public void assertTrue(boolean condition) {
		failNoMessage();
	}

	/**
	 * Asserts that a condition is false. If it isn't it throws an
	 * {@link AssertionError} with the given message.
	 *
	 * @param message
	 *            the identifying message for the {@link AssertionError}
	 *            (<code>null</code> okay)
	 * @param condition
	 *            condition to be checked
	 */
	static public void assertFalse(String message, boolean condition) {
		assertTrue(message, !condition);
	}

	/**
	 * Asserts that a condition is false. If it isn't it throws an
	 * {@link AssertionError} without a message.
	 *
	 * @param condition
	 *            condition to be checked
	 */
	static public void assertFalse(boolean condition) {
		failNoMessage();
	}

	/**
	 * Fails a test with the given message.
	 *
	 * @param message
	 *            the identifying message for the {@link AssertionError}
	 *            (<code>null</code> okay)
	 * @see AssertionError
	 */

	/**
	 * Fails a test with no message.
	 */
	static public void fail() {
		throw new AssertionError("fails");
	}

	/**
	 * Asserts that two objects are equal. If they are not, an
	 * {@link AssertionError} is thrown with the given message. If
	 * <code>expected</code> and <code>actual</code> are <code>null</code>, they
	 * are considered equal.
	 *
	 * @param message
	 *            the identifying message for the {@link AssertionError}
	 *            (<code>null</code> okay)
	 * @param expected
	 *            expected value
	 * @param actual
	 *            actual value
	 * @throws Exception
	 */
	static public void assertEquals(String message, Object expected, Object actual) {
		if (message == null) {
			failNoMessage();
		}
		if (equalsRegardingNull(expected, actual)) {
			return;
		} else if (expected instanceof String && actual instanceof String) {
			throw new AssertionError(message + ": expected=" + (String) expected + " actual=" + (String) actual);
		} else {
			throw new AssertionError(format(message, expected, actual));
		}
	}

	private static boolean equalsRegardingNull(Object expected, Object actual) {
		if (expected == null) {
			return actual == null;
		}
		return isEquals(expected, actual);
	}

	private static boolean isEquals(Object expected, Object actual) {
		return expected.equals(actual);
	}

	/**
	 * Asserts that two objects are equal. If they are not, an
	 * {@link AssertionError} without a message is thrown. If
	 * <code>expected</code> and <code>actual</code> are <code>null</code>, they
	 * are considered equal.
	 *
	 * @param expected
	 *            expected value
	 * @param actual
	 *            the value to check against <code>expected</code>
	 * @throws Exception
	 */
	static public void assertEquals(Object expected, Object actual) {
		failNoMessage();
	}

	/**
	 * Asserts that two objects are <b>not</b> equals. If they are, an
	 * {@link AssertionError} is thrown with the given message. If
	 * <code>unexpected</code> and <code>actual</code> are <code>null</code>,
	 * they are considered equal.
	 *
	 * @param message
	 *            the identifying message for the {@link AssertionError}
	 *            (<code>null</code> okay)
	 * @param unexpected
	 *            unexpected value to check
	 * @param actual
	 *            the value to check against <code>unexpected</code>
	 */
	static public void assertNotEquals(String message, Object unexpected, Object actual) {
		if (equalsRegardingNull(unexpected, actual)) {
			failEquals(message, actual);
		}
	}

	/**
	 * Asserts that two objects are <b>not</b> equals. If they are, an
	 * {@link AssertionError} without a message is thrown. If
	 * <code>unexpected</code> and <code>actual</code> are <code>null</code>,
	 * they are considered equal.
	 *
	 * @param unexpected
	 *            unexpected value to check
	 * @param actual
	 *            the value to check against <code>unexpected</code>
	 */
	static public void assertNotEquals(Object unexpected, Object actual) {
		failNoMessage();
	}

	private static void failEquals(String message, Object actual) {
		if (message == null) {
			failNoMessage();
		}
		String formatted = "Values should be different. ";
		if (message != null) {
			formatted = message + ". ";
		}
		formatted += "Actual: " + actual;
		throw new AssertionError(formatted);
	}

	/**
	 * Asserts that two longs are <b>not</b> equals. If they are, an
	 * {@link AssertionError} is thrown with the given message.
	 *
	 * @param message
	 *            the identifying message for the {@link AssertionError}
	 *            (<code>null</code> okay)
	 * @param unexpected
	 *            unexpected value to check
	 * @param actual
	 *            the value to check against <code>unexpected</code>
	 */
	static public void assertNotEquals(String message, long unexpected, long actual) {
		if (unexpected == actual) {
			failEquals(message, Long.valueOf(actual));
		}
	}

	/**
	 * Asserts that two longs are <b>not</b> equals. If they are, an
	 * {@link AssertionError} without a message is thrown.
	 *
	 * @param unexpected
	 *            unexpected value to check
	 * @param actual
	 *            the value to check against <code>unexpected</code>
	 */
	static public void assertNotEquals(long unexpected, long actual) {
		failNoMessage();
	}

	/**
	 * Asserts that two doubles are <b>not</b> equal to within a positive delta.
	 * If they are, an {@link AssertionError} is thrown with the given message.
	 * If the unexpected value is infinity then the delta value is ignored. NaNs
	 * are considered equal:
	 * <code>assertNotEquals(Double.NaN, Double.NaN, *)</code> fails
	 *
	 * @param message
	 *            the identifying message for the {@link AssertionError}
	 *            (<code>null</code> okay)
	 * @param unexpected
	 *            unexpected value
	 * @param actual
	 *            the value to check against <code>unexpected</code>
	 * @param delta
	 *            the maximum delta between <code>unexpected</code> and
	 *            <code>actual</code> for which both numbers are still
	 *            considered equal.
	 */
	static public void assertNotEquals(String message, double unexpected, double actual, double delta) {
		if (!doubleIsDifferent(unexpected, actual, delta)) {
			failEquals(message, Double.valueOf(actual));
		}
	}

	/**
	 * Asserts that two doubles are <b>not</b> equal to within a positive delta.
	 * If they are, an {@link AssertionError} is thrown. If the unexpected value
	 * is infinity then the delta value is ignored.NaNs are considered equal:
	 * <code>assertNotEquals(Double.NaN, Double.NaN, *)</code> fails
	 *
	 * @param unexpected
	 *            unexpected value
	 * @param actual
	 *            the value to check against <code>unexpected</code>
	 * @param delta
	 *            the maximum delta between <code>unexpected</code> and
	 *            <code>actual</code> for which both numbers are still
	 *            considered equal.
	 */
	static public void assertNotEquals(double unexpected, double actual, double delta) {
		failNoMessage();
	}

	/**
	 * Asserts that two floats are <b>not</b> equal to within a positive delta.
	 * If they are, an {@link AssertionError} is thrown. If the unexpected value
	 * is infinity then the delta value is ignored.NaNs are considered equal:
	 * <code>assertNotEquals(Float.NaN, Float.NaN, *)</code> fails
	 *
	 * @param unexpected
	 *            unexpected value
	 * @param actual
	 *            the value to check against <code>unexpected</code>
	 * @param delta
	 *            the maximum delta between <code>unexpected</code> and
	 *            <code>actual</code> for which both numbers are still
	 *            considered equal.
	 */
	static public void assertNotEquals(float unexpected, float actual, float delta) {
		failNoMessage();
	}

	/**
	 * Asserts that two object arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown with the given message. If
	 * <code>expecteds</code> and <code>actuals</code> are <code>null</code>,
	 * they are considered equal.
	 *
	 * @param message
	 *            the identifying message for the {@link AssertionError}
	 *            (<code>null</code> okay)
	 * @param expecteds
	 *            Object array or array of arrays (multi-dimensional array) with
	 *            expected values.
	 * @param actuals
	 *            Object array or array of arrays (multi-dimensional array) with
	 *            actual values
	 */
	public static void assertArrayEquals(String message, Object[] expecteds, Object[] actuals) {
		internalArrayEquals(message, expecteds, actuals);
	}

	/**
	 * Asserts that two object arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown. If <code>expected</code> and
	 * <code>actual</code> are <code>null</code>, they are considered equal.
	 *
	 * @param expecteds
	 *            Object array or array of arrays (multi-dimensional array) with
	 *            expected values
	 * @param actuals
	 *            Object array or array of arrays (multi-dimensional array) with
	 *            actual values
	 * @throws Exception
	 */
	public static void assertArrayEquals(Object[] expecteds, Object[] actuals) {
		failNoMessage();
	}

	/**
	 * Asserts that two boolean arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown with the given message. If
	 * <code>expecteds</code> and <code>actuals</code> are <code>null</code>,
	 * they are considered equal.
	 *
	 * @param message
	 *            the identifying message for the {@link AssertionError}
	 *            (<code>null</code> okay)
	 * @param expecteds
	 *            boolean array with expected values.
	 * @param actuals
	 *            boolean array with expected values.
	 */
	public static void assertArrayEquals(String message, boolean[] expecteds, boolean[] actuals) {
		internalArrayEquals(message, expecteds, actuals);
	}

	/**
	 * Asserts that two boolean arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown. If <code>expected</code> and
	 * <code>actual</code> are <code>null</code>, they are considered equal.
	 *
	 * @param expecteds
	 *            boolean array with expected values.
	 * @param actuals
	 *            boolean array with expected values.
	 * @throws Exception
	 */
	public static void assertArrayEquals(boolean[] expecteds, boolean[] actuals) {
		failNoMessage();
	}

	/**
	 * Asserts that two byte arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown with the given message.
	 *
	 * @param message
	 *            the identifying message for the {@link AssertionError}
	 *            (<code>null</code> okay)
	 * @param expecteds
	 *            byte array with expected values.
	 * @param actuals
	 *            byte array with actual values
	 */
	public static void assertArrayEquals(String message, byte[] expecteds, byte[] actuals) {
		internalArrayEquals(message, expecteds, actuals);
	}

	/**
	 * Asserts that two byte arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown.
	 *
	 * @param expecteds
	 *            byte array with expected values.
	 * @param actuals
	 *            byte array with actual values
	 * @throws Exception
	 */
	public static void assertArrayEquals(byte[] expecteds, byte[] actuals) {
		failNoMessage();
	}

	/**
	 * Asserts that two char arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown with the given message.
	 *
	 * @param message
	 *            the identifying message for the {@link AssertionError}
	 *            (<code>null</code> okay)
	 * @param expecteds
	 *            char array with expected values.
	 * @param actuals
	 *            char array with actual values
	 */
	public static void assertArrayEquals(String message, char[] expecteds, char[] actuals) {
		internalArrayEquals(message, expecteds, actuals);
	}

	/**
	 * Asserts that two char arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown.
	 *
	 * @param expecteds
	 *            char array with expected values.
	 * @param actuals
	 *            char array with actual values
	 * @throws Exception
	 */
	public static void assertArrayEquals(char[] expecteds, char[] actuals) {
		failNoMessage();
	}

	/**
	 * Asserts that two short arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown with the given message.
	 *
	 * @param message
	 *            the identifying message for the {@link AssertionError}
	 *            (<code>null</code> okay)
	 * @param expecteds
	 *            short array with expected values.
	 * @param actuals
	 *            short array with actual values
	 */
	public static void assertArrayEquals(String message, short[] expecteds, short[] actuals) {
		internalArrayEquals(message, expecteds, actuals);
	}

	/**
	 * Asserts that two short arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown.
	 *
	 * @param expecteds
	 *            short array with expected values.
	 * @param actuals
	 *            short array with actual values
	 * @throws Exception
	 */
	public static void assertArrayEquals(short[] expecteds, short[] actuals) {
		failNoMessage();
	}

	/**
	 * Asserts that two int arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown with the given message.
	 *
	 * @param message
	 *            the identifying message for the {@link AssertionError}
	 *            (<code>null</code> okay)
	 * @param expecteds
	 *            int array with expected values.
	 * @param actuals
	 *            int array with actual values
	 */
	public static void assertArrayEquals(String message, int[] expecteds, int[] actuals) {
		internalArrayEquals(message, expecteds, actuals);
	}

	/**
	 * Asserts that two int arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown.
	 *
	 * @param expecteds
	 *            int array with expected values.
	 * @param actuals
	 *            int array with actual values
	 * @throws Exception
	 */
	public static void assertArrayEquals(int[] expecteds, int[] actuals) {
		failNoMessage();
	}

	/**
	 * Asserts that two long arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown with the given message.
	 *
	 * @param message
	 *            the identifying message for the {@link AssertionError}
	 *            (<code>null</code> okay)
	 * @param expecteds
	 *            long array with expected values.
	 * @param actuals
	 *            long array with actual values
	 */
	public static void assertArrayEquals(String message, long[] expecteds, long[] actuals) {
		internalArrayEquals(message, expecteds, actuals);
	}

	/**
	 * Asserts that two long arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown.
	 *
	 * @param expecteds
	 *            long array with expected values.
	 * @param actuals
	 *            long array with actual values
	 * @throws Exception
	 */
	public static void assertArrayEquals(long[] expecteds, long[] actuals) {
		failNoMessage();
	}

	/**
	 * Asserts that two double arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown with the given message.
	 *
	 * @param message
	 *            the identifying message for the {@link AssertionError}
	 *            (<code>null</code> okay)
	 * @param expecteds
	 *            double array with expected values.
	 * @param actuals
	 *            double array with actual values
	 * @param delta
	 *            the maximum delta between <code>expecteds[i]</code> and
	 *            <code>actuals[i]</code> for which both numbers are still
	 *            considered equal.
	 */
	// not implemented
	// public static void assertArrayEquals(String message, double[] expecteds,
	// double[] actuals, double delta)
	// {
	// new InexactComparisonCriteria(delta).arrayEquals(message, expecteds,
	// actuals);
	// }

	/**
	 * Asserts that two double arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown.
	 *
	 * @param expecteds
	 *            double array with expected values.
	 * @param actuals
	 *            double array with actual values
	 * @param delta
	 *            the maximum delta between <code>expecteds[i]</code> and
	 *            <code>actuals[i]</code> for which both numbers are still
	 *            considered equal.
	 * @throws Exception
	 */
	// not implemented
	// public static void assertArrayEquals(double[] expecteds, double[]
	// actuals, double delta) {
	// assertArrayEquals(null, expecteds, actuals, delta);
	// }

	/**
	 * Asserts that two float arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown with the given message.
	 *
	 * @param message
	 *            the identifying message for the {@link AssertionError}
	 *            (<code>null</code> okay)
	 * @param expecteds
	 *            float array with expected values.
	 * @param actuals
	 *            float array with actual values
	 * @param delta
	 *            the maximum delta between <code>expecteds[i]</code> and
	 *            <code>actuals[i]</code> for which both numbers are still
	 *            considered equal.
	 */
	// not implemented
	// public static void assertArrayEquals(String message, float[] expecteds,
	// float[] actuals, float delta)
	// {
	// new InexactComparisonCriteria(delta).arrayEquals(message, expecteds,
	// actuals);
	// }

	/**
	 * Asserts that two float arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown.
	 *
	 * @param expecteds
	 *            float array with expected values.
	 * @param actuals
	 *            float array with actual values
	 * @param delta
	 *            the maximum delta between <code>expecteds[i]</code> and
	 *            <code>actuals[i]</code> for which both numbers are still
	 *            considered equal.
	 * @throws Exception
	 */
	// not implemented
	// public static void assertArrayEquals(float[] expecteds, float[] actuals,
	// float delta) {
	// assertArrayEquals(null, expecteds, actuals, delta);
	// }

	/**
	 * Asserts that two object arrays are equal. If they are not, an
	 * {@link AssertionError} is thrown with the given message. If
	 * <code>expecteds</code> and <code>actuals</code> are <code>null</code>,
	 * they are considered equal.
	 *
	 * @param message
	 *            the identifying message for the {@link AssertionError}
	 *            (<code>null</code> okay)
	 * @param expecteds
	 *            Object array or array of arrays (multi-dimensional array) with
	 *            expected values.
	 * @param actuals
	 *            Object array or array of arrays (multi-dimensional array) with
	 *            actual values
	 */
	private static void internalArrayEquals(String message, Object expecteds, Object actuals) {
		if (!equalsRegardingNull(expecteds, actuals)) {
			throw new AssertionError(format(message, expecteds, actuals));
		}
	}

	/**
	 * Asserts that two doubles are equal to within a positive delta. If they
	 * are not, an {@link AssertionError} is thrown with the given message. If
	 * the expected value is infinity then the delta value is ignored. NaNs are
	 * considered equal: <code>assertEquals(Double.NaN, Double.NaN, *)</code>
	 * passes
	 *
	 * @param message
	 *            the identifying message for the {@link AssertionError}
	 *            (<code>null</code> okay)
	 * @param expected
	 *            expected value
	 * @param actual
	 *            the value to check against <code>expected</code>
	 * @param delta
	 *            the maximum delta between <code>expected</code> and
	 *            <code>actual</code> for which both numbers are still
	 *            considered equal.
	 */
	static public void assertEquals(String message, double expected, double actual, double delta) {
		if (doubleIsDifferent(expected, actual, delta)) {
			throw new AssertionError(format(message, Double.valueOf(expected), Double.valueOf(actual)));
		}
	}

	/**
	 * Asserts that two floats are equal to within a positive delta. If they are
	 * not, an {@link AssertionError} is thrown with the given message. If the
	 * expected value is infinity then the delta value is ignored. NaNs are
	 * considered equal: <code>assertEquals(Float.NaN, Float.NaN, *)</code>
	 * passes
	 *
	 * @param message
	 *            the identifying message for the {@link AssertionError}
	 *            (<code>null</code> okay)
	 * @param expected
	 *            expected value
	 * @param actual
	 *            the value to check against <code>expected</code>
	 * @param delta
	 *            the maximum delta between <code>expected</code> and
	 *            <code>actual</code> for which both numbers are still
	 *            considered equal.
	 */
	static public void assertEquals(String message, float expected, float actual, float delta) {
		if (floatIsDifferent(expected, actual, delta)) {
			throw new AssertionError(format(message, Float.valueOf(expected), Float.valueOf(actual)));
		}
	}

	/**
	 * Asserts that two floats are <b>not</b> equal to within a positive delta.
	 * If they are, an {@link AssertionError} is thrown with the given message.
	 * If the unexpected value is infinity then the delta value is ignored. NaNs
	 * are considered equal:
	 * <code>assertNotEquals(Float.NaN, Float.NaN, *)</code> fails
	 *
	 * @param message
	 *            the identifying message for the {@link AssertionError}
	 *            (<code>null</code> okay)
	 * @param unexpected
	 *            unexpected value
	 * @param actual
	 *            the value to check against <code>unexpected</code>
	 * @param delta
	 *            the maximum delta between <code>unexpected</code> and
	 *            <code>actual</code> for which both numbers are still
	 *            considered equal.
	 */
	static public void assertNotEquals(String message, float unexpected, float actual, float delta) {
		if (!floatIsDifferent(unexpected, actual, delta)) {
			failEquals(message, Float.valueOf(actual));
		}
	}

	static private boolean doubleIsDifferent(double d1, double d2, double delta) {
		if (Double.compare(d1, d2) == 0) {
			return false;
		}
		if ((Math.abs(d1 - d2) <= delta)) {
			return false;
		}

		return true;
	}

	static private boolean floatIsDifferent(float f1, float f2, float delta) {
		if (Float.compare(f1, f2) == 0) {
			return false;
		}
		if ((Math.abs(f1 - f2) <= delta)) {
			return false;
		}

		return true;
	}

	/**
	 * Asserts that two longs are equal. If they are not, an
	 * {@link AssertionError} is thrown with the given message.
	 *
	 * @param message
	 *            the identifying message for the {@link AssertionError}
	 *            (<code>null</code> okay)
	 * @param expected
	 *            long expected value.
	 * @param actual
	 *            long actual value
	 */
	static public void assertEquals(String message, long expected, long actual) {
		if (expected != actual) {
			throw new AssertionError(format(message, Long.valueOf(expected), Long.valueOf(actual)));
		}
	}

	/**
	 * Asserts that two doubles are equal to within a positive delta. If they
	 * are not, an {@link AssertionError} is thrown. If the expected value is
	 * infinity then the delta value is ignored.NaNs are considered equal:
	 * <code>assertEquals(Double.NaN, Double.NaN, *)</code> passes
	 *
	 * @param expected
	 *            expected value
	 * @param actual
	 *            the value to check against <code>expected</code>
	 * @param delta
	 *            the maximum delta between <code>expected</code> and
	 *            <code>actual</code> for which both numbers are still
	 *            considered equal.
	 */
	static public void assertEquals(double expected, double actual, double delta) {
		failNoMessage();
	}

	/**
	 * Asserts that two floats are equal to within a positive delta. If they are
	 * not, an {@link AssertionError} is thrown. If the expected value is
	 * infinity then the delta value is ignored. NaNs are considered equal:
	 * <code>assertEquals(Float.NaN, Float.NaN, *)</code> passes
	 *
	 * @param expected
	 *            expected value
	 * @param actual
	 *            the value to check against <code>expected</code>
	 * @param delta
	 *            the maximum delta between <code>expected</code> and
	 *            <code>actual</code> for which both numbers are still
	 *            considered equal.
	 */

	static public void assertEquals(float expected, float actual, float delta) {
		failNoMessage();
	}

	/**
	 * Asserts that an object isn't null. If it is an {@link AssertionError} is
	 * thrown with the given message.
	 *
	 * @param message
	 *            the identifying message for the {@link AssertionError}
	 *            (<code>null</code> okay)
	 * @param object
	 *            Object to check or <code>null</code>
	 */
	static public void assertNotNull(String message, Object object) {
		assertTrue(message, object != null);
	}

	/**
	 * Asserts that an object isn't null. If it is an {@link AssertionError} is
	 * thrown.
	 *
	 * @param object
	 *            Object to check or <code>null</code>
	 */
	static public void assertNotNull(Object object) {
		failNoMessage();
	}

	/**
	 * Asserts that an object is null. If it is not, an {@link AssertionError}
	 * is thrown with the given message.
	 *
	 * @param message
	 *            the identifying message for the {@link AssertionError}
	 *            (<code>null</code> okay)
	 * @param object
	 *            Object to check or <code>null</code>
	 */
	static public void assertNull(String message, Object object) {
		if (object == null) {
			return;
		}
		failNotNull(message, object);
	}

	/**
	 * Asserts that an object is null. If it isn't an {@link AssertionError} is
	 * thrown.
	 *
	 * @param object
	 *            Object to check or <code>null</code>
	 */
	static public void assertNull(Object object) {
		failNoMessage();
	}

	static private void failNotNull(String message, Object actual) {
		String formatted = "";
		if (message != null) {
			formatted = message + " ";
		}
		throw new AssertionError(formatted + "expected null, but was:<" + actual + ">");
	}

	/**
	 * Asserts that two objects refer to the same object. If they are not, an
	 * {@link AssertionError} is thrown with the given message.
	 *
	 * @param message
	 *            the identifying message for the {@link AssertionError}
	 *            (<code>null</code> okay)
	 * @param expected
	 *            the expected object
	 * @param actual
	 *            the object to compare to <code>expected</code>
	 */
	static public void assertSame(String message, Object expected, Object actual) {
		if (expected == actual) {
			return;
		}
		failNotSame(message, expected, actual);
	}

	/**
	 * Asserts that two objects refer to the same object. If they are not the
	 * same, an {@link AssertionError} without a message is thrown.
	 *
	 * @param expected
	 *            the expected object
	 * @param actual
	 *            the object to compare to <code>expected</code>
	 */
	static public void assertSame(Object expected, Object actual) {
		failNoMessage();
	}

	/**
	 * Asserts that two objects do not refer to the same object. If they do
	 * refer to the same object, an {@link AssertionError} is thrown with the
	 * given message.
	 *
	 * @param message
	 *            the identifying message for the {@link AssertionError}
	 *            (<code>null</code> okay)
	 * @param unexpected
	 *            the object you don't expect
	 * @param actual
	 *            the object to compare to <code>unexpected</code>
	 */
	static public void assertNotSame(String message, Object unexpected, Object actual) {
		if (unexpected == actual) {
			failSame(message);
		}
	}

	/**
	 * Asserts that two objects do not refer to the same object. If they do
	 * refer to the same object, an {@link AssertionError} without a message is
	 * thrown.
	 *
	 * @param unexpected
	 *            the object you don't expect
	 * @param actual
	 *            the object to compare to <code>unexpected</code>
	 */
	static public void assertNotSame(Object unexpected, Object actual) {
		assertNotSame(null, unexpected, actual);
	}

	static private void failSame(String message) {
		if (message == null) {
			failNoMessage();
		}
		throw new AssertionError(message + ": expected not same");
	}

	static private void failNotSame(String message, Object expected, Object actual) {
		if (message == null) {
			failNoMessage();
		}
		throw new AssertionError(message + ": expected same:<" + expected + "> was not:<" + actual + ">");
	}

	static String format(String message, Object expected, Object actual) {
		if (message == null) {
			failNoMessage();
		}
		String formatted = "";
		if (message != null && !message.equals("")) {
			formatted = message + " ";
		}
		String expectedString = String.valueOf(expected);
		String actualString = String.valueOf(actual);
		if (expectedString.equals(actualString)) {
			return formatted + "expected: " + formatClassAndValue(expected, expectedString) + " but was: "
					+ formatClassAndValue(actual, actualString);
		} else {
			return formatted + "expected:<" + expectedString + "> but was:<" + actualString + ">";
		}
	}

	private static String formatClassAndValue(Object value, String valueString) {
		String className = value == null ? "null" : value.getClass().getName();
		return className + "<" + valueString + ">";
	}

	/**
	 * Asserts that <code>actual</code> satisfies the condition specified by
	 * <code>matcher</code>. If not, an {@link AssertionError} is thrown with
	 * information about the matcher and failing value. Example:
	 *
	 * <pre>
	 *   assertThat(0, is(1)); // fails:
	 *     // failure message:
	 *     // expected: is &lt;1&gt;
	 *     // got value: &lt;0&gt;
	 *   assertThat(0, is(not(1))) // passes
	 * </pre>
	 *
	 * <code>org.hamcrest.Matcher</code> does not currently document the meaning
	 * of its type parameter <code>T</code>. This method assumes that a matcher
	 * typed as <code>Matcher&lt;T&gt;</code> can be meaningfully applied only
	 * to values that could be assigned to a variable of type <code>T</code>.
	 *
	 * @param <T>
	 *            the static type accepted by the matcher (this can flag obvious
	 *            compile-time problems such as {@code assertThat(1, is("a"))}
	 * @param actual
	 *            the computed value being compared
	 * @param matcher
	 *            an expression, built of {@link Matcher}s, specifying allowed
	 *            values
	 * @see org.hamcrest.CoreMatchers
	 * @see org.hamcrest.MatcherAssert
	 */
	// not implemented
	// public static <T> void assertThat(T actual, Matcher<? super T> matcher) {
	// assertThat("", actual, matcher);
	// }

	/**
	 * Asserts that <code>actual</code> satisfies the condition specified by
	 * <code>matcher</code>. If not, an {@link AssertionError} is thrown with
	 * the reason and information about the matcher and failing value. Example:
	 *
	 * <pre>
	 *   assertThat(&quot;Help! Integers don't work&quot;, 0, is(1)); // fails:
	 *     // failure message:
	 *     // Help! Integers don't work
	 *     // expected: is &lt;1&gt;
	 *     // got value: &lt;0&gt;
	 *   assertThat(&quot;Zero is one&quot;, 0, is(not(1))) // passes
	 * </pre>
	 *
	 * <code>org.hamcrest.Matcher</code> does not currently document the meaning
	 * of its type parameter <code>T</code>. This method assumes that a matcher
	 * typed as <code>Matcher&lt;T&gt;</code> can be meaningfully applied only
	 * to values that could be assigned to a variable of type <code>T</code>.
	 *
	 * @param reason
	 *            additional information about the error
	 * @param <T>
	 *            the static type accepted by the matcher (this can flag obvious
	 *            compile-time problems such as {@code assertThat(1, is("a"))}
	 * @param actual
	 *            the computed value being compared
	 * @param matcher
	 *            an expression, built of {@link Matcher}s, specifying allowed
	 *            values
	 * @see org.hamcrest.CoreMatchers
	 * @see org.hamcrest.MatcherAssert
	 */
	// not implemented
	// public static <T> void assertThat(String reason, T actual, Matcher<?
	// super T> matcher) {
	// MatcherAssert.assertThat(reason, actual, matcher);
	// }
}
