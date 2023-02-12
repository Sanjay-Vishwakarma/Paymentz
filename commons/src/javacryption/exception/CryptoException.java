package javacryption.exception;

/**
 * Signals that a cryptographic exception has occurred, extends RuntimeException
 * 
 * @author Gabriel Andery
 * @version 1.0
 */
public class CryptoException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 2937872982389908084L;

	/**
	 * Constructs a new exception with null as its detail message
	 */
	public CryptoException() {
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message
	 * 
	 * @param message
	 *            the detail message
	 */
	public CryptoException(String message) {
		super(message);
	}

	/**
	 * Constructs a new exception with the specified cause
	 * 
	 * @param cause
	 *            the cause
	 */
	public CryptoException(Throwable cause) {
		super(cause);

	}

	/**
	 * Constructs a new exception with the specified detail message and cause
	 * 
	 * @param message
	 *            the detail message
	 * @param cause
	 *            the cause
	 */
	public CryptoException(String message, Throwable cause) {
		super(message, cause);
	}
}
