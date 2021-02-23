package com.code.supportportal.filters;

import com.code.supportportal.constant.SecurityConstant;
import com.code.supportportal.utility.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

import static com.code.supportportal.constant.SecurityConstant.OPTIONS_HTTP_METHOD;
import static com.code.supportportal.constant.SecurityConstant.TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private JwtTokenProvider provider;

    @Autowired
    public JwtAuthorizationFilter(JwtTokenProvider provider) {
        this.provider = provider;
    } // end constructor

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if(request.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD)) {
            response.setStatus(HttpStatus.OK.value());
        }else {
            String authorization = request.getHeader(AUTHORIZATION);
            if(authorization == null || !authorization.startsWith(TOKEN_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }

            // Get token
            String token = authorization.replace(TOKEN_PREFIX, "");
            String username = provider.getSubject(token);

            // Validate token and verify security context
            if(provider.isTokenValid(username, token) &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {
                Set<GrantedAuthority> authoritySet = provider.getAuthorities(token);
                Authentication authentication = provider.getAuthentication(username, authoritySet, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }else{
                SecurityContextHolder.clearContext();
            }
        } // end condition
        filterChain.doFilter(request, response);
    } // end method
} // end filter class
