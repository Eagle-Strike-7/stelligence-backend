package goorm.eagle7.stelligence.common.auth.memberinfo;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER}) // 파라미터 타입에만 사용 가능
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 유지
@Documented // javadoc에도 표시
public @interface Auth {

}
