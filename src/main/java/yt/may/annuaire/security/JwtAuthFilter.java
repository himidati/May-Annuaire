package yt.may.annuaire.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import yt.may.annuaire.service.UserDetailsServiceImpl;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthFilter(JwtService jwtService, UserDetailsServiceImpl userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Lire le header Authorization
        final String authHeader = request.getHeader("Authorization");

        // 2. Si pas de token ou mauvais format → on laisse passer sans authentifier
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraire le token (on enlève "Bearer ")
        final String token = authHeader.substring(7);

        // 4. Extraire l'email depuis le token
        final String email = jwtService.extractEmail(token);

        // 5. Si email trouvé ET user pas encore authentifié dans ce contexte
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Charger le user depuis la BDD
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // 7. Valider le token
            if (jwtService.isTokenValid(token, userDetails)) {

                // 8. Créer l'objet d'authentification Spring Security
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 9. Enregistrer l'authentification dans le contexte Spring Security
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 10. Passer la requête au filtre suivant (ou au controller)
        filterChain.doFilter(request, response);
    }
}
