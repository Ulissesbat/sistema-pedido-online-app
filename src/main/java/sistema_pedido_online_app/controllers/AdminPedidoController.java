package sistema_pedido_online_app.controllers;

import com.sistemapedido.model.Pedido;
import com.sistemapedido.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/pedidos")
public class AdminPedidoController {

    private final PedidoService pedidoService;

    @Autowired
    public AdminPedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public String listarPedidos(Model model) {
        model.addAttribute("pedidos", pedidoService.listarTodos());
        return "admin/pedidos/lista";
    }

    @GetMapping("/{id}")
    public String detalhesPedido(@PathVariable Long id, Model model) {
        Pedido pedido = pedidoService.buscarPedido(id);
        model.addAttribute("pedido", pedido);
        return "admin/pedidos/detalhes";
    }

    @GetMapping("/{id}/status/{status}")
    public String atualizarStatus(@PathVariable Long id, @PathVariable String status) {
        try {
            Pedido.StatusPedido novoStatus = Pedido.StatusPedido.valueOf(status.toUpperCase());
            pedidoService.atualizarStatusPedido(id, novoStatus);
            return "redirect:/admin/pedidos/" + id;
        } catch (IllegalArgumentException e) {
            return "redirect:/admin/pedidos/" + id + "?erro=Status inválido";
        }
    }
}
