package org.jiushan.fuzhu.biz.stock.db;

import org.jiushan.fuzhu.biz.stock.model.StockModel;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
//public interface StockRepository extends MongoOperations {
//    Mono<StockModel> findBySpecificationsAndProductId(String specifications,String productId);
//}
public interface StockRepository extends ReactiveMongoRepository<StockModel, String> {

    Mono<StockModel> findBySpecificationsAndProductId(String specifications, String productId);

//    List<Map<String, String>> findStockList(Query query, String name, Aggregation customerAgg, Class<?> cla);

}
