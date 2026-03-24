-- 为商品表添加审核字段
ALTER TABLE `tb_product` ADD COLUMN `audit_status` TINYINT DEFAULT 0 COMMENT '审核状态：0-待审核，1-已通过，2-已拒绝' AFTER `status`;
ALTER TABLE `tb_product` ADD COLUMN `reject_reason` VARCHAR(500) DEFAULT NULL COMMENT '拒绝原因' AFTER `audit_status`;
