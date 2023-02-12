package javacryption.aes;

/**
 * Utility class
 * 
 * @author Gabriel Andery
 * @version 1.0
 */
public final class Util {

	/**
	 * Private constructor, stops initialisation
	 */
	private Util() {
	}

	/**
	 * Adds two byte arrays together
	 * 
	 * @param first
	 *            the first array
	 * @param second
	 *            the second array
	 * @return the unified array
	 */
	public static byte[] addByteArrays(byte[] first, byte[] second) {
		byte[] result = new byte[first.length + second.length];

		System.arraycopy(first, 0, result, 0, first.length);
		System.arraycopy(second, 0, result, first.length, second.length);

		return result;
	}

	/**
	 * Compares two byte arrays for equality
	 * 
	 * @param first
	 *            the first array
	 * @param second
	 *            the second array
	 * @return true if the arrays have identical contents
	 */
	public static final boolean areEqual(byte[] first, byte[] second) {
		int aLength = first.length;

		if (aLength != second.length)
			return false;

		for (int i = 0; i < aLength; i++)
			if (first[i] != second[i])
				return false;

		return true;
	}
}