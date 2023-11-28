package com.wanted.bobo.user.controller;

import com.wanted.bobo.common.response.ApiResponse;
import com.wanted.bobo.user.dto.JoinRequest;
import com.wanted.bobo.user.dto.JoinResponse;
import com.wanted.bobo.user.service.JoinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원가입")
@RestController
@RequestMapping("/join")
public class JoinController {

    private final JoinService joinService;

    public JoinController(JoinService joinService) {
        this.joinService = joinService;
    }

    @PostMapping
    @Operation(summary = "회원가입")
    public ApiResponse<JoinResponse> join(@Valid @RequestBody JoinRequest request) {
        return ApiResponse.created(joinService.join(request));
    }

}
