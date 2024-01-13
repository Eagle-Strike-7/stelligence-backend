package goorm.eagle7.stelligence.domain.document.model;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import java.util.ArrayList;
import java.util.List;

import goorm.eagle7.stelligence.common.entity.BaseTimeEntity;
import goorm.eagle7.stelligence.domain.section.model.Section;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Document extends BaseTimeEntity { //추후 BaseTimeEntity 상속

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "document_id")
	private Long id;

	//Member (creator)

	private String title;

	private Long currentRevision;

	@OneToMany(mappedBy = "document", orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	//===생성===//
	public static Document createDocument(String title) {
		Document document = new Document();
		document.title = title;

		document.currentRevision = 1L;
		return document;
	}

	//===수정===//
	//Contribute에 대한 Merge가 끝나고 현재 버전을 올려줍니다.
	public void incrementCurrentRevision() {
		this.currentRevision++;
	}

}
