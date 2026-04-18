package com.bookrecommend.vo;

import lombok.Data;
import java.util.List;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String email;
    private String avatar;
    private Integer role;
    private List<String> interests;
    private List<Long> favoriteCategories;
}
