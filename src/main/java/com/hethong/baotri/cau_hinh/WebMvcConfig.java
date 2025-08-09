package com.hethong.baotri.cau_hinh;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Định tuyến các trang khác
        registry.addViewController("/thiet-bi").setViewName("thiet-bi/danh-sach-thiet-bi");
        registry.addViewController("/thiet-bi/them").setViewName("thiet-bi/them-thiet-bi");
        registry.addViewController("/thiet-bi/phan-cong").setViewName("thiet-bi/phan-cong-thiet-bi");

        registry.addViewController("/vat-tu").setViewName("vat-tu/danh-sach-vat-tu");
        registry.addViewController("/vat-tu/them").setViewName("vat-tu/them-vat-tu");
        registry.addViewController("/vat-tu/quan-ly-kho").setViewName("vat-tu/quan-ly-kho");

        registry.addViewController("/bao-tri/ke-hoach").setViewName("bao-tri/ke-hoach-bao-tri");
        registry.addViewController("/bao-tri/yeu-cau").setViewName("bao-tri/yeu-cau-bao-tri");
        registry.addViewController("/bao-tri/thuc-hien").setViewName("bao-tri/thuc-hien-bao-tri");
        registry.addViewController("/bao-tri/kiem-tra-dinh-ky").setViewName("bao-tri/kiem-tra-dinh-ky");
        registry.addViewController("/bao-tri/canh-bao").setViewName("bao-tri/canh-bao-loi");

        registry.addViewController("/doi-bao-tri").setViewName("doi-bao-tri/danh-sach-doi");
        registry.addViewController("/doi-bao-tri/them").setViewName("doi-bao-tri/them-doi");
        registry.addViewController("/doi-bao-tri/phan-cong").setViewName("doi-bao-tri/phan-cong-cong-viec");

        registry.addViewController("/nguoi-dung").setViewName("nguoi-dung/danh-sach-nguoi-dung");
        registry.addViewController("/nguoi-dung/them").setViewName("nguoi-dung/them-nguoi-dung");
        registry.addViewController("/nguoi-dung/phan-quyen").setViewName("xoa-nguoi-dung");

        registry.addViewController("/bao-cao/oee").setViewName("bao-cao/bao-cao-oee");
        registry.addViewController("/bao-cao/mtbf").setViewName("bao-cao/bao-cao-mtbf");
        registry.addViewController("/bao-cao/tong-hop").setViewName("bao-cao/bao-cao-tong-hop");
        registry.addViewController("/bao-cao/thong-ke").setViewName("bao-cao/thong-ke");

        registry.addViewController("/san-xuat/thong-tin").setViewName("san-xuat/thong-tin-san-xuat");
        registry.addViewController("/san-xuat/hieu-nang").setViewName("san-xuat/hieu-nang-thiet-bi");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Cấu hình để phục vụ static resources
        registry.addResourceHandler("/static/css/**")
                .addResourceLocations("classpath:/static/css/");

        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");

        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}