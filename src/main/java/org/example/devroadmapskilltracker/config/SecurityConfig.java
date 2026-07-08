package org.example.devroadmapskilltracker.config;

import org.example.devroadmapskilltracker.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

   private final UserRepository userRepository;

   public SecurityConfig(UserRepository userRepository) {
       this.userRepository = userRepository;
   }

   @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) {
       http.csrf(csrf -> csrf.disable())
               .authorizeHttpRequests(auth -> auth
                       .requestMatchers("/login", "/static/**", "/css/**", "/assets/**").permitAll()
                       .requestMatchers("/signup","/createAccount").permitAll()
                       .anyRequest().authenticated())

               .formLogin(form -> form
                       .loginPage("/login")
                       .defaultSuccessUrl("/skills")
                       .permitAll())
               .logout(logout -> logout
               .logoutUrl("/logout")
                       .logoutSuccessUrl("/login?logout")
                       .permitAll());


       return http.build();
   }

   @Bean
    public UserDetailsService userDetailsService() {
       return username -> userRepository.findByUsername(username)
               .map(user -> User
                       .withUsername(user.getUsername())
                       .password(user.getPassword())
                       .roles("USER")
                       .build())
               .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
