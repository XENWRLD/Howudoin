package edu.sabanciuniv.howudoin.config;

import edu.sabanciuniv.howudoin.Utility.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Extract JWT token from the Authorization header
        String authorizationHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + authorizationHeader); // Debug log

        String token = null;
        String senderId = null;

        // Check if the header contains a Bearer token
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            System.out.println("Extracted Token: " + token); // Debug log
            senderId = jwtUtil.extractSenderIdFromToken(token); // Extract senderId from the token
            System.out.println("Extracted Sender ID from Token: " + senderId); // Debug log
        }

        // Validate token and authenticate user
        if (senderId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(token, senderId)) { // Use senderId for validation
                System.out.println("Token is valid. Authenticating user ID: " + senderId); // Debug log
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(senderId, null, null); // Use senderId
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                System.out.println("Invalid Token"); // Debug log
            }
        } else if (senderId == null) {
            System.out.println("No sender ID extracted from token or user already authenticated."); // Debug log
        }
        System.out.println("doFilterInternal is being called"); // Add this log

        // Continue with the next filter in the chain
        chain.doFilter(request, response);
    }


    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
}



/*
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Extract JWT token from the Authorization header
        String authorizationHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + authorizationHeader); // Debug log

        String token = null;
        String email = null;

        // Check if the header contains a Bearer token
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            System.out.println("Extracted Token: " + token); // Debug log
            email = jwtUtil.extractEmail(token); // Extract email from the token
            System.out.println("Extracted Email from Token: " + email); // Debug log

        }

        // Validate token and authenticate user
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(token, email)) {
                System.out.println("Token is valid. Authenticating user: " + email); // Debug log
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        email, null, null);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            else{
                System.out.println("Invalid Token"); // Debug log
            }
        }
        else if (email == null) {
            System.out.println("No email extracted from token or user already authenticated."); // Debug log
        }

        // Continue with the next filter in the chain
        chain.doFilter(request, response);
    }


    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

}
*/