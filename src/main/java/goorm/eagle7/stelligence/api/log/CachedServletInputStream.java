package goorm.eagle7.stelligence.api.log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import lombok.extern.slf4j.Slf4j;

/**
 * 이 클래스에서는 먼저 캐시된 바디를 사용하여 새 생성자를 만들고 이 생성자에서 새 ByteArrayInputStream 개체를 반환합니다.
 * 우리는 다른 재정의된 메소드에서 이 InputStream 객체를 사용합니다.
 */
@Slf4j
public class CachedServletInputStream extends ServletInputStream {

	private InputStream cachedInputStream;

	public CachedServletInputStream(byte[] cachedBody) {
		this.cachedInputStream = new ByteArrayInputStream(cachedBody);
	}

	/**
	 * 스트림의 모든 데이터를 읽었을 때 true를 반환하고,
	 * 그렇지 않으면 false를 반환합니다 .
	 *
	 * @return
	 */
	@Override
	public boolean isFinished() {
		try {
			return cachedInputStream.available() == 0;
		} catch (IOException exp) {
			log.error(exp.getMessage());
		}
		return false;
	}

	/**
	 * isReady () 메서드는 차단 없이 데이터를 읽을 수 있는지 확인한 후
	 * 데이터를 읽는 데 사용됩니다
	 *
	 * @return
	 */
	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void setReadListener(ReadListener listener) {
		throw new UnsupportedOperationException();
	}

	/**
	 * read () 메서드는 캐시된 InputStream 개체 에서 읽습니다
	 *
	 * @return
	 * @throws IOException
	 */
	@Override
	public int read() throws IOException {
		return cachedInputStream.read();
	}
}
