package com.github.losevskiyfz.offerservice;

import org.springframework.boot.SpringApplication;

public class TestOfferServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(OfferServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
