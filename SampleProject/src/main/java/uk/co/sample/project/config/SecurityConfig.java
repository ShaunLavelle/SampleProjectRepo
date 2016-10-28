package uk.co.sample.project.config;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.WebExpressionVoter;

import uk.co.sample.project.role.RoleVoter;
import uk.co.sample.project.security.SampleProjectAuthenticationProvider;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Resource
  SampleProjectAuthenticationProvider sampleProjectAuthenticationProvider;

  @Resource
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(sampleProjectAuthenticationProvider);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .authorizeRequests()
      .antMatchers("/login", "/changePassword", "/checkAuth", "/monitoring").permitAll()
      .anyRequest()
      .authenticated()
      .accessDecisionManager(accessDecisionManager())
      .and()
      .formLogin()
      .loginPage("/login")
      .defaultSuccessUrl("/home")
      .and()
      .logout()
      .invalidateHttpSession(true)
      .and()
      .sessionManagement()
      .invalidSessionUrl("/login?messageType=error&message=Please log in to continue.")
      .and()
      .exceptionHandling()
      .accessDeniedPage("/accessDenied")
      .and()
      .csrf()
      .disable();
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers("/static/**");
  }

  // Access Decision Manager
  @Bean
  @SuppressWarnings("unchecked")
  public UnanimousBased accessDecisionManager() {
    List<AccessDecisionVoter<? extends Object>> voterList = new ArrayList<>();
    voterList.add(roleVoter());
    voterList.add(new WebExpressionVoter());
    voterList.add(new AuthenticatedVoter());
    return new UnanimousBased(voterList);
  }

  @Bean
  public RoleVoter roleVoter() {
    return new RoleVoter();
  }

  @Bean
  PasswordEncoder PasswordEncoder() {
    return new BCryptPasswordEncoder();
  }
}

