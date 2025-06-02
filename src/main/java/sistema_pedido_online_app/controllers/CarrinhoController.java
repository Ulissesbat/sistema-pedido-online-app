package sistema_pedido_online_app.controllers;

import com.sistemapedido.model.Carrinho;
import com.sistemapedido.service.CarrinhoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/carrinho")
public class CarrinhoController {

    private final CarrinhoService carrinhoService;

    @Autowired
    public CarrinhoController(CarrinhoService carrinhoService) {
        this.carrinhoService = carrinhoService;
    }

    @GetMapping
    public String visualizarCarrinho(HttpSession session, Model model) {
        String sessionId = session.getId();
        Carrinho carrinho = carrinhoService.obterCarrinho(sessionId);
        model.addAttribute("carrinho", carrinho);
        return "carrinho/visualizar";
    }

    @PostMapping("/adicionar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> adicionarItem(
            HttpSession session,
            @RequestParam Long produtoId,
            @RequestParam(defaultValue = "1") Integer quantidade,
            @RequestParam(required = false) String observacoes) {
        
        String sessionId = session.getId();
        carrinhoService.adicionarItem(sessionId, produtoId, quantidade, observacoes);
        Carrinho carrinho = carrinhoService.obterCarrinho(sessionId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("totalItens", carrinho.getQuantidadeItens());
        response.put("valorTotal", carrinho.getTotal());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/remover")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removerItem(
            HttpSession session,
            @RequestParam Long produtoId) {
        
        String sessionId = session.getId();
        carrinhoService.removerItem(sessionId, produtoId);
        Carrinho carrinho = carrinhoService.obterCarrinho(sessionId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("totalItens", carrinho.getQuantidadeItens());
        response.put("valorTotal", carrinho.getTotal());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/atualizar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> atualizarQuantidade(
            HttpSession session,
            @RequestParam Long produtoId,
            @RequestParam Integer quantidade) {
        
        String sessionId = session.getId();
        carrinhoService.atualizarQuantidade(sessionId, produtoId, quantidade);
        Carrinho carrinho = carrinhoService.obterCarrinho(sessionId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("totalItens", carrinho.getQuantidadeItens());
        response.put("valorTotal", carrinho.getTotal());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/limpar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> limparCarrinho(HttpSession session) {
        String sessionId = session.getId();
        carrinhoService.limparCarrinho(sessionId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        String sessionId = session.getId();
        Carrinho carrinho = carrinhoService.obterCarrinho(sessionId);
        
        if (carrinho.getItens().isEmpty()) {
            return "redirect:/carrinho";
        }
        
        model.addAttribute("carrinho", carrinho);
        return "carrinho/checkout";
    }
}
