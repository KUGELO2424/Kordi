package pl.kucharski.Kordi.security;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.kucharski.Kordi.service.user.UserService;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${jwt.secret}")
    private String secret;

    private final UserDetailsService userDetailsService;

    private final ApplicationContext applicationContext;

    public WebSecurityConfig(UserDetailsService userDetailsService, ApplicationContext applicationContext) {
        this.userDetailsService = userDetailsService;
        this.applicationContext = applicationContext;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/login", "/register", "/verify", "/sendToken", "/validate", "/users/**").permitAll()
                .antMatchers(HttpMethod.GET, "/collections**", "/collections/**").permitAll()
                .antMatchers("/swagger-ui/**", "/swagger-ui**", "/v3/api-docs/**", "/v3/api-docs**").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, ex) -> response.sendError(
                                HttpServletResponse.SC_UNAUTHORIZED,
                                ex.getMessage()
                        )
                );
        http.cors();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilter(new CustomAuthenticationFilter(authenticationManagerBean(), tokenGenerationAlgorithm(), (UserService) applicationContext.getBean("userServiceImpl")));
        http.addFilterBefore(new CustomAuthorizationFilter(tokenGenerationAlgorithm()), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Algorithm tokenGenerationAlgorithm() {
        return Algorithm.HMAC256(secret.getBytes());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
