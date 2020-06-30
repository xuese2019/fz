package org.jiushan.fuzhu.biz.user.db;

import org.jiushan.fuzhu.biz.user.model.UserModel;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveMongoRepository<UserModel, String> {

    Mono<UserModel> findByAcc(String acc);

    Flux<UserModel> findByAccLike(Example<UserModel> var1, Sort var2);
}
