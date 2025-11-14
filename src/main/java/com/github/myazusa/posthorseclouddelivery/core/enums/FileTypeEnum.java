package com.github.myazusa.posthorseclouddelivery.core.enums;

public enum FileTypeEnum {
    video("video"),
    image("image");
    private final String fileType;

    FileTypeEnum(String fileType) {
        this.fileType = fileType;
    }

    public String getFileTypeString() {
        return fileType;
    }

    public static FileTypeEnum fromString(String value) {
        for (FileTypeEnum filetype : FileTypeEnum.values()) {
            if (filetype.getFileTypeString().equalsIgnoreCase(value)) {
                return filetype;
            }
        }
        throw new IllegalArgumentException("从字符串转换到用户权限枚举失败");
    }
}
