package org.jiushan.fuzhu.biz.product.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.jiushan.fuzhu.biz.product.model.interfaces.ProductAddValid;
import org.jiushan.fuzhu.biz.product.model.interfaces.ProductEditValid;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Document(collection = "product_table")
public class ProductModel {

    @Id
    private String id;

    @NotBlank(message = "名称不能为空", groups = {ProductAddValid.class, ProductEditValid.class})
    @Length(min = 2, max = 30, message = "名称长度为1-30位字符", groups = {ProductAddValid.class, ProductEditValid.class})
    private String name;

    //    产品分类
    @NotBlank(message = "产品分类不能为空", groups = {ProductAddValid.class, ProductEditValid.class})
    private String classificationId;

    //    价格
    @Digits(integer = 4, fraction = 2, message = "价格小数为2位，整数位为4位", groups = {ProductAddValid.class, ProductEditValid.class})
    @DecimalMin(value = "0.01", message = "最小值为0.01", groups = {ProductAddValid.class, ProductEditValid.class})
    private Double price;

    //    图片
    private String img;

    //    简介
    @Length(max = 100, message = "简介最大长度100位", groups = {ProductAddValid.class, ProductEditValid.class})
    private String brief;

    //    是否上架 0:是
    private Integer shelf;

}
