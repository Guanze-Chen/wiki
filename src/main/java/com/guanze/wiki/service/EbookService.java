package com.guanze.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.guanze.wiki.domain.Ebook;
import com.guanze.wiki.domain.EbookExample;
import com.guanze.wiki.mapper.EbookMapper;
import com.guanze.wiki.req.EbookQueryReq;
import com.guanze.wiki.req.EbookSaveReq;
import com.guanze.wiki.resp.EbookQueryResp;
import com.guanze.wiki.resp.PageResp;
import com.guanze.wiki.utils.CopyUtil;
import com.guanze.wiki.utils.SnowFlake;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class EbookService {

    @Resource
    private EbookMapper ebookMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<EbookQueryResp> list(EbookQueryReq req) {
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        PageInfo<Ebook> pageInfo = new PageInfo<>(ebookList);

        List<EbookQueryResp> respList = new ArrayList<>();
        for(Ebook ebook: ebookList){
//            EbookResp ebookResp = new EbookResp();
//            BeanUtils.copyProperties(ebook, ebookResp);
            EbookQueryResp ebookQueryResp = CopyUtil.copy(ebook, EbookQueryResp.class);;
            respList.add(ebookQueryResp);
        }

        PageResp<EbookQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;
    }

    public void save(EbookSaveReq req) {
        Ebook ebook =CopyUtil.copy(req, Ebook.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            //
            ebook.setId(snowFlake.nextId());
            ebookMapper.insert(ebook);
        } else {
            // 更新
            ebookMapper.updateByPrimaryKey(ebook);
        }

    }


    public void delete(Long id) {
        ebookMapper.deleteByPrimaryKey(id);
    }
}
