package com.zelin.test;

import com.zelin.manager.LuceneManager;
import org.junit.Before;
import org.junit.Test;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description: 测试LuceneManager类
 * @Date: Create in 2019/4/18 10:29
 */
public class TestLuceneManager {
    private LuceneManager luceneManager;
    @Before
    public void init(){
        luceneManager = new LuceneManager();
    }
    //测试添加到索引库
    @Test
    public void testAddIndex() throws Exception {
        luceneManager.addIndex();
        System.out.println("添加到索引库成功！");
    }
    //测试查询索引库方法
    @Test
    public void testQueryIndex() throws Exception{
        luceneManager.queryIndex("java");
    }
    //添加一个文档到索引库
    @Test
    public void testAddIndexOne() throws Exception{
        luceneManager.addIndexOne();
        System.out.println("新添加一个文档成功！");
    }
    //修改索引库
    @Test
    public void testUpdateDocument() throws Exception{
        luceneManager.updateIndex();
        System.out.println("修改索引库成功！");
    }
    //测试删除索引库指定文档
    @Test
    public void testDeleteIndex() throws Exception{
        luceneManager.deleteIndex();
        System.out.println("删除索引库成功！");
    }
    //测试删除索引库所有文档
    @Test
    public void testDeleteAll() throws Exception{
        luceneManager.deleteAll();
        System.out.println("整个索引库删除成功！");
    }
}
