package com.eccolimp.cacamba_manager.domain.service;

import java.util.function.Consumer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eccolimp.cacamba_manager.domain.model.SystemSettings;
import com.eccolimp.cacamba_manager.domain.repository.SystemSettingsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SystemSettingsService {

    private final SystemSettingsRepository repository;

    @Transactional(readOnly = true)
    public SystemSettings getOrCreateDefaults() {
        return repository.findTopByOrderByIdAsc().orElseGet(() -> {
            SystemSettings s = new SystemSettings();
            s.setNotificationsEnabled(true);
            s.setFromName("Gerenciador de Caçambas");
            s.setTimeZone("America/Sao_Paulo");
            s.setVencimentoTime("08:00");
            s.setRelatorioTime("09:00");
            return repository.save(s);
        });
    }

    @Transactional
    public SystemSettings updateSettings(Consumer<SystemSettings> updater) {
        SystemSettings s = getOrCreateDefaults();
        updater.accept(s);
        return repository.save(s);
    }
}


