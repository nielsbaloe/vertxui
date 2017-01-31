package live.connector.vertxui.client;

/**
 * From
 * http://www.eclemma.org/jacoco/trunk/coverage/org.jacoco.core/org.jacoco.core.internal.data/CRC64.java.html
 *
 */
public final class CRC64 {

	private static final long POLY64REV = 0xd800000000000000L;

	private static final long[] LOOKUPTABLE;

	static {
		LOOKUPTABLE = new long[0x100];
		for (int i = 0; i < 0x100; i++) {
			long v = i;
			for (int j = 0; j < 8; j++) {
				if ((v & 1) == 1) {
					v = (v >>> 1) ^ POLY64REV;
				} else {
					v = (v >>> 1);
				}
			}
			LOOKUPTABLE[i] = v;
		}
	}

	/**
	 * Calculates the CRC64 checksum for the given data array.
	 * 
	 * @param data
	 *            data to calculate checksum for
	 * @return checksum value
	 */
	public static long checksum(final byte[] data) {
		long result = 0;
		for (final byte b : data) {
			final int lookupidx = ((int) result ^ b) & 0xff;
			result = (result >>> 8) ^ LOOKUPTABLE[lookupidx];
		}
		return result;
	}

	private CRC64() {
	}

}