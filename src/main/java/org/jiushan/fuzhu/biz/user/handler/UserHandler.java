package org.jiushan.fuzhu.biz.user.handler;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import lombok.extern.slf4j.Slf4j;
import org.jiushan.fuzhu.biz.user.db.UserRepository;
import org.jiushan.fuzhu.biz.user.model.UserModel;
import org.jiushan.fuzhu.biz.user.model.interfaces.UserAddValid;
import org.jiushan.fuzhu.biz.user.model.interfaces.UserEditValid;
import org.jiushan.fuzhu.util.check.CheckUtil;
import org.jiushan.fuzhu.util.md5.Md5Util;
import org.jiushan.fuzhu.util.uuid.UuidUtil;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
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

    /**
     * 新增
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> add(ServerRequest request) {
        return request.exchange()
                .getFormData()
                .map(map -> {
                    UserModel model = new UserModel();
                    model.setAcc(map.getFirst("acc"));
                    model.setPwd(Md5Util.md5("123456"));
                    model.setId(UuidUtil.uuid());
                    model.setType(0);
                    model.setName(map.getFirst("name"));
                    return model;
                })
                .flatMap(u -> {

//                    校验参数
                    String check = CheckUtil.check(u, UserAddValid.class);
                    if (check != null && !check.isEmpty()) {
                        return ServerResponse
                                .status(HttpStatus.BAD_REQUEST)
                                .bodyValue(check);
                    }

                    return this.userRepository.findByAcc(u.getAcc())
                            .flatMap(m -> {
                                return Mono.just(-1);
                            })
                            .switchIfEmpty(Mono.just(0))
                            .flatMap(f -> {
                                log.info(String.valueOf(f));
                                if (f == 0) {
                                    return this.userRepository.insert(u)
                                            .flatMap(a -> {
                                                return ServerResponse.ok().build();
                                            });
                                } else {
                                    return ServerResponse
                                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                            .bodyValue("账号重复");
                                }
                            });
                });
    }

    /**
     * 根据id删除
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> remove(ServerRequest request) {
        String uuid = request.pathVariable("id");
        return this.userRepository.findById(uuid)
                .flatMap(f -> {
                    return this.userRepository.deleteById(f.getId())
                            .then(ServerResponse.ok().build());
                })
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    /**
     * 修改
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> edit(ServerRequest request) {
        String uuid = request.pathVariable("id");
        return request.exchange()
                .getFormData()
                .flatMap(u -> {
                    return this.userRepository.findById(uuid)
                            .map(m -> {
                                if (Objects.nonNull(u.getFirst("pwd"))) {
                                    m.setPwd(u.getFirst("pwd"));
                                }
                                if (Objects.nonNull(u.getFirst("name"))) {
                                    m.setName(u.getFirst("name"));
                                }
                                if (Objects.nonNull(u.getFirst("type"))) {
                                    m.setType(Integer.valueOf(Objects.requireNonNull(u.getFirst("type"))));
                                }
                                return m;
                            })
                            .flatMap(f -> {
                                //                    校验参数
                                String check = CheckUtil.check(f, UserEditValid.class);
                                if (check != null && !check.isEmpty()) {
                                    return ServerResponse
                                            .status(HttpStatus.BAD_REQUEST)
                                            .bodyValue(check);
                                }

                                return this.userRepository.save(f)
                                        .flatMap(a -> {
                                            return ServerResponse.ok().build();
                                        });
                            })
                            .switchIfEmpty(ServerResponse
                                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .bodyValue("未查询到当前数据"));
                });
    }

    /**
     * 修改
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> restPwd(ServerRequest request) {
        String uuid = request.pathVariable("id");
        return this.userRepository.findById(uuid)
                .map(m->{
                    m.setPwd(Md5Util.md5("123456"));
                    return m;
                })
                .flatMap(f -> {
                    return this.userRepository.save(f)
                            .flatMap(a -> {
                                return ServerResponse.ok().build();
                            });
                })
                .switchIfEmpty(ServerResponse
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .bodyValue("未查询到当前数据"));
    }

    /**
     * 根据id查询
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> one(ServerRequest request) {
        String uuid = request.pathVariable("id");
        return this.userRepository.findById(uuid)
                .flatMap(f -> {
                    f.setPwd(null);
                    return ServerResponse
                            .ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(f);
                })
                .switchIfEmpty(ServerResponse
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .bodyValue("未查询到当前数据"));
    }

    /**
     * 分页条件查询
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> page(ServerRequest request) {
        int pageSize = Integer.parseInt(request.pathVariable("pageSize"));
        int pageNow = Integer.parseInt(request.pathVariable("pageNow"));
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(Sort.Order.asc("acc"));
        return request.formData()
                .flatMap(u -> {
                    UserModel model = new UserModel();
                    if (u.getFirst("acc") != null && !Objects.requireNonNull(u.getFirst("acc")).isEmpty()) {
                        model.setAcc(u.getFirst("acc"));
                    }
                    Flux<UserModel> userModelFlux = this.userRepository.findAll(Example.of(model), Sort.by(orders))
                            .skip(pageNow * pageSize)
                            .limitRequest(pageSize)
                            .map(f -> {
                                f.setPwd(null);
                                return f;
                            });
                    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(userModelFlux, Flux.class);

                });
    }
}
