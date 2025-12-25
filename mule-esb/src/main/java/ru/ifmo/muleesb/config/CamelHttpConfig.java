package ru.ifmo.muleesb.config;

import org.apache.camel.CamelContext;
import org.apache.camel.component.http.HttpComponent;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;

@Configuration
public class CamelHttpConfig {

    @Bean
    public HttpComponent httpComponent(CamelContext camelContext) throws Exception {
        SSLContext sslContext = SSLContextBuilder
                .create()
                .loadTrustMaterial(new TrustAllStrategy())
                .build();

        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                sslContext,
                NoopHostnameVerifier.INSTANCE
        );

        org.apache.http.client.HttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslSocketFactory)
                .build();

        HttpComponent http = camelContext.getComponent("https", HttpComponent.class);
        http.setHttpClientConfigurer(httpClientBuilder -> {
            httpClientBuilder.setSSLSocketFactory(sslSocketFactory);
        });
        http.setHttpClient(httpClient);

        return http;
    }
}
