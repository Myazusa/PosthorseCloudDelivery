package com.github.myazusa.posthorseclouddelivery.core.enums;

public enum UserRoleEnum {
    login("login"), // 拥有该权限用户可以登录
    manageUsers("manageUsers"); // 拥有该权限用户可以修改其他用户

    private final String role;
    public String getUserPermissionString(){
        return role;
    }

    UserRoleEnum(String role) {
        this.role = role;
    }
    public static UserRoleEnum fromString(String value) {
        for (UserRoleEnum role : UserRoleEnum.values()) {
            if (role.getUserPermissionString().equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("从字符串转换到用户权限枚举失败");
    }
}
