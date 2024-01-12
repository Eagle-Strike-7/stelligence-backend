package goorm.eagle7.stelligence.domain.graph;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import goorm.eagle7.stelligence.domain.graph.model.DocumentNode;

public interface DocumentNodeRepository extends Neo4jRepository<DocumentNode, String> {
}
