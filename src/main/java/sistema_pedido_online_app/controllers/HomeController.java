package sistema_pedido_online_app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/produtos";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
