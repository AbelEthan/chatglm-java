# 智谱Ai大模型开放SDK - By 小傅哥版本

为了让研发伙伴更快，更方便的接入使用智谱Ai大模型。从而开发的 chatglm-sdk-java 也欢迎👏🏻大家基于智谱API接口补充需要的功能。

此SDK设计，以 Session 会话模型，提供工厂🏭创建服务。代码非常清晰，易于扩展、易于维护。你的PR/ISSUE贡献💐会让AI更加璀璨，[感谢智谱AI团队](https://www.zhipuai.cn/)。

---

## 👣目录

1. 组件配置
2. 功能测试
    1. 代码执行 - `使用：代码的方式主要用于程序接入`
    2. 脚本测试 - `测试：生成Token，直接通过HTTP访问Ai服务`
3. 程序接入

## 1. 组件配置

- 申请ApiKey：[https://open.bigmodel.cn/usercenter/apikeys](https://open.bigmodel.cn/usercenter/apikeys) - 注册申请开通，即可获得 ApiKey
- 运行环境：JDK 1.8+
- maven pom - `已发布到Maven仓库`

```pom
<dependency>
    <groupId>cn.bugstack</groupId>
    <artifactId>chatglm-sdk-java</artifactId>
    <version>1.1</version>
</dependency>
```

## 2. 功能测试

### 2.1 代码执行

```java
@RestController
public class ChatGLMController {

    private final OpenAiSession openAiSession;

    public ChatGLMController(OpenAiSession openAiSession) {
        this.openAiSession = openAiSession;
    }

    @PostMapping("/completions")
    public SseEmitter completions(@RequestBody RequestDTO dto, HttpServletResponse response) throws JsonProcessingException {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        SseEmitter emitter = new SseEmitter();
        ChatCompletionRequest request = getRequest(dto.getContent());
        // 请求
        openAiSession.completions(request, new ChatGLMEventSourceListener(emitter));
        return emitter;
    }

    @NotNull
    private ChatCompletionRequest getRequest(String content) {
        // 入参；模型、请求信息
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel(Model.CHATGLM_TURBO);
        request.setPrompt(new ArrayList<ChatCompletionRequest.Prompt>() {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(ChatCompletionRequest.Prompt.builder()
                        .role(Role.user.getCode())
                        .content(content)
                        .build());
            }

        });
        return request;
    }
}

@Slf4j
public class ChatGLMEventSourceListener extends EventSourceListener {

    private final SseEmitter emitter;

    public ChatGLMEventSourceListener(SseEmitter sseEmitter){
        this.emitter = sseEmitter;
    }


    @SneakyThrows
    @Override
    public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
        ChatCompletionResponse response = JSON.parseObject(data, ChatCompletionResponse.class);
        emitter.send(response.getData());
        if (EventType.finish.getCode().equals(type)) {
            ChatCompletionResponse.Meta meta = JSON.parseObject(response.getMeta(), ChatCompletionResponse.Meta.class);
            log.info("[输出结束] Tokens {}", JSON.toJSONString(meta));
        }
    }

    @SneakyThrows
    @Override
    public void onClosed(@NotNull EventSource eventSource) {
        log.info("对话完成");
        emitter.send("[DONE]");
        emitter.complete();
    }
}
```

## 2. 程序接入

SpringBoot 配置类

```java
@Configuration
@EnableConfigurationProperties(ChatGLMSDKConfigProperties.class)
public class ChatGLMSDKConfig {

    @Bean
    @ConditionalOnProperty(value = "chatglm.config.enabled", havingValue = "true", matchIfMissing = false)
    public OpenAiSession openAiSession(ChatGLMSDKConfigProperties properties) {
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

@Data
@ConfigurationProperties(prefix = "chatglm.config", ignoreInvalidFields = true)
public class ChatGLMSDKConfigProperties {

    /** 状态；open = 开启、close 关闭 */
    private boolean enable;
    /** 转发地址 */
    private String apiHost;
    /** 可以申请 sk-*** */
    private String apiSecretKey;

}
```

```java
private final OpenAiSession openAiSession;

public ChatGLMController(OpenAiSession openAiSession) {
    this.openAiSession = openAiSession;
}
```

- 注意：如果你在服务中配置了关闭启动 ChatGLM SDK 那么注入 openAiSession 为 null

yml 配置

```pom
# ChatGLM SDK Config
chatglm:
    config:
      # 状态；true = 开启、false 关闭
      enabled: false
      # 官网地址 
      api-host: https://open.bigmodel.cn/
      # 官网申请 https://open.bigmodel.cn/usercenter/apikeys
      api-secret-key: 4e087e4135306ef4a676f0cce3cee560.sVyIfxAyyj0O0cki
```
