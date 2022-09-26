package br.com.monkey.ecx.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
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

	@Getter
	private final List<Alias> aliases = new ArrayList<>();

	@Getter
	private String monetaryIdentification;

	private static MongoDBSearchConfiguration configuration;

	public static MongoDBSearchConfiguration configure(List<String> prohibitedFields, boolean userProgramInAllQueries) {
		configuration = new MongoDBSearchConfiguration(prohibitedFields, userProgramInAllQueries, PROGRAM_FIELD, "$");
		return configuration;
	}

	public static MongoDBSearchConfiguration configure(List<String> prohibitedFields, boolean userProgramInAllQueries,
			String programField) {
		configuration = new MongoDBSearchConfiguration(prohibitedFields, userProgramInAllQueries, programField, "$");
		return configuration;
	}

	public static MongoDBSearchConfiguration configure(List<String> prohibitedFields, boolean userProgramInAllQueries,
			Alias... aliases) {
		configuration = new MongoDBSearchConfiguration(prohibitedFields, userProgramInAllQueries, PROGRAM_FIELD, "$");
		configuration.aliases.addAll(stream(aliases).collect(toList()));
		return configuration;
	}

	public MongoDBSearchConfiguration addAlias(Alias alias) {
		this.aliases.add(alias);
		return this;
	}

	public MongoDBSearchConfiguration withMonetaryIdentification(String value) {
		if (!StringUtils.hasText(value)) {
			throw new IllegalArgumentException("you need to define a monetary identification");
		}
		this.monetaryIdentification = value;
		return this;
	}

	public static MongoDBSearchConfiguration getInstance() {
		if (configuration == null) {
			return new MongoDBSearchConfiguration(emptyList(), false, PROGRAM_FIELD, "$");
		}
		return configuration;
	}

}
