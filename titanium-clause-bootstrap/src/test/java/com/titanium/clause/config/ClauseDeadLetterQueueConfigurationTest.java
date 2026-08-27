package com.titanium.clause.config;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.config.Configuration;
import org.axonframework.config.Configurer;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.deadletter.jpa.JpaSequencedDeadLetterQueue;
import org.axonframework.messaging.deadletter.SequencedDeadLetterQueue;
import org.axonframework.serialization.Serializer;
import org.axonframework.springboot.EventProcessorProperties;
import org.axonframework.springboot.util.DeadLetterQueueProviderConfigurerModule;
import org.junit.jupiter.api.Test;

class ClauseDeadLetterQueueConfigurationTest {

    private static final String PROCESSING_GROUP = "clause-query-group";

    @Test
    void shouldProvideJpaDeadLetterQueueForEnabledClauseProcessor() {
        EventProcessorProperties properties = enabledProperties(PROCESSING_GROUP);
        ClauseDeadLetterQueueConfiguration configuration = new ClauseDeadLetterQueueConfiguration();
        DeadLetterQueueProviderConfigurerModule module = configuration
                .clauseDeadLetterQueueProviderConfigurerModule(
                        properties,
                        mock(EntityManagerProvider.class),
                        mock(TransactionManager.class),
                        mock(Serializer.class),
                        mock(Serializer.class));
        Configurer configurer = mock(Configurer.class);
        EventProcessingConfigurer eventProcessingConfigurer = mock(EventProcessingConfigurer.class);
        AtomicReference<Function<String, Function<Configuration,
                SequencedDeadLetterQueue<EventMessage<?>>>>> providerReference = new AtomicReference<>();
        when(configurer.eventProcessing()).thenReturn(eventProcessingConfigurer);
        when(eventProcessingConfigurer.registerDeadLetterQueueProvider(any())).thenAnswer(invocation -> {
            providerReference.set(invocation.getArgument(0));
            return eventProcessingConfigurer;
        });

        module.configureModule(configurer);

        Function<String, Function<Configuration, SequencedDeadLetterQueue<EventMessage<?>>>> provider =
                providerReference.get();
        assertNotNull(provider);
        Function<Configuration, SequencedDeadLetterQueue<EventMessage<?>>> queueFactory =
                provider.apply(PROCESSING_GROUP);
        assertNotNull(queueFactory);
        assertInstanceOf(JpaSequencedDeadLetterQueue.class, queueFactory.apply(mock(Configuration.class)));
        assertNull(provider.apply("disabled-query-group"));
    }

    private EventProcessorProperties enabledProperties(String processingGroup) {
        EventProcessorProperties properties = new EventProcessorProperties();
        EventProcessorProperties.Dlq dlq = new EventProcessorProperties.Dlq();
        dlq.setEnabled(true);
        EventProcessorProperties.ProcessorSettings settings = new EventProcessorProperties.ProcessorSettings();
        settings.setDlq(dlq);
        properties.getProcessors().put(processingGroup, settings);
        return properties;
    }
}
