package com.dxbair.services.flightbooking.aspect;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TracingAspect {

    private final Tracer tracer;

    public TracingAspect(OpenTelemetry openTelemetry) {
        this.tracer = openTelemetry.getTracer(TracingAspect.class.getName());
    }

    @Around("within(com.dxbair.services.flightbooking..*) && " +
            "!within(com.dxbair.services.flightbooking.config..*) && " +
            "!within(com.dxbair.services.flightbooking.aspect..*)")
    public Object traceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();
        String spanName = className + "." + methodName;

        Span span = tracer.spanBuilder(spanName)
                .setParent(Context.current())
                .setAttribute("class", className)
                .setAttribute("method", methodName)
                .startSpan();

        try {
            return joinPoint.proceed();
        } catch (Throwable t) {
            span.recordException(t);
            throw t;
        } finally {
            span.end();
        }
    }
} 