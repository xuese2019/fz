package org.jiushan.fuzhu.biz.user.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Document(collection = "user_table")
public class UserModel {

    @Id
    private String id;

    private String name;

    private String acc;

    private String pwd;

    //    0:正常 -1：不允许登录
    private Integer type;
}
