package com.zmqx.chatglm.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ClassName: {@link ChatGLMProperties}
 * @Author: AbelEthan
 * @Email AbelEthan@vip.qq.com
 * @Date 2023/12/20 16:20
 * @Describes
 */
@Data
@ConfigurationProperties("chatglm.config")
public class ChatGLMProperties {
    private Boolean enabled;
    private String apiHost;
    private String apiSecretKey;
}
