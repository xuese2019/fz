package org.jiushan.fuzhu.biz.user.handler;

import lombok.extern.slf4j.Slf4j;
import org.jiushan.fuzhu.biz.user.db.UserRepository;
import org.jiushan.fuzhu.biz.user.model.UserModel;
import org.jiushan.fuzhu.util.uuid.UuidUtil;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class UserHandler {

    private UserRepository userRepository;

    public UserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<ServerResponse> add(ServerRequest request) {
        return request.exchange()
                .getFormData()
                .map(map -> {
                    UserModel model = new UserModel();
                    model.setAcc(map.getFirst("acc"));
                    model.setPwd(map.getFirst("pwd"));
                    model.setId(UuidUtil.uuid());
                    model.setType(0);
                    model.setName(map.getFirst("name"));
                    return model;
                })
                .flatMap(u -> {
                    return userRepository.findByAcc(Objects.requireNonNull(u.getAcc()))
                            .flatMap(m -> {
                                return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("账号重复");
                            })
                            .switchIfEmpty(ServerResponse.ok().body(m -> {
                                userRepository.insert(u)
                                        .map(a -> {
                                            return "成功";
                                        });
                            }, String.class));
                });
    }

    public Mono<ServerResponse> remove(ServerRequest request) {
        String uuid = request.pathVariable("id");
        return userRepository.findById(uuid)
                .flatMap(f -> {
                    return userRepository.deleteById(f.getId())
                            .then(ServerResponse.ok().build());
                })
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> edit(ServerRequest request) {
        String uuid = request.pathVariable("uuid");
        return request.exchange()
                .getFormData()
                .flatMap(map -> {
                    return userRepository.findById(uuid)
                            .flatMap(f -> {
                                UserModel model = new UserModel();
                                model.setName(map.getFirst("name"));
                                model.setId(uuid);
                                userRepository.save(model);
                                return ServerResponse.ok().build();
                            })
                            .switchIfEmpty(ServerResponse.notFound().build());
                });
    }

    public Mono<ServerResponse> one(ServerRequest request) {
        String uuid = request.pathVariable("uuid");
        return userRepository.findById(uuid)
                .flatMap(f -> {
                    f.setPwd(null);
                    return ServerResponse
                            .ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(f, UserModel.class);
                })
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> page(ServerRequest request) {
        int pageSize = Integer.parseInt(request.pathVariable("pageSize"));
        int pageNow = Integer.parseInt(request.pathVariable("pageNow"));
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(Sort.Order.asc("acc"));
        return request.formData()
                .filter(u -> u.getFirst("acc") != null)
                .flatMap(u -> {
                    UserModel model = new UserModel();
                    model.setAcc(u.getFirst("acc"));
                    return ServerResponse
                            .ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.userRepository.findAll(Example.of(model), Sort.by(orders))
                                            .skip((pageSize - 1) * pageNow)
                                            .limitRate(pageSize)
                                    , UserModel.class);
                })
                .switchIfEmpty(
                        ServerResponse
                                .ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(this.userRepository.findAll(Sort.by(orders))
                                                .skip((pageSize - 1) * pageNow)
                                                .limitRate(pageSize)
                                        , UserModel.class)
                );
    }
}
