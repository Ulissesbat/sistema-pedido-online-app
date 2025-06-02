package sistema_pedido_online_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WhatsAppConfig {

    @Value("${whatsapp.api.token}")
    private String apiToken;

    @Value("${whatsapp.api.url}")
    private String apiUrl;

    @Value("${whatsapp.sender.number}")
    private String senderNumber;

    public String getApiToken() {
        return apiToken;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getSenderNumber() {
        return senderNumber;
    }
}
