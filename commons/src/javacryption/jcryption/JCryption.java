package javacryption.jcryption;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javacryption.exception.CryptoException;

/**
 * 
 * jCryption implementation for Java
 * 
 * @author Gabriel Andery
 * @version 1.0
 */
public class JCryption {

	/** RSA key pair **/
	private KeyPair keyPair = null;

	/** Key size in bits **/
	private int keyLength = 1024;

	/**
	 * Initializes a newly created JCryption object and generates a
	 * <code>KeyPair</code> with 1024 bits
	 */
	public JCryption() {
		generateKeyPair(keyLength);
	}

	/**
	 * Initializes a newly created JCryption object and generates a
	 * <code>KeyPair</code> with the specified length
	 * 
	 * @param keyLength
	 *            key length in bits
	 */
	public JCryption(int keyLength) {
		generateKeyPair(keyLength);
	}

	/**
	 * Initializes a newly created JCryption object with the specified
	 * <code>KeyPair</code>
	 * 
	 * @param keyPair
	 *            the <code>KeyPair</code> to be set
	 */
	public JCryption(KeyPair keyPair) {
		setKeyPair(keyPair);
	}

	/**
	 * Returns the <code>KeyPair</code>
	 * 
	 * @return the <code>KeyPair</code> object
	 */
	public KeyPair getKeyPair() {
		return keyPair;
	}

	/**
	 * Sets the specified <code>KeyPair</code> object
	 * 
	 * @param keyPair
	 *            the <code>KeyPair</code> to be set
	 */
	public void setKeyPair(KeyPair keyPair) {
		this.keyPair = keyPair;
		this.keyLength = ((RSAPublicKey) keyPair.getPublic()).getModulus()
				.bitLength();
	}

	/**
	 * Returns the key length in bits
	 * 
	 * @return the key length
	 */
	public int getKeyLength() {
		return keyLength;
	}

	/**
	 * Generates a <code>KeyPair</code> with the specified length in bits
	 * 
	 * @param keyLength
	 *            the key length in bits
	 */
	public void generateKeyPair(int keyLength) {
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(keyLength);
			this.keyPair = kpg.generateKeyPair();
			this.keyLength = keyLength;
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoException("Error obtaining RSA algorithm", e);
		}
	}

	/**
	 * Returns the <code>KeyPair</code> modulus
	 * 
	 * @return the modulus as a <code>String</code>
	 */
	public String getKeyModulus() {
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		return publicKey.getModulus().toString(16);
	}

	/**
	 * Returns the public exponent of the <code>KeyPair</code>
	 * 
	 * @return the public exponent as a <code>String</code>
	 */
	public String getPublicExponent() {
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		return publicKey.getPublicExponent().toString(16);
	}

	/**
	 * Returns the maximum block size
	 * 
	 * @return the maximum block size
	 */
	public int getMaxDigits() {
		return ((keyLength * 2) / 16) + 3;
	}

	/**
	 * Decrypts a <code>String</code> using RSA
	 * 
	 * @param encrypted
	 *            encrypted <code>String</code>
	 * @return the decrypted <code>String</code>
	 */
	public String decrypt(String encrypted) {
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		String[] blocks = encrypted.split("\\s");
		String result = "";

		for (int i = 0; i < blocks.length; i++) {
			BigInteger data = new BigInteger(blocks[i], 16);
			BigInteger decryptedBlock = data.modPow(
					privateKey.getPrivateExponent(), publicKey.getModulus());
			result += decodeBigIntToHex(decryptedBlock);
		}

		return redundancyCheck(result);
	}

	/**
	 * Converts a BigInteger to the ascii code
	 * 
	 * @param bigint
	 *            the <code>BigInteger</code> to be decoded
	 * @return the decoded <code>String</code>
	 */
	private String decodeBigIntToHex(BigInteger bigint) {
		String message = "";
		while (bigint.compareTo(new BigInteger("0")) != 0) {
			BigInteger ascii = bigint.mod(new BigInteger("256"));
			bigint = bigint.divide(new BigInteger("256"));
			message += (char) (ascii.intValue());
		}
		return message;
	}

	/**
	 * Removes the redundancy code in the decrypted <code>String</code>
	 * 
	 * @param string
	 *            the decrypted <code>String</code> containing the redundancy
	 *            code
	 * @return the validated <code>String</code> without the redundancy code
	 */
	private String redundancyCheck(String string) {
		String r1 = string.substring(0, 2);
		String r2 = string.substring(2);
		int check = Integer.parseInt(r1, 16);
		String value = r2;
		int sum = 0;
		for (int i = 0; i < value.length(); i++) {
			sum += value.charAt(i);
		}
		if (check == (sum & 0xFF)) {
			return value;
		} else {
			return null;
		}
	}
}
