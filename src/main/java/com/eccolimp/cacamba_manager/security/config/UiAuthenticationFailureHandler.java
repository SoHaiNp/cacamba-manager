package com.eccolimp.cacamba_manager.security.config;

import com.eccolimp.cacamba_manager.security.service.LoginAttemptService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class UiAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private static final Logger log = LoggerFactory.getLogger(UiAuthenticationFailureHandler.class);
    private final LoginAttemptService loginAttemptService;

    public UiAuthenticationFailureHandler(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String loginParam = request.getParameter("login");
        String remoteAddr = request.getRemoteAddr();
        String exSimple = exception.getClass().getSimpleName();
        String exMsg = exception.getMessage();

        log.warn("[UI LOGIN FAIL] user='{}' ip='{}' ex='{}' msg='{}'", loginParam, remoteAddr, exSimple, exMsg);

        loginAttemptService.onFailure(remoteAddr, loginParam);

        String reason = (exception instanceof BadCredentialsException) ? "bad_credentials" : exSimple;
        String encodedMsg = URLEncoder.encode(exMsg != null ? exMsg : reason, StandardCharsets.UTF_8);
        response.sendRedirect("/login?error=true&reason=" + reason + "&detail=" + encodedMsg);
    }
}


