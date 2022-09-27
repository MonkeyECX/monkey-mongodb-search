package br.com.monkey.ecx.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.List;

public class ResourceRepositoryImpl<T, I extends Serializable> extends SimpleMongoRepository<T, I>
		implements ResourceRepository<T, I> {

	private final MongoOperations mongoOperations;

	private final MongoEntityInformation<T, I> entityInformation;

	public ResourceRepositoryImpl(final MongoEntityInformation<T, I> entityInformation,
			final MongoOperations mongoOperations) {
		super(entityInformation, mongoOperations);
		this.entityInformation = entityInformation;
		this.mongoOperations = mongoOperations;
	}

	@Override
	public List<T> findAll(Query query) {
		return mongoOperations.find(query, entityInformation.getJavaType(), entityInformation.getCollectionName());
	}

	@Override
	public Page<T> findAll(final Query query, final Pageable pageable) {
		Assert.notNull(query, "Query must not be null.");
		long total = mongoOperations.count(query, entityInformation.getJavaType(),
				entityInformation.getCollectionName());
		List<T> content = mongoOperations.find(query.with(pageable), entityInformation.getJavaType(),
				entityInformation.getCollectionName());
		return new PageImpl<>(content, pageable, total);
	}

}
