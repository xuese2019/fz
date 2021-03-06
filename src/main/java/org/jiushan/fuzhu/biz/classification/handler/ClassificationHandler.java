package org.jiushan.fuzhu.biz.classification.handler;

import lombok.extern.slf4j.Slf4j;
import org.jiushan.fuzhu.biz.classification.db.ClassificationRepository;
import org.jiushan.fuzhu.biz.classification.model.ClassificationModel;
import org.jiushan.fuzhu.biz.classification.model.interfaces.ClassificationAddValid;
import org.jiushan.fuzhu.biz.classification.model.interfaces.ClassificationEditValid;
import org.jiushan.fuzhu.util.check.CheckUtil;
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
public class ClassificationHandler {

    private ClassificationRepository classificationRepository;

    public ClassificationHandler(ClassificationRepository classificationRepository) {
        this.classificationRepository = classificationRepository;
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
                    ClassificationModel model = new ClassificationModel();
                    model.setId(UuidUtil.uuid());
                    model.setName(map.getFirst("name"));
                    return model;
                })
                .flatMap(u -> {

//                    校验参数
                    String check = CheckUtil.check(u, ClassificationAddValid.class);
                    if (check != null && !check.isEmpty()) {
                        return ServerResponse
                                .status(HttpStatus.BAD_REQUEST)
                                .bodyValue(check);
                    }

                    return this.classificationRepository.findByName(u.getName())
                            .flatMap(m -> {
                                return Mono.just(-1);
                            })
                            .switchIfEmpty(Mono.just(0))
                            .flatMap(f -> {
                                log.info(String.valueOf(f));
                                if (f == 0) {
                                    return this.classificationRepository.insert(u)
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
        return this.classificationRepository.findById(uuid)
                .flatMap(f -> {
                    return this.classificationRepository.deleteById(f.getId())
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
                    return this.classificationRepository.findById(uuid)
                            .map(m -> {
                                if (Objects.nonNull(u.getFirst("name"))) {
                                    m.setName(u.getFirst("name"));
                                }
                                return m;
                            })
                            .flatMap(f -> {
                                //                    校验参数
                                String check = CheckUtil.check(f, ClassificationEditValid.class);
                                if (check != null && !check.isEmpty()) {
                                    return ServerResponse
                                            .status(HttpStatus.BAD_REQUEST)
                                            .bodyValue(check);
                                }

                                return this.classificationRepository.save(f)
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
     * 根据id查询
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> one(ServerRequest request) {
        String uuid = request.pathVariable("id");
        return this.classificationRepository.findById(uuid)
                .flatMap(f -> {
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
        orders.add(Sort.Order.asc("name"));
        if (pageSize > 0) {
            return request.formData()
                    .flatMap(u -> {
                        ClassificationModel model = new ClassificationModel();
                        if (u.getFirst("name") != null && !Objects.requireNonNull(u.getFirst("name")).isEmpty()) {
                            model.setName(u.getFirst("name"));
                        }
                        Flux<ClassificationModel> ClassificationModelFlux = this.classificationRepository.findAll(Example.of(model), Sort.by(orders))
                                .skip(pageNow * pageSize)
                                .limitRequest(pageSize);
                        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(ClassificationModelFlux, Flux.class);
                    });
        } else {
            return request.formData()
                    .flatMap(u -> {
                        Flux<ClassificationModel> ClassificationModelFlux = this.classificationRepository.findAll(Sort.by(orders));
                        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(ClassificationModelFlux, Flux.class);
                    });
        }
    }
}
