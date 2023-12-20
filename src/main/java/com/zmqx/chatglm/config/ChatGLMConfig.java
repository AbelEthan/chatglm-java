package com.zmqx.chatglm.config;

import cn.bugstack.chatglm.session.OpenAiSession;
import cn.bugstack.chatglm.session.OpenAiSessionFactory;
import cn.bugstack.chatglm.session.defaults.DefaultOpenAiSessionFactory;
import com.zmqx.chatglm.properties.ChatGLMProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: {@link ChatGLMConfig}
 * @Author: AbelEthan
 * @Email AbelEthan@vip.qq.com
 * @Date 2023/12/20 16:22
 * @Describes
 */
@Configuration
@EnableConfigurationProperties(ChatGLMProperties.class)
public class ChatGLMConfig {
    @Bean
    @ConditionalOnProperty(value = "chatglm.config.enabled", havingValue = "true", matchIfMissing = false)
    public OpenAiSession openAiSession(ChatGLMProperties properties) {
        // 1. 配置文件
        cn.bugstack.chatglm.session.Configuration configuration = new cn.bugstack.chatglm.session.Configuration();
        configuration.setApiHost(properties.getApiHost());
        configuration.setApiSecretKey(properties.getApiSecretKey());
        // 2. 会话工厂
        OpenAiSessionFactory factory = new DefaultOpenAiSessionFactory(configuration);
        // 3. 开启会话
        return factory.openSession();
    }
}
