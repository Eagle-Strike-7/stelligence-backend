package goorm.eagle7.stelligence.domain.document.event;

/**
 * 새로운 Document가 생성될 때 발생하는 이벤트
 * @param documentId 문서 ID
 */
public record NewDocumentEvent(Long documentId) {

}
