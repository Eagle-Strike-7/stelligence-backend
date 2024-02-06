package goorm.eagle7.stelligence.domain.contribute;

import org.springdoc.core.annotations.ParameterObject;
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
import goorm.eagle7.stelligence.domain.contribute.dto.ContributePageResponse;
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

	@Operation(summary = "수정요청 목록 조회(투표중)", description = "투표 중인 수정요청 목록을 조회합니다. ")
	@ApiResponse(
		responseCode = "200",
		description = "수정요청 목록 조회 성공",
		useReturnTypeSchema = true
	)
	@GetMapping("/voting")
	public ResponseTemplate<ContributePageResponse> getVotingContributes(
		@ParameterObject
		@PageableDefault(page = 0, size = 10) Pageable pageable
	) {
		return ResponseTemplate.ok(contributeService.getContributesByStatus(ContributeStatus.VOTING, pageable));
	}

	@Operation(summary = "수정요청 목록 조회(투표 완료)",
		description = "투표가 완료된 수정요청 목록을 조회합니다. "
			+ "status에 값을 넣지 않으면 모든 완료된 수정요청을 조회합니다. "
			+ "status에 값을 넣으면 해당 상태의 수정요청을 조회합니다. "
			+ "status에는 MERGED, REJECTED, CANCELED 중 하나를 넣을 수 있습니다. ")
	@ApiResponse(
		responseCode = "200",
		description = "수정요청 목록 조회 성공",
		useReturnTypeSchema = true
	)
	@GetMapping("/complete")
	public ResponseTemplate<ContributePageResponse> getCompleteContributes(
		@Parameter(description = "수정요청의 상태", example = "MERGED")
		@RequestParam(required = false) ContributeStatus status,
		@ParameterObject
		@PageableDefault(page = 0, size = 10) Pageable pageable
	) {
		// status가 VOTING인 경우 실패 응답 반환
		if (ContributeStatus.VOTING.equals(status)) {
			return ResponseTemplate.fail("이 API에서는 status에 VOTING을 넣을 수 없습니다.");
		}

		if (status != null) {
			return ResponseTemplate.ok(contributeService.getContributesByStatus(status, pageable));
		} else {
			// status가 없으면 모든 완료된 수정요청을 조회
			return ResponseTemplate.ok(contributeService.getCompletedContributes(pageable));
		}
	}

	@Operation(summary = "수정요청 목록 조회(문서별)",
		description = "특정 문서의 수정요청을 조회할 때 문서의 ID를 입력하면 해당 문서의 수정요청을 조회할 수 있습니다. ")
	@ApiResponse(
		responseCode = "200",
		description = "수정요청 목록 조회 성공",
		useReturnTypeSchema = true
	)
	@GetMapping
	public ResponseTemplate<ContributePageResponse> getContributesByDocument(
		@Parameter(description = "특정 문서의 수정요청을 조회할 때 문서의 ID", example = "1")
		@RequestParam Long documentId,
		@Parameter(description = "수정요청이 MERGED = true, REJECTED or DEBATING = false", example = "true")
		@RequestParam boolean merged,
		@ParameterObject
		@PageableDefault(page = 0, size = 10) Pageable pageable
	) {
		if (documentId == null) {
			return ResponseTemplate.fail("documentId는 필수로 입력해야 합니다.");
		}

		return ResponseTemplate.ok(contributeService.getContributesByDocumentAndStatus(documentId, merged, pageable));
	}
}
