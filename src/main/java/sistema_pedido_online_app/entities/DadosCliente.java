package sistema_pedido_online_app.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DadosCliente {
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
}
