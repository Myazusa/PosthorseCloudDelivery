package com.github.myazusa.posthorseclouddelivery.core.enums;

public enum SortOrderEnum {
    asc("asc"),
    desc("desc");

    private final String sortOrder;

    SortOrderEnum(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getSortOrderString() {
        return sortOrder;
    }

    public static SortOrderEnum fromString(String value) {
        for (SortOrderEnum sortOrder : SortOrderEnum.values()) {
            if (sortOrder.getSortOrderString().equalsIgnoreCase(value)) {
                return sortOrder;
            }
        }
        throw new IllegalArgumentException("从字符串转换到用户权限枚举失败");
    }
}
