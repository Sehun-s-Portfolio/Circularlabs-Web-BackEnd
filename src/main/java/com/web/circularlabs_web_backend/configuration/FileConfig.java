package com.web.circularlabs_web_backend.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileConfig implements WebMvcConfigurer {

    // http://116.125.141.139:8080/music/dfbauhrlbvald.png
    // 리소스 접근 시 addResourceHandler를 통해 요청 경로 값을 설정하고
    // add ResourceLocations를 통해 접근할 파일 이들어가져있는 실질적인 경로를 입력받아
    // 요청 url + addResourceHandlers에서 설정한 경로 값을 요청하면 locations 에 설정한 파일들에 실질적으로 접근할 수 있또록 하는 Config 함수
    // 예) http://116.125.141.139:8080/music/dfbauhrlbvald.png 을 요청하면 locations에 설정한 접근 경로 중 하나에 일치한 파일에 접근한다.
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/image/**")
                .addResourceLocations("file:///home/circularlabs/web/image/");

        // 이후 배포 서버를 변경하게 될 때 그에 맞춰서 실제 이미지가 업로드될 경로를 지정하는 addResourceLocations 를 다시 지정해주어야 하고,
        // 마찬가지로 실제 배포 서버에서도 업로드 경로에 맞게끔 폴더 경로들을 생성해주어야 한다.
    }
}
