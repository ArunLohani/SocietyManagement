package com.project.societyManagement.filter;

import com.project.societyManagement.entity.User;
import com.project.societyManagement.service.CustomUserDetailService;
import com.project.societyManagement.util.AuthUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private AuthUtil util;
    @Autowired
    private CustomUserDetailService userDetailService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{

            String header = request.getHeader("Authorization");

            if(header == null || !header.startsWith("Bearer")){
                filterChain.doFilter(request,response);
                return;
            }



            String token = header.substring(7);
            String email = util.getEmailFromToken(token);


            if (email!= null && SecurityContextHolder.getContext().getAuthentication() == null){

                User user = (User) userDetailService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(user , null , user.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

            }

            filterChain.doFilter(request,response);

        } catch (Exception e) {
                throw new RuntimeException(e);
        }


    }
}
