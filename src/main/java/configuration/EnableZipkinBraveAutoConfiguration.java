package configuration;

import brave.spring.webmvc.TracingHandlerInterceptor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({TraceAutoConfiguration.class, TracingHandlerInterceptor.class})
public @interface EnableZipkinBraveAutoConfiguration {
}
