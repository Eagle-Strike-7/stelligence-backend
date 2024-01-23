package goorm.eagle7.stelligence.api.log;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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
@WebFilter(filterName = "RequestCachingFilter", urlPatterns = "/*") // 애플리케이션의 모든 요청에 대해 매핑됨
public class RequestCachingFilter extends OncePerRequestFilter {

	// 로깅 제외 URL 패턴
	private static final String[] EXCLUDE_URL_PATTERN = {
		"/swagger-ui/",
		"/api-docs",
		"/v3/api-docs"
	};

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

		String queryString =
			cachedHttpServletRequest.getQueryString() == null ? "" : cachedHttpServletRequest.getQueryString();

		// uri 출력
		log.debug("REQUEST URI: {}",
			cachedHttpServletRequest.getRequestURI() + "?" + queryString);

		// body 출력
		log.debug("REQUEST DATA: {}",
			IOUtils.toString(cachedHttpServletRequest.getInputStream(), StandardCharsets.UTF_8)
				.replace("\n", " "));  //줄바꿈 방지

		filterChain.doFilter(cachedHttpServletRequest, response);
	}

}
