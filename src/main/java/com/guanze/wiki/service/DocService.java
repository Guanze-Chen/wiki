package com.guanze.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.guanze.wiki.domain.Content;
import com.guanze.wiki.domain.Doc;
import com.guanze.wiki.domain.DocExample;
import com.guanze.wiki.mapper.ContentMapper;
import com.guanze.wiki.mapper.DocMapper;
import com.guanze.wiki.req.DocQueryReq;
import com.guanze.wiki.req.DocSaveReq;
import com.guanze.wiki.resp.DocQueryResp;
import com.guanze.wiki.resp.PageResp;
import com.guanze.wiki.utils.CopyUtil;
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
    private ContentMapper contentMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<DocQueryResp> list(DocQueryReq req) {
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        DocExample.Criteria criteria = docExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }
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

    public List<DocQueryResp> all() {
        DocExample docExample = new DocExample();
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
        return content.getContent();
    }
}
