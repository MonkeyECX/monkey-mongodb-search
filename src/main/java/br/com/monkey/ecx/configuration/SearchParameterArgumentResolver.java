package br.com.monkey.ecx.configuration;

import br.com.monkey.ecx.annotation.SearchParameter;
import br.com.monkey.ecx.criteria.MonkeyCriteria;
import br.com.monkey.ecx.parser.CriteriaParser;
import org.springframework.core.MethodParameter;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static br.com.monkey.ecx.configuration.MongoDBSearchConfiguration.getInstance;
import static br.com.monkey.ecx.core.tenant.TenantThreadLocalStorage.getProgram;

public class SearchParameterArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter methodParameter) {
		return methodParameter.getParameterType().equals(Query.class)
				&& methodParameter.hasParameterAnnotation(SearchParameter.class);
	}

	@Override
	public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
			NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) {
		SearchParameter parameterAnnotation = methodParameter.getParameterAnnotation(SearchParameter.class);
		return buildSpecification(nativeWebRequest.getParameter(parameterAnnotation.value()));
	}

	private Query buildSpecification(String search) {
		Query query = new Query();
		if (search == null || search.isEmpty()) {
			return query;
		}
		query = new CriteriaParser<>().parse(search);
		if (getInstance().isUserProgramInAllQueries()) {
			query.addCriteria(MonkeyCriteria.where(getInstance().getProgramField()).is(getProgram()));
		}
		return query;
	}

}
