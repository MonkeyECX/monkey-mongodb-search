package br.com.monkey.ecx.core;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.regex.Pattern;

import static br.com.monkey.ecx.configuration.MongoDBSearchConfiguration.getInstance;
import static br.com.monkey.ecx.core.MongoSearchGovernmentIdUtils.isValidGovernmentId;

public class ValueParser {

	static Pattern BOOLEAN = Pattern.compile("true|false", Pattern.CASE_INSENSITIVE);

	static Pattern NUMBER = Pattern.compile("-?\\d+(\\.\\d+)?");

	public static boolean isTemporal(String rawValue) {
		if (rawValue == null || rawValue.trim().isEmpty()) {
			return false;
		}
		String value = rawValue.trim();
		return canParseLocalDate(value) || canParseLocalDateTime(value);
	}

	public static boolean isDateOnly(String value) {
		if (value == null || value.trim().isEmpty()) {
			return false;
		}
		try {
			LocalDate.parse(value.trim());
			return true;
		}
		catch (DateTimeParseException e) {
			return false;
		}
	}

	public static boolean isDateTimeToMinute(String value) {
		if (value == null || value.trim().isEmpty()) {
			return false;
		}

		return value.trim().matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}$");
	}

	public static Date startOfMinute(String value) {
		LocalDateTime localDateTime = LocalDateTime.parse(value.trim());
		return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}

	public static Date nextMinute(String value) {
		LocalDateTime localDateTime = LocalDateTime.parse(value.trim()).plusMinutes(1);
		return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}

	public static Date parseToDate(String rawValue) {
		String value = rawValue.trim();

		try {
			LocalDateTime localDateTime = LocalDateTime.parse(value);
			return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
		}
		catch (DateTimeParseException ignored) {
		}

		try {
			LocalDate localDate = LocalDate.parse(value);
			return Date.from(localDate.atStartOfDay(ZoneOffset.UTC).toInstant());
		}
		catch (DateTimeParseException ignored) {
		}

		throw new IllegalArgumentException("Invalid temporal value: " + rawValue);
	}

	public static Date startOfDay(String value) {
		LocalDate localDate = LocalDate.parse(value.trim());
		return Date.from(localDate.atStartOfDay(ZoneOffset.UTC).toInstant());
	}

	public static Date nextDayStartOfDay(String value) {
		LocalDate localDate = LocalDate.parse(value.trim()).plusDays(1);
		return Date.from(localDate.atStartOfDay(ZoneOffset.UTC).toInstant());
	}

	private static boolean canParseLocalDateTime(String value) {
		try {
			LocalDateTime.parse(value);
			return true;
		}
		catch (DateTimeParseException e) {
			return false;
		}
	}

	private static boolean canParseLocalDate(String value) {
		try {
			LocalDate.parse(value);
			return true;
		}
		catch (DateTimeParseException e) {
			return false;
		}
	}

	public static Object convertScalarValue(String value) {
		String monetaryIdentification = getInstance().getMonetaryIdentification();

		if (isValidGovernmentId(value)) {
			return value;
		}

		if (value.startsWith(monetaryIdentification)) {
			return value.replace(monetaryIdentification, "");
		}

		if (BOOLEAN.matcher(value).matches()) {
			return Boolean.valueOf(value);
		}

		if (NUMBER.matcher(value).matches()) {
			try {
				return Integer.valueOf(value);
			}
			catch (NumberFormatException ignored) {
			}

			try {
				return Long.valueOf(value);
			}
			catch (NumberFormatException ignored) {
			}
			return Double.valueOf(value);
		}
		return value;
	}

}
