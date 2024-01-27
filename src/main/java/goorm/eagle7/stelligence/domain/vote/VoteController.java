package goorm.eagle7.stelligence.domain.vote;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.common.auth.memberinfo.Auth;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfo;
import goorm.eagle7.stelligence.domain.vote.dto.VoteRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Vote API", description = "투표를 생성,삭제 하는 API를 제공합니다")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/votes")
public class VoteController {

	private final VoteService voteService;

	@Operation(summary = "소중한 한 표 행사", description = "사용자가 투표를 하면 투표를 생성합니다")
	@ApiResponse(
		responseCode = "200",
		description = "투표 생성 성공",
		useReturnTypeSchema = true
	)
	@PostMapping
	public ResponseTemplate<Void> vote(
		@RequestBody VoteRequest voteRequest,
		@Auth MemberInfo memberInfo
	) {
		voteService.vote(voteRequest, memberInfo.getId());
		return ResponseTemplate.ok();
	}

	@Operation(summary = "투표 취소", description = "사용자가 투표를 취소하면 투표를 삭제합니다")
	@ApiResponse(
		responseCode = "200",
		description = "투표 삭제 성공",
		useReturnTypeSchema = true
	)
	@DeleteMapping
	public ResponseTemplate<Void> cancelVote(
		@RequestBody VoteRequest voteRequest,
		@Auth MemberInfo memberInfo
	) {
		voteService.cancelVote(voteRequest.getContributeId(), memberInfo.getId());
		return ResponseTemplate.ok();
	}
}
