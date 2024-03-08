package com.ari.application.controller;

import com.ari.application.common.model.User;
import com.ari.waiter.client.WaiterClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class Controller {

    @Resource
    private WaiterClient waiterClient;

    @PostMapping("/do")
    public String doController(@RequestBody User user) {
        String res = (String) waiterClient.post("/api/encrypt/md5", user);
        user.setUsername(res);
        return user.toString();
    }
}
