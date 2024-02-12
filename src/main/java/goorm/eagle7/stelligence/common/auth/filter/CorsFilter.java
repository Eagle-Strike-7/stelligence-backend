package goorm.eagle7.stelligence.common.auth.filter;

import static org.springframework.core.Ordered.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(HIGHEST_PRECEDENCE)
public class CorsFilter extends OncePerRequestFilter {


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {


		log.debug("CORS 필터 실행");
		List<String> allowedOrigins = Arrays.asList("https://api.stelligence.site",
			"http://localhost:3000",
			"http://3.39.192.156:80",
			"http://www.stelligence.site");

		String originHeader = request.getHeader("Origin");

		if (allowedOrigins.contains(originHeader)) {
			response.setHeader("Access-Control-Allow-Origin", originHeader);
		}

		response.setHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,OPTIONS");
		response.setHeader("Access-Control-Allow-Headers", "*");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Max-Age", "3600");
		if (request.getMethod().equals("OPTIONS")) {
			log.debug("corsFilter, OPTIONS 메소드 요청");
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			filterChain.doFilter(request, response);
		}

	}
}
