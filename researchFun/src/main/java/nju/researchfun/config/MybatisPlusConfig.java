package nju.researchfun.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("nju.researchfun.mapper")
public class MybatisPlusConfig {

    /**
     * 配置分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }

    /**
     * 分析器
     */
    @Bean
    public PerformanceInterceptor performanceInterceptor(){
        return new PerformanceInterceptor().setMaxTime(1000).setFormat(true);
    }

    /**
     * 自定义sql
     */
    /*@Bean
    public MySqlInjector mySqlInjector(){
        return new MySqlInjector();
    }*/
}
