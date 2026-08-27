package com.titanium.clause.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.util.List;

import org.axonframework.config.ProcessingGroup;
import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

import com.titanium.clause.query.handler.projection.ClauseProjectionEventHandler;

class AxonProcessingGroupConfigurationTest {

    @Test
    void configuresDeadLetterQueueForTheProjectionProcessingGroup() throws IOException {
        String processingGroup = ClauseProjectionEventHandler.class.getAnnotation(ProcessingGroup.class).value();
        List<PropertySource<?>> sources = new YamlPropertySourceLoader()
                .load("application", new ClassPathResource("application.yml"));

        Object dlqEnabled = property(sources,
                "axon.eventhandling.processors." + processingGroup + ".dlq.enabled");
        Object batchSize = property(sources,
                "axon.eventhandling.processors." + processingGroup + ".batchSize");
        Object staleSource = property(sources,
                "axon.eventhandling.processors.clauseEventProcessor.source");

        assertEquals("clause-query-group", processingGroup);
        assertEquals(Boolean.TRUE, dlqEnabled);
        assertEquals(1, batchSize);
        assertNull(staleSource);
    }

    private Object property(List<PropertySource<?>> sources, String key) {
        return sources.stream()
                .map(source -> source.getProperty(key))
                .filter(value -> value != null)
                .findFirst()
                .orElse(null);
    }
}
