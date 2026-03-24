-- 业务管理菜单SQL
-- 注意：请根据实际情况修改menu_id，确保不与现有菜单冲突

-- 1. 创建父级菜单：业务管理
INSERT INTO `tb_menu` (`menu_id`, `parent_id`, `menu_name`, `path`, `component`, `menu_type`, `order_num`, `perms`, `icon`, `hidden`, `user_id`, `create_time`, `update_time`, `status`, `ver`) 
VALUES (2970482232512675842, 0, '业务管理', 'admin/business', NULL, 1, 3, NULL, 'shopping', 1, 1974016655841509377, NOW(), NOW(), 1, 1);

-- 2. 商品审核
INSERT INTO `tb_menu` (`menu_id`, `parent_id`, `menu_name`, `path`, `component`, `menu_type`, `order_num`, `perms`, `icon`, `hidden`, `user_id`, `create_time`, `update_time`, `status`, `ver`) 
VALUES (2970482232512695902, 2970482232512675842, '商品审核', 'audit', 'business/product/audit', 2, 1, 'business:product:audit', 'audit', 1, 1974016655841509377, NOW(), NOW(), 1, 1);

-- 3. 商品管理（可选，如果有的话）
INSERT INTO `tb_menu` (`menu_id`, `parent_id`, `menu_name`, `path`, `component`, `menu_type`, `order_num`, `perms`, `icon`, `hidden`, `user_id`, `create_time`, `update_time`, `status`, `ver`) 
VALUES (2970482232512695903, 2970482232512675842, '商品管理', 'product', 'business/product/index', 2, 2, 'business:product:list', 'goods', 1, 1974016655841509377, NOW(), NOW(), 1, 1);

-- 4. 分类管理
INSERT INTO `tb_menu` (`menu_id`, `parent_id`, `menu_name`, `path`, `component`, `menu_type`, `order_num`, `perms`, `icon`, `hidden`, `user_id`, `create_time`, `update_time`, `status`, `ver`) 
VALUES (2970482232512695904, 2970482232512675842, '分类管理', 'category', 'business/category/index', 2, 3, 'business:category:list', 'category', 1, 1974016655841509377, NOW(), NOW(), 1, 1);

-- 5. 订单管理
INSERT INTO `tb_menu` (`menu_id`, `parent_id`, `menu_name`, `path`, `component`, `menu_type`, `order_num`, `perms`, `icon`, `hidden`, `user_id`, `create_time`, `update_time`, `status`, `ver`) 
VALUES (2970482232512695905, 2970482232512675842, '订单管理', 'order', 'business/order/index', 2, 4, 'business:order:list', 'order', 1, 1974016655841509377, NOW(), NOW(), 1, 1);

-- 6. 购物车管理
INSERT INTO `tb_menu` (`menu_id`, `parent_id`, `menu_name`, `path`, `component`, `menu_type`, `order_num`, `perms`, `icon`, `hidden`, `user_id`, `create_time`, `update_time`, `status`, `ver`) 
VALUES (2970482232512695906, 2970482232512675842, '购物车管理', 'cart', 'business/cart/index', 2, 5, 'business:cart:list', 'cart', 1, 1974016655841509377, NOW(), NOW(), 1, 1);

-- 7. 收藏管理
INSERT INTO `tb_menu` (`menu_id`, `parent_id`, `menu_name`, `path`, `component`, `menu_type`, `order_num`, `perms`, `icon`, `hidden`, `user_id`, `create_time`, `update_time`, `status`, `ver`) 
VALUES (2970482232512695907, 2970482232512675842, '收藏管理', 'favorite', 'business/favorite/index', 2, 6, 'business:favorite:list', 'star', 1, 1974016655841509377, NOW(), NOW(), 1, 1);
