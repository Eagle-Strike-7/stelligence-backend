package goorm.eagle7.stelligence.config;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.owasp.html.PolicyFactory;

/**
 * Config 클래스이지만, 이곳에서 생성되는 빈에 대해 테스트할 위치가 마땅치 않아 이곳에서 수행합니다.
 */
class HtmlPolicyConfigTest {

	HtmlPolicyConfig htmlPolicyConfig = new HtmlPolicyConfig();
	PolicyFactory policyFactory = htmlPolicyConfig.htmlPolicyBuilder();

	@Test
	void noSanitizeForNormalImgTag() {
		//given
		String rawContent = "<h1>title</h1><img src=\"http://example.com/image.jpg\"/>";

		//when
		String sanitizedContent = policyFactory.sanitize(rawContent);

		//then
		assertThat(sanitizedContent).isEqualTo("<h1>title</h1><img src=\"http://example.com/image.jpg\" />");
	}

	@Test
	void sanitizeImgWithScript() {
		//given
		String rawContent = "<p>hello</p><img src=\"javascript:악의적인코드()\"/>";

		//when
		String sanitizedContent = policyFactory.sanitize(rawContent);

		//then
		assertThat(sanitizedContent).isEqualTo("<p>hello</p>");
	}

}