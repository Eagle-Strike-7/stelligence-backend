package goorm.eagle7.stelligence.domain.document.content.dto.protobuf.converter;

import goorm.eagle7.stelligence.domain.document.content.dto.SectionResponse;
import goorm.eagle7.stelligence.domain.document.content.dto.protobuf.SectionResponseOuterClass;

public class ProtoBufSectionResponseConverter {

	private ProtoBufSectionResponseConverter() {
	}

	public static SectionResponse toMySectionResponse(
		SectionResponseOuterClass.SectionResponse protoBufSectionResponse) {
		return SectionResponse.of(
			protoBufSectionResponse.getSectionId(),
			protoBufSectionResponse.getRevision(),
			ProtoBufHeadingConverter.toHeading(protoBufSectionResponse.getHeading()),
			protoBufSectionResponse.getTitle(),
			protoBufSectionResponse.getContent()
		);
	}

	public static SectionResponseOuterClass.SectionResponse toProtoBufSectionResponse(
		SectionResponse mySectionResponse) {
		return SectionResponseOuterClass.SectionResponse.newBuilder()
			.setSectionId(mySectionResponse.getSectionId())
			.setRevision(mySectionResponse.getRevision())
			.setHeading(ProtoBufHeadingConverter.toProtoBufHeading(mySectionResponse.getHeading()))
			.setTitle(mySectionResponse.getTitle())
			.setContent(mySectionResponse.getContent())
			.build();
	}
}
