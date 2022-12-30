package com.guanze.wiki.mapper;

import org.apache.ibatis.annotations.Param;

public interface MyDocMapper {

    public void autoIncreViewCount(@Param("id") Long id);

    public void autoIncreVoteCount(@Param("id") Long id);

    public void updateEbookInfo();
}
