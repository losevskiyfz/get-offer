package com.github.losevskiyfz.offerservice;

import com.github.losevskiyfz.offerservice.base.annotation.EnableKafka;
import com.github.losevskiyfz.offerservice.base.annotation.EnableMongo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@EnableKafka
@EnableMongo
@SpringBootTest
class OfferServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
