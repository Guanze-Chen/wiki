package com.guanze.wiki.controller;

import com.alibaba.fastjson.JSONObject;
import com.guanze.wiki.req.UserLoginReq;
import com.guanze.wiki.req.UserQueryReq;
import com.guanze.wiki.req.UserResetPwdReq;
import com.guanze.wiki.req.UserSaveReq;
import com.guanze.wiki.resp.CommonResp;
import com.guanze.wiki.resp.PageResp;
import com.guanze.wiki.resp.UserLoginResp;
import com.guanze.wiki.resp.UserQueryResp;
import com.guanze.wiki.service.UserService;
import com.guanze.wiki.utils.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private SnowFlake snowFlake;

    @GetMapping("/list")
    public CommonResp list(@Valid UserQueryReq req) {
        CommonResp<PageResp<UserQueryResp>> resp= new CommonResp<>();
        PageResp<UserQueryResp> list =  userService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody UserSaveReq req) {
        CommonResp resp= new CommonResp<>();
        // 加密存储密码
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        userService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp= new CommonResp<>();
        userService.delete(id);
        return resp;
    }

    // 重置密码
    @PostMapping("/resetpwd")
    public CommonResp resetPwd(@Valid @RequestBody UserResetPwdReq req) {
        CommonResp resp= new CommonResp<>();
        // 加密存储密码
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        userService.resetPwd(req);
        return resp;
    }

    @PostMapping("/login")
    public CommonResp login(@Valid @RequestBody UserLoginReq req) {
        // 加密存储密码
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp<UserLoginResp> resp= new CommonResp<>();
        UserLoginResp userLoginResp =  userService.login(req);
        long token = snowFlake.nextId();
        userLoginResp.setToken(token);
        redisTemplate.opsForValue().set(token, JSONObject.toJSONString(userLoginResp), 3600*24, TimeUnit.SECONDS);
        resp.setContent(userLoginResp);
        return resp;
    }

    @GetMapping("/logout/{token}")
    public CommonResp logout(@PathVariable Long token) {
        CommonResp resp= new CommonResp<>();
        redisTemplate.delete(token);
        LOG.info("从redis中删除token: {}", token);
        return resp;
    }

}
