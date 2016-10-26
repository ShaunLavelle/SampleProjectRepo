package uk.co.sample.project.config;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import org.springframework.transaction.support.TransactionTemplate;

import com.zaxxer.hikari.HikariDataSource;

import uk.co.sample.project.exception.SampleProjectException;

import org.hibernate.SessionFactory;

@Configuration
@EnableTransactionManagement
public class DBConfiguration implements TransactionManagementConfigurer {

  @Resource
  Environment environment;

  @Bean
  @Primary
  public DataSource dataSource() {
    HikariDataSource ds = new HikariDataSource();
    ds.setDriverClassName(environment.getRequiredProperty("spring.datasource.driver-class-name"));
    ds.setJdbcUrl(environment.getRequiredProperty("spring.datasource.url"));
    ds.setUsername(environment.getRequiredProperty("spring.datasource.username"));
    ds.setPassword(environment.getRequiredProperty("spring.datasource.password"));
    ds.setMaximumPoolSize(Integer.parseInt(environment.getRequiredProperty("spring.datasource.max-active")));
    return ds;
  }

  @Bean(name = "SAMPLE_PROJECT_JDBC_TEMPLATE")
  public JdbcTemplate jdbcTemplate() {
    return new JdbcTemplate(dataSource());
  }

  @Bean
  public SessionFactory sessionFactory() {
    try {
      LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
      localSessionFactoryBean.setDataSource(dataSource());
      Properties hibernateProperties = new Properties();
      hibernateProperties.setProperty("hibernate.dialect",
                                      environment.getRequiredProperty("spring.jpa.database-platform"));
      hibernateProperties.setProperty("hibernate.transaction.flush_before_completion", "true");
      hibernateProperties.setProperty("hibernate.hbm2ddl.auto",
                                      environment.getRequiredProperty("spring.jpa.hibernate.ddl-auto"));
      hibernateProperties.setProperty("show_sql", "true");
      hibernateProperties.setProperty("hibernate.order_inserts", "true");
      hibernateProperties.setProperty("hibernate.order_updates", "true");
      hibernateProperties.setProperty("hibernate.jdbc.batch_sizes", "50");
      hibernateProperties.setProperty("hibernate.default_batch_fetch_size", "50");
      hibernateProperties.setProperty("hibernate.max_fetch_depth", "3");
      hibernateProperties.setProperty("hibernate.cache.use_second_level_cache", "false");
      localSessionFactoryBean.setHibernateProperties(hibernateProperties);
      localSessionFactoryBean.setPackagesToScan("uk.co.sample.project.domain");
      localSessionFactoryBean.setAnnotatedPackages("uk.co.sample.project.domain");
      localSessionFactoryBean.afterPropertiesSet();
      return localSessionFactoryBean.getObject();
    }
    catch (IOException aEx) {
      throw new SampleProjectException("Unable to create the session factory: " + aEx.getMessage());
    }
  }

  @Bean
  public HibernateTransactionManager transactionManager() {
    HibernateTransactionManager txManager = new HibernateTransactionManager();
    txManager.setSessionFactory(sessionFactory());
    txManager.setDefaultTimeout(90);
    txManager.setRollbackOnCommitFailure(true);
    return txManager;
  }

  @Bean
  public TransactionTemplate transactionTemplate() {
    TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager());
    transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    transactionTemplate.setTimeout(5000);
    return transactionTemplate;
  }

  @Override
  public PlatformTransactionManager annotationDrivenTransactionManager() {
    return transactionManager();
  }
}
