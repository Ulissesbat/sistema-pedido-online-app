package sistema_pedido_online_app.controllers;

import com.sistemapedido.model.Produto;
import com.sistemapedido.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/produtos")
public class CatalogoController {

    private final ProdutoService produtoService;

    @Autowired
    public CatalogoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public String listarProdutos(Model model) {
        List<Produto> produtos = produtoService.listarAtivos();
        model.addAttribute("produtos", produtos);
        return "catalogo/lista";
    }

    @GetMapping("/buscar")
    public String buscarProdutos(@RequestParam String termo, Model model) {
        List<Produto> produtos = produtoService.buscarPorNome(termo);
        model.addAttribute("produtos", produtos);
        model.addAttribute("termoBusca", termo);
        return "catalogo/lista";
    }

    @GetMapping("/{id}")
    public String detalhesProduto(@PathVariable Long id, Model model) {
        Optional<Produto> produto = produtoService.buscarPorId(id);
        if (produto.isPresent()) {
            model.addAttribute("produto", produto.get());
            return "catalogo/detalhes";
        } else {
            return "redirect:/produtos";
        }
    }
}
