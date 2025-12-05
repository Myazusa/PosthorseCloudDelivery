package com.github.myazusa.posthorseclouddelivery.core.enums;

public enum UserSortByEnum {
    uuid("uuid"),
    phone("phone"),
    username("username"),
    createTime("create_time");
    private final String userSortBy;

    UserSortByEnum(String userSortBy) {
        this.userSortBy = userSortBy;
    }

    public String getUserSortByString() {
        return userSortBy;
    }

    public static UserSortByEnum fromString(String value) {
        for (UserSortByEnum userSortBy : UserSortByEnum.values()) {
            if (userSortBy.getUserSortByString().equalsIgnoreCase(value)) {
                return userSortBy;
            }
        }
        throw new IllegalArgumentException("从字符串转换到数据库字段枚举失败");
    }
}
