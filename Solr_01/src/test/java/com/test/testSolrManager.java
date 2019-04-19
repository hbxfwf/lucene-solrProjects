package com.test;

import com.zelin.manager.SolrManager;
import org.junit.Before;
import org.junit.Test;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/4/19 11:29
 */
public class testSolrManager {
    private SolrManager solrManager;
    @Before
    public void init(){
        solrManager = new SolrManager();
    }
    //测试添加到索引库
    @Test
    public void testAddIndex() throws Exception{
        solrManager.addIndex();
        System.out.println("添加到索引库成功！");
    }
    //测试删除索引库
    @Test
    public void  testDeleteIndex() throws Exception{
        solrManager.deleteIndex();
        System.out.println("删除索引记录成功！");
    }
    //测试简单查询
    @Test
    public void testFindBySimple() throws Exception{
        solrManager.findBySimple();
    }
    //测试复杂查询
    @Test
    public void testFindByFuza() throws Exception{
        solrManager.queryByFuza();
    }
}
