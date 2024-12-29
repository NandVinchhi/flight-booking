package com.dxbair.services.flightbooking.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

@Configuration
public class OpenTelemetryConfig {

    @Bean
    public OpenTelemetry openTelemetry() {
        // Create logs directory if it doesn't exist
        File logsDir = new File("logs");
        if (!logsDir.exists()) {
            logsDir.mkdirs();
        }

        SpanExporter fileExporter = new CustomFileSpanExporter("logs/traces.log");

        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(SimpleSpanProcessor.create(fileExporter))
                .build();

        return OpenTelemetrySdk.builder()
                .setTracerProvider(sdkTracerProvider)
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .build();
    }

    private static class CustomFileSpanExporter implements SpanExporter {
        private final String filePath;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        private final ObjectMapper objectMapper = new ObjectMapper();

        public CustomFileSpanExporter(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public CompletableResultCode export(Collection<SpanData> spans) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
                for (SpanData span : spans) {
                    ObjectNode jsonSpan = objectMapper.createObjectNode();

                    // Add timestamp
                    jsonSpan.put("timestamp", dateFormat.format(new Date(span.getStartEpochNanos() / 1_000_000)));

                    // Add span information
                    jsonSpan.put("traceId", span.getTraceId());
                    jsonSpan.put("spanId", span.getSpanId());
                    jsonSpan.put("parentSpanId", span.getParentSpanId());
                    jsonSpan.put("name", span.getName());
                    jsonSpan.put("kind", span.getKind().toString());

                    // Add attributes
                    ObjectNode attributes = jsonSpan.putObject("attributes");
                    span.getAttributes().forEach((key, value) -> attributes.put(key.getKey(), value.toString()));

                    // Add events
                    ObjectNode events = jsonSpan.putObject("events");
                    span.getEvents().forEach(event -> {
                        String eventTime = dateFormat.format(new Date(event.getEpochNanos() / 1_000_000));
                        events.put(event.getName(), eventTime);
                    });

                    // Write the JSON object
                    writer.println(objectMapper.writeValueAsString(jsonSpan));
                }
            } catch (IOException e) {
                e.printStackTrace();
                return CompletableResultCode.ofFailure();
            }
            return CompletableResultCode.ofSuccess();
        }

        @Override
        public CompletableResultCode flush() {
            return CompletableResultCode.ofSuccess();
        }

        @Override
        public CompletableResultCode shutdown() {
            return CompletableResultCode.ofSuccess();
        }
    }
}