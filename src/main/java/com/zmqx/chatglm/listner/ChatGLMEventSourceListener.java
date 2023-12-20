package com.zmqx.chatglm.listner;

import cn.bugstack.chatglm.model.ChatCompletionResponse;
import cn.bugstack.chatglm.model.EventType;
import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @ClassName: {@link ChatGLMEventSourceListener}
 * @Author: AbelEthan
 * @Email AbelEthan@vip.qq.com
 * @Date 2023/12/20 16:25
 * @Describes
 */
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
