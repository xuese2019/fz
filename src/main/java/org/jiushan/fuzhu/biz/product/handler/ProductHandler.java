package org.jiushan.fuzhu.biz.product.handler;

import lombok.extern.slf4j.Slf4j;
import org.jiushan.fuzhu.biz.product.db.ProductRepository;
import org.jiushan.fuzhu.biz.product.model.ProductModel;
import org.jiushan.fuzhu.biz.product.model.interfaces.ProductAddValid;
import org.jiushan.fuzhu.biz.product.model.interfaces.ProductEditValid;
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
public class ProductHandler {

    private ProductRepository productRepository;

    public ProductHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
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
                    ProductModel model = new ProductModel();
                    model.setId(UuidUtil.uuid());
                    model.setName(map.getFirst("name"));
                    model.setClassificationId(map.getFirst("classificationId"));
                    model.setBrief(map.getFirst("brief"));
                    model.setPrice(Double.valueOf(Objects.requireNonNull(map.getFirst("price"))));
                    model.setImg(map.getFirst("img"));
                    model.setShelf(-1);
                    return model;
                })
                .flatMap(u -> {

//                    校验参数
                    String check = CheckUtil.check(u, ProductAddValid.class);
                    if (check != null && !check.isEmpty()) {
                        return ServerResponse
                                .status(HttpStatus.BAD_REQUEST)
                                .bodyValue(check);
                    }

                    return this.productRepository.findByName(u.getName())
                            .flatMap(m -> {
                                return Mono.just(-1);
                            })
                            .switchIfEmpty(Mono.just(0))
                            .flatMap(f -> {
                                log.info(String.valueOf(f));
                                if (f == 0) {
                                    return this.productRepository.insert(u)
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
        return ServerResponse
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .bodyValue("全局不允许删除商品信息");
//        String uuid = request.pathVariable("id");
//        return this.productRepository.findById(uuid)
//                .flatMap(f -> {
//                    return this.productRepository.deleteById(f.getId())
//                            .then(ServerResponse.ok().build());
//                })
//                .switchIfEmpty(ServerResponse.notFound().build());
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
                    return this.productRepository.findById(uuid)
                            .map(m -> {
                                if (Objects.nonNull(u.getFirst("name"))) {
                                    m.setName(u.getFirst("name"));
                                }
                                if (Objects.nonNull(u.getFirst("classificationId"))) {
                                    m.setClassificationId(u.getFirst("classificationId"));
                                }
                                if (Objects.nonNull(u.getFirst("price"))) {
                                    m.setPrice(Double.valueOf(Objects.requireNonNull(u.getFirst("price"))));
                                }
                                if (Objects.nonNull(u.getFirst("brief"))) {
                                    m.setBrief(u.getFirst("brief"));
                                }
                                if (Objects.nonNull(u.getFirst("img"))) {
                                    m.setImg(u.getFirst("img"));
                                }
                                return m;
                            })
                            .flatMap(f -> {
                                //                    校验参数
                                String check = CheckUtil.check(f, ProductEditValid.class);
                                if (check != null && !check.isEmpty()) {
                                    return ServerResponse
                                            .status(HttpStatus.BAD_REQUEST)
                                            .bodyValue(check);
                                }

                                return this.productRepository.save(f)
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
     * @param request
     * @return
     */
    public Mono<ServerResponse> uord(ServerRequest request) {
        String uuid = request.pathVariable("id");
        return request.exchange()
                .getFormData()
                .flatMap(u -> {
                    return this.productRepository.findById(uuid)
                            .map(m -> {
                                int i = (m.getShelf() == null || m.getShelf() < 0) ? 0 : -1;
                                m.setShelf(i);
                                return m;
                            })
                            .flatMap(f -> {
                                return this.productRepository.save(f)
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
        return this.productRepository.findById(uuid)
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
        return request.formData()
                .flatMap(u -> {
                    ProductModel model = new ProductModel();
                    if (u.getFirst("name") != null && !Objects.requireNonNull(u.getFirst("name")).isEmpty()) {
                        model.setName(u.getFirst("name"));
                    }
                    Flux<ProductModel> ProductModelFlux = this.productRepository.findAll(Example.of(model), Sort.by(orders))
                            .skip(pageNow * pageSize)
                            .limitRequest(pageSize);
                    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(ProductModelFlux, Flux.class);

                });
    }
}
