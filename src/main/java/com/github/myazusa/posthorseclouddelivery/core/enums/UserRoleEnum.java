package com.github.myazusa.posthorseclouddelivery.core.enums;

public enum UserRoleEnum {
    login("login"), // 拥有该权限用户可以登录
    addRole("addRole"), // 拥有该权限用户可以添加其他用户权限
    removeRole("removeRole"), // 拥有该权限用户可以删除其他用户权限
    queryOthersRole("queryOthersRole"), // 拥有该权限用户可以查询其他用户权限
    addDevice("addDevice"), // 拥有该权限可以获得设备验证器的密码
    bindDevice("bindDevice"), // 拥有该权限可以绑定用户和设备
    unbindDevice("unbindDevice"), // 拥有该权限可以绑解绑用户和设备
    manageUsers("manageUsers"); // 拥有该权限用户可以修改其他用户

    private final String role;
    public String getUserRoleString(){
        return role;
    }

    UserRoleEnum(String role) {
        this.role = role;
    }
    public static UserRoleEnum fromString(String value) {
        for (UserRoleEnum role : UserRoleEnum.values()) {
            if (role.getUserRoleString().equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("从字符串转换到用户权限枚举失败");
    }
}
