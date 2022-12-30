package com.guanze.wiki.controller;

import com.guanze.wiki.req.DocQueryReq;
import com.guanze.wiki.req.DocSaveReq;
import com.guanze.wiki.resp.CommonResp;
import com.guanze.wiki.resp.DocQueryResp;
import com.guanze.wiki.resp.PageResp;
import com.guanze.wiki.service.DocService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/doc")
public class DocController {

    @Resource
    private DocService docService;

    @GetMapping("/list")
    public CommonResp list(@Valid DocQueryReq req) {
        CommonResp<PageResp<DocQueryResp>> resp= new CommonResp<>();
        PageResp<DocQueryResp> list =  docService.list(req);
        resp.setContent(list);
        return resp;
    }
    @GetMapping("/all/{ebookId}")
    public CommonResp all(@PathVariable Long ebookId) {
        CommonResp<List<DocQueryResp>> resp= new CommonResp<>();
        List<DocQueryResp> list =  docService.all(ebookId);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody DocSaveReq req) {
        CommonResp resp= new CommonResp<>();
        docService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{idStr}")
    public CommonResp delete(@PathVariable String idStr) {
        CommonResp resp= new CommonResp<>();
        String[] idArray = idStr.split(",");
        List<Long> list = Arrays.stream(idArray).map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
        docService.delete(list);
        return resp;
    }

    @GetMapping("/mediumtext/{id}")
    public CommonResp mediumtext(@PathVariable Long id) {
        CommonResp<String> resp= new CommonResp<>();
        String content = docService.findContent(id);
        resp.setContent(content);
        return resp;
    }

    @GetMapping("/vote/{id}")
    public CommonResp vote(@PathVariable Long id) {
        CommonResp resp= new CommonResp();
        docService.vote(id);
        return resp;
    }

}
