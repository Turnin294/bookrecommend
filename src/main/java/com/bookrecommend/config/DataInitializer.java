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
            return; // 已经有数据了，不再初始化
        }

        System.out.println(">>> 正在初始化档案馆基础数据...");

        // 1. 初始化用户
        // 管理员: admin / 123456
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("123456"));
        admin.setEmail("admin@archive.com");
        admin.setRole(1);
        admin.setStatus(1);
        userMapper.insert(admin);

        // 普通用户: reader / 123456
        User reader = new User();
        reader.setUsername("reader");
        reader.setPassword(passwordEncoder.encode("123456"));
        reader.setEmail("reader@archive.com");
        reader.setRole(0);
        reader.setStatus(1);
        userMapper.insert(reader);

        // 2. 初始化用户画像 (为 reader 预设兴趣)
        UserProfile profile = new UserProfile();
        profile.setUserId(reader.getId());
        profile.setInterests(List.of("科幻", "技术", "哲学"));
        userProfileMapper.insert(profile);

        // 3. 初始化分类
        String[] catNames = {"文学", "技术", "科幻", "艺术", "历史"};
        for (int i = 0; i < catNames.length; i++) {
            BookCategory cat = new BookCategory();
            cat.setName(catNames[i]);
            cat.setParentId(0L);
            cat.setSort(10 - i);
            categoryMapper.insert(cat);
        }

        // 4. 初始化图书 (精选几本具有视觉冲击力的书)
        initBooks();

        System.out.println(">>> 数据初始化完成！你可以使用 admin/123456 或 reader/123456 登录。");
    }

    private void initBooks() {
        // 科幻类
        insertBook("三体：死神永生", "刘慈欣", "9787229030933", 3L, "地球往事三部曲的终章。", 
            "https://img9.doubanio.com/view/subject/s/public/s4475471.jpg", List.of("科幻", "硬核", "宇宙"));
        
        insertBook("沙丘", "弗兰克·赫伯特", "9787550020733", 3L, "科幻史上的不朽名著，权力的史诗。", 
            "https://img1.doubanio.com/view/subject/s/public/s29505676.jpg", List.of("科幻", "史诗", "沙漠"));

        insertBook("海伯利安", "丹·西蒙斯", "9787539976723", 3L, "伟大的太空歌剧。", 
            "https://img1.doubanio.com/view/subject/s/public/s27926135.jpg", List.of("科幻", "太空歌剧", "经典"));

        // 技术类
        insertBook("代码整洁之道", "Robert C. Martin", "9787115213907", 2L, "程序员的必读书籍。", 
            "https://img1.doubanio.com/view/subject/s/public/s4051610.jpg", List.of("技术", "编程", "重构"));

        insertBook("深入理解Java虚拟机", "周志明", "9787111641247", 2L, "Java开发者的进阶神作。", 
            "https://img3.doubanio.com/view/subject/s/public/s33519803.jpg", List.of("技术", "Java", "JVM"));

        // 文学类
        insertBook("百年孤独", "加西亚·马尔克斯", "9787544253994", 1L, "魔幻现实主义的巅峰。", 
            "https://img3.doubanio.com/view/subject/s/public/s6384944.jpg", List.of("文学", "经典", "拉美"));

        insertBook("局外人", "阿尔贝·加缪", "9787532751501", 1L, "今天，妈妈死了。也许是昨天，我不知道。", 
            "https://img2.doubanio.com/view/subject/s/public/s4401072.jpg", List.of("文学", "哲学", "荒诞"));

        // 历史/艺术类
        insertBook("人类简史", "尤瓦尔·赫拉利", "9787508647357", 5L, "从认知革命到生物革命。", 
            "https://img3.doubanio.com/view/subject/s/public/s27814323.jpg", List.of("历史", "科普", "哲学"));
            
        insertBook("万历十五年", "黄仁宇", "9787101014303", 5L, "大明王朝的一年，也是平淡的一年。", 
            "https://img1.doubanio.com/view/subject/s/public/s1012979.jpg", List.of("历史", "明朝", "经典"));
    }

    private void insertBook(String title, String author, String isbn, Long catId, String desc, String cover, List<String> tags) {
        Book b = new Book();
        b.setTitle(title);
        b.setAuthor(author);
        b.setIsbn(isbn);
        b.setCategoryId(catId);
        b.setDescription(desc);
        b.setCover(cover);
        b.setPublisher("象牙出版社");
        b.setPublishDate(LocalDate.now());
        b.setTags(tags);
        b.setStock(100);
        b.setStatus(1);
        bookMapper.insert(b);
    }
}
