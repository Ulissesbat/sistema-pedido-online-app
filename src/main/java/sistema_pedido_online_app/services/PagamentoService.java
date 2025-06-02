package sistema_pedido_online_app.services;

import com.sistemapedido.config.MercadoPagoConfig;
import com.sistemapedido.model.Carrinho;
import com.sistemapedido.model.DadosCliente;
import com.sistemapedido.model.Endereco;
import com.sistemapedido.model.ItemCarrinho;
import com.sistemapedido.model.ItemPedido;
import com.sistemapedido.model.Pedido;
import com.sistemapedido.model.Produto;
import com.sistemapedido.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PagamentoService {

    private final PedidoRepository pedidoRepository;
    private final CarrinhoService carrinhoService;
    private final MercadoPagoConfig mercadoPagoConfig;

    @Autowired
    public PagamentoService(PedidoRepository pedidoRepository, CarrinhoService carrinhoService, MercadoPagoConfig mercadoPagoConfig) {
        this.pedidoRepository = pedidoRepository;
        this.carrinhoService = carrinhoService;
        this.mercadoPagoConfig = mercadoPagoConfig;
    }

    @Transactional
    public Map<String, Object> criarPagamento(String sessionId, DadosCliente dadosCliente, Endereco endereco, Pedido.FormaPagamento formaPagamento) {
        // Obter o carrinho atual
        Carrinho carrinho = carrinhoService.obterCarrinho(sessionId);
        
        if (carrinho.getItens().isEmpty()) {
            throw new IllegalStateException("Não é possível criar um pagamento com carrinho vazio");
        }
        
        // Criar o pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(dadosCliente);
        pedido.setEndereco(endereco);
        pedido.setFormaPagamento(formaPagamento);
        pedido.setValorTotal(carrinho.getTotal());
        
        // Converter itens do carrinho para itens do pedido
        for (ItemCarrinho itemCarrinho : carrinho.getItens()) {
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setProduto(itemCarrinho.getProduto());
            itemPedido.setQuantidade(itemCarrinho.getQuantidade());
            itemPedido.setPrecoUnitario(itemCarrinho.getPrecoUnitario());
            itemPedido.setObservacoes(itemCarrinho.getObservacoes());
            pedido.getItens().add(itemPedido);
        }
        
        // Salvar o pedido
        pedidoRepository.save(pedido);
        
        // Simular a criação de preferência de pagamento no Mercado Pago
        Map<String, Object> preferencia = criarPreferenciaMercadoPago(pedido);
        
        // Atualizar o pedido com os dados do pagamento
        pedido.setIdPagamento((String) preferencia.get("id"));
        pedido.setUrlPagamento((String) preferencia.get("init_point"));
        pedidoRepository.save(pedido);
        
        // Limpar o carrinho após criar o pedido
        carrinhoService.limparCarrinho(sessionId);
        
        return preferencia;
    }
    
    private Map<String, Object> criarPreferenciaMercadoPago(Pedido pedido) {
        // Em um ambiente real, aqui seria utilizado o SDK do Mercado Pago para criar
        // uma preferência de pagamento. Para fins de demonstração, vamos simular o retorno.
        
        Map<String, Object> preferencia = new HashMap<>();
        preferencia.put("id", "PREF_" + System.currentTimeMillis());
        preferencia.put("init_point", "https://www.mercadopago.com.br/checkout/v1/redirect?pref_id=PREF_" + System.currentTimeMillis());
        preferencia.put("sandbox_init_point", "https://sandbox.mercadopago.com.br/checkout/v1/redirect?pref_id=PREF_" + System.currentTimeMillis());
        
        return preferencia;
    }
    
    @Transactional
    public void processarWebhook(String tipo, String idPagamento, String status) {
        // Em um ambiente real, aqui seria processado o webhook do Mercado Pago
        // Para fins de demonstração, vamos simular o processamento
        
        if ("payment".equals(tipo)) {
            Optional<Pedido> pedidoOpt = pedidoRepository.findAll().stream()
                    .filter(p -> idPagamento.equals(p.getIdPagamento()))
                    .findFirst();
            
            if (pedidoOpt.isPresent()) {
                Pedido pedido = pedidoOpt.get();
                
                switch (status) {
                    case "approved":
                        pedido.setStatus(Pedido.StatusPedido.PAGO);
                        break;
                    case "pending":
                        pedido.setStatus(Pedido.StatusPedido.AGUARDANDO_PAGAMENTO);
                        break;
                    case "rejected":
                        pedido.setStatus(Pedido.StatusPedido.CANCELADO);
                        break;
                }
                
                pedidoRepository.save(pedido);
            }
        }
    }
    
    public Optional<Pedido> buscarPedido(Long id) {
        return pedidoRepository.findById(id);
    }
    
    public Optional<Pedido> buscarPedidoPorNumero(String numeroPedido) {
        return pedidoRepository.findByNumeroPedido(numeroPedido);
    }
}
