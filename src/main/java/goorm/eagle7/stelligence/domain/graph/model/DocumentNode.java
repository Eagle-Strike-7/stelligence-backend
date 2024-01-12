package goorm.eagle7.stelligence.domain.graph.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
public class DocumentNode {

	@Id
	@GeneratedValue
	private Long id;

	private String title;

	private Long rdbId;
}
