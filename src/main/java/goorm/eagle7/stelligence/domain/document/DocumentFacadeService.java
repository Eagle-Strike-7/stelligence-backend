package goorm.eagle7.stelligence.domain.document;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.domain.document.content.DocumentService;
import goorm.eagle7.stelligence.domain.document.graph.DocumentGraphService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DocumentFacadeService {

	private final DocumentService documentService;
	private final DocumentGraphService documentGraphService;

}
