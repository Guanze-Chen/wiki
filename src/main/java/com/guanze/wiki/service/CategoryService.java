package com.guanze.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.guanze.wiki.domain.Category;
import com.guanze.wiki.domain.CategoryExample;
import com.guanze.wiki.mapper.CategoryMapper;
import com.guanze.wiki.req.CategoryQueryReq;
import com.guanze.wiki.req.CategorySaveReq;
import com.guanze.wiki.resp.CategoryQueryResp;
import com.guanze.wiki.resp.PageResp;
import com.guanze.wiki.utils.CopyUtil;
import com.guanze.wiki.utils.SnowFlake;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<CategoryQueryResp> list(CategoryQueryReq req) {
        CategoryExample categoryExample = new CategoryExample();
        CategoryExample.Criteria criteria = categoryExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);

        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);

        List<CategoryQueryResp> respList = new ArrayList<>();
        for(Category category: categoryList){
//            CategoryResp categoryResp = new CategoryResp();
//            BeanUtils.copyProperties(category, categoryResp);
            CategoryQueryResp categoryQueryResp = CopyUtil.copy(category, CategoryQueryResp.class);;
            respList.add(categoryQueryResp);
        }

        PageResp<CategoryQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;
    }

    public void save(CategorySaveReq req) {
        Category category =CopyUtil.copy(req, Category.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            //
            category.setId(snowFlake.nextId());
            categoryMapper.insert(category);
        } else {
            // 更新
            categoryMapper.updateByPrimaryKey(category);
        }

    }


    public void delete(Long id) {
        categoryMapper.deleteByPrimaryKey(id);
    }
}
