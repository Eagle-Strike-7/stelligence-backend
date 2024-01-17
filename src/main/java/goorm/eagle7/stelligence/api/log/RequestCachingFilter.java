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

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		CachedHttpServletRequest cachedHttpServletRequest = new CachedHttpServletRequest(request);

		// uri 출력
		log.debug("REQUEST URI: {}",
			cachedHttpServletRequest.getRequestURI() + "?" + cachedHttpServletRequest.getQueryString());

		// body 출력
		log.debug("REQUEST DATA: {}",
			IOUtils.toString(cachedHttpServletRequest.getInputStream(), StandardCharsets.UTF_8)
				.replace("\n", " "));  //줄바꿈 방지

		filterChain.doFilter(cachedHttpServletRequest, response);
	}

}
