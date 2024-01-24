package goorm.eagle7.stelligence.domain.contribute.scheduler.template;

import org.springframework.stereotype.Component;

import goorm.eagle7.stelligence.domain.amendment.model.AmendmentType;
import lombok.RequiredArgsConstructor;

/**
 * AmendmentMergeTemplateMapper
 *
 * Amendment 타입에 따라 적합한 AmendmentMergeTemplate을 반환합니다.
 */
@Component
@RequiredArgsConstructor
public class AmendmentMergeTemplateMapper {

	private final CreateAmendmentMergeTemplate createAmendmentMergeTemplate;
	private final UpdateAmendmentMergeTemplate updateAmendmentMergeTemplate;
	private final DeleteAmendmentMergeTemplate deleteAmendmentMergeTemplate;

	public AmendmentMergeTemplate getTemplateForType(AmendmentType type) {
		return switch (type) {
			case CREATE -> createAmendmentMergeTemplate;
			case UPDATE -> updateAmendmentMergeTemplate;
			case DELETE -> deleteAmendmentMergeTemplate;
		};
	}
}
