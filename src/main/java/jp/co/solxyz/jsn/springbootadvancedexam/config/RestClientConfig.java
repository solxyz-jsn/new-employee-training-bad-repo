package jp.co.solxyz.jsn.springbootadvancedexam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * RESTクライアント設定
 */
@Configuration
public class RestClientConfig {

    /**
     * openBD APIのベースURL
     */
    private static final String OPENBD_BASE_URL = "https://api.openbd.jp";

    /**
     * openBD API用RESTクライアント
     * @return openBD API用RESTクライアント
     */
    @Bean
    public RestClient openBdRestClient() {
        return RestClient.builder()
                .baseUrl(OPENBD_BASE_URL)
                .build();
    }
}
