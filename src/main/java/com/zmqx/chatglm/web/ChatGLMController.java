package com.zmqx.chatglm.web;

import cn.bugstack.chatglm.model.ChatCompletionRequest;
import cn.bugstack.chatglm.model.Model;
import cn.bugstack.chatglm.model.Role;
import cn.bugstack.chatglm.session.OpenAiSession;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.zmqx.chatglm.dto.RequestDTO;
import com.zmqx.chatglm.listner.ChatGLMEventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/**
 * @ClassName: {@link ChatGLMController}
 * @Author: AbelEthan
 * @Email AbelEthan@vip.qq.com
 * @Date 2023/12/20 16:28
 * @Describes
 */
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
