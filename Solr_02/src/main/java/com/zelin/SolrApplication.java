package com.zelin;

import org.apache.solr.client.solrj.SolrClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.solr.core.SolrTemplate;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/4/20 09:14
 */
@SpringBootApplication
public class SolrApplication {
    public static void main(String[] args) {
        SpringApplication.run(SolrApplication.class);
    }
    @Bean
    public SolrTemplate solrTemplate(SolrClient solrClient){
        return new SolrTemplate(solrClient);
    }
}
