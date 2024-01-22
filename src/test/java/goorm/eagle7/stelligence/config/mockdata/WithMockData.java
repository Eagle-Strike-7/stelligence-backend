package goorm.eagle7.stelligence.config.mockdata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
/**
 * WithMockData 애노테이션
 * @SpringBootTest가 적용된 테스트 클래스에 함께 작성하면
 * dummy.sql의 데이터들이 들어간 상태로 테스트가 수행됩니다.
*/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import(MockDataConfig.class)
public @interface WithMockData {
}
