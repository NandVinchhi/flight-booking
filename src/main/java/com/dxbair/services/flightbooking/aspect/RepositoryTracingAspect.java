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

@Aspect
@Component
public class RepositoryTracingAspect {

    private final Tracer tracer;

    @Autowired
    public RepositoryTracingAspect(@Lazy OpenTelemetry openTelemetry) {
        this.tracer = openTelemetry.getTracer(RepositoryTracingAspect.class.getName());
    }

    @Pointcut("within(@org.springframework.stereotype.Repository *)")
    public void springRepositoryPointcut() {
    }

    @Pointcut("execution(* org.springframework.data.repository.Repository+.*(..))")
    public void repositoryMethodPointcut() {
    }

    @Around("springRepositoryPointcut() || repositoryMethodPointcut()")
    public Object traceRepositoryMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();

        // Get all interfaces implemented by the proxy
        Class<?>[] interfaces = joinPoint.getTarget().getClass().getInterfaces();
        // Find the custom repository interface (not the Spring Data ones)
        String className = null;
        for (Class<?> iface : interfaces) {
            if (!iface.getName().startsWith("org.springframework")) {
                className = iface.getName();
                break;
            }
        }
        // Fallback to the declaring type if no custom interface is found
        if (className == null) {
            className = methodSignature.getDeclaringType().getName();
        }

        Span parentSpan = Span.current();
        Span span = tracer.spanBuilder("Repository." + className + "." + methodName)
                .setParent(Context.current().with(parentSpan))
                .setSpanKind(SpanKind.INTERNAL)
                .startSpan();

        // Add the required attributes to the span
        span.setAttribute("method", methodName);
        span.setAttribute("class", className);
        span.setAttribute("type", "repository");
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