package com.eccolimp.cacamba_manager.controller.ui;

import com.eccolimp.cacamba_manager.notification.service.NotificationInboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ui/notifications")
@RequiredArgsConstructor
public class NotificationsController {

    private final NotificationInboxService inboxService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("notifications", inboxService.latest());
        model.addAttribute("unread", inboxService.countUnread());
        return "notifications/list";
    }

    @PostMapping("/refresh")
    public String refresh() {
        inboxService.refreshDailyAlerts();
        return "redirect:/ui/notifications";
    }

    @PostMapping("/mark-all-read")
    public String markAllRead() {
        inboxService.markAllAsRead();
        return "redirect:/ui/notifications";
    }

    @PostMapping("/{id}/read")
    public String markOneRead(@PathVariable Long id) {
        inboxService.markOneAsRead(id);
        return "redirect:/ui/notifications";
    }
}


