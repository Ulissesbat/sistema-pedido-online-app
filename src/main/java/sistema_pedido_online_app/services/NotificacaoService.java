package sistema_pedido_online_app.services;

import com.sistemapedido.config.WhatsAppConfig;
import com.sistemapedido.model.Pedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class NotificacaoService {

    private static final Logger logger = Logger.getLogger(NotificacaoService.class.getName());
    
    private final WhatsAppConfig whatsAppConfig;
    private final RestTemplate restTemplate;

    @Autowired
    public NotificacaoService(WhatsAppConfig whatsAppConfig) {
        this.whatsAppConfig = whatsAppConfig;
        this.restTemplate = new RestTemplate();
    }

    public boolean enviarConfirmacaoPedido(Pedido pedido) {
        try {
            // Formatar a mensagem de confirmação
            String mensagem = criarMensagemConfirmacao(pedido);
            
            // Enviar a mensagem via WhatsApp (simulação)
            return enviarMensagemWhatsApp(pedido.getCliente().getTelefone(), mensagem);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao enviar confirmação do pedido via WhatsApp", e);
            return false;
        }
    }
    
    private String criarMensagemConfirmacao(Pedido pedido) {
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        
        StringBuilder mensagem = new StringBuilder();
        mensagem.append("✅ *Pedido Confirmado!* ✅\n\n");
        mensagem.append("Olá, ").append(pedido.getCliente().getNome()).append("!\n\n");
        mensagem.append("Seu pedido #").append(pedido.getNumeroPedido()).append(" foi confirmado e está sendo processado.\n\n");
        
        mensagem.append("*Resumo do Pedido:*\n");
        for (int i = 0; i < pedido.getItens().size(); i++) {
            var item = pedido.getItens().get(i);
            mensagem.append(i + 1).append(". ");
            mensagem.append(item.getQuantidade()).append("x ");
            mensagem.append(item.getProduto().getNome()).append(" - ");
            mensagem.append(formatoMoeda.format(item.getSubtotal())).append("\n");
        }
        
        mensagem.append("\n*Valor Total:* ").append(formatoMoeda.format(pedido.getValorTotal()));
        mensagem.append("\n*Forma de Pagamento:* ").append(formatarFormaPagamento(pedido.getFormaPagamento()));
        
        mensagem.append("\n\nAgradecemos pela sua compra! Em caso de dúvidas, entre em contato conosco.");
        
        return mensagem.toString();
    }
    
    private String formatarFormaPagamento(Pedido.FormaPagamento formaPagamento) {
        switch (formaPagamento) {
            case CARTAO_CREDITO:
                return "Cartão de Crédito";
            case CARTAO_DEBITO:
                return "Cartão de Débito";
            case PIX:
                return "PIX";
            default:
                return formaPagamento.toString();
        }
    }
    
    private boolean enviarMensagemWhatsApp(String numeroDestino, String mensagem) {
        try {
            // Em um ambiente real, aqui seria feita a integração com a API do Twilio ou Z-API
            // Para fins de demonstração, vamos simular o envio
            
            logger.info("Simulando envio de mensagem WhatsApp para " + numeroDestino);
            logger.info("Mensagem: " + mensagem);
            
            // Simulação de chamada à API do Twilio
            if (whatsAppConfig.getApiUrl().contains("twilio")) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                
                MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
                map.add("From", "whatsapp:" + whatsAppConfig.getSenderNumber());
                map.add("To", "whatsapp:" + numeroDestino);
                map.add("Body", mensagem);
                
                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
                
                // Em um ambiente real, esta chamada seria descomentada
                // restTemplate.postForEntity(whatsAppConfig.getApiUrl(), request, String.class);
                
                logger.info("Mensagem enviada com sucesso via Twilio (simulação)");
            } 
            // Simulação de chamada à API Z-API
            else {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + whatsAppConfig.getApiToken());
                
                // Em um ambiente real, seria criado um objeto JSON adequado
                // e feita a chamada à API Z-API
                
                logger.info("Mensagem enviada com sucesso via Z-API (simulação)");
            }
            
            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao enviar mensagem WhatsApp", e);
            return false;
        }
    }
}
