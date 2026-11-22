package com.cardsystem.smartdoor.controller;

import com.cardsystem.smartdoor.dto.Dtos.AccessLogDto;
import com.cardsystem.smartdoor.service.AccessLogService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/access-logs")
public class AccessLogController {

    private final AccessLogService accessLogService;

    public AccessLogController(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
    }

    @GetMapping
    public Page<AccessLogDto> recent(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "50") int size) {
        return accessLogService.recent(page, Math.min(size, 200));
    }
}
