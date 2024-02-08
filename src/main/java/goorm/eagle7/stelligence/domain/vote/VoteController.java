package goorm.eagle7.stelligence.domain.vote;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.common.auth.memberinfo.Auth;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfo;
import goorm.eagle7.stelligence.domain.vote.dto.VoteRequest;
import goorm.eagle7.stelligence.domain.vote.dto.VoteSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Vote API", description = "투표를 생성하고 투표 현황을 조회하는 API를 제공합니다")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class VoteController {

	private final VoteService voteService;

	@Operation(summary = "소중한 한 표 행사", description = "사용자가 투표를 하면 투표를 생성합니다")
	@ApiResponse(
		responseCode = "200",
		description = "투표 생성 성공",
		useReturnTypeSchema = true
	)
	@PostMapping("/votes")
	public ResponseTemplate<VoteSummaryResponse> vote(
		@RequestBody VoteRequest voteRequest,
		@Auth MemberInfo memberInfo
	) {
		voteService.vote(voteRequest, memberInfo.getId());
		return ResponseTemplate.ok();
	}

	@Operation(summary = "투표 현황 조회", description = "현재 투표 현황을 조회합니다")
	@ApiResponse(
		responseCode = "200",
		description = "투표 현황 조회 성공",
		useReturnTypeSchema = true
	)
	@GetMapping("/contributes/{contributeId}/votes")
	public ResponseTemplate<VoteSummaryResponse> getVoteSummary(
		@Parameter(description = "투표를 조회할 수정요청의 ID", example = "1")
		@PathVariable Long contributeId,
		@Auth MemberInfo memberInfo
	) {
		// 로그인한 경우 id를 받아오고, 로그인하지 않은 경우 null 처리
		Long memberId = memberInfo != null ? memberInfo.getId() : null;

		return ResponseTemplate.ok(voteService.getVoteSummary(contributeId, memberId));
	}
}
