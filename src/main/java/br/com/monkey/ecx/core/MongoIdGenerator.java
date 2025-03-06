package br.com.monkey.ecx.core;

import org.apache.commons.lang3.RandomStringUtils;

public class MongoIdGenerator {

	public static String generateId() {
		return RandomStringUtils.random(10, true, true);
	}

}
