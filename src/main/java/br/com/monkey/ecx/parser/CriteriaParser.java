package br.com.monkey.ecx.parser;

import br.com.monkey.ecx.QueryLexer;
import br.com.monkey.ecx.QueryParser;
import br.com.monkey.ecx.criteria.MonkeyCriteria;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.groupingBy;

public class CriteriaParser<T> {

	private final QueryVisitor<T> visitor = new QueryVisitor<>();

	public Query parse(String search) {
		MonkeyCriteria criteria = visitor.visit(getParser(search).input());
		List<MonkeyCriteria> criteriaList = new ArrayList<>();

		criteria.getCriteriaOrClause().stream().collect(groupingBy(MonkeyCriteria::getPriorityGroup))
				.forEach((key, value) -> criteriaList.add(new MonkeyCriteria().orOperator(value)));

		criteria.getCriteriaAndClause().stream().collect(groupingBy(MonkeyCriteria::getPriorityGroup))
				.forEach((key, value) -> criteriaList.add(new MonkeyCriteria().andOperator(value)));

		if (criteriaList.isEmpty()) {
			return new Query(criteria);
		}
		else {
			return new Query(new MonkeyCriteria().andOperator(criteriaList));
		}
	}

	private QueryParser getParser(String search) {
		QueryLexer lexer = new QueryLexer(CharStreams.fromString(search));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		return new QueryParser(tokens);
	}

}
