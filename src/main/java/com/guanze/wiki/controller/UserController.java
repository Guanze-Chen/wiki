package com.guanze.wiki.controller;

import com.guanze.wiki.req.UserQueryReq;
import com.guanze.wiki.req.UserSaveReq;
import com.guanze.wiki.resp.CommonResp;
import com.guanze.wiki.resp.PageResp;
import com.guanze.wiki.resp.UserQueryResp;
import com.guanze.wiki.service.UserService;
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
        userService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp= new CommonResp<>();
        userService.delete(id);
        return resp;
    }
}
