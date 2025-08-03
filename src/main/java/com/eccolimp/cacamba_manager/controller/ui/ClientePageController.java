package com.eccolimp.cacamba_manager.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("ui/clientes")
public class ClientePageController {

    @GetMapping
    public String list(Model model) {
        model.addAttribute("clientes", java.util.Collections.emptyList());
        return "cliente/list";
    }
} 