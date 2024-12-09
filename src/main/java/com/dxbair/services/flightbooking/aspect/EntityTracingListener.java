package com.dxbair.services.flightbooking.aspect;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.*;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
@DependsOn("entityManagerFactory")
public class EntityTracingListener
        implements PostLoadEventListener, PreInsertEventListener, PreUpdateEventListener, PreDeleteEventListener {

    private final Tracer tracer;
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public EntityTracingListener(@Lazy OpenTelemetry openTelemetry, EntityManagerFactory entityManagerFactory) {
        this.tracer = openTelemetry.getTracer(EntityTracingListener.class.getName());
        this.entityManagerFactory = entityManagerFactory;
    }

    @PostConstruct
    public void registerListeners() {
        SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);

        registry.getEventListenerGroup(EventType.POST_LOAD).appendListener(this);
        registry.getEventListenerGroup(EventType.PRE_INSERT).appendListener(this);
        registry.getEventListenerGroup(EventType.PRE_UPDATE).appendListener(this);
        registry.getEventListenerGroup(EventType.PRE_DELETE).appendListener(this);
    }

    @Override
    public void onPostLoad(PostLoadEvent event) {
        createEntitySpan("load", event.getEntity(), event.getPersister());
    }

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        createEntitySpan("insert", event.getEntity(), event.getPersister());
        return false;
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        createEntitySpan("update", event.getEntity(), event.getPersister());
        return false;
    }

    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
        createEntitySpan("delete", event.getEntity(), event.getPersister());
        return false;
    }

    private void createEntitySpan(String operation, Object entity, EntityPersister persister) {
        String entityName = persister.getEntityName();
        String methodName = operation;
        String className = entityName;

        Span parentSpan = Span.current();
        Span span = tracer.spanBuilder(className + "." + methodName)
                .setParent(Context.current().with(parentSpan))
                .setSpanKind(SpanKind.INTERNAL)
                .startSpan();

        // Add the required attributes to the span
        span.setAttribute("method", methodName);
        span.setAttribute("class", className);
        span.setAttribute("traceId", span.getSpanContext().getTraceId());
        span.setAttribute("spanId", span.getSpanContext().getSpanId());
        span.setAttribute("parentSpanId", parentSpan.getSpanContext().getSpanId());

        try (Scope scope = span.makeCurrent()) {
            // The entity operation happens in the surrounding context
        } finally {
            span.end();
        }
    }
}