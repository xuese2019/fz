package org.jiushan.fuzhu.biz.stock.db;

import org.jiushan.fuzhu.biz.stock.model.StockModel;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface StockRepository extends ReactiveMongoRepository<StockModel, String> {

    Mono<StockModel> findBySpecifications(String specifications);
}
