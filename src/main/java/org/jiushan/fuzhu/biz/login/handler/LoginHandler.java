package org.jiushan.fuzhu.biz.login.handler;

import lombok.extern.slf4j.Slf4j;
import org.jiushan.fuzhu.biz.login.model.LoginModel;
import org.jiushan.fuzhu.biz.user.db.UserRepository;
import org.jiushan.fuzhu.util.check.CheckUtil;
import org.jiushan.fuzhu.util.jwt.JwtUtil;
import org.jiushan.fuzhu.util.md5.Md5Util;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.groups.Default;

@Slf4j
@Component
public class LoginHandler {

    private UserRepository userRepository;

    public LoginHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 登录
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> login(ServerRequest request) {
        return request
                .formData()
                .map(m -> {
                    LoginModel model = new LoginModel();
                    model.setAcc(m.getFirst("acc"));
                    model.setPwd(m.getFirst("pwd"));
                    return model;
                })
                .flatMap(u -> {
//                    校验参数
                    String check = CheckUtil.check(u, Default.class);
                    if (check != null && !check.isEmpty()) {
                        return ServerResponse
                                .status(HttpStatus.BAD_REQUEST)
                                .bodyValue(check);
                    }
//                    密码加密
                    u.setPwd(Md5Util.md5(u.getPwd()));
                    return this.userRepository.findByAcc(u.getAcc())
                            .filter(f -> (f != null && f.getPwd().equals(u.getPwd())))
                            .flatMap(m -> {
                                if (m.getType() != 0) {
                                    return ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("禁止登录");
                                }
                                String jwt = JwtUtil.createJwt(m.getId()
                                        , m.getAcc()
                                        , "user");
                                return ServerResponse
                                        .ok()
                                        .bodyValue(jwt);
                            });
                })
                .switchIfEmpty(ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("账号或密码错误"));
    }
}
