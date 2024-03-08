package com.ari.application.test;

import com.ari.application.common.model.User;
import com.ari.application.controller.Controller;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class ControllerTest {

    @Resource
    private Controller controller;

    @Test
    public void test() {
        User user = new User();
        user.setId(1L);
        user.setUsername("ari");
        user.setPassword("12345678");
        System.out.println("user = " + user);
        String res = controller.doController(user);
        System.out.println("res = " + res);
    }
}
