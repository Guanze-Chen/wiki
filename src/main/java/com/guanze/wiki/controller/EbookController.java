package com.guanze.wiki.controller;

import com.guanze.wiki.req.EbookQueryReq;
import com.guanze.wiki.req.EbookSaveReq;
import com.guanze.wiki.resp.CommonResp;
import com.guanze.wiki.resp.EbookQueryResp;
import com.guanze.wiki.resp.PageResp;
import com.guanze.wiki.service.EbookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Resource
    private EbookService ebookService;

    @GetMapping("/list")
    public CommonResp list(EbookQueryReq req) {
        CommonResp<PageResp<EbookQueryResp>> resp= new CommonResp<>();
        PageResp<EbookQueryResp> list =  ebookService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@RequestBody EbookSaveReq req) {
        CommonResp resp= new CommonResp<>();
        ebookService.save(req);
        return resp;
    }
}
