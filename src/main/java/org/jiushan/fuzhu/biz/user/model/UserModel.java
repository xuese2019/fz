package org.jiushan.fuzhu.biz.user.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.jiushan.fuzhu.biz.user.model.interfaces.UserAddValid;
import org.jiushan.fuzhu.biz.user.model.interfaces.UserEditPwdValid;
import org.jiushan.fuzhu.biz.user.model.interfaces.UserEditValid;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Document(collection = "user_table")
public class UserModel {

    @Id
    private String id;

    @NotBlank(message = "姓名不能为空", groups = {UserAddValid.class, UserEditValid.class})
    @Length(min = 2, max = 30, message = "姓名长度为2-30位字符", groups = {UserAddValid.class, UserEditValid.class})
    private String name;

    @NotBlank(message = "账号不能为空", groups = UserAddValid.class)
    @Length(min = 6, max = 15, message = "账号长度为6-15位字符", groups = UserAddValid.class)
    private String acc;

    @NotBlank(message = "密码不能为空", groups = UserEditPwdValid.class)
    @Length(min = 6, max = 15, message = "密码长度为6-15位字符", groups = UserEditPwdValid.class)
    private String pwd;

    //    0:正常 -1：不允许登录
    @NotNull(message = "账号类型不能空", groups = UserEditValid.class)
    @Range(min = -2, max = 1, message = "错误的账号类型", groups = UserEditValid.class)
    private Integer type;
}
