package sistema_pedido_online_app.controllers;

import com.sistemapedido.model.DadosCliente;
import com.sistemapedido.model.Endereco;
import com.sistemapedido.model.Pedido;
import com.sistemapedido.service.PagamentoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @Autowired
    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping("/processar")
    public String processarPagamento(
            HttpSession session,
            @ModelAttribute DadosCliente dadosCliente,
            @ModelAttribute Endereco endereco,
            @RequestParam Pedido.FormaPagamento formaPagamento,
            Model model) {
        
        try {
            String sessionId = session.getId();
            Map<String, Object> resultado = pagamentoService.criarPagamento(sessionId, dadosCliente, endereco, formaPagamento);
            
            // Redirecionar para a URL de pagamento do Mercado Pago
            return "redirect:" + resultado.get("init_point");
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao processar pagamento: " + e.getMessage());
            return "carrinho/checkout";
        }
    }

    @GetMapping("/sucesso")
    public String pagamentoSucesso(
            @RequestParam(required = false) String collection_id,
            @RequestParam(required = false) String collection_status,
            @RequestParam(required = false) String payment_id,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String external_reference,
            @RequestParam(required = false) String preference_id,
            Model model) {
        
        model.addAttribute("paymentId", payment_id);
        model.addAttribute("status", status);
        model.addAttribute("preferenceId", preference_id);
        
        return "pagamentos/sucesso";
    }

    @GetMapping("/falha")
    public String pagamentoFalha(
            @RequestParam(required = false) String collection_id,
            @RequestParam(required = false) String collection_status,
            @RequestParam(required = false) String payment_id,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String external_reference,
            @RequestParam(required = false) String preference_id,
            Model model) {
        
        model.addAttribute("paymentId", payment_id);
        model.addAttribute("status", status);
        model.addAttribute("preferenceId", preference_id);
        
        return "pagamentos/falha";
    }

    @GetMapping("/pendente")
    public String pagamentoPendente(
            @RequestParam(required = false) String collection_id,
            @RequestParam(required = false) String collection_status,
            @RequestParam(required = false) String payment_id,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String external_reference,
            @RequestParam(required = false) String preference_id,
            Model model) {
        
        model.addAttribute("paymentId", payment_id);
        model.addAttribute("status", status);
        model.addAttribute("preferenceId", preference_id);
        
        return "pagamentos/pendente";
    }

    @PostMapping("/webhook")
    @ResponseBody
    public ResponseEntity<String> webhook(
            @RequestParam String type,
            @RequestParam(required = false) String data_id,
            @RequestParam(required = false) String topic) {
        
        String id = data_id != null ? data_id : "";
        String tipo = type != null ? type : topic;
        
        // Processar o webhook
        pagamentoService.processarWebhook(tipo, id, "approved"); // Simulando status aprovado
        
        return ResponseEntity.ok("Webhook processado com sucesso");
    }
}
