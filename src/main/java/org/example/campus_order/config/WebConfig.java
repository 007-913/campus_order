package org.example.campus_order.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.path:/tmp/upload}")
    private String uploadPath;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login/**")
                .excludePathPatterns("/auth/login")
                .excludePathPatterns("/notice/list")
                .excludePathPatterns("/notice/detail/**")
                .excludePathPatterns("/notice/shop/list")
                .excludePathPatterns("/shop/open/**")
                .excludePathPatterns("/food/open/**")
                .excludePathPatterns("/comment/open/**")
                .excludePathPatterns("/upload/**")
                .excludePathPatterns("/pay/qrcode")
                .excludePathPatterns("/pay/status")
                .excludePathPatterns("/pay/scan")
                .excludePathPatterns("/pay/scan/success")
                .excludePathPatterns("/auth/register")
                .excludePathPatterns("/auth/captcha");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 拼接末尾斜杠，让Spring识别为文件夹目录
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}
