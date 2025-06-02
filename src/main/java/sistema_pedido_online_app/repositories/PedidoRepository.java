package sistema_pedido_online_app.repositories;

import com.sistemapedido.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Optional<Pedido> findByNumeroPedido(String numeroPedido);
    List<Pedido> findByClienteEmail(String email);
    List<Pedido> findByStatus(Pedido.StatusPedido status);
}
