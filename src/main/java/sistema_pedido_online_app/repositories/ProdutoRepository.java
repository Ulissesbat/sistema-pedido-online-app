package sistema_pedido_online_app.repositories;

import com.sistemapedido.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByStatus(Produto.StatusProduto status);
    List<Produto> findByNomeContainingIgnoreCase(String nome);
}
