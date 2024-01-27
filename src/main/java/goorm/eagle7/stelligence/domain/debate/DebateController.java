package goorm.eagle7.stelligence.domain.debate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/api/debates")
@RestController
@Slf4j
@RequiredArgsConstructor
public class DebateController {

	@GetMapping
	public ResponseTemplate<Page<DebateSimpleResponse>> getDebateList(
		@RequestParam("status") DebateStatus status,
		Pageable pageable
	) {

		return ResponseTemplate.ok(null);
	}

	@GetMapping("/{debateId}")
	public ResponseTemplate<DebateResponse> getDebateDetail(
		@PathVariable("debateId") Long debateId
	) {
		return ResponseTemplate.ok(null);
	}

	@DeleteMapping("/{debateId}")
	public ResponseTemplate<Void> closeDebate(
		@PathVariable("debateId") Long debateId,
		@Auth MemberInfo memberInfo
	) {
		return ResponseTemplate.ok();
	}
}
