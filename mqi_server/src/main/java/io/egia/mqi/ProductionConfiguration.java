package io.egia.mqi;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
 
@Configuration
@EnableAutoConfiguration
@ComponentScan("io.egia.mqi.service")
@EnableJpaRepositories(basePackages = {"io.egia.mqi.domain"})
@EnableTransactionManagement
public class ProductionConfiguration {
}