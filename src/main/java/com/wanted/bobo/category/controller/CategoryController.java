package com.wanted.bobo.category.controller;

import com.wanted.bobo.category.Category;
import com.wanted.bobo.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "카테고리 목록 조회")
    public ApiResponse<List<Category>> categoryList() {
        return ApiResponse.ok(Category.toList());
    }

}
