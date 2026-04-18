package com.bookrecommend.dto;

import lombok.Data;
import java.util.List;

@Data
public class UpdateUserRequest {
    private String email;
    private String avatar;
    private List<String> interests;
    private List<Long> favoriteCategories;
}
