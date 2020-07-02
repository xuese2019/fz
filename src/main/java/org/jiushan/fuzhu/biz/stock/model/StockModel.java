package org.jiushan.fuzhu.biz.stock.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.jiushan.fuzhu.biz.stock.model.interfaces.StockAddValid;
import org.jiushan.fuzhu.biz.stock.model.interfaces.StockEditValid;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Document(collection = "stock_table")
//更改数据库存储的_class字段内容
//@TypeAlias("stock")
public class StockModel {

    @Id
//    @MongoId
    private String id;

    //    商品
    @NotBlank(message = "商品不能为空", groups = {StockAddValid.class})
    private String productId;

    //    规格
    private String specifications;

    //    数量
    @NotNull(message = "数量不能为空", groups = {StockAddValid.class, StockEditValid.class})
    private Integer stock;
}
