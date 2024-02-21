package goorm.eagle7.stelligence.config;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
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

	@Test
	void whenNullReturnEmptyString() {
		//given
		String rawContent = null;

		//when
		String sanitizedContent = policyFactory.sanitize(rawContent);

		//then
		assertThat(sanitizedContent).isEmpty();
	}

	@Test
	@DisplayName("a 태그가 허용되어야 한다.")
	void aTagLink() {
		//given
		String rawContent = "<a target=\"_blank\" rel=\"noopener noreferrer nofollow\" href=\"http://www.stelligence.site/stars/77\">[[테스트 주도 개발]]</a>";

		String sanitizedContent = policyFactory.sanitize(rawContent);

		assertThat(sanitizedContent).isEqualTo(
			"<a target=\"_blank\" href=\"http://www.stelligence.site/stars/77\" rel=\"noopener noreferrer\">[[테스트 주도 개발]]</a>");
	}

	@Test
	@DisplayName("pre 허용")
	void authorizePre() {
		//given
		String rawContent = "<pre><code>hello(hello(hello()))</code></pre>";

		//when
		String sanitizedContent = policyFactory.sanitize(rawContent);

		//then
		assertThat(sanitizedContent).isEqualTo(rawContent);
	}

}