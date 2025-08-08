package com.eccolimp.cacamba_manager.controller.ui;

import com.eccolimp.cacamba_manager.notification.service.NotificationInboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = {DashboardController.class, CacambaPageController.class, ClientePageController.class, AluguelPageController.class})
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final NotificationInboxService inboxService;

    @ModelAttribute("unreadNotifications")
    public Long unreadNotifications() {
        try {
            return inboxService.countUnread();
        } catch (Exception e) {
            return 0L;
        }
    }
}


