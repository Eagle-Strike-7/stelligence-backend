package goorm.eagle7.stelligence.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;

/**
 * <h2>RequestContextHolder를 사용하기 위해 RequestContextListener를 Bean으로 등록</h2>
 * <p>- RequestContextHolder는 현재 스레드에 바인딩된 HttpServletRequest, HttpServletResponse, HttpSession을 제공하는 유틸리티 클래스</p>
 * <p>- 웹 요청 외부에서 요청 속성을 참조하거나, 원래 스레드 외부에서 요청을 처리하려고 현재 요청의 HttpServletRequest를 가져오려고 시도할 때 DispatcherServlet
 * 외부에서도 현재 웹 요청에 접근할 수 있도록 현재 요청을 노출함.</p>
 * @see org.springframework.web.context.request.RequestContextHolder
 */
@Configuration
public class RequestContextListenerConfig {
	@Bean
	public RequestContextListener requestContextListener() {
		return new RequestContextListener();
	}

}
