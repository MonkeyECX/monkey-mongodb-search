package br.com.monkey.ecx.parser;

import br.com.monkey.ecx.QueryBaseVisitor;
import br.com.monkey.ecx.QueryParser;
import br.com.monkey.ecx.criteria.MonkeyCriteria;
import br.com.monkey.ecx.criteria.SearchCriteria;
import br.com.monkey.ecx.criteria.SearchOperation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static br.com.monkey.ecx.criteria.SearchOperation.*;
import static java.util.Objects.nonNull;

class QueryVisitor<T> extends QueryBaseVisitor<MonkeyCriteria> {

	private final Pattern REGEX = Pattern.compile("^(\\*?)(.+?)(\\*?)$");

	private static final Map<SearchOperation, Function<SearchCriteria, MonkeyCriteria>> FILTER_CRITERIA = new HashMap<>();

	// Create map of filter
	static {
		FILTER_CRITERIA.put(EQUAL, condition -> MonkeyCriteria.where(condition.getKey()).is(condition.getValue()));
		FILTER_CRITERIA.put(NOT, condition -> MonkeyCriteria.where(condition.getKey()).ne(condition.getValue()));
		FILTER_CRITERIA.put(GREATER_THAN_EQUAL,
				condition -> MonkeyCriteria.where(condition.getKey()).gte(condition.getValue()));
		FILTER_CRITERIA.put(LESS_THAN_EQUAL,
				condition -> MonkeyCriteria.where(condition.getKey()).lte(condition.getValue()));
		FILTER_CRITERIA.put(CONTAINS,
				condition -> MonkeyCriteria.where(condition.getKey()).regex(condition.getValue()));
		FILTER_CRITERIA.put(NOT_CONTAINS,
				condition -> MonkeyCriteria.where(condition.getKey()).not().regex(condition.getValue()));
	}

	@Override
	public MonkeyCriteria visitInput(QueryParser.InputContext ctx) {
		return visit(ctx.query());
	}

	@Override
	public MonkeyCriteria visitAtomQuery(QueryParser.AtomQueryContext ctx) {
		return visit(ctx.criteria());
	}

	@Override
	public MonkeyCriteria visitPriorityQuery(QueryParser.PriorityQueryContext ctx) {
		return visit(ctx.query());
	}

	@Override
	public MonkeyCriteria visitOpQuery(QueryParser.OpQueryContext ctx) {
		MonkeyCriteria left = visit(ctx.left);
		MonkeyCriteria right = visit(ctx.right);
		String op = ctx.logicalOp.getText();

		if (op.equalsIgnoreCase(SearchOperation.AND)) {
			return left.addAndClause(left).addAndClause(right);
		}
		else if (op.equalsIgnoreCase(SearchOperation.OR)) {
			return left.addOrClause(left).addOrClause(right);
		}
		else {
			return left.addAndClause(left).addAndClause(right);
		}
	}

	@Override
	public MonkeyCriteria visitCriteria(QueryParser.CriteriaContext ctx) {
		String key = ctx.key().getText();
		String op = ctx.op().getText();
		String value = ctx.value().getText();

		if (nonNull(ctx.value().STRING())) {
			value = value.replace("'", "").replace("\"", "").replace("\\\"", "\"").replace("\\'", "'");
		}
		Matcher matchResult = REGEX.matcher(value);
		SearchCriteria criteria;
		if (matchResult.matches()) {
			criteria = new SearchCriteria(key, op, matchResult.group(1), matchResult.group(2), matchResult.group(3));
		}
		else {
			criteria = new SearchCriteria(key, op, null, matchResult.group(2), null);
		}

		return this.buildCriteria(criteria);
	}

	private MonkeyCriteria buildCriteria(SearchCriteria condition) {
		Function<SearchCriteria, MonkeyCriteria> function = FILTER_CRITERIA.get(condition.getOperation());

		if (function == null) {
			throw new IllegalArgumentException("Invalid function param type: ");
		}

		return function.apply(condition);
	}

}
