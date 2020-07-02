package com.trade.trade.api.batches;

import com.trade.trade.api.batches.processors.UserSnapshotItemProcessor;
import com.trade.trade.api.repositories.*;
import com.trade.trade.api.repositories.AssetRepository;
import com.trade.trade.api.services.AssetValuationService;
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
public class BatchConfiguration {
    public final JobBuilderFactory jobBuilderFactory;
    public final StepBuilderFactory stepBuilderFactory;

    private final AssetRepository assetRepository;
    private final AssetValuationRepository assetValuationRepository;
    private final AssetValuationService assetValuationService;
    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final UserSnapshotRepository userSnapshotRepository;

    public BatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
                              AssetRepository assetRepository, AssetValuationRepository assetValuationRepository,
                              AssetValuationService assetValuationService, OrderRepository orderRepository,
                              TransactionRepository transactionRepository, UserRepository userRepository,
                              UserSnapshotRepository userSnapshotRepository) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.assetRepository = assetRepository;
        this.assetValuationRepository = assetValuationRepository;
        this.assetValuationService = assetValuationService;
        this.orderRepository = orderRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.userSnapshotRepository = userSnapshotRepository;
    }

    @Bean
    public RepositoryItemReader<User> reader() {
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
    public UserSnapshotItemProcessor processor() {
        return new UserSnapshotItemProcessor(assetRepository, assetValuationRepository, assetValuationService, orderRepository, transactionRepository);
    }

    @Bean
    public RepositoryItemWriter<UserSnapshot> writer() {
        return new RepositoryItemWriterBuilder<UserSnapshot>()
                .repository(userSnapshotRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public Job userSnapshotJob(Step processStep) {
        return jobBuilderFactory.get("userSnapshotJob")
                .incrementer(new RunIdIncrementer())
                .flow(processStep)
                .end()
                .build();
    }

    @Bean
    public Step processStep(RepositoryItemWriter<UserSnapshot> writer) {
        return stepBuilderFactory.get("processStep")
                .<User, UserSnapshot> chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }
}
