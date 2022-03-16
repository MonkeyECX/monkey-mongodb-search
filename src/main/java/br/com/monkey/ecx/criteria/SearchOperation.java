package br.com.monkey.ecx.criteria;

public enum SearchOperation {

	EQUAL, NOT, GREATER_THAN_EQUAL, LESS_THAN_EQUAL, CONTAINS, NOT_CONTAINS;

	public static final String LIKE = "*";

	public static final String OR = "OR";

	public static final String AND = "AND";

	public static SearchOperation getSimpleOperation(final char input) {
		switch (input) {
		case ':':
			return EQUAL;
		case '!':
			return NOT;
		case '>':
			return GREATER_THAN_EQUAL;
		case '<':
			return LESS_THAN_EQUAL;
		default:
			return null;
		}
	}

}
