package goorm.eagle7.stelligence.domain.document.content.dto.protobuf.converter;

import java.util.List;

import goorm.eagle7.stelligence.domain.document.content.dto.DocumentResponse;
import goorm.eagle7.stelligence.domain.document.content.dto.SectionResponse;
import goorm.eagle7.stelligence.domain.document.content.dto.protobuf.DocumentResponseOuterClass;
import goorm.eagle7.stelligence.domain.document.content.dto.protobuf.SectionResponseOuterClass;

public class ProtoBufDocumentResponseConverter {

	private ProtoBufDocumentResponseConverter() {
	}

	public static DocumentResponse toMyDocumentResponse(
		DocumentResponseOuterClass.DocumentResponse protoBufDocumentResponse) {

		List<SectionResponse> mySections = protoBufDocumentResponse.getSections()
			.stream()
			.map(ProtoBufSectionResponseConverter::toMySectionResponse)
			.toList();

		return DocumentResponse.of(
			protoBufDocumentResponse.getDocumentId(),
			protoBufDocumentResponse.getTitle(),
			mySections
		);
	}

	public static DocumentResponseOuterClass.DocumentResponse toProtoBufDocumentResponse(
		DocumentResponse myDocumentResponse) {

		List<SectionResponseOuterClass.SectionResponse> protoBufSections = myDocumentResponse.getSections()
			.stream()
			.map(ProtoBufSectionResponseConverter::toProtoBufSectionResponse)
			.toList();

		return DocumentResponseOuterClass.DocumentResponse.newBuilder()
			.setDocumentId(myDocumentResponse.getDocumentId())
			.setTitle(myDocumentResponse.getTitle())
			.addAllSections(protoBufSections)
			.build();
	}

}
