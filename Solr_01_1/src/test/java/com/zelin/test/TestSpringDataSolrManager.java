package com.zelin.test;

import com.zelin.Product;
import com.zelin.manager.SpringDataSolrManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/4/19 15:37
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext.xml")
public class TestSpringDataSolrManager {
    @Autowired
    private SpringDataSolrManager solrManager;
    @Test
    public void test01() throws Exception {
        solrManager.queryIndex();
    }
    @Test
    public void test02() throws  Exception{
        for (Product product : solrManager.queryByHighlighting()) {
            System.out.println(product);
        }
    }
}
