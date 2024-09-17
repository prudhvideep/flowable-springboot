package com.flowable.flowable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.flowable.flowable", "com.flowable.repositories", "com.flowable.services","com.flowable.entity"})
public class FlowableApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlowableApplication.class, args);
	}

}
