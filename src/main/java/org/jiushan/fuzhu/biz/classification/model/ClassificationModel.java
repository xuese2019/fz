package org.jiushan.fuzhu.biz.classification.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.jiushan.fuzhu.biz.classification.model.interfaces.ClassificationAddValid;
import org.jiushan.fuzhu.biz.classification.model.interfaces.ClassificationEditValid;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Document(collection = "classification_table")
public class ClassificationModel {

    @Id
    private String id;

    @NotBlank(message = "分类名称不能为空", groups = {ClassificationAddValid.class, ClassificationEditValid.class})
    @Length(min = 2, max = 30, message = "分类名称长度为1-30位字符", groups = {ClassificationAddValid.class, ClassificationEditValid.class})
    private String name;

}
