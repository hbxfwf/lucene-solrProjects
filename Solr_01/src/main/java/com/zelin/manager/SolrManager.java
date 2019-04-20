package com.zelin.manager;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/4/19 11:24
 */
public class SolrManager {
    //添加文档到索引库中
    public void addIndex() throws Exception {
        //1.构造solrServer
        SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr");
        //2.构造一个solrDocument对象
        SolrInputDocument document = new SolrInputDocument();
        //3.向文档对象中添加域
        document.addField("id",10000);
        document.addField("product_name","联想电脑");
        document.addField("product_price",12000);
        //4.将文档对象添加到索引库中
        solrServer.add(document);
        //5.提交
        solrServer.commit();
    }
    //删除指定索引
    public void deleteIndex() throws Exception{
        //1.构造solrServer
        SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr");
        //2.删除指定id的索引
        solrServer.deleteById("10000");
        //3.删除根据查询条件得到结果
        solrServer.deleteByQuery("product_name:幸福");
        //4.提交删除
        solrServer.commit();

    }
    //进行简单查询
    public void findBySimple() throws Exception{
        //1.构造solrServer
        SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr");
        //2.定义SolrParams查询参数
        SolrQuery params = new SolrQuery();
        //3.向查询参数中添加查询条件
        params.setQuery("*:*");                             //查询所有
        params.addFilterQuery("product_name:浪漫");         //添加过滤查询
        params.addSort("product_price", SolrQuery.ORDER.desc); //添加排序查询
        //4.根据查询参数进行查询
        QueryResponse queryResponse = solrServer.query(params);
        //5.得到查询的结果集
        SolrDocumentList results = queryResponse.getResults();
        //6.得到查询的总记录数
        long total = results.getNumFound();
        System.out.println("一共查询出" + total + "条记录！");
        //7.打印查询内容
        for (SolrDocument doc : results) {
            System.out.println("-----------------------------------------------------------------");
            showInfo(doc);
            System.out.println("-----------------------------------------------------------------");
        }
        //8.提交查询
        solrServer.commit();
    }
    //显示文档信息
    private void showInfo(SolrDocument doc) {
        String id = (String) doc.get("id");                                             //id
        String product_name = (String) doc.get("product_name");                         //product_name
        float product_price =  Float.parseFloat(doc.get("product_price").toString());   //product_price
        String product_category_name = (String) doc.get("product_catalog_name");        //product_catalog_name
        String product_picture = (String) doc.get("product_picture");                   //product_picture
        System.out.println("id:" + id);
        System.out.println("product_name:" + product_name);
        System.out.println("product_price:" + product_price);
        System.out.println("product_category_name:" + product_category_name);
        System.out.println("product_picture:" + product_picture);

    }

    //进行复杂查询
    public void queryByFuza() throws  Exception{
        //1.构造solrServer
        SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr");
        //2.定义SolrParams查询参数
        SolrQuery params = new SolrQuery();
        //3.向查询参数中添加查询条件
        params.set("q","浪漫");          //添加查询，此时没有指定要查询的字段名，就使用默认查询字段进行查询
        //4.设置默认查询字段
        params.set("df","product_name","product_catalog_name");
        //5.设置查询的字段列表
        params.setFields("id","product_name","product_catalog_name","product_price","product_picture");

        //6.添加过滤查询
        params.addFilterQuery("product_catalog_name:幽默杂货");
        //7.添加排序查询
        params.addSort("product_price", SolrQuery.ORDER.desc);

        //8.设置高亮查询
        params.setHighlight(true);                      //可以进行高亮查询
        params.addHighlightField("product_name");       //添加高亮查询的字段
        params.setHighlightSimplePre("<span style='color:red'>");//设置高亮查询的前缀
        params.setHighlightSimplePost("</span>");       //设置高亮查询的后缀

        //9.设置开始分页
        params.setStart(0);                             //从第0条记录开始查询
        params.setRows(10);                             //每次显示10条

        //10.开始查询
        QueryResponse queryResponse = solrServer.query(params);
        //11.得到查询结果
        SolrDocumentList results = queryResponse.getResults();
        //12.得到高亮查询结果
        Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();

        //13.遍历查询结果
        for (SolrDocument doc : results) {
            System.out.println("---------------------------------------------------------------------");
            //① 打印文档信息
            showInfo(doc);
           //② 根据文档id得到某条高亮数据
            Map<String, List<String>> maps = highlighting.get(doc.get("id").toString());
            //③ 根据存放到高亮中的字段取出此字段的内容
            List<String> list = maps.get("product_name");
            //④ 打印取出的数据
            System.out.println("●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●");
            if(list != null && list.size() > 0){
                System.out.println("商品名称[高亮]:");
                System.out.println(list.get(0));
            }
            System.out.println("●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●");
            System.out.println("---------------------------------------------------------------------");
        }
        //14.提交查询
        solrServer.commit();
    }
}
