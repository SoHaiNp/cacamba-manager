package com.eccolimp.cacamba_manager.security.service;

import com.eccolimp.cacamba_manager.security.dto.UserRegistrationDto;
import com.eccolimp.cacamba_manager.security.model.User;
import com.eccolimp.cacamba_manager.security.repository.UserRepository;
import com.eccolimp.cacamba_manager.domain.service.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registra novo usuário
     */
    public User registrarUsuario(UserRegistrationDto registrationDto) {
        // Validações
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new BusinessException("Senhas não coincidem");
        }

        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new BusinessException("Username já existe");
        }

        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new BusinessException("Email já está em uso");
        }

        // Criar usuário
        User user = User.builder()
                .username(registrationDto.getUsername())
                .password(passwordEncoder.encode(registrationDto.getPassword()))
                .email(registrationDto.getEmail())
                .nomeCompleto(registrationDto.getNomeCompleto())
                .role(User.Role.USER) // Novos usuários começam como USER
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .criadoEm(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }

    /**
     * Busca usuário por ID
     */
    @Transactional(readOnly = true)
    public Optional<User> buscarPorId(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Busca usuário por username
     */
    @Transactional(readOnly = true)
    public Optional<User> buscarPorUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Busca usuário por username ou email
     */
    @Transactional(readOnly = true)
    public Optional<User> buscarPorLogin(String login) {
        return userRepository.findByUsernameOrEmail(login);
    }

    /**
     * Lista todos usuários ativos
     */
    @Transactional(readOnly = true)
    public List<User> listarUsuariosAtivos() {
        return (List<User>) userRepository.findAllEnabled();
    }

    /**
     * Atualiza último login do usuário
     */
    public void atualizarUltimoLogin(String username) {
        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    user.setUltimoLogin(LocalDateTime.now());
                    userRepository.save(user);
                });
    }

    /**
     * Desabilita usuário
     */
    public void desabilitarUsuario(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));
        
        user.setEnabled(false);
        userRepository.save(user);
    }

    /**
     * Habilita usuário
     */
    public void habilitarUsuario(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));
        
        user.setEnabled(true);
        userRepository.save(user);
    }

    /**
     * Altera role do usuário
     */
    public void alterarRole(Long userId, User.Role novaRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));
        
        user.setRole(novaRole);
        userRepository.save(user);
    }
}