package com.guanze.wiki.mapper;

import com.guanze.wiki.resp.StatisticResp;

import java.util.List;

public interface EbookSnapshotMapperCust {

    public void genSnapshot();
    List<StatisticResp> getStatistic();
}