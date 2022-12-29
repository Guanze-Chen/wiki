package com.guanze.wiki.controller;

import com.guanze.wiki.req.UserQueryReq;
import com.guanze.wiki.req.UserResetPwdReq;
import com.guanze.wiki.req.UserSaveReq;
import com.guanze.wiki.resp.CommonResp;
import com.guanze.wiki.resp.PageResp;
import com.guanze.wiki.resp.UserQueryResp;
import com.guanze.wiki.service.UserService;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

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

}
