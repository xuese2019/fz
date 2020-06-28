package org.jiushan.fuzhu.util.uuid;

public class UuidUtil {

    private String uuid;
    public static String uuid() {
        return java.util.UUID.randomUUID().toString().replaceAll("-", "");
    }
}
