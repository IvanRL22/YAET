package com.ivanrl.yaet.yaetApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@SpringBootApplication
public class YaetAppApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(YaetAppApplication.class, args);
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new YearMonthArgumentResolver());
	}

}
