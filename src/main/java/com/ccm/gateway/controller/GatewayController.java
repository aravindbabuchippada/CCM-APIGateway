package com.ccm.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("ccm")

public class GatewayController {

    @GetMapping("check")
    public ResponseEntity<String> check() {
        return ResponseEntity.ok("CCM-APIGateway is running");
    }
}
