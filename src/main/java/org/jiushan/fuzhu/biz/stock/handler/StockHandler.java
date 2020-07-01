package org.jiushan.fuzhu.biz.stock.handler;

import lombok.extern.slf4j.Slf4j;
import org.jiushan.fuzhu.biz.stock.db.StockRepository;
import org.jiushan.fuzhu.biz.stock.model.StockModel;
import org.jiushan.fuzhu.biz.stock.model.interfaces.StockAddValid;
import org.jiushan.fuzhu.biz.stock.model.interfaces.StockEditValid;
import org.jiushan.fuzhu.util.check.CheckUtil;
import org.jiushan.fuzhu.util.uuid.UuidUtil;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
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
public class StockHandler {

    private StockRepository stockRepository;

    public StockHandler(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
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
                    StockModel model = new StockModel();
                    model.setId(UuidUtil.uuid());
                    model.setProductId(map.getFirst("productId"));
                    model.setSpecifications(map.getFirst("specifications"));
                    model.setStock(Integer.valueOf(Objects.requireNonNull(map.getFirst("stock"))));
                    return model;
                })
                .flatMap(u -> {

//                    校验参数
                    String check = CheckUtil.check(u, StockAddValid.class);
                    if (check != null && !check.isEmpty()) {
                        return ServerResponse
                                .status(HttpStatus.BAD_REQUEST)
                                .bodyValue(check);
                    }

                    return this.stockRepository.findBySpecifications(u.getSpecifications())
                            .flatMap(m -> {
                                return Mono.just(-1);
                            })
                            .switchIfEmpty(Mono.just(0))
                            .flatMap(f -> {
                                log.info(String.valueOf(f));
                                if (f == 0) {
                                    return this.stockRepository.insert(u)
                                            .flatMap(a -> {
                                                return ServerResponse.ok().build();
                                            });
                                } else {
                                    return ServerResponse
                                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                            .bodyValue("规格重复");
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
                .bodyValue("全局不允许删除");
//        String uuid = request.pathVariable("id");
//        return this.stockRepository.findById(uuid)
//                .flatMap(f -> {
//                    return this.stockRepository.deleteById(f.getId())
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
                    return this.stockRepository.findById(uuid)
                            .map(m -> {
                                if (Objects.nonNull(u.getFirst("specifications"))) {
                                    m.setSpecifications(u.getFirst("specifications"));
                                }
                                if (Objects.nonNull(u.getFirst("stock"))) {
                                    m.setStock(Integer.valueOf(Objects.requireNonNull(u.getFirst("stock"))));
                                }
                                return m;
                            })
                            .flatMap(f -> {
                                //                    校验参数
                                String check = CheckUtil.check(f, StockEditValid.class);
                                if (check != null && !check.isEmpty()) {
                                    return ServerResponse
                                            .status(HttpStatus.BAD_REQUEST)
                                            .bodyValue(check);
                                }

                                return this.stockRepository.save(f)
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
                    return this.stockRepository.findById(uuid)
                            .map(m -> {
                                return m;
                            })
                            .flatMap(f -> {
                                return this.stockRepository.save(f)
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
        return this.stockRepository.findById(uuid)
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
                    StockModel model = new StockModel();
                    ExampleMatcher matcher = ExampleMatcher.matching()
                            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                            .withIgnoreCase(true) //改变默认大小写忽略方式：忽略大小写
                            .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains()) //采用“包含匹配”的方式查询
                            .withIgnorePaths("pageNum", "pageSize");  //忽略属性，不参与查询;
                    Flux<StockModel> stockModelFlux = this.stockRepository.findAll(Example.of(model,matcher), Sort.by(orders))
                            .skip(pageNow * pageSize)
                            .limitRequest(pageSize);
                    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(stockModelFlux, Flux.class);

                });
    }
}
