package com.rafetcelik.gatewayserver.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallBackController {
    @RequestMapping("/contactSupport")
    public String contactSupport() {
        return "An error occurred. Please try after some time or contact support team!!!";
    }
}
