package goorm.eagle7.stelligence;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.test.annotation.Rollback;

@DataNeo4jTest
class Neo4jClearTest {

	@Autowired
	Neo4jClient neo4jClient;

	/**
	 * Neo4j의 데이터를 초기화하는 쿼리를 실행하고, 초기화가 되었는지를 검증합니다.
	 * Disabled: 의도한 상황에서만 테스트가 실행될 수 있도록 Disabled 처리하였습니다.
	 */
	@Test
	@Rollback(false)
	@Disabled
	// 의도한 상황에서만 테스트가 실행될 수 있도록 Disabled 처리하였습니다.
	void clearNeo4j() {

		String clearQuery = "match (n) detach delete n;";
		neo4jClient.query(clearQuery).run();

		String verifyQuery = "match (n) return count(n);";
		Long nodeCount = neo4jClient.query(verifyQuery).fetchAs(Long.class).one().get();

		assertThat(nodeCount).isZero();
	}

}
