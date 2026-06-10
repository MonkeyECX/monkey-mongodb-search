package br.com.monkey.ecx.parser;

import br.com.monkey.ecx.QueryBaseVisitor;
import br.com.monkey.ecx.QueryParser;
import br.com.monkey.ecx.configuration.Alias;
import br.com.monkey.ecx.configuration.MongoDBSearchConfiguration;
import br.com.monkey.ecx.core.ValueParser;
import br.com.monkey.ecx.criteria.MonkeyCriteria;
import br.com.monkey.ecx.criteria.SearchCriteria;
import br.com.monkey.ecx.criteria.SearchOperation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static br.com.monkey.ecx.core.MongoIdGenerator.generateId;
import static java.util.Objects.nonNull;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.StringUtils.hasText;

class QueryVisitor<T> extends QueryBaseVisitor<MonkeyCriteria> {

	private final Pattern REGEX = Pattern.compile("^(\\*?)(.+?)(\\*?)$");

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
		return visit(ctx.query()).withPriorityGroup(generateId());
	}

	@Override
	public MonkeyCriteria visitOpQuery(QueryParser.OpQueryContext ctx) {
		MonkeyCriteria left = visit(ctx.left);
		MonkeyCriteria right = visit(ctx.right);
		String op = ctx.logicalOp.getText();

		MonkeyCriteria criteria;
		if (op.equalsIgnoreCase(SearchOperation.AND)) {
			if (left.getCriteriaAndClause().isEmpty() && left.getCriteriaOrClause().isEmpty()
					&& hasText(left.getKey())) {
				left.addAndClause(left);
			}
			right.getCriteriaOrClause().forEach(left::addOrClause);
			right.getCriteriaAndClause().forEach(left::addAndClause);
			if (right.isDefaultPriorityGroup()) {
				left.addAndClause(right);
			}
			criteria = new MonkeyCriteria();
			left.getCriteriaOrClause().forEach(criteria::addOrClause);
			left.getCriteriaAndClause().forEach(criteria::addAndClause);
		}
		else if (op.equalsIgnoreCase(SearchOperation.OR)) {
			if (left.getCriteriaAndClause().isEmpty() && left.getCriteriaOrClause().isEmpty()
					&& hasText(left.getKey())) {
				left.addOrClause(left);
			}
			right.getCriteriaOrClause().forEach(left::addOrClause);
			right.getCriteriaAndClause().forEach(left::addAndClause);
			if (right.isDefaultPriorityGroup()) {
				left.addOrClause(right);
			}
			criteria = new MonkeyCriteria();
			left.getCriteriaOrClause().forEach(criteria::addOrClause);
			left.getCriteriaAndClause().forEach(criteria::addAndClause);
		}
		else {
			return left.addAndClause(left).addAndClause(right);
		}
		return criteria;
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
		Alias alias = MongoDBSearchConfiguration.getInstance().getAliases().stream()
				.filter(item -> item.getAlias().equals(condition.getKey())).findFirst().orElse(null);

		String resolvedKey = alias != null ? alias.getKey() : condition.getKey();
		MonkeyCriteria apply = buildTypedCriteria(resolvedKey, condition.getOperation(), condition.getValueAsString());
		if (alias != null) {
			addCombinedCondition(condition, apply, alias);
		}
		return apply;
	}

	private MonkeyCriteria buildTypedCriteria(String key, SearchOperation operation, String value) {
		if (ValueParser.isTemporal(value)) {
			return buildTemporalCriteria(key, operation, value);
		}
		return buildDefaultCriteria(key, operation, value);
	}

	private MonkeyCriteria buildTemporalCriteria(String key, SearchOperation operation, String value) {
		switch (operation) {
		case EQUAL:
			if (ValueParser.isDateOnly(value)) {
				return new MonkeyCriteria().andOperator(
						MonkeyCriteria.where(key).gte(ValueParser.startOfDay(value)),
						MonkeyCriteria.where(key).lt(ValueParser.nextDayStartOfDay(value)));
			}

			if (ValueParser.isDateTimeToMinute(value)) {
				return new MonkeyCriteria().andOperator(
						MonkeyCriteria.where(key).gte(ValueParser.startOfMinute(value)),
						MonkeyCriteria.where(key).lt(ValueParser.nextMinute(value)));
			}
			return MonkeyCriteria.where(key).is(ValueParser.parseToDate(value));

		case NOT:
			if (ValueParser.isDateOnly(value)) {
				return new MonkeyCriteria().orOperator(
						MonkeyCriteria.where(key).lt(ValueParser.startOfDay(value)),
						MonkeyCriteria.where(key).gte(ValueParser.nextDayStartOfDay(value)));
			}
			return MonkeyCriteria.where(key).ne(ValueParser.parseToDate(value));

		case GREATER_THAN:
			if (ValueParser.isDateOnly(value)) {
				return MonkeyCriteria.where(key).gt(ValueParser.startOfDay(value));
			}
			return MonkeyCriteria.where(key).gt(ValueParser.parseToDate(value));

		case GREATER_THAN_EQUAL:
			if (ValueParser.isDateOnly(value)) {
				return MonkeyCriteria.where(key).gte(ValueParser.startOfDay(value));
			}
			return MonkeyCriteria.where(key).gte(ValueParser.parseToDate(value));

		case LESS_THAN:
			if (ValueParser.isDateOnly(value)) {
				return MonkeyCriteria.where(key).lt(ValueParser.startOfDay(value));
			}
			return MonkeyCriteria.where(key).lt(ValueParser.parseToDate(value));

		case LESS_THAN_EQUAL:
			if (ValueParser.isDateOnly(value)) {
				return MonkeyCriteria.where(key).lt(ValueParser.nextDayStartOfDay(value));
			}

			return MonkeyCriteria.where(key).lte(ValueParser.parseToDate(value));

		default:
			throw new IllegalArgumentException("Temporal operation not supported: " + operation);
		}
	}

	private MonkeyCriteria buildDefaultCriteria(String key, SearchOperation operation, String rawValue) {
		Object value = ValueParser.convertScalarValue(rawValue);
		switch (operation) {
		case EQUAL:
			return MonkeyCriteria.where(key).is(value);
		case NOT:
			return MonkeyCriteria.where(key).ne(value);
		case GREATER_THAN:
			return MonkeyCriteria.where(key).gt(value);
		case GREATER_THAN_EQUAL:
			return MonkeyCriteria.where(key).gte(value);
		case LESS_THAN:
			return MonkeyCriteria.where(key).lt(value);
		case LESS_THAN_EQUAL:
			return MonkeyCriteria.where(key).lte(value);
		case CONTAINS:
			return MonkeyCriteria.where(key).regex(rawValue);
		case NOT_CONTAINS:
			return MonkeyCriteria.where(key).not().regex(rawValue);
		default:
			throw new IllegalArgumentException("Operation not supported: " + operation);

		}

	}

	private void addCombinedCondition(SearchCriteria condition, MonkeyCriteria apply, Alias alias) {
		if (!isEmpty(alias.getCombinedKey())) {
			alias.getCombinedKey().forEach(c -> {
				MonkeyCriteria combined = buildTypedCriteria(c.getKey(), condition.getOperation(),
						condition.getValueAsString());
				apply.addOrClause(apply).addOrClause(combined).withPriorityGroup(alias.getAlias());
			});
		}
	}

}
