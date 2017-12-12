package com.fuhe.youse.back.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

        private static Md5PasswordEncoder md5PasswordEncoder=new Md5PasswordEncoder();

        @Autowired
        private DataSource dataSource;

        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth)throws Exception {
            auth.jdbcAuthentication().dataSource(dataSource).passwordEncoder(md5PasswordEncoder);
        }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin().loginPage("/login.jsp").and().formLogin().loginProcessingUrl("/login").and()
                .formLogin().defaultSuccessUrl("/home").and().formLogin().failureUrl("/?error=1");
        //这里配置的是登出的请求
        http.logout().logoutUrl("/logout").
                //登陆成功后的跳转的地址，以及删除的cookie名称
                        and().logout().logoutSuccessUrl("/").and()
                .logout().deleteCookies("JSESSIONID");
        //配置记住我的过期时间
        http.rememberMe().tokenValiditySeconds(1209600)
                .and().rememberMe().rememberMeParameter("remember-me");
        CharacterEncodingFilter encodingFilter=new CharacterEncodingFilter();
        encodingFilter.setEncoding("utf-8");
        encodingFilter.setForceEncoding(true);
        http.addFilterBefore(encodingFilter, CsrfFilter.class); // 放在csrf filter前面
        http.headers().disable();
        HeaderWriter headerWriter = new HeaderWriter() {
            public void writeHeaders(HttpServletRequest request, HttpServletResponse response) {
                response.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
                response.setHeader("Expires", "0");
                response.setHeader("Pragma", "no-cache");
                response.setHeader("X-Frame-Options", "SAMEORIGIN");
                response.setHeader("X-XSS-Protection", "1; mode=block");
                response.setHeader("x-content-type-options", "nosniff");
            }
        };
        List<HeaderWriter> headerWriterFilterList = new ArrayList<>();
        headerWriterFilterList.add(headerWriter);
        HeaderWriterFilter headerFilter = new HeaderWriterFilter(headerWriterFilterList);
        http.addFilter(headerFilter);
    }
}
