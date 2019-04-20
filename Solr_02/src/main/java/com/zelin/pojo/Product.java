package com.zelin.pojo;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description: product实体类
 * @Date: Create in 2019/4/19 15:25
 */
public class Product implements Serializable {
    @Field("id")
    @Id  //如果数据表中的主键不叫id，则此处要添加此注解
    private int pid;
    @Field("product_name")
    private String name;
    @Field("product_catalog_name")
    private String catelog_name;
    @Field("product_price")
    private float price;
    private int number;
    @Field("product_description")
    private String description;
    @Field("product_picture")
    private String picture;
    private String release_time;

    public Product() {
    }

    public Product(String name, String catelog_name, float price, int number, String description, String picture, String release_time) {
        this.name = name;
        this.catelog_name = catelog_name;
        this.price = price;
        this.number = number;
        this.description = description;
        this.picture = picture;
        this.release_time = release_time;
    }

    public Product(int pid, String name, String catelog_name, float price, int number, String description, String picture, String release_time) {
        this.pid = pid;
        this.name = name;
        this.catelog_name = catelog_name;
        this.price = price;
        this.number = number;
        this.description = description;
        this.picture = picture;
        this.release_time = release_time;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatelog_name() {
        return catelog_name;
    }

    public void setCatelog_name(String catelog_name) {
        this.catelog_name = catelog_name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getRelease_time() {
        return release_time;
    }

    public void setRelease_time(String release_time) {
        this.release_time = release_time;
    }

    @Override
    public String toString() {
        return "Product{" +
                "pid='" + pid + '\'' +
                ", name='" + name + '\'' +
                ", catelog_name='" + catelog_name + '\'' +
                ", price=" + price +
                ", number=" + number +
                ", description='" + description + '\'' +
                ", picture='" + picture + '\'' +
                ", release_time='" + release_time + '\'' +
                '}';
    }
}
