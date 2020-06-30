package org.jiushan.fuzhu.biz.classification.db;

import org.jiushan.fuzhu.biz.classification.model.ClassificationModel;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ClassificationRepository extends ReactiveMongoRepository<ClassificationModel, String> {

    Mono<ClassificationModel> findByName(String name);
}
