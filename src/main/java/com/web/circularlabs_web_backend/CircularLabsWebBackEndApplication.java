package com.web.circularlabs_web_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.TimeZone;

@EnableJpaAuditing
@SpringBootApplication
public class CircularLabsWebBackEndApplication {

	public static void main(String[] args) {

		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		SpringApplication.run(CircularLabsWebBackEndApplication.class, args);
		System.out.println("웹(WEB) 어플리케이션 실행 ~~~~~~~~~~~~~~~~~~~~");
		System.out.println("JDK 버전 17");
	}

}
