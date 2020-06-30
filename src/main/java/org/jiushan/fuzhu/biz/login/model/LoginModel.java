package org.jiushan.fuzhu.biz.login.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LoginModel {

    @NotBlank(message = "账号或密码不能为空")
    private String acc;
    @NotBlank(message = "账号或密码不能为空")
    private String pwd;

}
