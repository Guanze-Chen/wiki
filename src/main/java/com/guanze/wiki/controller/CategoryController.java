package com.guanze.wiki.controller;

import com.guanze.wiki.req.CategoryQueryReq;
import com.guanze.wiki.req.CategorySaveReq;
import com.guanze.wiki.resp.CategoryQueryResp;
import com.guanze.wiki.resp.CommonResp;
import com.guanze.wiki.resp.PageResp;
import com.guanze.wiki.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @GetMapping("/list")
    public CommonResp list(@Valid CategoryQueryReq req) {
        CommonResp<PageResp<CategoryQueryResp>> resp= new CommonResp<>();
        PageResp<CategoryQueryResp> list =  categoryService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody CategorySaveReq req) {
        CommonResp resp= new CommonResp<>();
        categoryService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp= new CommonResp<>();
        categoryService.delete(id);
        return resp;
    }
}