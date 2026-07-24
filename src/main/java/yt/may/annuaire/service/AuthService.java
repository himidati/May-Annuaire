package yt.may.annuaire.service;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import yt.may.annuaire.dtos.AuthResponse;
import yt.may.annuaire.dtos.LoginRequest;
import yt.may.annuaire.dtos.RegisterRequest;
import yt.may.annuaire.model.Role;
import yt.may.annuaire.model.User;
import yt.may.annuaire.repository.UserRepository;
import yt.may.annuaire.security.JwtService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        // Vérifier que l'email n'existe pas déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }

        // Construire le user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // hash BCrypt
        user.setRole(Role.valueOf(request.getRole().toUpperCase()));

        // Sauvegarder en BDD
        userRepository.save(user);

        // Générer et retourner le JWT
        String token = jwtService.generationToken(user);
        return new AuthResponse(token, user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        // Spring vérifie email + password — lève une exception si invalide
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Si on arrive ici, les credentials sont valides
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        String token = jwtService.generationToken(user);
        return new AuthResponse(token, user.getRole().name());
    }
}