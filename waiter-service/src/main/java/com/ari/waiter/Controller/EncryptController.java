package com.ari.waiter.Controller;

import cn.hutool.crypto.digest.DigestUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public Object md5Encrypt(@RequestBody Object object) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        Gson gson = gsonBuilder.create();
        String content = gson.toJson(object);
        return DigestUtil.md5Hex(content);
    }
}
