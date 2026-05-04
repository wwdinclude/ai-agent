package com.re.aiagent.agent;

import cn.hutool.core.util.StrUtil;
import com.re.aiagent.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 抽象基础代理类，用于管理代理状态和执行流程。
 *
 * 提供状态转换、异常处理和基于步骤的执行循环的基础功能。
 * 子类必须实现step方法。
 */
@Data
@Slf4j
public abstract class BaseAgent {

    // 核心属性
    private String name;

    // 提示词
    private String systemPrompt;
    private String nextStepPrompt;

    // 代理状态
    private AgentState state = AgentState.IDLE;

    // 执行步骤控制
    private int currentStep = 0;
    private int maxSteps = 10;

    // LLM 大模型
    private ChatClient chatClient;

    // Memory 记忆（需要自主维护会话上下文）
    private List<Message> messageList = new ArrayList<>();

    /**
     * 执行代理
     * @param userPrompt
     * @return
     */
    public String run(String userPrompt) {
        // 基础校验
        if (this.state != AgentState.IDLE) {
            throw new RuntimeException("Cannot run agent from state: " + this.state);
        }
        if (StrUtil.isBlank(userPrompt)) {
            throw new RuntimeException("Cannot run agent with empty user prompt");
        }
        // 初始化状态
        state = AgentState.RUNNING;
        // 记录上下文
        messageList.add(new UserMessage(userPrompt));
        // 保存信息列表
        List<String> results = new ArrayList<>();
        try {
            for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                int stepNumber = i + 1;
                currentStep = stepNumber;
                log.info("Executing step " + stepNumber + "/" + maxSteps);

                String stepResult = step();
                String result = "Step " + stepNumber + ": " + stepResult;
                results.add(result);
            }
            // 检查是否超过最大步骤
            if (currentStep >= maxSteps) {
                state = AgentState.FINISHED;
                results.add("Terminated: Reached max steps (" + maxSteps + ")");
            }
            return String.join("\n", results);
        } catch (Exception e) {
            state = AgentState.ERROR;
            log.error("Error executing agent", e);
            return "执行错误" + e.getMessage();
        } finally {
            // 清理资源
            this.cleanup();
        }
    }

    /**
     * 执行代理（流式输出）
     * @param userPrompt
     * @return
     */
    public SseEmitter runStream(String userPrompt) {
        // 创建 SseEmitter 对象， 设置超时时间为 5 分钟
        SseEmitter emitter = new SseEmitter(300000L);

        // 使用线程异步处理，避免阻塞主线程
        CompletableFuture.runAsync(() -> {
            try {
                if (this.state != AgentState.IDLE) {
                    emitter.send("错误：无法从状态运行代理: " + this.state);
                    emitter.complete();
                    return;
                }
                if (StrUtil.isBlank(userPrompt)) {
                    emitter.send("错误：不能使用空提示词运行代理");
                    emitter.complete();
                    return;
                }

                // 初始化状态
                state = AgentState.RUNNING;
                // 记录上下文
                messageList.add(new UserMessage(userPrompt));

                try {
                    for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                        int stepNumber = i + 1;
                        currentStep = stepNumber;
                        log.info("Executing step " + stepNumber + "/" + maxSteps);

                        String stepResult = step();
                        String result = "Step " + stepNumber + ": " + stepResult;

                        // 不发送步骤信息给前端（保留用于调试）
                        // emitter.send(result);
                    }

                    if (currentStep >= maxSteps) {
                        state = AgentState.FINISHED;
                    }

                    // 从 messageList 中获取AI的最终回答并发送
                    sendFinalAnswer(emitter);

                    // 发送结束标记
                    emitter.send("[DONE]");
                    emitter.complete();
                } catch (Exception e) {
                    state = AgentState.ERROR;
                    log.error("执行智能体失败", e);
                    try {
                        emitter.send("执行错误: " + e.getMessage());
                        emitter.complete();
                    } catch (Exception ex) {
                        emitter.completeWithError(ex);
                    }
                } finally {
                    this.cleanup();
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        emitter.onTimeout(() -> {
            this.state = AgentState.ERROR;
            this.cleanup();
            log.warn("SSE connection timed out");
        });

        emitter.onCompletion(() -> {
            if (this.state == AgentState.RUNNING) {
                this.state = AgentState.FINISHED;
            }
            this.cleanup();
            log.info("SSE connection completed");
        });

        return emitter;
    }

    /**
     * 从 messageList 中获取并发送AI的最终回答
     */
    private void sendFinalAnswer(SseEmitter emitter) throws IOException {
        // 从后往前遍历 messageList，找到最后一个 AssistantMessage
        for (int i = messageList.size() - 1; i >= 0; i--) {
            Message message = messageList.get(i);
            if (message instanceof AssistantMessage) {
                AssistantMessage assistantMessage = (AssistantMessage) message;
                String finalAnswer = assistantMessage.getText();
                if (StrUtil.isNotBlank(finalAnswer)) {
                    log.info("发送AI最终回答: " + finalAnswer);
                    emitter.send(finalAnswer);
                    return;
                }
            }
        }

        // 如果没有找到AI的最终回答，发送默认消息
        emitter.send("抱歉，我无法生成回答。");
    }

    /**
     * 定义单个单步
     */
    public abstract String step();

    /**
     * 清理资源
     */
    protected void cleanup() {
        // 可以重写此方法，清理资源，如关闭连接、释放内存等
    }
}
