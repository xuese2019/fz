package org.jiushan.fuzhu.biz.product.db;

import org.jiushan.fuzhu.biz.product.model.ProductModel;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends ReactiveMongoRepository<ProductModel, String> {

    Mono<ProductModel> findByName(String name);
}
