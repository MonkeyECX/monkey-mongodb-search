package br.com.monkey.ecx.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import static java.util.Collections.emptyList;
import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public class MongoDBSearchConfiguration {

	private static final String PROGRAM_FIELD = "program";

	@Getter
	private final List<String> prohibitedFields;

	@Getter
	private final boolean userProgramInAllQueries;

	@Getter
	private final String programField;

	private static MongoDBSearchConfiguration configuration;

	public static MongoDBSearchConfiguration configure(List<String> prohibitedFields, boolean userProgramInAllQueries) {
		configuration = new MongoDBSearchConfiguration(prohibitedFields, userProgramInAllQueries, PROGRAM_FIELD);
		return configuration;
	}

	public static MongoDBSearchConfiguration configure(List<String> prohibitedFields, boolean userProgramInAllQueries,
			String programField) {
		configuration = new MongoDBSearchConfiguration(prohibitedFields, userProgramInAllQueries, programField);
		return configuration;
	}

	public static MongoDBSearchConfiguration getInstance() {
		if (configuration == null) {
			return new MongoDBSearchConfiguration(emptyList(), false, PROGRAM_FIELD);
		}
		return configuration;
	}

}
