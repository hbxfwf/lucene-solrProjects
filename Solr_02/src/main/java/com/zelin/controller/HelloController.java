package com.zelin.controller;

import com.zelin.pojo.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.xml.ws.RequestWrapper;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/4/20 09:14
 */
@Controller
public class HelloController {
    @RequestMapping("/hello")
    public String hello(Model model){
        //1.放一个普通的属性
        model.addAttribute("name","张三丰");
        //2.定义一个集合用于存放商品信息
        List<Product> productList = new ArrayList<>();
        Product p1 = new Product(1001,"联想电脑","家电类",12000,100,"aaa","a.jpg","2000-10-1");
        Product p2 = new Product(1002,"洽洽瓜子","食品类",5,200,"bbb","b.jpg","2001-10-1");
        Product p3 = new Product(1003,"iphone 7 plus","手机类",4000,200,"ccc","c.jpg","2200-10-1");
        productList.add(p1);
        productList.add(p2);
        productList.add(p3);
        //3.放一个集合到model中
        model.addAttribute("productList",productList);
        return "index";
    }
}
