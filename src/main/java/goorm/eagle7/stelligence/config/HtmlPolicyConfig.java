package goorm.eagle7.stelligence.config;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 사용자로부터 들어온 HTML 태그 값을 정화(sanitize)하기 위한
 * PolicyFactory를 빈으로 등록하는 설정 클래스입니다.
 *
 * <p>OWASP Java HTML Sanitizer를 사용합니다.
 */
@Configuration
public class HtmlPolicyConfig {
	@Bean
	public PolicyFactory htmlPolicyBuilder() {
		return new HtmlPolicyBuilder()
			.allowUrlProtocols("http", "https") // URL로 들어오는 값들에 대해 허용할 프로토콜 지정
			.allowCommonBlockElements() //"p", "div", "h1", "h2", "h3", "h4", "h5", "h6", "ul", "ol", "li", "blockquote",
			.allowCommonInlineFormattingElements() //"b", "i", "em", "strong", "a", "br", "img", "span", "hr", "code", "pre"
			.allowElements("img")
			.allowAttributes("src", "alt").onElements("img")
			.toFactory();
	}
}
