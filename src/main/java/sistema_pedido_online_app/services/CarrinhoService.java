package sistema_pedido_online_app.services;

import com.sistemapedido.model.Carrinho;
import com.sistemapedido.model.Produto;
import com.sistemapedido.repository.CarrinhoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final ProdutoService produtoService;

    @Autowired
    public CarrinhoService(CarrinhoRepository carrinhoRepository, ProdutoService produtoService) {
        this.carrinhoRepository = carrinhoRepository;
        this.produtoService = produtoService;
    }

    public Carrinho obterOuCriarCarrinho(String sessionId) {
        Optional<Carrinho> carrinhoExistente = carrinhoRepository.findBySessionId(sessionId);
        
        if (carrinhoExistente.isPresent()) {
            return carrinhoExistente.get();
        } else {
            Carrinho novoCarrinho = new Carrinho();
            novoCarrinho.setSessionId(sessionId);
            return carrinhoRepository.save(novoCarrinho);
        }
    }

    @Transactional
    public void adicionarItem(String sessionId, Long produtoId, Integer quantidade, String observacoes) {
        Carrinho carrinho = obterOuCriarCarrinho(sessionId);
        Produto produto = produtoService.buscarPorId(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + produtoId));
        
        carrinho.adicionarItem(produto, quantidade, observacoes);
        carrinhoRepository.save(carrinho);
    }

    @Transactional
    public void removerItem(String sessionId, Long produtoId) {
        Carrinho carrinho = obterOuCriarCarrinho(sessionId);
        carrinho.removerItem(produtoId);
        carrinhoRepository.save(carrinho);
    }

    @Transactional
    public void atualizarQuantidade(String sessionId, Long produtoId, Integer quantidade) {
        Carrinho carrinho = obterOuCriarCarrinho(sessionId);
        carrinho.atualizarQuantidade(produtoId, quantidade);
        carrinhoRepository.save(carrinho);
    }

    @Transactional
    public void limparCarrinho(String sessionId) {
        Carrinho carrinho = obterOuCriarCarrinho(sessionId);
        carrinho.limpar();
        carrinhoRepository.save(carrinho);
    }

    public Carrinho obterCarrinho(String sessionId) {
        return obterOuCriarCarrinho(sessionId);
    }
}
