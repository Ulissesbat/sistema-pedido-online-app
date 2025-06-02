package sistema_pedido_online_app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "carrinhos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Carrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sessionId;

    @OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarrinho> itens = new ArrayList<>();

    private LocalDateTime dataCriacao;
    
    private LocalDateTime dataAtualizacao;

    @Enumerated(EnumType.STRING)
    private StatusCarrinho status = StatusCarrinho.ATIVO;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
        if (this.sessionId == null) {
            this.sessionId = UUID.randomUUID().toString();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void adicionarItem(Produto produto, Integer quantidade, String observacoes) {
        // Verificar se o produto já existe no carrinho
        for (ItemCarrinho item : itens) {
            if (item.getProduto().getId().equals(produto.getId())) {
                // Atualizar quantidade
                item.setQuantidade(item.getQuantidade() + quantidade);
                item.setObservacoes(observacoes);
                return;
            }
        }
        
        // Adicionar novo item
        ItemCarrinho novoItem = new ItemCarrinho();
        novoItem.setCarrinho(this);
        novoItem.setProduto(produto);
        novoItem.setQuantidade(quantidade);
        novoItem.setPrecoUnitario(produto.getPreco());
        novoItem.setObservacoes(observacoes);
        itens.add(novoItem);
    }

    public void removerItem(Long produtoId) {
        itens.removeIf(item -> item.getProduto().getId().equals(produtoId));
    }

    public void atualizarQuantidade(Long produtoId, Integer quantidade) {
        for (ItemCarrinho item : itens) {
            if (item.getProduto().getId().equals(produtoId)) {
                if (quantidade <= 0) {
                    removerItem(produtoId);
                } else {
                    item.setQuantidade(quantidade);
                }
                return;
            }
        }
    }

    public void limpar() {
        itens.clear();
    }

    public BigDecimal getTotal() {
        return itens.stream()
                .map(ItemCarrinho::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getQuantidadeItens() {
        return itens.stream()
                .mapToInt(ItemCarrinho::getQuantidade)
                .sum();
    }

    public enum StatusCarrinho {
        ATIVO, ABANDONADO, CONVERTIDO
    }
}
