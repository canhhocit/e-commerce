
package sv.project.e_commerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
  private final CustomJwtDecoder customJwtDecoder;

  private final String[] PUBLIC_POST_ENDPOINTS = {
      "/mouse-shop/auth/login",
      "/mouse-shop/auth/register",
      "/mouse-shop/auth/logout",
      "/mouse-shop/auth/refresh",
      "/mouse-shop/auth/introspect"
  };

  private final String[] PUBLIC_GET_ENDPOINTS = {
      "/mouse-shop/auth/verify",
      "/categories/**",
      "/products/**",
      "/v3/api-docs/**",
      "/swagger-ui/**",
      "/swagger-ui.html"
  };

  @Value("${application.security.jwt.secret-key}")
  private String signerKey;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.authorizeHttpRequests(request -> request
        .requestMatchers(HttpMethod.POST, PUBLIC_POST_ENDPOINTS).permitAll()
        .requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS).permitAll()
        .anyRequest().authenticated());

    httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(customJwtDecoder)
        .jwtAuthenticationConverter(jwtAuthenticationConverter())));

    httpSecurity.csrf(AbstractHttpConfigurer::disable);

    return httpSecurity.build();
  }

  @Bean
  JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

    return jwtAuthenticationConverter;
  }

}
