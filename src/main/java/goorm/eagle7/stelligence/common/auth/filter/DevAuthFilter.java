package goorm.eagle7.stelligence.common.auth.filter;

import java.io.IOException;
import java.util.Optional;

import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.common.auth.jwt.JwtTokenProvider;
import goorm.eagle7.stelligence.common.auth.jwt.JwtTokenService;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfo;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfoContextHolder;
import goorm.eagle7.stelligence.common.login.CookieType;
import goorm.eagle7.stelligence.common.login.CookieUtils;
import goorm.eagle7.stelligence.common.login.LoginService;
import goorm.eagle7.stelligence.common.login.RandomUtils;
import goorm.eagle7.stelligence.common.login.dto.DevLoginRequest;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <h2>개발용 토큰 검증 필터</h2>
 * <p>토큰 검증이 필요한 리소스에 대해 토큰을 검증하고, ThreadLocal에 memberInfo를 저장한다.</p>
 * <p>repository에 저장되지 않은 httpmethod, uri에 대해 토큰 검증 진행</p>
 */
@Slf4j
// @Component
@RequiredArgsConstructor
public class DevAuthFilter extends OncePerRequestFilter {

	private final PermittedPathStore permittedPathStore;
	private final JwtTokenService jwtTokenService;
	private final JwtTokenProvider jwtTokenProvider;
	private final LoginService loginService;
	private final MemberRepository memberRepository;
	private final CookieUtils cookieUtils;

	/**
	 * 토큰 검증 필터
	 * 	- 유효하지 않은 토큰이나 쿠키 응답인 경우, 자동 회원 가입 및 로그인으로 토큰 재발급
	 * 	- 회원 가입 시에는 닉네임을 랜덤으로 생성
	 * 	- 닉네임 중복이면 랜덤 닉네임 생성
	 * 	- response에 accessToken, refreshToken, nickname 추가
	 * 		- 쿠키:accessToken, refreshToken
	 * 		- header:nickname
	 *
	 * 1. 토큰 검증이 필요한 리소스인지 확인
	 * 2. 유효한 쿠키인지 확인 후 accessToken 추출
	 * 3. accessToken null 검증 후 memberId 추출 (만료여도 id 추출) - 동일한 사용자로 테스트하기 위함
	 * 4. DB에 저장된 사용자(id)인지 확인, 있다면 accessToken 유효성 검증
	 * 5. accessToken 만료라면 refreshToken 재발급 후 update(기간에 관계 없이 재발급, accessToken은 login 시 재발급)
	 * 6. 즉, 쿠키가 null이거나 accessToken이 없거나 DB에 저장된 사용자가 아니라면 자동 회원 가입
	 * 7. accessToken이 있고, 해당 id가 DB에 있다면 로그인 진행
	 * 8. 검증 완료 이후 memberInfo를 ThreadLocal에 저장
	 * 9. BaseException 예외 발생 시 ApiResponse로 응답
	 * 10. 무슨 일이 있어도 ThreadLocal 초기화
	 */
	@Override
	protected void doFilterInternal(
		HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
		ServletException,
		IOException {

		String httpMethod = request.getMethod();
		String uri = request.getRequestURI();

		try {
			// 토큰 검증이 필요한 uri라면 토큰 검증
			if (isTokenValidationRequired(httpMethod, uri)) {

				log.debug("토큰 검증이 필요한 uri");
				Optional<Cookie> cookie = cookieUtils.getCookieFromRequest(CookieType.ACCESS_TOKEN);
				String nickname = null;
				boolean signUpMode = true;

				if (cookie.isPresent()) {

					log.debug("accessTokenCookie 존재");
					// request에서 accessToken 추출
					String accessToken = jwtTokenService.extractJwtFromCookie(cookie.get(), CookieType.ACCESS_TOKEN);
					log.debug("accessToken: {}", accessToken);
					// accessToken이 존재하는지 검증 - null
					boolean tokenExists = jwtTokenService.validateIsTokenExists(accessToken);

					// accessToken이 존재한다면 id가 DB에 저장되어 있는지 확인
					if (tokenExists) {

						// 만료 상관 없이 member id 추출
						Long memberId = Long.parseLong(jwtTokenService.extractSubFromExpiredToken(accessToken));
						Optional<Member> memberOptional = memberRepository.findById(memberId);

						// accessToken이 만료 전, 유효하다면 DB에 저장된 사용자인지 확인
						// test 시에는 DB에 없더라도 쿠키가 남아 있어서 실제 사용자가 없는데, 조회해 꼬이는 것 방지.
						if (memberOptional.isPresent()) {
							log.debug("memberOptional 존재");
							log.debug("memberId: {}", memberId);
							// 회원 가입 모드 off
							signUpMode = false;
							Member member = memberOptional.get();
							nickname = member.getNickname();

							// 서명 검증 후 서명 얻어 오기
							Claims claims = jwtTokenService.validateAndGetClaims(accessToken);
							// accessToken 만료 검증
							boolean validatedIsVerified = jwtTokenService.validateActiveToken(claims);
							if (!validatedIsVerified) {

								// refreshToken 기간에 관계 없이 accessToken 재발급, refresh 토큰 만료라면 throw BaseException
								String refreshToken = jwtTokenProvider.createRefreshToken(memberId);

								member.updateRefreshToken(refreshToken);
								memberRepository.save(member);

							}
						}
					}
				}
				// 자동 회원 가입을 위한 랜덤 nickname 생성
				if (signUpMode) {
					// test+랜덤숫자 5개로 nickname 생성
					nickname = RandomUtils.createNicknameWithRandomNumber("test");
				}
				log.debug("nickname: {}", nickname);
				// login - nickname 따라 회원 가입, 로그인 결정됨.
				String accessToken = loginService.login(DevLoginRequest.from(nickname));

				Claims claims = jwtTokenService.validateAndGetClaims(accessToken);

				// 검증 완료 이후 memberInfo를 ThreadLocal에 저장
				// ThreadLocal 초기화
				MemberInfoContextHolder.clear();
				MemberInfo memberInfo = jwtTokenService.extractMemberInfo(claims);

				// ThreadLocal에 token에서 추출한 memberInfo 저장
				MemberInfoContextHolder.setMemberInfo(memberInfo);
			}

			filterChain.doFilter(request, response);

		} catch (BaseException e) {
			handleBaseException(response, e);
		} finally {
			// 무슨 일이 있어도 ThreadLocal 초기화
			MemberInfoContextHolder.clear();
		}
	}

	/**
	 * BaseException 예외 발생 시 ApiResponse로 응답
	 * @param response response
	 * @param e BaseException
	 * @throws IOException JSON 변환 실패 시
	 */
	private static void handleBaseException(HttpServletResponse response, BaseException e) throws IOException {
		// 사용자 정의 오류 응답 생성
		ResponseTemplate<Void> apiResponse = ResponseTemplate.fail(e.getMessage());

		// JSON으로 변환
		String jsonResponse = new ObjectMapper().writeValueAsString(apiResponse);

		// 응답 설정
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 코드
		response.getWriter().write(jsonResponse);
	}

	/**
	 * repository에 저장된 RequestResource set 기준으로 토큰 검증이 필요한 httpMethod, uri인지 확인
	 * @param httpMethod String 타입으로 추출.
	 * @param uri uri String 타입으로 추출.
	 * @return boolean 토큰 검증이 필요하면 true, 아니면 false
	 */
	private boolean isTokenValidationRequired(String httpMethod, String uri) {
		return !permittedPathStore.exist(httpMethod, uri);
	}

}