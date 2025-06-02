package sistema_pedido_online_app.controllers;

import com.sistemapedido.model.Produto;
import com.sistemapedido.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    @Autowired
    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public String listar(Model model) {
        List<Produto> produtos = produtoService.listarTodos();
        model.addAttribute("produtos", produtos);
        return "admin/produtos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("produto", new Produto());
        return "admin/produtos/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("produto") Produto produto, 
                         BindingResult result, 
                         RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "admin/produtos/form";
        }
        
        produtoService.salvar(produto);
        attributes.addFlashAttribute("mensagem", "Produto salvo com sucesso!");
        return "redirect:/admin/produtos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Produto produto = produtoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto inválido: " + id));
        model.addAttribute("produto", produto);
        return "admin/produtos/form";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes attributes) {
        if (!produtoService.existeProduto(id)) {
            attributes.addFlashAttribute("mensagemErro", "Produto não encontrado!");
            return "redirect:/admin/produtos";
        }
        
        produtoService.excluir(id);
        attributes.addFlashAttribute("mensagem", "Produto excluído com sucesso!");
        return "redirect:/admin/produtos";
    }
}
