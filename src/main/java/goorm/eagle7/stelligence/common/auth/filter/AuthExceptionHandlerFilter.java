package goorm.eagle7.stelligence.common.auth.filter;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthExceptionHandlerFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (UsernameNotFoundException e) {
			log.debug("UsernameNotFoundException catched in AuthExceptionHandlerFilter : {}", e.getMessage());
			ResponseTemplateUtils.toErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, ResponseTemplate.fail(e.getMessage()));
		}
	}

}
