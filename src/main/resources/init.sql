-- 启用扩展UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
-- 启用地图扩展
CREATE EXTENSION IF NOT EXISTS postgis;

-- 用户表
CREATE TABLE user
(
    uuid        UUID PRIMARY KEY,
    phone       VARCHAR(20) UNIQUE NOT NULL,
    username    VARCHAR(50) DEFAULT 'user',
    password    VARCHAR(255) NOT NULL,
    enabled     BOOLEAN   DEFAULT TRUE,
    create_time TIMESTAMP DEFAULT now()
);
CREATE INDEX idx_user_phone ON user (phone);

-- 权限表
CREATE TABLE role
(
    uuid        UUID PRIMARY KEY,
    name        VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(100)
);
-- 用户与权限关系表
CREATE TABLE user_role
(
    user_uuid INT REFERENCES user (uuid) ON DELETE CASCADE,
    role_uuid INT REFERENCES role (uuid) ON DELETE CASCADE,
    UNIQUE (user_uuid, role_uuid)
);

-- 验证码缓存表，效果和redis一样
CREATE UNLOGGED TABLE verification_code_cache (
    id BIGSERIAL PRIMARY KEY,
    phone VARCHAR(20) NOT NULL,
    code VARCHAR(6) NOT NULL,
    expire_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT now()
);
CREATE INDEX idx_code_phone ON verification_code_cache (phone);

CREATE UNLOGGED TABLE verification_password_cache (
  id BIGSERIAL PRIMARY KEY,
  verifyPassword VARCHAR(6) NOT NULL,
  expire_at TIMESTAMP NOT NULL,
  created_at TIMESTAMP DEFAULT now()
);

-- 设备表
CREATE TABLE device
(
    uuid        UUID PRIMARY KEY,
    device_name      VARCHAR(128) DEFAULT 'device',
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,

    location GEOGRAPHY(Point, 4326),
    last_online TIMESTAMP
);
CREATE INDEX idx_device_location ON device USING GIST (location);

-- 用户与设备关系表
CREATE TABLE user_device
(
    user_uuid INT REFERENCES user (uuid) ON DELETE CASCADE,
    device_uuid INT REFERENCES device (uuid) ON DELETE CASCADE,
    UNIQUE (device_uuid)
)

-- 广告元数据表
CREATE TABLE ad
(
    uuid       UUID PRIMARY KEY,
    title      VARCHAR(256),
    type       VARCHAR(16) NOT NULL,
    file_path  VARCHAR(1024) NOT NULL,
    file_size  BIGINT DEFAULT 0,
    duration   INT DEFAULT 10,
    priority   INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT now()
);

-- 用户与广告元数据关系表
CREATE TABLE user_ad
(
    user_uuid INT REFERENCES user (uuid) ON DELETE CASCADE,
    ad_uuid INT REFERENCES ad (uuid) ON DELETE CASCADE,
    UNIQUE (user_uuid, ad_uuid)
)
