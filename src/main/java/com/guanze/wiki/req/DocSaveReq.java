package com.guanze.wiki.req;

import javax.validation.constraints.NotNull;

public class DocSaveReq {
    private Long id;

    @NotNull(message = "电子书id不能为空")
    private Long ebookId;

    @NotNull(message = "一级分类不能为空")
    private Long parent;

    @NotNull(message = "名称不能为空")
    private String name;

    @NotNull(message = "序号不能为空")
    private Integer sort;

    @NotNull(message = "内容不能为空")
    private String content;



    private Integer viewCount;

    @Override
    public String toString() {
        return "DocSaveReq{" +
                "id=" + id +
                ", ebookId=" + ebookId +
                ", parent=" + parent +
                ", name='" + name + '\'' +
                ", sort=" + sort +
                ", content='" + content + '\'' +
                ", viewCount=" + viewCount +
                ", voteCount=" + voteCount +
                '}';
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private Integer voteCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEbookId() {
        return ebookId;
    }

    public void setEbookId(Long ebookId) {
        this.ebookId = ebookId;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

}