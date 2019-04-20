package com.zelin.controller;

import com.zelin.pojo.Product;
import com.zelin.pojo.ProductVo;
import com.zelin.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/4/20 10:46
 */
@Controller
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    /**
     * 查询所有的商品信息
     * @param model
     * @return
     */
    @RequestMapping("/list")
    public String findAll(Model model){
        List<Product> productList = productService.findAll();
        model.addAttribute("products",productList);
        return "product_list";
    }

    /**
     * 根据查询条件进行查询
     * @param productVo
     * @return
     */
    @RequestMapping("/search")
    public String search(ProductVo productVo,Model model){
        System.out.println(productVo);
        //1、将原始的数据重新放回到model中，便于在页面下次查询使用
        model.addAttribute("vo",productVo);
        //2、从索引库中查询出商品列表
        List<Product> products = productService.search(productVo);
        //3、将商品列表放到model中
        model.addAttribute("products",products);
        return "product_list";
    }
}
