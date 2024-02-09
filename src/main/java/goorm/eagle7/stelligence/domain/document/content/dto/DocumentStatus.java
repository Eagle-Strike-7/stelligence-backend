package goorm.eagle7.stelligence.domain.document.content.dto;

public enum DocumentStatus {

	EDITABLE,	//편집 가능
	VOTING,	//투표중
	DEBATING,	//토론중
	PENDING	//토론 종료 후 토론 참여자의 수정 요청 대기중
}
