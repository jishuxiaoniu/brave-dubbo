package brave.internal.recorder;

import brave.Span;
import brave.internal.HexCodec;
import brave.internal.Nullable;
import brave.propagation.TraceContext;
import lombok.extern.slf4j.Slf4j;
import zipkin2.Endpoint;

@Slf4j
final class MutableSpan {
    final zipkin2.Span.Builder span;
    boolean finished;
    long timestamp;
    TraceContext context;

    // Since this is not exposed, this class could be refactored later as needed to act in a pool
    // to reduce GC churn. This would involve calling span.clear and resetting the fields below.
    MutableSpan(TraceContext context, Endpoint localEndpoint) {
        this.span = zipkin2.Span.newBuilder()
                .traceId(context.traceIdString())
                .parentId(context.parentId() != null ? HexCodec.toLowerHex(context.parentId()) : null)
                .id(HexCodec.toLowerHex(context.spanId()))
                .debug(context.debug())
                .shared(context.shared())
                .localEndpoint(localEndpoint);
        this.context = context;
        finished = false;
    }

    synchronized MutableSpan start(long timestamp) {
        span.timestamp(this.timestamp = timestamp);
        log.debug("span.start-timestamp={}, span.id={} span.parentId={}, span.traceId={}", this.timestamp, HexCodec.toLowerHex(context.spanId()), context.parentId() != null ? HexCodec.toLowerHex(context.parentId()) : null, context.traceIdString());
        return this;
    }

    synchronized MutableSpan name(String name) {
        span.name(name);
        return this;
    }

    synchronized MutableSpan kind(Span.Kind kind) {
        try {
            span.kind(zipkin2.Span.Kind.valueOf(kind.name()));
        } catch (IllegalArgumentException e) {
            // TODO: log
        }
        return this;
    }

    synchronized MutableSpan annotate(long timestamp, String value) {
        if ("cs".equals(value)) {
            span.kind(zipkin2.Span.Kind.CLIENT).timestamp(this.timestamp = timestamp);
        } else if ("sr".equals(value)) {
            span.kind(zipkin2.Span.Kind.SERVER).timestamp(this.timestamp = timestamp);
        } else if ("cr".equals(value)) {
            span.kind(zipkin2.Span.Kind.CLIENT);
            finish(timestamp);
        } else if ("ss".equals(value)) {
            span.kind(zipkin2.Span.Kind.SERVER);
            finish(timestamp);
        } else {
            span.addAnnotation(timestamp, value);
        }
        return this;
    }

    synchronized MutableSpan tag(String key, String value) {
        span.putTag(key, value);
        return this;
    }

    synchronized MutableSpan remoteEndpoint(Endpoint remoteEndpoint) {
        span.remoteEndpoint(remoteEndpoint);
        return this;
    }

    /**
     * Completes and reports the span
     */
    synchronized MutableSpan finish(@Nullable Long finishTimestamp) {
        if (finished) return this;
        finished = true;

        if (timestamp != 0 && finishTimestamp != null) {
            span.duration(Math.max(finishTimestamp - timestamp, 1));
            log.debug("span.end-timestamp={}, span.id={} span.parentId={}, span.traceId={}, duration={}", finishTimestamp, HexCodec.toLowerHex(context.spanId()), context.parentId() != null ? HexCodec.toLowerHex(context.parentId()) : null, context.traceIdString(), finishTimestamp - timestamp);
        }
        return this;
    }

    synchronized zipkin2.Span toSpan() {
        return span.build();
    }
}