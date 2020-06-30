package org.jiushan.fuzhu.util.check;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * 校验工具类
 */
public class CheckUtil {

    public static String check(@NotNull Object object, @NotNull Class<?> object2) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Object>> validate = validator.validate(object, object2);
        if (validate != null && !validate.isEmpty()) {
            return validate.iterator().next().getMessage();
        }
        return null;
    }
}
