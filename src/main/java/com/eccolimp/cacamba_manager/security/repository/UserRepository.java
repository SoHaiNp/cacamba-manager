package com.eccolimp.cacamba_manager.security.repository;

import com.eccolimp.cacamba_manager.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca usuário por username
     */
    Optional<User> findByUsername(String username);

    /**
     * Busca usuário por email
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica se existe usuário com o username
     */
    boolean existsByUsername(String username);

    /**
     * Verifica se existe usuário com o email
     */
    boolean existsByEmail(String email);


    /**
     * Busca usuário por username ou email (para login flexível)
     */
    @Query("SELECT u FROM User u WHERE u.username = :login OR u.email = :login")
    Optional<User> findByUsernameOrEmail(@Param("login") String login);

    /**
     * Busca usuários ativos
     */
    @Query("SELECT u FROM User u WHERE u.enabled = true")
    Iterable<User> findAllEnabled();
}