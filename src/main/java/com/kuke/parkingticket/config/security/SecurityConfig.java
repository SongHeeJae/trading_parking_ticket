package com.kuke.parkingticket.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;


    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/**"
                , "/swagger-ui.html", "/webjars/**", "/swagger/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable() // rest api이므로 기본설정 미사용
                .csrf().disable() // rest api이므로 csrf 보안 미사용
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt로 인증하므로 세션 미사용
                .and()
                    .authorizeRequests() // 다음 리퀘스트에 대한 사용권한 체크
                        .antMatchers(HttpMethod.POST, "/api/regions", "/api/towns").hasRole("ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/api/regions", "/api/towns").hasRole("ADMIN")
                        .antMatchers("/api/sign/", "/api/sign/**").permitAll()
                        .antMatchers(HttpMethod.GET,"/exception", "/exception/**", "/api/users",
                                "/api/users/**", "/api/regions", "/api/regions/**", "/api/tickets", "/api/tickets/**",
                                "/api/comments", "/api/comments/**", "/api/reviews", "/api/reviews/**",
                                "/api/histories", "/api/histories/**", "/api/towns", "/api/towns/**").permitAll()
                        .antMatchers("/alarm/stomp", "/alarm/stomp/**", "/ws-stomp", "/ws-stomp/**").permitAll() // stomp
                        .antMatchers("/social", "/social/**").permitAll() // social login view test
                        .anyRequest().authenticated()
                .and()
                    .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())
                .and()
                    .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .and()
                    .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class); // jwt 필터 추가

    }

}
