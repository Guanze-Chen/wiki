package com.guanze.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.guanze.wiki.domain.Content;
import com.guanze.wiki.domain.Doc;
import com.guanze.wiki.domain.DocExample;
import com.guanze.wiki.exception.BusinessException;
import com.guanze.wiki.exception.BusinessExceptionCode;
import com.guanze.wiki.mapper.ContentMapper;
import com.guanze.wiki.mapper.DocMapper;
import com.guanze.wiki.mapper.MyDocMapper;
import com.guanze.wiki.req.DocQueryReq;
import com.guanze.wiki.req.DocSaveReq;
import com.guanze.wiki.resp.DocQueryResp;
import com.guanze.wiki.resp.PageResp;
import com.guanze.wiki.utils.CopyUtil;
import com.guanze.wiki.utils.RedisUtil;
import com.guanze.wiki.utils.RequestContext;
import com.guanze.wiki.utils.SnowFlake;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocService {

    @Resource
    private DocMapper docMapper;

    @Resource
    public RedisUtil redisUtil;

    @Resource
    private MyDocMapper myDocMapper;
    @Resource
    private ContentMapper contentMapper;

    @Resource
    private SnowFlake snowFlake;

    @Resource WebSocketService webSocketService;


    public PageResp<DocQueryResp> list(DocQueryReq req) {
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        DocExample.Criteria criteria = docExample.createCriteria();
//        if (!ObjectUtils.isEmpty(req.getName())) {
//            criteria.andNameLike("%" + req.getName() + "%");
//        }
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Doc> docList = docMapper.selectByExample(docExample);

        PageInfo<Doc> pageInfo = new PageInfo<>(docList);

        List<DocQueryResp> respList = new ArrayList<>();
        for(Doc doc: docList){
//            DocResp docResp = new DocResp();
//            BeanUtils.copyProperties(doc, docResp);
            DocQueryResp docQueryResp = CopyUtil.copy(doc, DocQueryResp.class);;
            respList.add(docQueryResp);
        }

        PageResp<DocQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;
    }

    public List<DocQueryResp> all(Long ebookId) {
        DocExample docExample = new DocExample();
        docExample.createCriteria().andEbookIdEqualTo(ebookId);
        docExample.setOrderByClause("sort asc");
        List<Doc> docList = docMapper.selectByExample(docExample);


        List<DocQueryResp> respList = new ArrayList<>();
        for(Doc doc: docList){
//            DocResp docResp = new DocResp();
//            BeanUtils.copyProperties(doc, docResp);
            DocQueryResp docQueryResp = CopyUtil.copy(doc, DocQueryResp.class);;
            respList.add(docQueryResp);
        }

        return respList;
    }

    public void save(DocSaveReq req) {
        Doc doc =CopyUtil.copy(req, Doc.class);
        Content content = CopyUtil.copy(req, Content.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            //
            doc.setId(snowFlake.nextId());
            doc.setViewCount(0);
            doc.setVoteCount(0);
            docMapper.insert(doc);

            content.setId(doc.getId());
            contentMapper.insert(content);
        } else {
            // 更新
            docMapper.updateByPrimaryKey(doc);
            int count = contentMapper.updateByPrimaryKeyWithBLOBs(content);
            // blob 表示富文本字段
            if (count == 0) {
                contentMapper.insert(content);
            }

        }

    }


    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
    }

    public void delete(List<Long> ids) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }

    public String findContent(Long id) {

        Content content = contentMapper.selectByPrimaryKey(id);
        // # 文档阅读数+1
        myDocMapper.autoIncreViewCount(id);
        if (ObjectUtils.isEmpty(content)) {
            return "";
        } else {
            return content.getContent();
        }
    }

    //点赞
    public void vote(Long id) {
        String ip = RequestContext.getRemoteAddr();
        if (redisUtil.validateRepeat("DOC_VOTE" + id + "_" + ip, 3600*24*7)) {
            myDocMapper.autoIncreVoteCount(id);
        } else {
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
        }

        // 推送消息
        Doc docDb = docMapper.selectByPrimaryKey(id);
        webSocketService.sendInfo("【"+docDb.getName() + "】被点赞!");
    }

    public void updateEbookInfo() {
        myDocMapper.updateEbookInfo();
    }
}
