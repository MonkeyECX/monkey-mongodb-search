package br.com.monkey.ecx.criteria;

import br.com.monkey.ecx.core.exception.BadRequestException;
import lombok.Getter;

import java.io.Serializable;
import java.util.regex.Pattern;

import static br.com.monkey.ecx.configuration.MongoDBSearchConfiguration.getInstance;
import static br.com.monkey.ecx.core.MongoSearchGovernmentIdUtils.isValidGovernmentId;
import static lombok.AccessLevel.NONE;

@Getter
public class SearchCriteria implements Serializable {

	Pattern BOOLEAN = Pattern.compile("true|false", Pattern.CASE_INSENSITIVE);

	Pattern NUMBER = Pattern.compile("-?\\d+(\\.\\d+)?");

	private String key;

	private SearchOperation operation;

	@Getter(NONE)
	private String value;

	private Enum enumValue;

	public SearchCriteria(final String key, final String operation, String prefix, final String value, String suffix) {

		if (getInstance().getProhibitedFields().contains(key)) {
			throw new BadRequestException("The field: " + key + " can't be used in query search.");
		}

		SearchOperation op = SearchOperation.getSimpleOperation(operation.charAt(0));
		if (op != null) {

			boolean startsWithAsterisk = prefix != null && prefix.contains(SearchOperation.LIKE);
			boolean endsWithAsterisk = suffix != null && suffix.contains(SearchOperation.LIKE);

			if (op.equals(SearchOperation.EQUAL) && startsWithAsterisk && endsWithAsterisk) {
				op = SearchOperation.CONTAINS;
			}
			else if (op.equals(SearchOperation.EQUAL) && startsWithAsterisk) {
				op = SearchOperation.CONTAINS;
			}
			else if (op.equals(SearchOperation.EQUAL) && endsWithAsterisk) {
				op = SearchOperation.CONTAINS;
			}

			if (op.equals(SearchOperation.NOT) && startsWithAsterisk && endsWithAsterisk) {
				op = SearchOperation.NOT_CONTAINS;
			}
			else if (op.equals(SearchOperation.NOT) && startsWithAsterisk) {
				op = SearchOperation.NOT_CONTAINS;
			}
			else if (op.equals(SearchOperation.NOT) && endsWithAsterisk) {
				op = SearchOperation.NOT_CONTAINS;
			}
		}

		this.key = key;
		this.operation = op;
		this.value = value;
	}

	public Object getValue() {
		String monetaryIdentification = getInstance().getMonetaryIdentification();
		if (isValidGovernmentId(value)) {
			return value;
		}
		if (value.startsWith(monetaryIdentification)) {
			return value.replace(monetaryIdentification, "");
		}
		else if (BOOLEAN.matcher(value).matches()) {
			return Boolean.valueOf(value);
		}
		else if (NUMBER.matcher(value).matches()) {
			return Double.valueOf(value);
		}
		return value;
	}

	public String getValueAsString() {
		return value;
	}

}
