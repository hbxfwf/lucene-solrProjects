package com.zelin.service;

import com.zelin.pojo.Product;
import com.zelin.pojo.ProductVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/4/20 10:42
 */
@Service
public class ProductService {
    @Autowired
    private SolrTemplate solrTemplate;
    //查询索引库
    public List<Product> findAll(){
        List<Product> productList = new ArrayList<>();
        //1.定义查询对象
        SimpleQuery query = new SimpleQuery("*:*");
        query.setOffset(0L);
        query.setRows(16);
        //2.定义查询分页对象
        ScoredPage<Product> products = solrTemplate.query("",query,Product.class);

        for (Product product : products) {
            productList.add(product);
        }
        return productList;
    }

    /**
     * 根据传递过来的商品信息，查询出商品列表
     * @param productVo
     * @return
     */
    public List<Product> search(ProductVo productVo) {
        List<Product> products = new ArrayList<>();
        /*---------------------- 第一部分：设置高亮查询参数及条件 ----------------------*/
        //1.定义高亮查询对象
        SimpleHighlightQuery highlightQuery = new SimpleHighlightQuery();
        //2.创建查询条件(在solrHome-->collection1--->schema.xml文件中有复制域的定义)
        Criteria criteria = new Criteria("product_keywords");
        //3.根据查询参数是否有内容为当前查询条件添加内容
        //例如：在文本框(name="queryString")输入"手机"，相当于在product_name,product_description这两个域中查询内容。
        if(StringUtils.isNotEmpty(productVo.getQueryString())){
            criteria.is(productVo.getQueryString());
        }
        //4.将查询条件放到高亮查询对象中
        highlightQuery.addCriteria(criteria);
        //5.为高亮查询设置参数
        //5.1)设置高亮选项
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("product_name");      //添加高亮查询的字段
        highlightOptions.setSimplePrefix("<span style='color:red'>");//设置高亮查询的前缀
        highlightOptions.setSimplePostfix("</span>");                //设置高亮查询的后缀
        //5.2)为高亮查询对象设置查询参数选项
        highlightQuery.setHighlightOptions(highlightOptions);

        /*---------------------- 第二部分：创建过滤查询 ----------------------*/
        //1.分类过滤查询
        if(StringUtils.isNotEmpty(productVo.getCatalogName())){
            //1.1)创建分类查询的条件
            Criteria catalogNameCriteria = new Criteria("product_catalog_name");
            //1.2)为分类查询赋值
            catalogNameCriteria.is(productVo.getCatalogName());
            //1.3)为分类过滤查询设置查询条件
            SimpleFilterQuery catalogFilterQuery = new SimpleFilterQuery(catalogNameCriteria);
            //1.4)将分类过滤查询与高亮查询绑定
            highlightQuery.addFilterQuery(catalogFilterQuery);
        }

        //2.价格区间过滤查询
        if(StringUtils.isNotEmpty(productVo.getPrice())){
            //2.1)定义过滤查询的对象
            SimpleFilterQuery priceQuery = new SimpleFilterQuery();
            //2.2)创建价格区间查询的条件（开始区间）
            Criteria priceCriteriaStart = new Criteria("product_price");
            //2.2)为价格区间查询赋值
            //2.2.1)截取字符串
            String[] split = productVo.getPrice().split("-");
            //2.2.2)设置查询条件
            priceCriteriaStart.greaterThanEqual(split[0]);
            //2.2.3) 将开始区间添加到priceQuery中
            priceQuery.addCriteria(priceCriteriaStart);
            //2.2.3)判断截取后的第二个字符串是否是*
            if(!split[1].equals("*")){
                Criteria priceCriteriaEnd = new Criteria("product_price");
                priceCriteriaEnd.lessThanEqual(Float.parseFloat(split[1]+""));
                //2.3)将结束区间添加到priceQuery中
                priceQuery.addCriteria(priceCriteriaEnd);
            }
            //1.4)将价格区间过滤查询与高亮查询绑定
            highlightQuery.addFilterQuery(priceQuery);
        }

        //3.排序过滤查询
        //3.1)得到排序字段
        if(StringUtils.isNotEmpty(productVo.getSort())){
            //3.2)根据得到的值来判断是升序还是降序
            if(productVo.getSort().equals("1")){        //降序
                highlightQuery.addSort(new Sort(Sort.Direction.DESC,"product_price"));
            }else{                                      //升序
                highlightQuery.addSort(new Sort(Sort.Direction.ASC,"product_price"));
            }
        }
        //4.分页查询
        highlightQuery.setOffset(0L);
        highlightQuery.setRows(16);

        /*---------------------- 第三部分：开始查询 ----------------------*/
        //1.得到高亮查询页
        HighlightPage<Product> highlightPage = solrTemplate.queryForHighlightPage("", highlightQuery, Product.class);
        //2.得到高亮查询的数据
        List<HighlightEntry<Product>> highlighted = highlightPage.getHighlighted();
        //3.遍历高亮查询的数据(其中的highlightEntry:代表高亮查询的条目)
        for (HighlightEntry<Product> highlightEntry : highlighted) {
            //3.1) 得到高亮查询所关联的实体类
            Product product = highlightEntry.getEntity();
            //3.2) 得到某一条高亮查询的数据
            List<HighlightEntry.Highlight> highlights = highlightEntry.getHighlights();
            //3.3) 判断是否有值，如果有值，就取出并重新加product的name属性赋值
            if(highlights != null && highlights.size() > 0){
                //3.4) 得到某一条中的某个高亮字段的数据
                HighlightEntry.Highlight highlight = highlights.get(0);
                //3.5) 因为考虑到某个字段有多值的情况，所以这里使用集合
                List<String> snipplets = highlight.getSnipplets();
                //3.6) 再一次判断得到值
                if(snipplets != null && snipplets.size() > 0 ){
                    //3.7) 得到经过高亮处理了的name字段的值
                    String name = snipplets.get(0);
                    product.setName(name);
                    //3.8) 将处理完后的product放到products集合中
                    products.add(product);
                }
            }
        }
        //4.提交查询
        solrTemplate.commit("");
        return products;
    }
}
