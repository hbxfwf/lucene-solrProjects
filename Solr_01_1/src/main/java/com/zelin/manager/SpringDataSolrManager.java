package com.zelin.manager;

import com.zelin.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/4/19 14:56
 */
@Repository
public class SpringDataSolrManager {
    @Autowired
    private SolrTemplate solrTemplate;
    //显示索引库
    public void queryIndex() throws Exception{
        Query query = new SimpleQuery("*:*");
        query.setOffset(0);
        query.setRows(5);
        ScoredPage<Product> products = solrTemplate.queryForPage(query, Product.class);
        for (Product product : products) {
            System.out.println(product);
        }
    }
    //高亮查询
    public List<Product> queryByHighlighting() {
        List<Product> productList = new ArrayList<>();
        //1.创建高亮查询对象
        HighlightQuery highlightQuery = new SimpleHighlightQuery();

        //3.设置查询条件
        HighlightOptions highlightOptions = new HighlightOptions();
        //4.为高亮选项对象添加内容
        highlightOptions.addField("product_name");
        highlightOptions.setSimplePrefix("<span style='color:red'>");
        highlightOptions.setSimplePostfix("</span>");
        highlightQuery.setHighlightOptions(highlightOptions);
        //2.将过滤查询与高亮查询关联
        //这里使用producte_keywords代表从product_name与producte_description两个域中查询“牙膏”
        //producte_keywords因为这个是复制域，即从product_name与producte_description两个域中复制的内容进行查询
        Criteria criteria = new Criteria("product_keywords").is("牙膏");
        highlightQuery.addCriteria(criteria);   //高亮查询必须加此条件查询，否则报错

        //3.添加过滤查询
        FilterQuery filterQuery = new SimpleFilterQuery();
        filterQuery.addCriteria(new Criteria("product_catalog_name").is("时尚卫浴"));
        highlightQuery.addFilterQuery(filterQuery);

        //4.进行分页查询参数设置
        highlightQuery.setOffset(0);
        highlightQuery.setRows(100);
        //5.进行高亮查询
        HighlightPage<Product> products = solrTemplate.queryForHighlightPage(highlightQuery, Product.class);
        List<HighlightEntry<Product>> highlighted = products.getHighlighted();
        for (HighlightEntry<Product> highlightEntry : highlighted) {
            Product product = highlightEntry.getEntity();
            //下面代表某条高亮条目
            List<HighlightEntry.Highlight> highlights = highlightEntry.getHighlights();
            if(highlights.size()>0){
                //代表如果有多个高亮字段的话，我们这里因为只有一个高亮字段，只取第一个高亮字段，这里是product=_name
                HighlightEntry.Highlight highlight = highlights.get(0);
                List<String> snipplets = highlight.getSnipplets();
                if(snipplets.size()>0){
                    //这里代表此字段如果是多域的话，这里是单值字段，所以只取第一个值
                    String name = snipplets.get(0);
                    product.setName(name);
                }
            }
            productList.add(product);
        }
        //7.提交查询
        solrTemplate.commit();
        return productList;
    }
}
