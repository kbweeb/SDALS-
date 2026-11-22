package com.cardsystem.smartdoor.controller;

import com.cardsystem.smartdoor.dto.Dtos.DeviceStatusDto;
import com.cardsystem.smartdoor.service.DeviceService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/device")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping("/status")
    public DeviceStatusDto status() {
        return deviceService.getStatus();
    }

    @PostMapping("/unlock")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void unlock() {
        deviceService.remoteUnlock();
    }

    @PostMapping("/lock")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void lock() {
        deviceService.remoteLock();
    }
}
