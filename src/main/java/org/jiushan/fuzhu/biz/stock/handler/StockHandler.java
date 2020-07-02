package org.jiushan.fuzhu.biz.stock.handler;

import lombok.extern.slf4j.Slf4j;
import org.jiushan.fuzhu.biz.product.model.ProductModel;
import org.jiushan.fuzhu.biz.stock.db.StockRepository;
import org.jiushan.fuzhu.biz.stock.model.StockModel;
import org.jiushan.fuzhu.biz.stock.model.interfaces.StockAddValid;
import org.jiushan.fuzhu.biz.stock.model.interfaces.StockEditValid;
import org.jiushan.fuzhu.util.check.CheckUtil;
import org.jiushan.fuzhu.util.uuid.UuidUtil;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class StockHandler {

    private StockRepository stockRepository;
    //    基础数据库对象操作，日常操作首选 ReactiveMongoOperations
    private ReactiveMongoTemplate reactiveMongoTemplate;
    //    首选
    private ReactiveMongoOperations reactiveMongoOperations;

    public StockHandler(
            StockRepository stockRepository,
            ReactiveMongoTemplate reactiveMongoTemplate,
            ReactiveMongoOperations reactiveMongoOperations
    ) {
        this.stockRepository = stockRepository;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.reactiveMongoOperations = reactiveMongoOperations;
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

                    return this.stockRepository.findBySpecificationsAndProductId(u.getSpecifications(), u.getProductId())
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
//                    ----------------------------------------
//                    StockModel model = new StockModel();
//                    ExampleMatcher matcher = ExampleMatcher.matching()
//                            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
//                            .withIgnoreCase(true) //改变默认大小写忽略方式：忽略大小写
//                            .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains()) //采用“包含匹配”的方式查询
//                            .withIgnorePaths("pageNum", "pageSize");  //忽略属性，不参与查询;
//                    Flux<StockModel> stockModelFlux = this.stockRepository.findAll(Example.of(model, matcher), Sort.by(orders))
//                            .skip(pageNow * pageSize)
//                            .limitRequest(pageSize);
//                    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(stockModelFlux, Flux.class);
//                    -------------------------------------------

//                    BasicQuery query = new BasicQuery("{ age : { $lt : 50 }, accounts.balance : { $gt : 1000.00 }}");
//                    List<Person> result = mongoTemplate.find(query, Person.class);
//                    List<Person> result = template.query(Person.class)
//                            .matching(query(where("age").lt(50).and("accounts.balance").gt(1000.00d)))
//                            .all();

//                    @AllowDiskUse
//                            @Aggregation("{ $group: { _id : $lastname, names : { $addToSet : $firstname } } }")
//                    ------------------------------------------------

//                    //定义分组字段
//                    String[] groupIds = new String[]{"$userid", "$userrole.roleid"};
////                    定义查询条件
//                    Criteria criteria = new Criteria();
////                    相当于where username = "zhangsan"
//                    criteria.and("username").is("zhangsan");
////                    相当于 where age not in("15", "20")
//                    criteria.and("age").nin("15", "20");
////                    in操作对应的语句
////                    criteria.and("").in();
////                    联合查询总条数，分页用
//                    Aggregation aggregationCount = Aggregation.newAggregation(
//                            Aggregation.match(criteria),//查询条件
//                            Aggregation.group(groupIds)//分组字段
//                    );
////                    联合查询条件
//                    Aggregation newAggregation = Aggregation.newAggregation(
//                            Aggregation.lookup('B', 'userid', 'userid', 'userinfo'),//从表名，主表联接字段，从表联接字段，别名
//                            Aggregation.unwind("$userrole"),
//                            Aggregation.match(criteria),
//                            Aggregation.group(groupIds)
//                                    .last("$operateTime").as("operateTime")//取值，起别名
//                                    .first("$userinfo").as("info"),
//                            Aggregation.sort( Sort.by(orders)),
//                            Aggregation.skip(pageSize * (pageNow - 1L)),//Long类型的参数
//                            Aggregation.limit(pageSize)
//                    );
////查询
//                    AggregationResults<BasicDBObject> aggregate = reactiveMongoTemplate.aggregate(
//                            newAggregation, "A", BasicDBObject.class//A表，是查询的主表
//                    );
//                    int count = reactiveMongoTemplate.aggregate(aggregationCount, "A", BasicDBObject.class).getMappedResults().size();
////组装分页对象
//                    Page<BasicDBObject> pager = new Page<>(aggregate.getMappedResults(), count, pageSize, pageNow, page * (pageNumber - 1));
//                });
//                    ---------------------------------------------------------------
                    /*
                     *
                     *
                     *
                     *
                     *      sum：求和(同sql查询)
                     *      count：数量(同sql查询)
                     *      as:别名(同sql查询)
                     *      addToSet：将符合的字段值添加到一个集合或数组中
                     * sort：排序
                     * skip&limit：分页查询
                     */
//                    Criteria criteria = Criteria.where("name").is(u.getFirst("name"));
//                    int startRows = pageNow * pageSize;
//                    其它条件
//                    if (buyerNick != null && !"".equals(buyerNick)) {
//                        sql =
//                        criteria.and("buyerNick").is(buyerNick);
//                    }

//                    if (phones != null && phones.size() > 0) {
//                    sql in()
//                        criteria.and("mobile").in(phoneList);
//                    }

                    /*Aggregation customerAgg = Aggregation.newAggregation(
//                            project:列出所有本次查询的字段，包括查询条件的字段和需要搜索的字段;
                            Aggregation.project(
                                    "id",
                                    "name",
                                    "stock_table.specifications",
                                    "stock_table.stock"),
                            Aggregation
                                    .lookup(
                                            "stock_table",
                                            "_id",
                                            "productId",
                                            "stockInfo"
                                    )
//                            ------------------------
                            *//*
//                            match:搜索条件criteria
                                    Aggregation.match(criteria),
//                            unwind：某一个字段是集合，将该字段分解成数组
//                            Aggregation.unwind("orders"),
//                            group：分组的字段，以及聚合相关查询
//                            Aggregation
//                                    .group("buyerNick")
//                                    .first("buyerNick").as("buyerNick")
//                                    .first("mobile").as("mobile")
//                                    .first("address").as("address")
//                                    .sum("payment").as("totalPayment")
//                                    .sum("num").as("itemNum")
//                                    .count().as("orderNum"),
//                            排序
//                            Aggregation.sort(Sort.by(orders)),
                            Aggregation.skip(startRows),
                            Aggregation.limit(pageSize)
                             *//*
//                            -----------------------
                    );
                    Flux<Map> aggregate = reactiveMongoTemplate
                            .aggregate(customerAgg, ProductModel.class, Map.class)
                            .skip(pageNow * pageSize)
                            .limitRequest(pageSize);
                    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(aggregate, Flux.class);*/


                    Criteria criteria = Criteria.where("_id").ne("");
                    String name = u.getFirst("name");
                    if (Objects.nonNull(name)) {
//                        模糊条件
                        criteria.and("name").regex(name);
                    }
                    Aggregation customerAgg = Aggregation.newAggregation(
//                            project:列出所有本次查询的字段，包括查询条件的字段和需要搜索的字段;
                            Aggregation.project(
                                    "name"
//                                    "specifications",
//                                    "stock"
                            ),
                            Aggregation.match(criteria),
                            Aggregation.lookup(
                                    "stock_table",
                                    "_id",
                                    "productId",
                                    "stockInfo"
                            ),
                            Aggregation.sort(Sort.by(orders))
                    );
                    Flux<Map> aggregate = reactiveMongoTemplate
                            .aggregate(customerAgg, ProductModel.class, Map.class)
                            .skip(pageNow * pageSize)
                            .limitRequest(pageSize);
                    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(aggregate, Flux.class);
                });
    }
}
