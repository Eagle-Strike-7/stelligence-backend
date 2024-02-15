package goorm.eagle7.stelligence.domain.document.event.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import goorm.eagle7.stelligence.domain.badge.BadgeService;
import goorm.eagle7.stelligence.domain.badge.model.BadgeCategory;
import goorm.eagle7.stelligence.domain.document.content.DocumentContentRepository;
import goorm.eagle7.stelligence.domain.document.event.NewDocumentEvent;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DocumentEventListener {

	private final BadgeService badgeService;
	private final DocumentContentRepository documentContentRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@TransactionalEventListener(value = NewDocumentEvent.class)
	public void onDocumentNew(NewDocumentEvent event) {

		if (documentContentRepository.existsById(event.documentId())) {

			documentContentRepository.findByIdWithAuthor(event.documentId())
				.ifPresent(document ->
					badgeService.checkAndAwardBadge(BadgeCategory.DOCUMENT, document.getAuthor())
				);

		}

	}

}

