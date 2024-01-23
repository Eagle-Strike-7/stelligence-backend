package goorm.eagle7.stelligence.api.log;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 로깅 필터
 */
@Slf4j
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Component
@WebFilter(filterName = "RequestLoggingFilter", urlPatterns = "/*") // 애플리케이션의 모든 요청에 대해 매핑됨
public class RequestLoggingFilter extends OncePerRequestFilter {

	// 로깅 제외 URL 패턴
	private static final String[] EXCLUDE_URL_PATTERN = {"/swagger-ui/", "/api-docs", "/v3/api-docs"};
	private static final int MAX_BODY_PRINT_LENGTH = 100; // body 출력 길이 제한

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		CachedHttpServletRequest cachedHttpServletRequest = new CachedHttpServletRequest(request);

		// 로깅 제외 URL 패턴에 포함되어 있으면 로깅하지 않음
		for (String pattern : EXCLUDE_URL_PATTERN) {
			if (cachedHttpServletRequest.getRequestURI().startsWith(pattern)) {
				filterChain.doFilter(cachedHttpServletRequest, response);
				return;
			}
		}

		// uri 출력
		String queryString = cachedHttpServletRequest.getQueryString() == null ? "" :
			"?" + UriUtils.decode(cachedHttpServletRequest.getQueryString(), StandardCharsets.UTF_8);

		log.debug("REQUEST URI: {}", cachedHttpServletRequest.getRequestURI() + queryString);

		// body 출력
		String requestBody = IOUtils.toString(cachedHttpServletRequest.getInputStream(), StandardCharsets.UTF_8);

		if (StringUtils.hasText(requestBody)) {
			// body가 100자 이상이면 100자까지만 출력
			if (requestBody.length() > MAX_BODY_PRINT_LENGTH) {
				requestBody = requestBody.substring(0, MAX_BODY_PRINT_LENGTH) + "...";
			}

			// 개행문자 제거
			requestBody = requestBody.replace("\n", " ");

			log.debug("REQUEST BODY: {}", requestBody);
		}

		filterChain.doFilter(cachedHttpServletRequest, response);
	}

}
