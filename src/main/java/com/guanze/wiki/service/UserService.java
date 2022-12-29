package com.guanze.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.guanze.wiki.domain.User;
import com.guanze.wiki.domain.UserExample;
import com.guanze.wiki.exception.BusinessException;
import com.guanze.wiki.exception.BusinessExceptionCode;
import com.guanze.wiki.mapper.UserMapper;
import com.guanze.wiki.req.UserQueryReq;
import com.guanze.wiki.req.UserSaveReq;
import com.guanze.wiki.resp.UserQueryResp;
import com.guanze.wiki.resp.PageResp;
import com.guanze.wiki.utils.CopyUtil;
import com.guanze.wiki.utils.SnowFlake;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<UserQueryResp> list(UserQueryReq req) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getLoginName())) {
            criteria.andNameLike("%" + req.getLoginName() + "%");
        }

        PageHelper.startPage(req.getPage(), req.getSize());
        List<User> userList = userMapper.selectByExample(userExample);

        PageInfo<User> pageInfo = new PageInfo<>(userList);

        List<UserQueryResp> respList = new ArrayList<>();
        for(User user: userList){
//            UserResp userResp = new UserResp();
//            BeanUtils.copyProperties(user, userResp);
            UserQueryResp userQueryResp = CopyUtil.copy(user, UserQueryResp.class);;
            respList.add(userQueryResp);
        }

        PageResp<UserQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;
    }

    public void save(UserSaveReq req) {
        User user =CopyUtil.copy(req, User.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            //
            if (ObjectUtils.isEmpty(selectByLoginName(req.getLoginName()))) {
                user.setId(snowFlake.nextId());
                userMapper.insert(user);
            } else {
                // 用户名已存在
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            }

        } else {
            // 更新 Loginname不能更改
            user.setLoginName(null);
            userMapper.updateByPrimaryKeySelective(user);
        }

    }


    public void delete(Long id) {
        userMapper.deleteByPrimaryKey(id);
    }

    public User selectByLoginName(String loginName) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        if (!ObjectUtils.isEmpty(loginName)) {
            criteria.andNameLike("%" + loginName + "%");
        }

        List<User> userList = userMapper.selectByExample(userExample);

        if (CollectionUtils.isEmpty(userList)) {
            return null;
        } else {
            return userList.get(0);
        }
    }
}
