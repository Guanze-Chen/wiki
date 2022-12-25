package com.guanze.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.guanze.wiki.domain.Ebook;
import com.guanze.wiki.domain.EbookExample;
import com.guanze.wiki.mapper.EbookMapper;
import com.guanze.wiki.req.EbookReq;
import com.guanze.wiki.resp.EbookResp;
import com.guanze.wiki.resp.PageResp;
import com.guanze.wiki.utils.CopyUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class EbookService {

    @Resource
    private EbookMapper ebookMapper;

    public PageResp<EbookResp> list(EbookReq req) {
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        PageInfo<Ebook> pageInfo = new PageInfo<>(ebookList);

        List<EbookResp> respList = new ArrayList<>();
        for(Ebook ebook: ebookList){
//            EbookResp ebookResp = new EbookResp();
//            BeanUtils.copyProperties(ebook, ebookResp);
            EbookResp ebookResp = CopyUtil.copy(ebook, EbookResp.class);;
            respList.add(ebookResp);
        }

        PageResp<EbookResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;
    }
}
