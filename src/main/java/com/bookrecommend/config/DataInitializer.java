package com.bookrecommend.config;

import com.bookrecommend.entity.Book;
import com.bookrecommend.entity.BookCategory;
import com.bookrecommend.entity.User;
import com.bookrecommend.entity.UserProfile;
import com.bookrecommend.mapper.BookCategoryMapper;
import com.bookrecommend.mapper.BookMapper;
import com.bookrecommend.mapper.UserMapper;
import com.bookrecommend.mapper.UserProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final BookMapper bookMapper;
    private final BookCategoryMapper categoryMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userMapper.count("") > 0) {
            return;
        }

        System.out.println(">>> 正在启动【精准7本精品馆藏】初始化...");

        // 1. 初始化用户
        User admin = createU("admin", 1);
        User reader = createU("reader", 0);

        // 2. 初始画像 (针对这7本书的标签)
        saveProfile(reader.getId(), List.of("文学", "推理", "科幻", "宇宙"));

        // 3. 建立分类
        String[] cats = {"文学", "技术", "科幻", "艺术", "历史"};
        for (int i = 0; i < cats.length; i++) {
            BookCategory c = new BookCategory();
            c.setName(cats[i]);
            c.setParentId(0L);
            c.setSort(100 - i);
            categoryMapper.insert(c);
        }

        // 4. 灌入 7 本真·图书 (封面 100% 存在于本地)
        b("人类简史", "尤瓦尔·赫拉利", "9787508647357", 5L, "一部关于人类进化的震撼史诗。", List.of("历史", "科普", "哲学"));
        b("边城", "沈从文", "9787530202562", 1L, "湘西世界的灵动与哀愁，华语文学经典。", List.of("文学", "经典", "纯爱"));
        b("流浪地球", "刘慈欣", "9787536693968", 3L, "中国硬核科幻的代表作，带着家园去流浪。", List.of("科幻", "硬核", "刘慈欣"));
        b("银河帝国：基地", "阿西莫夫", "9787539943039", 3L, "心理史学的传奇，银河帝国的史诗。", List.of("科幻", "经典", "阿西莫夫"));
        b("白夜行", "东野圭吾", "9787544242516", 1L, "凄凉的爱情与绝望的守护，东野圭吾巅峰之作。", List.of("文学", "推理", "日本"));
        b("百年孤独", "马尔克斯", "9787544253994", 1L, "拉丁美洲魔幻现实主义的巅峰。", List.of("文学", "经典", "拉美"));
        b("解忧杂货店", "东野圭吾", "9787544270878", 1L, "温暖人心的奇迹，治愈系的推理经典。", List.of("文学", "治愈", "日本"));

        System.out.println(">>> 7本精品图书已精准上架！所有图片均为本地高清。");
    }

    private User createU(String n, int r) {
        User u = new User(); u.setUsername(n); u.setPassword(passwordEncoder.encode("123456"));
        u.setEmail(n + "@archive.com"); u.setRole(r); u.setStatus(1);
        userMapper.insert(u); return u;
    }

    private void saveProfile(Long uid, List<String> i) {
        UserProfile p = new UserProfile(); p.setUserId(uid); p.setInterests(i);
        userProfileMapper.insert(p);
    }

    private void b(String t, String a, String i, Long c, String d, List<String> tags) {
        Book b = new Book(); b.setTitle(t); b.setAuthor(a); b.setIsbn(i); b.setCategoryId(c);
        b.setDescription(d); 
        // 100% 本地图源
        b.setCover("/covers/" + i + ".jpg");
        b.setPublisher("中国档案馆出版社"); b.setPublishDate(LocalDate.now()); b.setTags(tags);
        
        // 灌入模拟正文
        b.setContent("### " + t + " - 第一章\n\n文字正在加载中...\n\n这是一部伟大的著作。由作者 " + a + " 倾力打造。");
        b.setStock(100); b.setStatus(1);
        bookMapper.insert(b);
    }
}
