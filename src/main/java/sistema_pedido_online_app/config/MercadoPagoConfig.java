package sistema_pedido_online_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MercadoPagoConfig {

    @Value("${mercadopago.access.token}")
    private String accessToken;

    @Value("${mercadopago.notification.url}")
    private String notificationUrl;

    @Value("${mercadopago.success.url}")
    private String successUrl;

    @Value("${mercadopago.failure.url}")
    private String failureUrl;

    @Value("${mercadopago.pending.url}")
    private String pendingUrl;

    @Bean
    public void initMercadoPago() {
        // Em um ambiente real, aqui seria inicializado o SDK do Mercado Pago
        // com o token de acesso
        System.out.println("Inicializando configuração do Mercado Pago");
    }

    public String getAccessToken() {

        return accessToken;
    }

    public String getNotificationUrl() {
        return notificationUrl;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public String getFailureUrl() {
        return failureUrl;
    }

    public String getPendingUrl() {
        return pendingUrl;
    }
}
