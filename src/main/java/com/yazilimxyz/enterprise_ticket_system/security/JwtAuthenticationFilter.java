package com.yazilimxyz.enterprise_ticket_system.security;

import com.yazilimxyz.enterprise_ticket_system.entities.Role;
import com.yazilimxyz.enterprise_ticket_system.entities.User;
import com.yazilimxyz.enterprise_ticket_system.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final List<RequestMatcher> SKIP_MATCHERS = List.of(
            new AntPathRequestMatcher("/auth/login"),
            new AntPathRequestMatcher("/auth/register"),
            new AntPathRequestMatcher("/auth/refresh"),

            new AntPathRequestMatcher("/api/auth/login"),
            new AntPathRequestMatcher("/api/auth/register"),
            new AntPathRequestMatcher("/api/auth/refresh"),

            new AntPathRequestMatcher("/actuator/**"),

            new AntPathRequestMatcher("/v3/api-docs/**"),
            new AntPathRequestMatcher("/swagger-ui/**"),
            new AntPathRequestMatcher("/swagger-ui.html"),
            new AntPathRequestMatcher("/swagger-resources/**"),
            new AntPathRequestMatcher("/webjars/**"),
            new AntPathRequestMatcher("/configuration/**"),
            new AntPathRequestMatcher("/api-docs/**"),

            new AntPathRequestMatcher("/ws/**"),
            new AntPathRequestMatcher("/chat-test.html"),
            new AntPathRequestMatcher("/public/**"),
            new AntPathRequestMatcher("/error")
    );

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        for (RequestMatcher m : SKIP_MATCHERS) {
            if (m.matches(request)) {
                log.trace("[JwtFilter] Skipping {}", request.getRequestURI());
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7).trim();
        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Zaten authenticate ise tekrar uğraşma
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            Claims claims = jwtUtil.parseClaims(token);

            String sub = claims.getSubject();
            if (sub == null || sub.isBlank()) {
                writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token: missing subject");
                return;
            }

            Long userId;
            try {
                userId = Long.valueOf(sub);
            } catch (NumberFormatException nfe) {
                writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token: subject is not a user id");
                return;
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.isActive()) {
                writeError(response, HttpServletResponse.SC_FORBIDDEN, "Account disabled");
                return;
            }

            Role role = user.getRole();
            if (role == null) {
                writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "User has no role");
                return;
            }

            String roleFromToken = claims.get("role", String.class);
            if (roleFromToken != null && !role.name().equalsIgnoreCase(roleFromToken)) {
                writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token role does not match user");
                return;
            }

            AuthenticatedUser principal = new AuthenticatedUser(user.getId(), user.getEmail(), role);
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.name());

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(principal, null, List.of(authority));

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            log.debug("[JwtFilter] Authenticated userId={} email={} path={}", user.getId(), user.getEmail(), path);

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            log.debug("[JwtFilter] Token expired path={} reason={}", path, e.getMessage(), e);
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
        } catch (Exception e) {
            log.debug("[JwtFilter] Token validation failed path={} reason={}", path, e.getMessage(), e);
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token: " + e.getMessage());
        }
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + escapeJson(message) + "\"}");
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
