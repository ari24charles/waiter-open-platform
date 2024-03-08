# the database of waiter-open-platform
DROP DATABASE IF EXISTS `waiter-open-platform`;

CREATE DATABASE IF NOT EXISTS `waiter-open-platform`;

USE `waiter-open-platform`;

DROP TABLE IF EXISTS `user`;

CREATE TABLE IF NOT EXISTS `user`
(
    id          BIGINT                      NOT NULL COMMENT '雪花算法主键',
    username    VARCHAR(255)                NOT NULL COMMENT '账号',
    password    VARCHAR(255)                NOT NULL COMMENT '密码',
    nickname    VARCHAR(255)                NOT NULL COMMENT '昵称',
    phone       VARCHAR(255)                NOT NULL COMMENT '电话',
    email       VARCHAR(255)  DEFAULT NULL  NULL COMMENT '邮箱',
    avatar_url  VARCHAR(1024) DEFAULT NULL  NULL COMMENT '头像地址',
    gender      TINYINT       DEFAULT 0     NOT NULL COMMENT '性别: 0 -> 保密, 1 -> 男, 2 -> 女',
    status      TINYINT       DEFAULT 0     NOT NULL COMMENT '用户状态: 0 -> 正常, 1 -> 封禁',
    role        TINYINT       DEFAULT 2     NOT NULL COMMENT '用户角色: 0 -> 超级管理员, 1 -> 管理员, 2 -> 普通用户',
    access_key  VARCHAR(512)                NOT NULL COMMENT '访问密钥',
    secret_key  VARCHAR(512)                NOT NULL COMMENT '密钥',
    create_time DATETIME      DEFAULT NOW() NOT NULL COMMENT '创建时间',
    update_time DATETIME      DEFAULT NOW() NOT NULL COMMENT '更新时间' ON UPDATE NOW(),
    is_deleted  TINYINT       DEFAULT 0     NOT NULL COMMENT '逻辑删除: 0 -> 正常, 1 -> 删除',
    PRIMARY KEY (id),
    KEY `idx_nickname` (nickname)
) COMMENT '用户表';

DROP TABLE IF EXISTS `interface_info`;

CREATE TABLE IF NOT EXISTS `interface_info`
(
    id              BIGINT                     NOT NULL COMMENT '雪花算法主键',
    name            VARCHAR(255)               NOT NULL COMMENT '接口名',
    description     VARCHAR(255) DEFAULT NULL  NULL COMMENT '接口描述',
    uri             VARCHAR(1024)              NOT NULL COMMENT '接口地址',
    request_params  TEXT                       NOT NULL COMMENT '序列化后的请求体',
    request_header  TEXT                       NOT NULL COMMENT '请求头',
    response_header TEXT                       NOT NULL COMMENT '响应头',
    method          VARCHAR(255)               NOT NULL COMMENT '请求类型: POST / GET',
    user_id         BIGINT                     NOT NULL COMMENT '创建人 id',
    status          TINYINT      DEFAULT 0     NOT NULL COMMENT '接口状态: 0 -> 关闭, 1 -> 开启',
    create_time     DATETIME     DEFAULT NOW() NOT NULL COMMENT '创建时间',
    update_time     DATETIME     DEFAULT NOW() NOT NULL COMMENT '更新时间' ON UPDATE NOW(),
    is_deleted      TINYINT      DEFAULT 0     NOT NULL COMMENT '逻辑删除: 0 -> 正常, 1 -> 删除',
    # (method, uri) 是一个候选键
    PRIMARY KEY (id),
    KEY `idx_interface_name` (name)
) COMMENT '接口信息表';


DROP TABLE IF EXISTS `user_interface_info`;

CREATE TABLE IF NOT EXISTS `user_interface_info`
(
    id                BIGINT                 NOT NULL COMMENT '雪花算法主键',
    user_id           BIGINT                 NOT NULL COMMENT '用户 id',
    interface_info_id BIGINT                 NOT NULL COMMENT '接口信息 id',
    total_num         INT      DEFAULT 0     NOT NULL COMMENT '总调用次数',
    left_num          INT      DEFAULT 0     NOT NULL COMMENT '剩余调用次数',
    status            TINYINT  DEFAULT 0     NOT NULL COMMENT '用户是否可以调用该接口: 0 -> 正常, 1 -> 禁用',
    create_time       DATETIME DEFAULT NOW() NOT NULL COMMENT '创建时间',
    update_time       DATETIME DEFAULT NOW() NOT NULL COMMENT '更新时间' ON UPDATE NOW(),
    is_deleted        TINYINT  DEFAULT 0     NOT NULL COMMENT '逻辑删除: 0 -> 正常, 1 -> 删除',
    PRIMARY KEY (id)
) COMMENT '用户与接口信息的调用关系表';

INSERT INTO `user`(id, username, password, nickname, phone, role, access_key, secret_key)
VALUES (1, 'ari24charles', '80b8a49d4d1fea8b5eeca77d5bd853de', 'Duck Seer', '10020034000', '0',
        'ba349d97149eaf5ba81d24afc9161fa6', 'e450cd3a2c9050a659eabd617d042acd'); # 12345678

INSERT INTO `interface_info`(id, name, description, uri, request_params, request_header, response_header, method,
                             user_id)
VALUES (1, 'NO.1', 'this is NO.1', '/api/encrypt/md5', '{"id": 1, "username": "ari", "password": "12345678"}', 'request', 'response', 'POST', 1),
       (2, 'N2', 'this is N2', '/service/n2', 'params', 'request', 'response', 'POST', 1),
       (3, 'nn3', 'this is nn3', '/service/nn3', 'params', 'request', 'response', 'GET', 2);

INSERT INTO `user_interface_info`(id, user_id, interface_info_id, total_num, left_num, status)
VALUES (1, 1, 1, 0, 9999, 0),
       (2, 1, 2, 0, 9999, 0),
       (3, 1, 3, 0, 9999, 9),
       (4, 1, 2, 200, 0, 0),
       (5, 2, 1, 2000, 200, 0),
       (6, 3, 3, 100, 20, 0);