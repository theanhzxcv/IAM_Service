package com.theanh.dev.IAM_Service.Security;

import com.theanh.dev.IAM_Service.Exception.AppException;
import com.theanh.dev.IAM_Service.Exception.ErrorCode;
import com.theanh.dev.IAM_Service.Service.Blacklist.JwtBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtFilter extends OncePerRequestFilter {

    JwtUtil jwtUtil;

    UserDetailsService userDetailsService;

    JwtBlacklistService jwtBlacklistService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        if (jwtBlacklistService.isBlacklisted(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String userEmail = jwtUtil.extractEmail(token);
        if (userEmail == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);

            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
        if (jwtUtil.isTokenValid(token, userDetails)) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            authToken.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        filterChain.doFilter(request, response);
    }
}
