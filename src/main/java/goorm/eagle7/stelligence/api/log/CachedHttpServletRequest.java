package goorm.eagle7.stelligence.api.log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.util.StreamUtils;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * HttpServletRequest의 Payload는 InputStream으로 단 한번만 읽어올 수 있습니다.
 * 따라서 로깅 등의 이유로 Payload를 여러번 읽어와야 하는 경우, 별도의 저장소에 저장해두고 사용해야 합니다.
 * <p>
 * CachedHttpServletRequest는 InputStream을 복사하여 저장해두고, 여러번 읽어올 수 있도록 합니다.
 */
public class CachedHttpServletRequest extends HttpServletRequestWrapper {

	private byte[] cachedPayload;

	/**
	 * 본문 요청을 읽고 이를 캐시된 byte 배열에 저장합니다.
	 *
	 * @param request
	 * @throws IOException
	 */
	public CachedHttpServletRequest(HttpServletRequest request) throws IOException {
		super(request);
		InputStream requestInputStream = request.getInputStream();
		this.cachedPayload = StreamUtils.copyToByteArray(requestInputStream);

	}

	/**
	 * getInputStream에서는 캐시된 byte 배열을 읽어오는 InputStream을 반환합니다.
	 *
	 * @return
	 * @throws IOException
	 */
	@Override
	public ServletInputStream getInputStream() throws IOException {
		return new CachedServletInputStream(this.cachedPayload);
	}

	/**
	 * 이 메소드는 요청 본문을 문자 데이터로 읽는 데 사용할 수 있는 BufferedReader 객체를 반환합니다.
	 *
	 * @return
	 * @throws IOException
	 */
	@Override
	public BufferedReader getReader() throws IOException {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedPayload);
		return new BufferedReader(new InputStreamReader(byteArrayInputStream));
	}
}
