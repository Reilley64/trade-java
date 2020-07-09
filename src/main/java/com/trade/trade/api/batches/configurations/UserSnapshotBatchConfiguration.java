package com.trade.trade.api.batches.configurations;

import com.trade.trade.api.batches.processors.UserSnapshotItemProcessor;
import com.trade.trade.api.repositories.*;
import com.trade.trade.api.services.UserSnapshotService;
import com.trade.trade.domain.models.User;
import com.trade.trade.domain.models.UserSnapshot;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.HashMap;

@Configuration
@EnableBatchProcessing
public class UserSnapshotBatchConfiguration {
    public final JobBuilderFactory jobBuilderFactory;
    public final StepBuilderFactory stepBuilderFactory;

    private final UserRepository userRepository;
    private final UserSnapshotRepository userSnapshotRepository;
    private final UserSnapshotService userSnapshotService;

    public UserSnapshotBatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
                                          UserRepository userRepository, UserSnapshotRepository userSnapshotRepository,
                                          UserSnapshotService userSnapshotService) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.userRepository = userRepository;
        this.userSnapshotRepository = userSnapshotRepository;
        this.userSnapshotService = userSnapshotService;
    }

    @Bean
    public RepositoryItemReader<User> userSnapshotBatchReader() {
        final HashMap<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("id", Sort.Direction.ASC);
        return new RepositoryItemReaderBuilder<User>()
                .name("User item reader")
                .repository(userRepository)
                .methodName("findAll")
                .pageSize(10)
                .sorts(sorts)
                .build();
    }

    @Bean
    public UserSnapshotItemProcessor userSnapshotBatchProcessor() {
        return new UserSnapshotItemProcessor(userSnapshotService);
    }

    @Bean
    public RepositoryItemWriter<UserSnapshot> userSnapshotBatchWriter() {
        return new RepositoryItemWriterBuilder<UserSnapshot>()
                .repository(userSnapshotRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public Job userSnapshotBatchJob(Step userSnapshotBatchChunk) {
        return jobBuilderFactory.get("userSnapshotBatchJob")
                .incrementer(new RunIdIncrementer())
                .flow(userSnapshotBatchChunk)
                .end()
                .build();
    }

    @Bean
    public Step userSnapshotBatchChunk(RepositoryItemWriter<UserSnapshot> userSnapshotBatchWriter) {
        return stepBuilderFactory.get("userSnapshotBatchChunk")
                .<User, UserSnapshot> chunk(10)
                .reader(userSnapshotBatchReader())
                .processor(userSnapshotBatchProcessor())
                .writer(userSnapshotBatchWriter)
                .build();
    }
}
