package goorm.eagle7.stelligence.common.image;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;

import goorm.eagle7.stelligence.api.ResponseTemplate;
import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.common.auth.memberinfo.MemberInfo;
import goorm.eagle7.stelligence.domain.member.model.Role;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@ExtendWith(MockitoExtension.class)
class ImageUploadControllerTest {

	@Mock
	S3Client s3Client;

	@InjectMocks
	ImageUploadController imageUploadController;

	@Test
	@DisplayName("이미지 파일 업로드 - 성공")
	void uploadSuccess() throws IOException {
		//given
		HttpServletRequest request = mock(HttpServletRequest.class);
		MemberInfo memberInfo = MemberInfo.of(1L, Role.USER);
		UUID testUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

		// ReflectionTestUtils를 사용하여 private 필드에 값을 주입
		ReflectionTestUtils.setField(imageUploadController, "bucketName", "test-bucket");
		ReflectionTestUtils.setField(imageUploadController, "bucketUrl", "http://test-bucket.com/");

		try (MockedStatic<UUID> uuid = mockStatic(UUID.class)) {
			//when
			when(request.getContentType()).thenReturn(MediaType.IMAGE_JPEG_VALUE);
			when(request.getContentLengthLong()).thenReturn(1024L); // 1KB
			when(request.getInputStream()).thenReturn(mock(ServletInputStream.class));
			uuid.when(UUID::randomUUID).thenReturn(testUUID);

			ResponseTemplate<String> response = imageUploadController.uploadImage(request, memberInfo);
			//then
			assertThat(response.isSuccess()).isTrue();
			assertThat(response.getResults()).isEqualTo(
				"http://test-bucket.com/00000000-0000-0000-0000-000000000000");
			verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
		}
	}

	@Test
	@DisplayName("이미지 파일 업로드 - 실패: 로그인 하지 않은 사용자")
	void uploadFail_Unauthorized() throws IOException {
		//given
		HttpServletRequest request = mock(HttpServletRequest.class);

		//then
		assertThatThrownBy(() ->
			imageUploadController.uploadImage(request, null))
			.isInstanceOf(BaseException.class)
			.hasMessage("로그인한 사용자만 이미지를 업로드할 수 있습니다.");

		verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
	}

	@Test
	@DisplayName("이미지 파일 업로드 - 실패: 이미지 파일이 아닌 경우")
	void uploadFail_NotImage() throws IOException {
		//given
		HttpServletRequest request = mock(HttpServletRequest.class);
		MemberInfo memberInfo = MemberInfo.of(1L, Role.USER);

		//when
		when(request.getContentType()).thenReturn(MediaType.APPLICATION_JSON_VALUE);

		//then
		assertThatThrownBy(() ->
			imageUploadController.uploadImage(request, memberInfo))
			.isInstanceOf(BaseException.class)
			.hasMessage("이미지 파일만 업로드할 수 있습니다.");

		verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
	}

	@Test
	@DisplayName("이미지 파일 업로드 - 실패: 이미지 파일 크기 초과")
	void uploadFail_SizeExceed() throws IOException {
		//given
		HttpServletRequest request = mock(HttpServletRequest.class);
		MemberInfo memberInfo = MemberInfo.of(1L, Role.USER);

		//when
		when(request.getContentType()).thenReturn(MediaType.IMAGE_JPEG_VALUE);
		when(request.getContentLengthLong()).thenReturn(1024L * 1024 * 10); // 10MB

		//then
		assertThatThrownBy(() ->
			imageUploadController.uploadImage(request, memberInfo))
			.isInstanceOf(BaseException.class)
			.hasMessage("이미지 파일은 3MB 이하만 업로드할 수 있습니다.");

		verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
	}

}