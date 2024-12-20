package com.dxbair.services.flightbooking.aspect;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

@Aspect
@Component
public class TracingAspect {

    private final Tracer tracer;

    @Autowired
    public TracingAspect(@Lazy OpenTelemetry openTelemetry) {
        this.tracer = openTelemetry.getTracer(TracingAspect.class.getName());
    }

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void controllerPointcut() {
    }

    @Pointcut("within(com.dxbair.services.flightbooking..*) && !within(com.dxbair.services.flightbooking.config..*) && !within(com.dxbair.services.flightbooking.aspect..*)")
    public void applicationPointcut() {
    }

    @Around("controllerPointcut() || applicationPointcut()")
    public Object traceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();
        String className = methodSignature.getDeclaringType().getName();

        SpanKind spanKind = joinPoint.getTarget().getClass().isAnnotationPresent(RestController.class)
                ? SpanKind.SERVER
                : SpanKind.INTERNAL;

        Span parentSpan = Span.current();
        Span span = tracer.spanBuilder(className + "." + methodName)
                .setParent(Context.current().with(parentSpan))
                .setSpanKind(spanKind)
                .startSpan();

        // Add the required attributes to the span
        span.setAttribute("method", methodName);
        span.setAttribute("class", className);
        span.setAttribute("traceId", span.getSpanContext().getTraceId());
        span.setAttribute("spanId", span.getSpanContext().getSpanId());
        span.setAttribute("parentSpanId", parentSpan.getSpanContext().getSpanId());

        try (Scope scope = span.makeCurrent()) {
            return joinPoint.proceed();
        } catch (Throwable t) {
            span.recordException(t);
            throw t;
        } finally {
            span.end();
        }
    }
}