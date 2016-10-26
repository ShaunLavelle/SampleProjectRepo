package uk.co.sample.project;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan("uk.co.sample.project.config") // Only scan the config packages, these in turn will scan the rest
//of the app as required
@EnableAutoConfiguration(exclude = { JacksonAutoConfiguration.class })
@EnableAsync
public class SampleProjectApplication {

	public static void main(String[] args) {
		 ApplicationContext context = new SpringApplicationBuilder(SampleProjectApplication.class).run(args);
	}
}