package br.com.monkey.ecx.parser;

import br.com.monkey.ecx.QueryLexer;
import br.com.monkey.ecx.QueryParser;
import br.com.monkey.ecx.criteria.MonkeyCriteria;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;

public class CriteriaParser<T> {

	private final QueryVisitor<T> visitor = new QueryVisitor<>();

	public Query parse(String search) {
		QueryParser parser = getParser(search);
		MonkeyCriteria visit = visitor.visit(parser.input());
		MonkeyCriteria monkeyCriteria = new MonkeyCriteria();
		if (!CollectionUtils.isEmpty(visit.getCriteriaOrClause())
				&& !CollectionUtils.isEmpty(visit.getCriteriaAndClause())) {
			monkeyCriteria = new MonkeyCriteria().andOperator(visit.getCriteriaAndClause())
					.orOperator(visit.getCriteriaOrClause());
		}
		else if (!CollectionUtils.isEmpty(visit.getCriteriaAndClause())) {
			monkeyCriteria = new MonkeyCriteria().andOperator(visit.getCriteriaAndClause());
		}
		else if (!CollectionUtils.isEmpty(visit.getCriteriaOrClause())) {
			monkeyCriteria = new MonkeyCriteria().orOperator(visit.getCriteriaOrClause());
		}
		else if (!visit.getCriteriaObject().isEmpty()) {
			return new Query(visit);
		}
		return new Query(monkeyCriteria);
	}

	private QueryParser getParser(String search) {
		QueryLexer lexer = new QueryLexer(CharStreams.fromString(search));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		return new QueryParser(tokens);
	}

}
