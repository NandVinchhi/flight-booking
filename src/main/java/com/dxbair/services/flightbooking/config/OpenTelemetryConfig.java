package com.dxbair.services.flightbooking.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTelemetryConfig {

    private static volatile OpenTelemetry openTelemetry;

    @Bean
    public OpenTelemetry openTelemetry() {
        if (openTelemetry == null) {
            synchronized (OpenTelemetryConfig.class) {
                if (openTelemetry == null) {
                    Resource resource = Resource.getDefault()
                            .merge(Resource.create(Attributes.builder()
                                    .put("service.name", "flight-booking-service")
                                    .build()));

                    SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
                            .addSpanProcessor(SimpleSpanProcessor.create(LoggingSpanExporter.create()))
                            .setResource(resource)
                            .build();

                    openTelemetry = OpenTelemetrySdk.builder()
                            .setTracerProvider(sdkTracerProvider)
                            .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                            .build();
                }
            }
        }
        return openTelemetry;
    }
} 