package com.github.myazusa.posthorseclouddelivery.core.enums;

public enum FileSortByEnum {
    uuid("uuid"),
    title("title"),
    createdAt("created_at");

    private final String fileSortBy;

    FileSortByEnum(String fileSortBy) {
        this.fileSortBy = fileSortBy;
    }

    public String getFileSortByString() {
        return fileSortBy;
    }

    public static FileSortByEnum fromString(String value) {
        for (FileSortByEnum fileSortBy : FileSortByEnum.values()) {
            if (fileSortBy.getFileSortByString().equalsIgnoreCase(value)) {
                return fileSortBy;
            }
        }
        throw new IllegalArgumentException("从字符串转换到用户权限枚举失败");
    }
}
