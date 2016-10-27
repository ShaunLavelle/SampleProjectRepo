package uk.co.sample.project.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import freemarker.cache.ConditionalTemplateConfigurationFactory;
import freemarker.cache.FileExtensionMatcher;
import freemarker.core.HTMLOutputFormat;
import freemarker.core.TemplateConfiguration;
import freemarker.template.TemplateException;
import uk.co.sample.project.SampleProjectApplication;

@Configuration
@ComponentScan(basePackages = { "uk.co.sample.project" })
public class FrontEndConfig extends WebMvcConfigurerAdapter {

	@Resource
	ApplicationContext applicationContext;

	@Resource
	ServletContext servletContext;

	@Resource
	Environment environment;

	@Bean
	public FreeMarkerConfigurer configFreeMarkerConfigurer() throws IOException, TemplateException {
		FreeMarkerConfigurer freemarkerConfig = new FreeMarkerConfigurer();
		Properties settings = new Properties();
		settings.put("default_encoding", "UTF-8");
		// properties globally available to all freemarker views
		Map<String, Object> globalModel = new HashMap<>();
		globalModel.put("context_path", environment.getRequiredProperty("server.contextPath"));
		globalModel.put("app_version", getApplicationVersion());

		freemarkerConfig.setFreemarkerVariables(globalModel);
		freemarkerConfig.setFreemarkerSettings(settings);
		freemarkerConfig.setTemplateLoaderPath("classpath:/templates");
		freemarkerConfig.afterPropertiesSet();
		TemplateConfiguration templateConfiguration = new TemplateConfiguration();
		templateConfiguration.setOutputFormat(HTMLOutputFormat.INSTANCE);
		freemarkerConfig.getConfiguration().setTemplateConfigurations(
				new ConditionalTemplateConfigurationFactory(new FileExtensionMatcher("ftl"), templateConfiguration));
		return freemarkerConfig;
	}

	@Bean
	public FreeMarkerViewResolver viewResolver() {
		FreeMarkerViewResolver freeMarkerViewResolver = new FreeMarkerViewResolver();
		freeMarkerViewResolver.setCache(true);
		freeMarkerViewResolver.setPrefix("");
		freeMarkerViewResolver.setSuffix(".ftl");
		return freeMarkerViewResolver;
	}

	private String getApplicationVersion() {
		String version = SampleProjectApplication.class.getPackage().getImplementationVersion();
		return version == null ? "Development" : version;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
	}
}
