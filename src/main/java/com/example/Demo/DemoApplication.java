package com.example.Demo;

import com.example.Demo.security.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder(){
		return  new BCryptPasswordEncoder();
	}
	@Bean
	public  SpringApplicationContext springApplicationContext(){
		return  new SpringApplicationContext();
	}

	@Bean(name = "AppProperties")
	public AppProperties getappProperties(){
		return new AppProperties();
	}
}
