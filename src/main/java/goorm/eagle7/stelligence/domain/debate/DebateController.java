package goorm.eagle7.stelligence.domain.debate;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.common.auth.memberinfo.Auth;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfo;
import goorm.eagle7.stelligence.domain.debate.dto.DebateResponse;
import goorm.eagle7.stelligence.domain.debate.dto.DebateSimpleResponse;
import goorm.eagle7.stelligence.domain.debate.model.DebateStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Debate API", description = "토론을 조회하고 종료하는 API를 제공합니다.")
@RequestMapping("/api/debates")
@RestController
@Slf4j
@RequiredArgsConstructor
public class DebateController {

	@Operation(summary = "토론 리스트 조회", description = "토론의 상태(OPEN / CLOSED)에 따라 토론 리스트를 조회합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "토론 리스트 조회 성공",
		useReturnTypeSchema = true
	)
	@GetMapping
	public ResponseTemplate<Page<DebateSimpleResponse>> getDebateList(
		@Parameter(description = "조회할 토론의 상태", example = "OPEN")
		@RequestParam("status") DebateStatus status,
		@ParameterObject
		@PageableDefault(page = 0, size = 4) Pageable pageable
	) {

		return ResponseTemplate.ok(null);
	}

	@Operation(summary = "토론 상세 조회", description = "특정 토론을 조회합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "토론 상세 조회 성공",
		useReturnTypeSchema = true
	)
	@GetMapping("/{debateId}")
	public ResponseTemplate<DebateResponse> getDebateDetail(
		@Parameter(description = "조회할 토론의 ID", example = "1")
		@PathVariable("debateId") Long debateId
	) {
		return ResponseTemplate.ok(null);
	}

	@Operation(summary = "토론 종료", description = "[OPEN] 상태인 특정 토론을 임의로 종료합니다. 해당 수정 요청을 작성했던 회원의 종료 요청만이 허용됩니다.")
	@ApiResponse(
		responseCode = "200",
		description = "토론 종료 성공",
		useReturnTypeSchema = true
	)
	@DeleteMapping("/{debateId}")
	public ResponseTemplate<Void> closeDebate(
		@Parameter(description = "종료할 토론의 ID", example = "1")
		@PathVariable("debateId") Long debateId,
		@Auth MemberInfo memberInfo
	) {
		return ResponseTemplate.ok();
	}
}
