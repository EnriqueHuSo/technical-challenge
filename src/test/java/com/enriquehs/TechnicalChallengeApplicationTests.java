package com.enriquehs;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

@SpringBootTest
class TechnicalChallengeApplicationTests {

	@Autowired
    private DatabaseClient databaseClient;

	@Test
	void contextLoads() {
		Mono<Integer> result = databaseClient.sql("SELECT COUNT(*) FROM PRODUCTOS")
                .map((row, rowMetadata) -> row.get(0, Integer.class))
                .one();

        Integer count = result.block();
        assertNotNull(count);
	}

}
