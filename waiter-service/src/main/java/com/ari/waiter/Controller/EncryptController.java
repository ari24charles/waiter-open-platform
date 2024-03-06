package com.ari.waiter.Controller;

import cn.hutool.crypto.digest.DigestUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 加密接口
 *
 * @author ari24charles
 */
@RestController
@RequestMapping("/encrypt")
public class EncryptController {

    @PostMapping("/md5")
    public String md5Digest(String content) {
        return DigestUtil.md5Hex(content);
    }
}
