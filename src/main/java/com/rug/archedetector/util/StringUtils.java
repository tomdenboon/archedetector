package com.rug.archedetector.util;

import java.util.Random;

public class StringUtils {
	/**
	 * Generates a random string of characters of a given length.
	 * @param length The length of the string.
	 * @return A random string.
	 */
	public static String random(int length) {
		final String alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder sb = new StringBuilder(length);
		Random rand = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(alphabet.charAt(rand.nextInt(alphabet.length())));
		}
		return sb.toString();
	}
}
