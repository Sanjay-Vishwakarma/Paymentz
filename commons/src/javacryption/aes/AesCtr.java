package javacryption.aes;

import javacryption.exception.CryptoException;

import org.apache.commons.codec.binary.Base64;

/**
 * AES CTR mode implementation
 * 
 * @author Gabriel Andery
 * @version 1.0
 */
public class AesCtr {

	/**
	 * Private constructor, stops initialisation
	 */
	private AesCtr() {
	}

	/**
	 * Encrypts a <code>String</code> using AES encryption in Counter mode of
	 * operation
	 * 
	 * @param plaintext
	 *            source text to be encrypted
	 * @param password
	 *            the password to use to generate a key
	 * @param nBits
	 *            number of bits to be used in the key (128, 192, or 256)
	 * @return the encrypted <code>String</code>
	 */
	public static String encrypt(String plaintext, String password, int nBits) {
		Rijndael aes = new Rijndael();

		/** Standard allows 128/192/256 bit keys **/
		if (!(nBits == 128 || nBits == 192 || nBits == 256))
			throw new CryptoException("Invalid key size (" + nBits + " bits)");

		/** Uses AES itself to encrypt password to get cipher key **/
		int nBytes = nBits / 8;
		byte[] pwBytes = new byte[nBytes];

		/** Uses 1st 16/24/32 chars of password for key **/
		for (int i = 0; i < nBytes; i++) {
			pwBytes[i] = (byte) password.charAt(i);
		}

		/** Creates a 16-byte key (block size) **/
		aes.makeKey(pwBytes, 256, Rijndael.DIR_ENCRYPT);
		byte[] key = aes.encryptBlock(pwBytes, new byte[Rijndael.BLOCK_SIZE]);
		aes.finalize();

		/** Expands key to 16/24/32 bytes long **/
		if (nBytes > 16) {
			byte[] keySlice = new byte[nBytes - 16];
			for (int i = 0; i < nBytes - 16; i++)
				keySlice[i] = key[i];
			key = Util.addByteArrays(key, keySlice);
		}

		/**
		 * Initialises 1st 8 bytes of counter block with nonce (NIST SP800-38A
		 * B.2): [0-1] = millisec, [2-3] = random, [4-7] = seconds
		 **/
		byte[] counterBlock = new byte[Rijndael.BLOCK_SIZE];

		/** Timestamp: milliseconds since 1-Jan-1970 **/
		long nonce = (new java.util.Date()).getTime();
		int nonceMs = (int) nonce % 1000;
		int nonceSec = (int) Math.floor(nonce / 1000);
		int nonceRnd = (int) Math.floor(Math.random() * 0xffff);

		/** Copies values to counter block **/
		for (int i = 0; i < 2; i++)
			counterBlock[i] = (byte) ((nonceMs >>> i * 8) & 0xff);
		for (int i = 0; i < 2; i++)
			counterBlock[i + 2] = (byte) ((nonceRnd >>> i * 8) & 0xff);
		for (int i = 0; i < 4; i++)
			counterBlock[i + 4] = (byte) ((nonceSec >>> i * 8) & 0xff);

		/** Creates a header for the encrypted text **/
		byte[] ctrTxt = new byte[8];
		for (int i = 0; i < 8; i++)
			ctrTxt[i] = counterBlock[i];

		/** Initialises Rijndael algorithm **/
		aes.makeKey(key, 256, Rijndael.DIR_ENCRYPT);

		/** Calculates number of blocks **/
		int blockCount = (int) Math.ceil(new Float(plaintext.length())
				/ Rijndael.BLOCK_SIZE);

		/** Variable to store encrypted text **/
		byte[] ciphertxt = new byte[plaintext.length()];

		/** Encrypts block by block **/
		for (int b = 0; b < blockCount; b++) {
			/** Initialises last 8 bytes of counter block with block number **/
			for (int c = 0; c < 4; c++)
				counterBlock[15 - c] = (byte) ((b >>> c * 8) & 0xff);
			for (int c = 0; c < 4; c++)
				counterBlock[15 - c - 4] = (byte) 0;

			/** Encrypts counter block **/
			byte[] cipherCntr = aes.encryptBlock(counterBlock,
					new byte[Rijndael.BLOCK_SIZE]);

			/** Block size (size may be reduced on final block) **/
			int blockLength = b < blockCount - 1 ? Rijndael.BLOCK_SIZE
					: (plaintext.length() - 1) % Rijndael.BLOCK_SIZE + 1;

			/** Xor plaintext with ciphered counter byte by byte **/
			for (int i = 0; i < blockLength; i++) {
				ciphertxt[b * Rijndael.BLOCK_SIZE + i] = (byte) (cipherCntr[i] ^ plaintext
						.charAt(b * Rijndael.BLOCK_SIZE + i));
			}
		}

		/** Finalizes Rijndael algorithm **/
		aes.finalize();

		/** Joins header with encrypted text **/
		byte[] ciphertext = Util.addByteArrays(ctrTxt, ciphertxt);

		/** Encodes in Base64 **/
		String ciphertext64 = new String(Base64.encodeBase64(ciphertext));

		return ciphertext64;
	}

	/**
	 * Decrypts a <code>String</code> encrypted by AES in counter mode of
	 * operation
	 * 
	 * @param ciphertext
	 *            source text to be encrypted
	 * @param password
	 *            the password to use to generate a key
	 * @param nBits
	 *            number of bits to be used in the key (128, 192, or 256)
	 * @return the decrypted <code>String</code>
	 */
	public static String decrypt(String ciphertext, String password, int nBits) {
		Rijndael aes = new Rijndael();

		/** Standard allows 128/192/256 bit keys **/
		if (!(nBits == 128 || nBits == 192 || nBits == 256))
			return null;

		/** Decodes from Base64 **/
		byte[] cipherByte = Base64.decodeBase64(ciphertext.getBytes());

		/** Uses AES itself to encrypt password to get cipher key **/
		int nBytes = nBits / 8;
		byte[] pwBytes = new byte[nBytes];

		/** Uses 1st 16/24/32 chars of password for key **/
		for (int i = 0; i < nBytes; i++) {
			pwBytes[i] = (byte) password.charAt(i);
		}

		/** Creates a 16-byte key (block size) **/
		aes.makeKey(pwBytes, 256, Rijndael.DIR_ENCRYPT);
		byte[] key = aes.encryptBlock(pwBytes, new byte[Rijndael.BLOCK_SIZE]);
		aes.finalize();

		/** Expands key to 24/32 bytes long **/
		if (nBytes > 16) {
			byte[] keySlice = new byte[nBytes - 16];
			for (int i = 0; i < nBytes - 16; i++)
				keySlice[i] = key[i];
			key = Util.addByteArrays(key, keySlice);
		}

		/** Recovers nonce from 1st 8 bytes of ciphertext **/
		byte[] counterBlock = new byte[Rijndael.BLOCK_SIZE];
		for (int i = 0; i < 8; i++)
			counterBlock[i] = cipherByte[i];

		/** Initialises Rijndael algorithm **/
		aes.makeKey(key, 256, Rijndael.DIR_ENCRYPT);

		/** Calculates number of blocks **/
		int blockCount = (int) Math.ceil(new Float(cipherByte.length - 8)
				/ Rijndael.BLOCK_SIZE);

		/** Variable to store plain text **/
		byte[] plaintxt = new byte[cipherByte.length - 8];

		/** Decrypts block by block **/
		for (int b = 0; b < blockCount; b++) {
			/** Initialises last 8 bytes of counter block with block number **/
			for (int c = 0; c < 4; c++)
				counterBlock[15 - c] = (byte) ((b >>> c * 8) & 0xff);
			for (int c = 0; c < 4; c++)
				counterBlock[15 - c - 4] = (byte) 0;

			/** Encrypts counter block **/
			byte[] cipherCntr = aes.encryptBlock(counterBlock,
					new byte[Rijndael.BLOCK_SIZE]);

			/** Block size (size may be reduced on final block) **/
			int blockLength = b < blockCount - 1 ? Rijndael.BLOCK_SIZE
					: (cipherByte.length - 9) % Rijndael.BLOCK_SIZE + 1;

			/** Xor plaintext with ciphered counter byte by byte **/
			for (int i = 0; i < blockLength; i++) {
				plaintxt[b * Rijndael.BLOCK_SIZE + i] = (byte) (cipherCntr[i] ^ cipherByte[8
						+ b * Rijndael.BLOCK_SIZE + i]);
			}
		}

		/** Finalizes Rijndael algorithm **/
		aes.finalize();

		/** Creates final string **/
		String plaintext = new String(plaintxt);

		return plaintext;
	}
}
