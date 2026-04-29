-- ============================================
-- 我的厨房 - 数据库初始化脚本
-- 执行方式: mysql -u root -p < schema.sql
-- 或在 MySQL 命令行: SOURCE schema.sql
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS my_kitchen DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE my_kitchen;

-- ============================================
-- 用户表
-- ============================================
DROP TABLE IF EXISTS user;
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) DEFAULT '/avatar/default.jpg' COMMENT '头像',
    bio VARCHAR(255) COMMENT '个人简介',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================
-- 食谱表
-- ============================================
DROP TABLE IF EXISTS recipe;
CREATE TABLE IF NOT EXISTS recipe (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL COMMENT '标题',
    description TEXT COMMENT '描述',
    image_url VARCHAR(255) COMMENT '封面图',
    category VARCHAR(50) COMMENT '分类: breakfast/lunch/dinner/dessert/soup/snack/vegetable/main/drink',
    difficulty VARCHAR(20) COMMENT '难度: easy/medium/hard',
    cook_time INT COMMENT '烹饪时间(分钟)',
    servings INT COMMENT '份量',
    author_id BIGINT COMMENT '作者ID',
    author_name VARCHAR(50) COMMENT '作者名称',
    author_avatar VARCHAR(255) COMMENT '作者头像',
    favorites_count INT DEFAULT 0 COMMENT '收藏数',
    views_count INT DEFAULT 0 COMMENT '浏览数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_author (author_id),
    INDEX idx_favorites (favorites_count DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食谱表';

-- ============================================
-- 食材表
-- ============================================
DROP TABLE IF EXISTS ingredient;
CREATE TABLE IF NOT EXISTS ingredient (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    recipe_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL COMMENT '食材名称',
    amount VARCHAR(50) COMMENT '用量',
    sort_order INT DEFAULT 0,
    FOREIGN KEY (recipe_id) REFERENCES recipe(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食材表';

-- ============================================
-- 步骤表
-- ============================================
DROP TABLE IF EXISTS step;
CREATE TABLE IF NOT EXISTS step (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    recipe_id BIGINT NOT NULL,
    step_number INT NOT NULL COMMENT '步骤序号',
    content TEXT COMMENT '步骤内容',
    image_url VARCHAR(255) COMMENT '步骤图片',
    FOREIGN KEY (recipe_id) REFERENCES recipe(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='步骤表';

-- ============================================
-- 收藏表
-- ============================================
DROP TABLE IF EXISTS favorite;
CREATE TABLE IF NOT EXISTS favorite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_recipe (user_id, recipe_id),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (recipe_id) REFERENCES recipe(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- ============================================
-- 评论表
-- ============================================
DROP TABLE IF EXISTS comment;
CREATE TABLE IF NOT EXISTS comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    recipe_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    user_nickname VARCHAR(50) COMMENT '评论者昵称',
    user_avatar VARCHAR(255) COMMENT '评论者头像',
    content TEXT NOT NULL COMMENT '评论内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (recipe_id) REFERENCES recipe(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_recipe (recipe_id),
    INDEX idx_create_time (create_time DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- ============================================
-- 插入测试数据 - 用户
-- ============================================
INSERT INTO user (username, password, nickname, avatar, bio) VALUES
('chef_wang', '123456', '王大厨', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop', '热爱美食，专注家常菜20年'),
('sweet_home', '123456', '甜甜家', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&h=100&fit=crop', '分享甜蜜生活'),
('healthy_eat', '123456', '健康饮食', 'https://images.unsplash.com/photo-1544725176-7c40e5a71c5e?w=100&h=100&fit=crop', '健康美味两不误');

-- ============================================
-- 插入测试数据 - 食谱
-- ============================================
INSERT INTO recipe (title, description, image_url, category, difficulty, cook_time, servings, author_id, author_name, author_avatar, favorites_count, views_count) VALUES

-- 主食类
('红烧肉', '经典家常红烧肉，肥而不腻，入口即化，拌饭一绝', 'https://images.unsplash.com/photo-1623689046286-adcf6bc93a5b?w=800&h=600&fit=crop', 'main', 'medium', 60, 4, 1, '王大厨', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop', 328, 2560),
('番茄炒蛋', '最简单的家常菜，却有着最经典的味道，酸甜可口', 'https://images.unsplash.com/photo-1482049016gy-84aa025f8ed1?w=800&h=600&fit=crop', 'main', 'easy', 15, 2, 1, '王大厨', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop', 456, 5120),
('可乐鸡翅', '甜香可口，外焦里嫩，孩子们的最爱', 'https://images.unsplash.com/photo-1527477396000-e27163b481c2?w=800&h=600&fit=crop', 'main', 'easy', 30, 4, 1, '王大厨', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop', 289, 1780),
('蛋炒饭', '简单却不简单，粒粒分明，完美的黄金炒饭', 'https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=800&h=600&fit=crop', 'main', 'easy', 15, 1, 1, '王大厨', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop', 198, 3560),
('酸辣土豆丝', '开胃下饭，酸辣可口，超级无敌下饭菜', 'https://images.unsplash.com/photo-1518779578993-ec3579fee39f?w=800&h=600&fit=crop', 'vegetable', 'easy', 15, 2, 1, '王大厨', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop', 167, 1960),
('宫保鸡丁', '川菜经典，麻辣鲜香，超级下饭', 'https://images.unsplash.com/photo-1525755662778-989d0524087e?w=800&h=600&fit=crop', 'main', 'medium', 25, 3, 1, '王大厨', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop', 234, 2890),

-- 素菜类
('蒜蓉西兰花', '清淡健康，营养丰富，减肥人士首选', 'https://images.unsplash.com/photo-1540420773420-3366772f4999?w=800&h=600&fit=crop', 'vegetable', 'easy', 10, 2, 3, '健康饮食', 'https://images.unsplash.com/photo-1544725176-7c40e5a71c5e?w=100&h=100&fit=crop', 145, 890),
('清炒时蔬', '新鲜时令蔬菜，简单清淡，保留原味', 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800&h=600&fit=crop', 'vegetable', 'easy', 8, 2, 3, '健康饮食', 'https://images.unsplash.com/photo-1544725176-7c40e5a71c5e?w=100&h=100&fit=crop', 89, 456),

-- 早餐类
('皮蛋瘦肉粥', '绵密顺滑，温暖胃的经典粤式早餐', 'https://images.unsplash.com/photo-1476718406336-bb5a9690ee2a?w=800&h=600&fit=crop', 'breakfast', 'easy', 40, 4, 3, '健康饮食', 'https://images.unsplash.com/photo-1544725176-7c40e5a71c5e?w=100&h=100&fit=crop', 178, 1234),
('鸡蛋三明治', '营养早餐，五分钟搞定', 'https://images.unsplash.com/photo-1525351484163-7529414344d8?w=800&h=600&fit=crop', 'breakfast', 'easy', 5, 1, 2, '甜甜家', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&h=100&fit=crop', 234, 987),

-- 甜点类
('巧克力蛋糕', '绵软湿润，巧克力浓郁，甜品控的最爱', 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=800&h=600&fit=crop', 'dessert', 'medium', 90, 6, 2, '甜甜家', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&h=100&fit=crop', 412, 6240),
('提拉米苏', '来自意大利的经典甜点，咖啡香与芝士的完美融合', 'https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=800&h=600&fit=crop', 'dessert', 'hard', 30, 4, 2, '甜甜家', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&h=100&fit=crop', 356, 4321),
('草莓奶昔', '甜蜜粉嫩，少女心爆棚', 'https://images.unsplash.com/photo-1553530666-ba11a7da3888?w=800&h=600&fit=crop', 'dessert', 'easy', 5, 2, 2, '甜甜家', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&h=100&fit=crop', 189, 2345),

-- 饮品
('鲜榨橙汁', '新鲜健康，早餐必备', 'https://images.unsplash.com/photo-1621506289937-a8e4df240d0b?w=800&h=600&fit=crop', 'drink', 'easy', 5, 1, 2, '甜甜家', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&h=100&fit=crop', 167, 1340),
('珍珠奶茶', '经典港式奶茶，Q弹珍珠', 'https://images.unsplash.com/photo-1558857563-b371033873b8?w=800&h=600&fit=crop', 'drink', 'medium', 20, 2, 2, '甜甜家', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&h=100&fit=crop', 289, 3456),

-- 汤类
('奶油蘑菇汤', '法式经典，汤汁浓郁丝滑', 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=800&h=600&fit=crop', 'soup', 'medium', 40, 4, 3, '健康饮食', 'https://images.unsplash.com/photo-1544725176-7c40e5a71c5e?w=100&h=100&fit=crop', 156, 876),
('番茄蛋花汤', '酸甜开胃，饭前一碗', 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=800&h=600&fit=crop', 'soup', 'easy', 15, 3, 1, '王大厨', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop', 123, 654),

-- 小吃
('炸鸡翅', '外酥里嫩，追剧必备零食', 'https://images.unsplash.com/photo-1527477396000-e27163b481c2?w=800&h=600&fit=crop', 'snack', 'easy', 25, 3, 1, '王大厨', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop', 298, 2345);

-- ============================================
-- 插入测试数据 - 食材
-- ============================================
INSERT INTO ingredient (recipe_id, name, amount, sort_order) VALUES
-- 红烧肉 (recipe_id=1)
(1, '五花肉', '500g', 1),
(1, '冰糖', '30g', 2),
(1, '生抽', '2勺', 3),
(1, '老抽', '1勺', 4),
(1, '料酒', '2勺', 5),
(1, '八角', '2个', 6),
(1, '桂皮', '1小块', 7),
(1, '姜片', '5片', 8),

-- 番茄炒蛋 (recipe_id=2)
(2, '番茄', '2个', 1),
(2, '鸡蛋', '3个', 2),
(2, '盐', '适量', 3),
(2, '糖', '少许', 4),
(2, '葱花', '少许', 5),

-- 可乐鸡翅 (recipe_id=3)
(3, '鸡翅', '8个', 1),
(3, '可乐', '330ml', 2),
(3, '生抽', '2勺', 3),
(3, '姜片', '3片', 4),

-- 蛋炒饭 (recipe_id=4)
(4, '米饭', '1碗', 1),
(4, '鸡蛋', '2个', 2),
(4, '葱花', '适量', 3),
(4, '盐', '适量', 4),

-- 酸辣土豆丝 (recipe_id=5)
(5, '土豆', '2个', 1),
(5, '干辣椒', '5个', 2),
(5, '花椒', '10粒', 3),
(5, '蒜末', '适量', 4),
(5, '醋', '2勺', 5),
(5, '盐', '适量', 6),

-- 宫保鸡丁 (recipe_id=6)
(6, '鸡胸肉', '300g', 1),
(6, '花生米', '50g', 2),
(6, '干辣椒', '10个', 3),
(6, '花椒', '1勺', 4),
(6, '葱段', '适量', 5),
(6, '生抽', '1勺', 6),
(6, '醋', '1勺', 7),
(6, '糖', '1勺', 8),

-- 蒜蓉西兰花 (recipe_id=7)
(7, '西兰花', '1颗', 1),
(7, '蒜蓉', '3瓣', 2),
(7, '盐', '适量', 3),

-- 清炒时蔬 (recipe_id=8)
(8, '时令蔬菜', '300g', 1),
(8, '蒜片', '适量', 2),
(8, '盐', '适量', 3),

-- 皮蛋瘦肉粥 (recipe_id=9)
(9, '大米', '100g', 1),
(9, '瘦肉', '100g', 2),
(9, '皮蛋', '2个', 3),
(9, '姜丝', '少许', 4),
(9, '盐', '适量', 5),

-- 鸡蛋三明治 (recipe_id=10)
(10, '吐司面包', '2片', 1),
(10, '鸡蛋', '1个', 2),
(10, '生菜', '2片', 3),
(10, '沙拉酱', '适量', 4),

-- 巧克力蛋糕 (recipe_id=11)
(11, '低筋面粉', '100g', 1),
(11, '可可粉', '30g', 2),
(11, '鸡蛋', '4个', 3),
(11, '黄油', '50g', 4),
(11, '糖', '60g', 5),
(11, '牛奶', '50ml', 6),

-- 提拉米苏 (recipe_id=12)
(12, '马斯卡彭奶酪', '250g', 1),
(12, '淡奶油', '200ml', 2),
(12, '手指饼干', '200g', 3),
(12, '浓缩咖啡', '200ml', 4),
(12, '蛋黄', '3个', 5),
(12, '细砂糖', '60g', 6),
(12, '可可粉', '适量', 7),

-- 草莓奶昔 (recipe_id=13)
(13, '草莓', '150g', 1),
(13, '牛奶', '200ml', 2),
(13, '蜂蜜', '1勺', 3),

-- 鲜榨橙汁 (recipe_id=14)
(14, '橙子', '3个', 1),

-- 珍珠奶茶 (recipe_id=15)
(15, '红茶', '1包', 1),
(15, '牛奶', '200ml', 2),
(15, '珍珠', '50g', 3),
(15, '糖', '适量', 4),

-- 奶油蘑菇汤 (recipe_id=16)
(16, '蘑菇', '300g', 1),
(16, '洋葱', '1个', 2),
(16, '黄油', '30g', 3),
(16, '淡奶油', '200ml', 4),
(16, '鸡汤', '500ml', 5),

-- 番茄蛋花汤 (recipe_id=17)
(17, '番茄', '2个', 1),
(17, '鸡蛋', '2个', 2),
(17, '葱花', '适量', 3),
(17, '盐', '适量', 4),

-- 炸鸡翅 (recipe_id=18)
(18, '鸡翅', '10个', 1),
(18, '面粉', '适量', 2),
(18, '炸粉', '适量', 3),
(18, '盐', '适量', 4);

-- ============================================
-- 插入测试数据 - 步骤
-- ============================================
INSERT INTO step (recipe_id, step_number, content) VALUES
-- 红烧肉
(1, 1, '五花肉切成3厘米见方的块，冷水下锅焯水去血沫，捞出沥干备用'),
(1, 2, '锅中放少许油，加入冰糖小火炒至焦糖色，要注意不要炒糊'),
(1, 3, '下入五花肉翻炒均匀，使其表面裹上糖色'),
(1, 4, '加入料酒、生抽、老抽翻炒上色'),
(1, 5, '加入足量开水没过肉块，放入八角、桂皮、姜片'),
(1, 6, '大火烧开后转小火炖煮1小时，期间注意观察水量'),
(1, 7, '最后大火收汁，汤汁浓稠即可出锅，撒上葱花点缀'),

-- 番茄炒蛋
(2, 1, '番茄洗净切块，鸡蛋打散加少许盐搅匀'),
(2, 2, '锅中多放些油，油热后倒入蛋液，快速划散成蛋花'),
(2, 3, '盛出鸡蛋，锅中留底油，下番茄块翻炒'),
(2, 4, '炒至番茄出汁，加入少许糖提鲜'),
(2, 5, '倒入炒好的鸡蛋，翻炒均匀后加盐调味，撒上葱花即可出锅'),

-- 可乐鸡翅
(3, 1, '鸡翅洗净，两面划几刀方便入味，冷水下锅焯水'),
(3, 2, '捞出沥干，锅中少许油，将鸡翅煎至两面金黄'),
(3, 3, '倒入可乐和生抽，大火烧开'),
(3, 4, '转中小火煮20分钟，最后大火收汁即可'),

-- 蛋炒饭
(4, 1, '准备隔夜米饭，用筷子拨散备用'),
(4, 2, '鸡蛋打散，可以先把蛋液和米饭拌在一起'),
(4, 3, '锅中多放油，大火烧热后下入米饭快速翻炒'),
(4, 4, '炒至米饭粒粒分明，蛋液凝固'),
(4, 5, '加入盐和葱花，翻炒均匀后出锅'),

-- 酸辣土豆丝
(5, 1, '土豆去皮切成细丝，用清水浸泡去除淀粉'),
(5, 2, '锅中烧水，水开后下土豆丝焯烫30秒，捞出过凉水'),
(5, 3, '锅中放油，下花椒和干辣椒段爆香'),
(5, 4, '下蒜末翻炒出香味，倒入土豆丝大火快炒'),
(5, 5, '烹入醋，加盐调味，翻炒均匀即可出锅'),

-- 宫保鸡丁
(6, 1, '鸡胸肉切丁，加入少许盐、料酒、淀粉抓匀腌制15分钟'),
(6, 2, '调酱汁：生抽、醋、糖、淀粉、水混合均匀'),
(6, 3, '锅中放油，下花生米炸至金黄酥脆，捞出备用'),
(6, 4, '锅中留底油，下干辣椒和花椒爆香'),
(6, 5, '下鸡丁大火翻炒至变色',
(6, 6, '倒入酱汁快速翻炒，最后加入花生米和葱段即可'),

-- 蒜蓉西兰花
(7, 1, '西兰花掰成小朵，用盐水浸泡10分钟洗净'),
(7, 2, '烧一锅水，加少许盐和油，下西兰花焯水1分钟'),
(7, 3, '捞出过凉水沥干，锅中放油爆香蒜蓉'),
(7, 4, '下西兰花快速翻炒，加盐调味即可出锅'),

-- 清炒时蔬
(8, 1, '蔬菜洗净切段',
(8, 2, '锅中放油，下蒜片爆香'),
(8, 3, '下蔬菜大火快炒，加盐调味'),
(8, 4, '炒至断生即可出锅，保持蔬菜的清脆'),

-- 皮蛋瘦肉粥
(9, 1, '大米洗净，提前浸泡30分钟'),
(9, 2, '瘦肉切丝，用盐、料酒腌制10分钟'),
(9, 3, '皮蛋切成小块备用'),
(9, 4, '锅中加水烧开，放入大米煮开后转小火慢熬'),
(9, 5, '熬至粥水浓稠时，加入肉丝和皮蛋'),
(9, 6, '继续小火煮10分钟，加盐和白胡椒粉调味，撒上姜丝即可'),

-- 鸡蛋三明治
(10, 1, '吐司面包两面烤至微黄'),
(10, 2, '鸡蛋煮熟或煎熟，切片'),
(10, 3, '生菜洗净沥干'),
(10, 4, '在面包上依次放生菜、鸡蛋、沙拉酱，盖上另一片即可'),

-- 巧克力蛋糕
(11, 1, '蛋黄加糖打发至颜色发白，体积膨胀'),
(11, 2, '加入融化的黄油和牛奶搅拌均匀'),
(11, 3, '筛入低筋面粉和可可粉，翻拌至无干粉'),
(11, 4, '蛋白打发至硬性发泡，分三次拌入面糊'),
(11, 5, '倒入模具，放入预热好的烤箱，180度烤40分钟'),

-- 提拉米苏
(12, 1, '蛋黄加糖打发至颜色发白，体积膨胀'),
(12, 2, '加入马斯卡彭奶酪，搅拌均匀'),
(12, 3, '淡奶油打发至六分发，轻轻拌入奶酪糊中'),
(12, 4, '咖啡放凉后，手指饼干快速蘸湿铺底'),
(12, 5, '铺上一层奶酪糊，重复以上步骤两层'),
(12, 6, '放入冰箱冷藏4小时以上，食用前撒上厚厚的可可粉'),

-- 草莓奶昔
(13, 1, '草莓洗净去蒂'),
(13, 2, '将草莓、牛奶、蜂蜜放入搅拌机'),
(13, 3, '搅拌至顺滑细腻'),
(13, 4, '倒入杯中即可饮用，可以加几颗草莓装饰'),

-- 鲜榨橙汁
(14, 1, '橙子剥皮',
(14, 2, '放入榨汁机或用手挤'),
(14, 3, '过滤掉渣滓',
(14, 4, '倒入杯中，冰镇后饮用更佳'),

-- 珍珠奶茶
(15, 1, '先煮珍珠：水开后下珍珠，煮10分钟焖5分钟'),
(15, 2, '捞出珍珠过冷水，泡在糖水里备用'),
(15, 3, '泡一杯浓红茶，滤掉茶叶'),
(15, 4, '加入牛奶和糖搅拌均匀'),
(15, 5, '杯底加入珍珠，倒入奶茶即可'),

-- 奶油蘑菇汤
(16, 1, '蘑菇切片，洋葱切丁备用'),
(16, 2, '锅中放黄油，融化后下洋葱丁炒软'),
(16, 3, '加入蘑菇片继续翻炒至软'),
(16, 4, '加入面粉翻炒均匀，慢慢倒入鸡汤搅拌'),
(16, 5, '小火烧煮15分钟，汤汁变稠'),
(16, 6, '加入淡奶油搅匀，继续煮5分钟，用盐和黑胡椒调味'),

-- 番茄蛋花汤
(17, 1, '番茄切块，鸡蛋打散备用'),
(17, 2, '锅中加水烧开，下番茄块煮软'),
(17, 3, '慢慢倒入蛋液，用筷子轻轻搅动形成蛋花'),
(17, 4, '加盐调味，撒上葱花即可出锅'),

-- 炸鸡翅
(18, 1, '鸡翅洗净，用厨房纸吸干水分'),
(18, 2, '加入盐、料酒、生抽腌制30分钟'),
(18, 3, '裹上炸粉或面粉',
(18, 4, '油温170度，下鸡翅炸8-10分钟至金黄'),
(18, 5, '捞出沥油，可以撒上孜然或辣椒粉');

-- ============================================
-- 插入测试数据 - 评论
-- ============================================
INSERT INTO comment (recipe_id, user_id, user_nickname, user_avatar, content, create_time) VALUES
(1, 2, '甜甜家', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&h=100&fit=crop', '看起来太诱人了！周末试试', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 3, '健康饮食', 'https://images.unsplash.com/photo-1544725176-7c40e5a71c5e?w=100&h=100&fit=crop', '肥而不腻，赞！', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 1, '王大厨', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop', '这是我家孩子最爱吃的菜', NOW()),
(2, 3, '健康饮食', 'https://images.unsplash.com/photo-1544725176-7c40e5a71c5e?w=100&h=100&fit=crop', '加一点点糖会更鲜哦', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(3, 2, '甜甜家', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&h=100&fit=crop', '孩子超级爱吃！', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(11, 1, '王大厨', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop', '蛋糕很绵软，成功！', NOW()),
(11, 3, '健康饮食', 'https://images.unsplash.com/photo-1544725176-7c40e5a71c5e?w=100&h=100&fit=crop', '可可粉多放点更好吃', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(12, 1, '王大厨', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop', '正宗的做法！', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(15, 2, '甜甜家', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&h=100&fit=crop', '珍珠Q弹，比奶茶店的还好喝', DATE_SUB(NOW(), INTERVAL 3 DAY));

-- ============================================
-- 插入测试数据 - 收藏
-- ============================================
INSERT INTO favorite (user_id, recipe_id, create_time) VALUES
(1, 2, NOW()),
(1, 11, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 1, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 3, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2, 11, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2, 15, NOW()),
(3, 7, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, 9, DATE_SUB(NOW(), INTERVAL 2 DAY));

-- ============================================
-- 完成
-- ============================================
SELECT '数据库初始化完成!' AS message;
SELECT CONCAT('用户: ', COUNT(*), ' | 食谱: ', (SELECT COUNT(*) FROM recipe), ' | 评论: ', (SELECT COUNT(*) FROM comment)) AS stats FROM user;

-- ============================================
-- 企业级扩展表
-- ============================================

-- 标签表
CREATE TABLE IF NOT EXISTS tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(30) UNIQUE NOT NULL COMMENT '标签名称',
    recipe_count INT DEFAULT 0 COMMENT '使用次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- 食谱-标签关联表
CREATE TABLE IF NOT EXISTS recipe_tag (
    recipe_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (recipe_id, tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食谱标签关联表';

-- 评分表
CREATE TABLE IF NOT EXISTS rating (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    recipe_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    score TINYINT NOT NULL COMMENT '1-5星',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_recipe_user (recipe_id, user_id),
    FOREIGN KEY (recipe_id) REFERENCES recipe(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评分表';

-- 关注表
CREATE TABLE IF NOT EXISTS follow (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    follower_id BIGINT NOT NULL COMMENT '关注者',
    following_id BIGINT NOT NULL COMMENT '被关注者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_follow (follower_id, following_id),
    FOREIGN KEY (follower_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关注表';

-- 动态表
CREATE TABLE IF NOT EXISTS feed (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    content TEXT COMMENT '动态内容',
    image_url VARCHAR(255) COMMENT '图片',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_create_time (create_time DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态表';

-- 动态点赞表
CREATE TABLE IF NOT EXISTS feed_like (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    feed_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_feed_user (feed_id, user_id),
    FOREIGN KEY (feed_id) REFERENCES feed(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态点赞表';

-- 通知表
CREATE TABLE IF NOT EXISTS notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '接收者',
    type VARCHAR(20) NOT NULL COMMENT '类型: favorite/comment/follow',
    title VARCHAR(100) COMMENT '标题',
    content TEXT COMMENT '内容',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_read (user_id, is_read),
    INDEX idx_create_time (create_time DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT COMMENT '操作用户',
    operation VARCHAR(50) COMMENT '操作名称',
    method VARCHAR(10) COMMENT '请求方法',
    url VARCHAR(255) COMMENT '请求URL',
    ip VARCHAR(50) COMMENT 'IP地址',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_create_time (create_time DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 插入默认标签
INSERT INTO tag (name, recipe_count) VALUES
('快手', 5), ('下饭', 8), ('健康', 6), ('甜点', 4), ('早餐', 3),
('简单', 10), ('开胃', 4), ('家常', 15), ('孩子爱吃', 5), ('减肥', 3);
