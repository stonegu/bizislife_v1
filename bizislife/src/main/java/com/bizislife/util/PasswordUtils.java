package com.bizislife.util;

import java.io.IOException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Random;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Utilities for hashing user passwords.
 * To use it, make the salt, then use the salt to hash and check the passwords.
 * byte[] versions of salt and hash are 16 bytes long.
 * Base64 versions of salt ans hash are 24 characters long.
 *
 * @author Stone
 */
public class PasswordUtils {
	protected static Random random = new Random();

	/**
	 * Returns 16 bytes random-generated salt.
	 */
	public static byte[] makeSalt() {
		byte[] salt = new byte[16];
		random.nextBytes(salt);
		return salt;
	} // makeSalt

	/**
	 * Returns base64 encoded random-generated salt.
	 */
	public static String makeSaltBase64() {
		return Base64.encodeBytes(makeSalt());
	}

	/**
	 * Hashes the plaintext password using provided salt: PBKDF2WithHmacSHA1 128bit key length with 65536 iterations.
	 * Returns the byte array of the hashed password.
	 */
	public static byte[] hashPassword (String passwordPlaintext, byte[] salt) {
		try {
			KeySpec spec = new PBEKeySpec(passwordPlaintext.toCharArray(), salt, 65536, 128);
			SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] hash = f.generateSecret(spec).getEncoded();
			return hash;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	} // hashPassword

	/**
	 * Hashes the plaintext password (see {@link PasswordUtils#hashPassword}), given a plaintext password
	 * and base64-encoded salt. Returns base64 encoded hash.
	 */
	public static String hashPasswordBase64 (String passwordPlaintext, String saltBase64) {
		try {
			byte[] hash = hashPassword (passwordPlaintext, Base64.decode(saltBase64));
			return Base64.encodeBytes(hash);

		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	} // hashPasswordBase64

	/**
	 * Tests whether the passwordChallengePlaintext has the same hash as the given passwordHash/salt.
	 */
	public static boolean isEqual (String passwordChallengePlaintext, byte[] passwordHash, byte[] salt) {
		byte[] hash = hashPassword(passwordChallengePlaintext, salt);
		return Arrays.equals(passwordHash,hash);
	}

	/**
	 * Tests whether the passwordChallengePlaintext has the same hash as the given passwordHashBase64/saltBase64.
	 */
	public static boolean isEqual (String passwordChallengePlaintext, String passwordHashBase64, String saltBase64) {
		try {
			byte[] salt = Base64.decode(saltBase64);
			byte[] passwordHash = Base64.decode(passwordHashBase64);
			return isEqual(passwordChallengePlaintext, passwordHash, salt);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

}
