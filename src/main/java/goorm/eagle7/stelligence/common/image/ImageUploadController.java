package goorm.eagle7.stelligence.common.image;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.common.auth.memberinfo.Auth;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * 이미지 업로드 컨트롤러
 *
 * <p>PNG, JPEG, GIF 이미지 파일만 업로드할 수 있습니다.
 *
 * <p>이미지 파일은 MAX_IMAGE_MB 이하만 업로드할 수 있습니다.
 * MAX_IMAGE_MB는 클래스 내에 정의된 상수입니다.
 *
 * <p>어떠한 섹션에도 포함되지 않는 고아 이미지가 존재할 수 있습니다.
 * 불필요한 이미지를 청소하는 방법은 추후 고민해봐야 할 것 같습니다.
 */
@Tag(name = "이미지 업로드", description = "이미지 파일을 업로드합니다.")
@Slf4j
@RestController
@RequiredArgsConstructor
public class ImageUploadController {

	@Value("${aws.s3.bucketName}")
	private String bucketName;

	@Value("${aws.s3.bucketUrl}")
	private String bucketUrl;

	/**
	 * 이미지 파일 최대 크기 (MB)
	 */
	private static final long MAX_IMAGE_MB = 3;

	private static final long MAX_IMAGE_BYTE = MAX_IMAGE_MB * 1024 * 1024;

	/**
	 * 이미지 파일 컨텐츠 타입
	 */
	private static final List<String> IMAGE_CONTENT_TYPES = List.of(
		MediaType.IMAGE_JPEG_VALUE,
		MediaType.IMAGE_PNG_VALUE,
		MediaType.IMAGE_GIF_VALUE
	);

	private final S3Client s3Client;

	@Operation(
		summary = "이미지 업로드",
		description = "이미지를 업로드 합니다.  "
			+ "이미지 파일은 PNG, JPEG, GIF 형식만 업로드할 수 있으며, " + MAX_IMAGE_MB + "MB 이하만 업로드할 수 있습니다. "
			+ "업로드된 이미지의 URL을 반환합니다. "
			+ "Content-Type: image/jpeg, image/png, image/gif 만 지원합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "이미지 업로드 성공",
		useReturnTypeSchema = true
	)
	@PostMapping("/api/images/upload")
	public ResponseTemplate<String> uploadImage(
		HttpServletRequest request,
		@Auth MemberInfo memberInfo
	) {

		// 로그인한 사용자인지 확인
		if (memberInfo == null) {
			throw new BaseException("로그인한 사용자만 이미지를 업로드할 수 있습니다.");
		}

		// 이미지 파일인지 확인
		String contentType = request.getContentType();
		if (!isValidImageContentType(contentType)) {
			throw new BaseException("이미지 파일만 업로드할 수 있습니다.");
		}

		// 이미지 파일 크기 확인
		long contentLength = request.getContentLengthLong();
		if (contentLength > MAX_IMAGE_BYTE) {
			throw new BaseException("이미지 파일은 " + MAX_IMAGE_MB + "MB 이하만 업로드할 수 있습니다.");
		}

		// 이미지 업로드 - Stream 방식
		try {
			String fileName = UUID.randomUUID().toString();
			s3Client.putObject(
				PutObjectRequest.builder()
					.bucket(bucketName)
					.key(fileName)
					.contentType(contentType)
					.build(),
				RequestBody.fromInputStream(request.getInputStream(), contentLength)
			);

			// 이미지 업로드 성공
			return ResponseTemplate.ok(bucketUrl + fileName);

		} catch (IOException e) {
			log.error("이미지 업로드에 실패했습니다.", e);
			throw new BaseException("이미지 업로드에 실패했습니다.");
		}

	}

	/**
	 * 이미지 파일인지 확인
	 * @param contentType 컨텐츠 타입
	 * @return 이미지 파일이면 true
	 */
	private boolean isValidImageContentType(String contentType) {
		return IMAGE_CONTENT_TYPES.contains(contentType);
	}

}
