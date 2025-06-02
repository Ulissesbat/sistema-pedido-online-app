package sistema_pedido_online_app.services;

import com.sistemapedido.model.Pedido;
import com.sistemapedido.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final NotificacaoService notificacaoService;

    @Autowired
    public PedidoService(PedidoRepository pedidoRepository, NotificacaoService notificacaoService) {
        this.pedidoRepository = pedidoRepository;
        this.notificacaoService = notificacaoService;
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    @Transactional
    public void atualizarStatusPedido(Long pedidoId, Pedido.StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + pedidoId));
        
        pedido.setStatus(novoStatus);
        pedidoRepository.save(pedido);
        
        // Se o status for alterado para PAGO, enviar notificação
        if (novoStatus == Pedido.StatusPedido.PAGO) {
            notificacaoService.enviarConfirmacaoPedido(pedido);
        }
    }
    
    public Pedido buscarPedido(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + id));
    }
}
