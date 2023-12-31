package THJava.Ngay3.Books.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import THJava.Ngay3.Books.Services.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	String[] pathArrayPermitAll = new String[] { "/", "/webjars/**",
			"/forgot_password", "/reset_password", "/signup","/verify/**", "/process_register","/photos/**" };
	String[] pathArrayView = new String[] { "/books/" };
	String[] pathArrayNew = new String[] { "/books/new" };
	String[] pathArrayDelete = new String[] { "/books/edit/**" };
	String[] pathArrayUpdate = new String[] { "/books/delete/**" };
	String[] pathArrayRoleStrings = new String[] {"/roles/"};
	String[] pathArrayUserStrings = new String[] {"/users/"};
	
	@Bean
	protected BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	protected UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	protected DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		System.out.println("filter");
		//rememberme
		http.rememberMe().key("uniqueAndSecret").tokenValiditySeconds(10000000);
		http.authorizeHttpRequests(requests -> requests.antMatchers(
				pathArrayPermitAll).permitAll()
				.antMatchers(pathArrayRoleStrings).hasAnyAuthority("ADMIN","CREATE","EDITOR")
				.antMatchers(pathArrayUserStrings).hasAnyAuthority("ADMIN","CREATE","EDITOR")
				.antMatchers(pathArrayView).hasAnyAuthority("USER", "CREATER", "EDITOR", "ADMIN")
				.antMatchers(pathArrayUpdate).hasAnyAuthority("ADMIN", "CREATER")
				.antMatchers(pathArrayUpdate).hasAnyAuthority("ADMIN", "EDITOR")
				.antMatchers(pathArrayDelete).hasAuthority("ADMIN").anyRequest().authenticated())
				.formLogin(login -> login.loginPage("/login").defaultSuccessUrl("/").permitAll())
				.logout(logout -> logout.permitAll()).exceptionHandling(handling -> handling.accessDeniedPage("/403"));
		return http.build();
	}


}
