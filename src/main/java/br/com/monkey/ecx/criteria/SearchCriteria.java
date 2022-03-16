package br.com.monkey.ecx.criteria;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class SearchCriteria implements Serializable {

	private String key;

	private SearchOperation operation;

	private String value;

	private Enum enumValue;

	public SearchCriteria(final String key, final String operation, String prefix, final String value, String suffix) {
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

	public SearchCriteria changeKey(String key) {
		this.key = key;
		return this;
	}

	public SearchCriteria addEnumValue(Enum enumValue) {
		this.enumValue = enumValue;
		return this;
	}

}
