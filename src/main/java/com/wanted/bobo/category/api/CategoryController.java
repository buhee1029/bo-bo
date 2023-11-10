package com.wanted.bobo.category.api;

import com.wanted.bobo.category.Category;
import com.wanted.bobo.category.CategoryResponse;
import com.wanted.bobo.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "카테고리")
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    @GetMapping()
    public ApiResponse<List<CategoryResponse>> categoryList() {
        return ApiResponse.ok(Category.toList());
    }

}