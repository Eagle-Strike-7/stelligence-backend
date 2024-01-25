package goorm.eagle7.stelligence.domain.contribute;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.common.auth.memberinfo.Auth;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfo;
import goorm.eagle7.stelligence.domain.contribute.dto.ContributeListResponse;
import goorm.eagle7.stelligence.domain.contribute.dto.ContributeRequest;
import goorm.eagle7.stelligence.domain.contribute.dto.ContributeResponse;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Contribute API", description = "수정요청을 생성,조회,삭제 하는 API를 제공합니다")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contributes")
public class ContributeController {

	private final ContributeService contributeService;

	@Operation(summary = "수정요청 생성", description = "수정요청을 생성합니다")
	@ApiResponse(
		responseCode = "200",
		description = "수정요청 생성 성공",
		useReturnTypeSchema = true
	)
	@PostMapping
	public ResponseTemplate<ContributeResponse> createContribute(
		@RequestBody ContributeRequest contributeRequest,
		@Auth MemberInfo memberInfo
	) {
		return ResponseTemplate.ok(contributeService.createContribute(contributeRequest, memberInfo.getId()));
	}

	@Operation(summary = "수정요청 조회", description = "수정요청을 조회합니다")
	@ApiResponse(
		responseCode = "200",
		description = "수정요청 조회 성공",
		useReturnTypeSchema = true
	)
	@GetMapping("/{contributeId}")
	public ResponseTemplate<ContributeResponse> getContribute(
		@Parameter(description = "조회할 수정요청의 ID", example = "1")
		@PathVariable Long contributeId
	) {
		return ResponseTemplate.ok(contributeService.getContribute(contributeId));
	}

	@Operation(summary = "수정요청 삭제", description = "지정된 수정요청을 삭제합니다")
	@ApiResponse(
		responseCode = "200",
		description = "수정요청 삭제 성공",
		useReturnTypeSchema = true
	)
	@DeleteMapping("/{contributeId}")
	public ResponseTemplate<Void> deleteContribute(
		@Parameter(description = "삭제할 수정요청의 ID", example = "1")
		@PathVariable Long contributeId,
		@Auth MemberInfo memberInfo
	) {
		contributeService.deleteContribute(contributeId, memberInfo.getId());
		return ResponseTemplate.ok();
	}

	@Operation(summary = "수정요청 목록 조회",
		description = "status=VOTING으로 조회하면 투표 중인 모든 수정 요청을 조회할 수 있습니다. "
			+ "documentId로 조회하면 해당 문서의 수정 요청을 필터링하여 조회할 수 있습니다. "
			+ "단, status와 documentId를 함께 사용하는 것은 지원하지 않으니 하나만 입력해 주세요.")
	@ApiResponse(
		responseCode = "200",
		description = "수정요청 목록 조회 성공",
		useReturnTypeSchema = true
	)
	@GetMapping
	public ResponseTemplate<Page<ContributeListResponse>> getContributes(
		@Parameter(description = "특정 문서의 수정요청을 조회할 때 문서의 ID", example = "1")
		@RequestParam(required = false) Long documentId,
		@Parameter(description = "수정요청의 상태", example = "VOTING")
		@RequestParam(required = false) ContributeStatus status,
		@ParameterObject
		@PageableDefault(page = 0, size = 10) Pageable pageable
	) {
		if (status != null && documentId != null) {
			return ResponseTemplate.fail("status와 documentId는 함께 사용할 수 없습니다.");
		}

		if (status != null) {
			return ResponseTemplate.ok(contributeService.getVotingContributes(pageable));
		} else if (documentId != null) {
			return ResponseTemplate.ok(contributeService.getContributesByDocument(documentId, pageable));
		} else {
			return ResponseTemplate.fail("status와 documentId 중 하나는 필수로 입력해야 합니다.");
		}
	}
}
