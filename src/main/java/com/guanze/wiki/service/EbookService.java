package com.guanze.wiki.service;

import com.guanze.wiki.domain.Ebook;
import com.guanze.wiki.domain.EbookExample;
import com.guanze.wiki.mapper.EbookMapper;
import com.guanze.wiki.req.EbookReq;
import com.guanze.wiki.resp.EbookResp;
import com.guanze.wiki.utils.CopyUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class EbookService {

    @Resource
    private EbookMapper ebookMapper;

    public List<EbookResp> list(EbookReq req) {
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        criteria.andNameLike("%" + req.getName() + "%");
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        List<EbookResp> respList = new ArrayList<>();
        for(Ebook ebook: ebookList){
//            EbookResp ebookResp = new EbookResp();
//            BeanUtils.copyProperties(ebook, ebookResp);
            EbookResp ebookResp = CopyUtil.copy(ebook, EbookResp.class);;
            respList.add(ebookResp);
        }
        return respList;
    }
}
