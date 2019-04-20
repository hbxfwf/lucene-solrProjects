package com.zelin.pojo;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description: 定义查询对象,这四个参数主要是对前台查询参数的包装
 * @Date: Create in 2019/4/20 10:21
 */
public class ProductVo {
    private String queryString;
    private String catalogName;
    private String price;
    private String sort;

    public ProductVo() {
    }

    public ProductVo(String queryString, String catalogName, String price, String sort) {
        this.queryString = queryString;
        this.catalogName = catalogName;
        this.price = price;
        this.sort = sort;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "ProductVo{" +
                "queryString='" + queryString + '\'' +
                ", catalogName='" + catalogName + '\'' +
                ", price='" + price + '\'' +
                ", sort='" + sort + '\'' +
                '}';
    }
}
