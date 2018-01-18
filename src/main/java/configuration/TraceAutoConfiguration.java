package configuration;

import brave.Tracing;
import brave.dubbo.DubboTracing;
import brave.http.HttpAdapter;
import brave.http.HttpClientParser;
import brave.http.HttpServerParser;
import brave.http.HttpTracing;
import brave.sampler.Sampler;
import brave.spring.webmvc.TracingHandlerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;

@EnableConfigurationProperties({TraceProperties.class})
@Slf4j
public class TraceAutoConfiguration extends WebMvcConfigurerAdapter {
    @Autowired
    private TraceProperties traceProperties;
    @Autowired
    private TracingHandlerInterceptor tracingHandlerInterceptor;

    @Bean
    public Sender sender() {
        return OkHttpSender.create(traceProperties.getZipkin());
    }

    @Bean
    HttpTracing httpTracing(Tracing tracing) {
        HttpTracing httpTracing = HttpTracing.create(tracing);
        return httpTracing.toBuilder()
                .serverParser(new HttpServerParser() {
                    @Override
                    public <Req> String spanName(HttpAdapter<Req, ?> adapter, Req req) {
                        return adapter.path(req);
                    }
                }).clientParser(new HttpClientParser() {
                    @Override
                    public <Req> String spanName(HttpAdapter<Req, ?> adapter, Req req) {
                        return adapter.path(req);
                    }
                }).build();
    }

    @Bean
    public AsyncReporter<Span> spanReporter() {
        return AsyncReporter.create(sender());
    }

    @Bean
    public Tracing tracing() {
        Tracing tracing = Tracing.newBuilder()
                .localServiceName(traceProperties.getServerName())
                .spanReporter(spanReporter())
                .sampler(Sampler.create(traceProperties.getRate())).build();
        log.info("zipkin-info: url={}, serverName={}, rate={}", traceProperties.getZipkin(), traceProperties.getServerName(), traceProperties.getRate());
        return tracing;
    }

    @Bean
    DubboTracing dubboTracing(Tracing tracing) {
        return DubboTracing.create(tracing);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tracingHandlerInterceptor);
    }
}