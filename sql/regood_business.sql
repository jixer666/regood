-- =============================================
-- ReGood 业务模块数据库表
-- =============================================

-- 商品分类表
DROP TABLE IF EXISTS `tb_category`;
CREATE TABLE `tb_category` (
    `category_id` BIGINT(20) NOT NULL COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `icon` VARCHAR(50) DEFAULT NULL COMMENT '分类图标',
    `parent_id` BIGINT(20) DEFAULT 0 COMMENT '父分类ID',
    `sort` INT(4) DEFAULT 0 COMMENT '排序',
    `status` INT(1) DEFAULT 1 COMMENT '状态（1正常 0停用）',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `ver` INT(4) DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 商品表
DROP TABLE IF EXISTS `tb_product`;
CREATE TABLE `tb_product` (
    `product_id` BIGINT(20) NOT NULL COMMENT '商品ID',
    `title` VARCHAR(200) NOT NULL COMMENT '商品标题',
    `description` TEXT COMMENT '商品描述',
    `images` TEXT COMMENT '图片JSON数组',
    `price` DECIMAL(10,2) NOT NULL COMMENT '出售价格',
    `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
    `product_condition` VARCHAR(20) DEFAULT NULL COMMENT '成色',
    `category_id` BIGINT(20) DEFAULT NULL COMMENT '分类ID',
    `seller_id` BIGINT(20) NOT NULL COMMENT '卖家ID',
    `status` INT(1) DEFAULT 1 COMMENT '状态（1在售 2已卖出 3下架）',
    `view_count` INT(10) DEFAULT 0 COMMENT '浏览量',
    `want_count` INT(10) DEFAULT 0 COMMENT '想要数',
    `trade_methods` VARCHAR(200) DEFAULT NULL COMMENT '交易方式JSON',
    `meeting_place` VARCHAR(100) DEFAULT NULL COMMENT '面交地点',
    `free_shipping` TINYINT(1) DEFAULT 1 COMMENT '是否包邮（0否 1是）',
    `shipping_fee` DECIMAL(10,2) DEFAULT 0.00 COMMENT '运费',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `ver` INT(4) DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`product_id`),
    KEY `idx_seller_id` (`seller_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 购物车表
DROP TABLE IF EXISTS `tb_cart`;
CREATE TABLE `tb_cart` (
    `cart_id` BIGINT(20) NOT NULL COMMENT '购物车ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `product_id` BIGINT(20) NOT NULL COMMENT '商品ID',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`cart_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 收藏表
DROP TABLE IF EXISTS `tb_favorite`;
CREATE TABLE `tb_favorite` (
    `favorite_id` BIGINT(20) NOT NULL COMMENT '收藏ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `product_id` BIGINT(20) NOT NULL COMMENT '商品ID',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`favorite_id`),
    UNIQUE KEY `uk_user_product` (`user_id`,`product_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 订单表
DROP TABLE IF EXISTS `tb_order`;
CREATE TABLE `tb_order` (
    `order_id` BIGINT(20) NOT NULL COMMENT '订单ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
    `buyer_id` BIGINT(20) NOT NULL COMMENT '买家ID',
    `seller_id` BIGINT(20) NOT NULL COMMENT '卖家ID',
    `product_id` BIGINT(20) NOT NULL COMMENT '商品ID',
    `price` DECIMAL(10,2) NOT NULL COMMENT '成交价格',
    `status` INT(1) DEFAULT 1 COMMENT '状态（1待支付 2已支付 3已完成 4已取消）',
    `trade_method` VARCHAR(20) NOT NULL COMMENT '交易方式',
    `address` VARCHAR(500) DEFAULT NULL COMMENT '收货地址',
    `meeting_place` VARCHAR(100) DEFAULT NULL COMMENT '面交地点',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `ver` INT(4) DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`order_id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_buyer_id` (`buyer_id`),
    KEY `idx_seller_id` (`seller_id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 商品分类数据
INSERT INTO `tb_category` (`category_id`, `name`, `icon`, `parent_id`, `sort`, `status`, `create_time`, `update_time`, `ver`) VALUES
(1, '数码电子', '📱', 0, 1, 1, NOW(), NOW(), 1),
(2, '书籍教材', '📚', 0, 2, 1, NOW(), NOW(), 1),
(3, '生活代步', '🚲', 0, 3, 1, NOW(), NOW(), 1),
(4, '衣物鞋帽', '👕', 0, 4, 1, NOW(), NOW(), 1),
(5, '美妆日用', '💄', 0, 5, 1, NOW(), NOW(), 1);

INSERT INTO `tb_category` (`category_id`, `name`, `icon`, `parent_id`, `sort`, `status`, `create_time`, `update_time`, `ver`) VALUES
(101, '手机', '📱', 1, 1, 1, NOW(), NOW(), 1),
(102, '电脑', '💻', 1, 2, 1, NOW(), NOW(), 1),
(103, '平板', '📲', 1, 3, 1, NOW(), NOW(), 1),
(104, '配件', '🎧', 1, 4, 1, NOW(), NOW(), 1),
(201, '教材', '📖', 2, 1, 1, NOW(), NOW(), 1),
(202, '小说', '📕', 2, 2, 1, NOW(), NOW(), 1),
(301, '自行车', '🚲', 3, 1, 1, NOW(), NOW(), 1),
(302, '电动车', '🛵', 3, 2, 1, NOW(), NOW(), 1);
