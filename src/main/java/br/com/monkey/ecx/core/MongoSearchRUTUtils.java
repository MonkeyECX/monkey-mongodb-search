package br.com.monkey.ecx.core;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MongoSearchRUTUtils {

	private static final int[] MULTIPLIERS = new int[] { 2, 3, 4, 5, 6, 7 };

	public static boolean isValidRUT(String RUTNumber) {
		Pattern pattern = Pattern.compile("^[0-9]{7,8}[0-9kK]$");
		Matcher matcher = pattern.matcher(RUTNumber);
		if (!matcher.matches())
			return false;

		String lastRUTDigit = RUTNumber.substring(RUTNumber.length() - 1);
		String RUTWithoutLastDigit = RUTNumber.substring(0, RUTNumber.length() - 1);
		return lastRUTDigit.toUpperCase().equals(generateCheckDigit(RUTWithoutLastDigit));
	}

	private static String generateCheckDigit(String RUTNumber) {
		char[] reverseRUTChars = StringUtils.reverse(RUTNumber).toCharArray();

		int multiplierIndex = 0;
		int sum = 0;
		for (char digit : reverseRUTChars) {
			sum += Integer.parseInt(String.valueOf(digit)) * MULTIPLIERS[multiplierIndex];
			multiplierIndex = ((multiplierIndex + 1) == MULTIPLIERS.length) ? 0 : multiplierIndex + 1;
		}
		int mod11 = sum % 11;

		String checkDigit = String.valueOf(11 - mod11);
		if (checkDigit.equals("11")) {
			checkDigit = "0";
		}
		if (checkDigit.equals("10")) {
			checkDigit = "K";
		}
		return checkDigit;
	}

	public static String format(String RUTNumber) {
		final DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance();

		final DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
		symbols.setGroupingSeparator('.');

		decimalFormat.setDecimalFormatSymbols(symbols);
		decimalFormat.setMaximumFractionDigits(0);
		decimalFormat.setMinimumFractionDigits(0);
		decimalFormat.setGroupingSize(3);
		decimalFormat.setGroupingUsed(true);

		String lastRUTDigit = RUTNumber.substring(RUTNumber.length() - 1);
		String RUTWithoutLastDigit = RUTNumber.substring(0, RUTNumber.length() - 1);
		return decimalFormat.format(Integer.parseInt(RUTWithoutLastDigit)) + "-" + lastRUTDigit;
	}

}
