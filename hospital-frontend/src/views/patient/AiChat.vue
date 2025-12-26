<!--
 * AI智能分诊助手页面
-->
<template>
    <div class="ai-chat-container">
        <div class="chat-header">
            <h2>智能分诊助手</h2>
            <p class="subtitle">我可以帮您：找科室、查排班、问流程</p>
        </div>
        
        <div class="chat-body" ref="chatBody">
            <div 
                v-for="(message, index) in messages" 
                :key="index"
                :class="['message', message.role]"
            >
                <div class="message-avatar">
                    <i v-if="message.role === 'user'" class="el-icon-user"></i>
                    <i v-else class="el-icon-service"></i>
                </div>
                <div class="message-content">
                    <div class="message-text" v-html="formatMessage(message.content)"></div>
                    <div class="message-time">{{ formatTime(message.time) }}</div>
                </div>
            </div>
            
            <!-- 加载中提示 -->
            <div v-if="loading" class="message assistant">
                <div class="message-avatar">
                    <i class="el-icon-service"></i>
                </div>
                <div class="message-content">
                    <div class="message-text typing">
                        <span></span>
                        <span></span>
                        <span></span>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="chat-input">
            <el-input
                v-model="inputMessage"
                type="textarea"
                :rows="2"
                placeholder="请输入您的问题，例如：我头疼应该挂什么科？"
                @keyup.ctrl.enter="sendMessage"
                :disabled="loading"
            ></el-input>
            <div class="input-actions">
                <el-button 
                    type="primary" 
                    @click="sendMessage"
                    :loading="loading"
                    :disabled="!inputMessage.trim()"
                >
                    发送 (Ctrl+Enter)
                </el-button>
                <el-button @click="clearChat">清空对话</el-button>
            </div>
        </div>
    </div>
</template>

<script>
import request from "@/utils/request.js";

export default {
    name: "AiChat",
    data() {
        return {
            messages: [],
            inputMessage: "",
            loading: false,
            sessionId: "",
            eventSource: null
        };
    },
    mounted() {
        // 生成会话ID
        this.sessionId = this.generateSessionId();
    },
    beforeDestroy() {
        // 清理EventSource
        if (this.eventSource) {
            this.eventSource.close();
        }
    },
    methods: {
        generateSessionId() {
            return "session_" + Date.now() + "_" + Math.random().toString(36).substr(2, 9);
        },
        
        sendMessage() {
            if (!this.inputMessage.trim() || this.loading) {
                return;
            }
            
            const userMessage = this.inputMessage.trim();
            this.inputMessage = "";
            
            // 添加用户消息
            this.messages.push({
                role: "user",
                content: userMessage,
                time: new Date()
            });
            
            // 滚动到底部
            this.$nextTick(() => {
                this.scrollToBottom();
            });
            
            // 发送AI请求
            this.loading = true;
            this.sendStreamRequest(userMessage);
        },
        
        sendStreamRequest(message) {
            // 添加AI消息占位符
            const aiMessageIndex = this.messages.length;
            this.messages.push({
                role: "assistant",
                content: "",
                time: new Date()
            });
            
            // 使用EventSource接收流式响应
            // 使用相对路径，通过vue.config.js的proxy代理
            const url = `/ai/chat/stream?message=${encodeURIComponent(message)}&sessionId=${this.sessionId}`;
            
            console.log("连接AI服务:", url);
            
            this.eventSource = new EventSource(url);
            
            // 监听message事件（默认事件）
            this.eventSource.onmessage = (event) => {
                console.log("收到消息:", event.data);
                
                if (event.data === "DONE") {
                    console.log("收到完成标记");
                    this.loading = false;
                    if (this.eventSource) {
                        this.eventSource.close();
                        this.eventSource = null;
                    }
                    this.scrollToBottom();
                    return;
                }
                
                // 检查是否是错误消息
                if (event.data && (event.data.startsWith("错误:") || event.data.startsWith("抱歉"))) {
                    if (this.messages[aiMessageIndex].content === "") {
                        this.messages[aiMessageIndex].content = event.data;
                    } else {
                        this.messages[aiMessageIndex].content += "\n\n" + event.data;
                    }
                    this.loading = false;
                    if (this.eventSource) {
                        this.eventSource.close();
                        this.eventSource = null;
                    }
                    this.scrollToBottom();
                    return;
                }
                
                // 追加内容
                if (event.data && event.data.trim() !== "") {
                    this.messages[aiMessageIndex].content += event.data;
                    this.scrollToBottom();
                }
            };
            
            // 监听打开事件
            this.eventSource.onopen = () => {
                console.log("EventSource连接已打开");
            };
            
            // 错误处理
            this.eventSource.onerror = (error) => {
                console.error("EventSource错误:", error);
                console.error("EventSource状态:", this.eventSource.readyState);
                
                // 如果收到错误数据，显示错误消息
                if (error.data) {
                    if (this.messages[aiMessageIndex].content === "") {
                        this.messages[aiMessageIndex].content = error.data;
                    } else {
                        this.messages[aiMessageIndex].content += "\n\n" + error.data;
                    }
                    this.loading = false;
                    if (this.eventSource) {
                        this.eventSource.close();
                        this.eventSource = null;
                    }
                    this.scrollToBottom();
                    return;
                }
                
                // readyState: 0=CONNECTING, 1=OPEN, 2=CLOSED
                if (this.eventSource.readyState === EventSource.CLOSED) {
                    if (this.messages[aiMessageIndex].content === "") {
                        this.messages[aiMessageIndex].content = "连接已关闭。\n\n可能的原因：\n1. 后端服务未启动或端口不正确\n2. API Key配置错误\n3. 网络连接问题\n4. DeepSeek API服务异常\n\n请检查后端控制台日志获取详细错误信息。";
                    }
                    this.loading = false;
                    if (this.eventSource) {
                        this.eventSource.close();
                        this.eventSource = null;
                    }
                } else if (this.eventSource.readyState === EventSource.CONNECTING) {
                    // 正在重连，暂时不处理
                    console.log("EventSource正在重连...");
                }
            };
        },
        
        clearChat() {
            this.messages = [];
            if (this.eventSource) {
                this.eventSource.close();
                this.eventSource = null;
            }
            this.loading = false;
            
            // 清除后端会话
            if (this.sessionId) {
                request.post(`/ai/chat/clear?sessionId=${this.sessionId}`);
            }
            
            // 生成新会话ID
            this.sessionId = this.generateSessionId();
        },
        
        scrollToBottom() {
            this.$nextTick(() => {
                const chatBody = this.$refs.chatBody;
                if (chatBody) {
                    chatBody.scrollTop = chatBody.scrollHeight;
                }
            });
        },
        
        formatMessage(content) {
            // 简单的Markdown格式化
            if (!content) return "";
            
            return content
                .replace(/\n/g, "<br>")
                .replace(/\*\*(.*?)\*\*/g, "<strong>$1</strong>")
                .replace(/\*(.*?)\*/g, "<em>$1</em>");
        },
        
        formatTime(time) {
            if (!time) return "";
            const date = new Date(time);
            const hours = String(date.getHours()).padStart(2, "0");
            const minutes = String(date.getMinutes()).padStart(2, "0");
            return `${hours}:${minutes}`;
        }
    }
};
</script>

<style scoped lang="scss">
.ai-chat-container {
    display: flex;
    flex-direction: column;
    height: calc(100vh - 70px);
    max-width: 1200px;
    margin: 0 auto;
    background: #f5f7fa;
}

.chat-header {
    background: #fff;
    padding: 20px 30px;
    border-bottom: 1px solid #e4e7ed;
    
    h2 {
        margin: 0 0 5px 0;
        font-size: 24px;
        color: #303133;
    }
    
    .subtitle {
        margin: 0;
        font-size: 14px;
        color: #909399;
    }
}

.chat-body {
    flex: 1;
    overflow-y: auto;
    padding: 20px;
    
    .message {
        display: flex;
        margin-bottom: 20px;
        animation: fadeIn 0.3s ease;
        
        .message-avatar {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;
            margin-right: 12px;
            
            i {
                font-size: 20px;
            }
        }
        
        .message-content {
            flex: 1;
            max-width: 70%;
            
            .message-text {
                background: #fff;
                padding: 12px 16px;
                border-radius: 8px;
                line-height: 1.6;
                word-wrap: break-word;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            }
            
            .message-time {
                font-size: 12px;
                color: #909399;
                margin-top: 5px;
            }
        }
        
        &.user {
            flex-direction: row-reverse;
            
            .message-avatar {
                margin-right: 0;
                margin-left: 12px;
                background: #409EFF;
                color: #fff;
            }
            
            .message-content {
                text-align: right;
                
                .message-text {
                    background: #409EFF;
                    color: #fff;
                }
            }
        }
        
        &.assistant {
            .message-avatar {
                background: #67C23A;
                color: #fff;
            }
        }
        
        .typing {
            display: flex;
            gap: 4px;
            padding: 12px 16px;
            
            span {
                width: 8px;
                height: 8px;
                border-radius: 50%;
                background: #909399;
                animation: typing 1.4s infinite;
                
                &:nth-child(2) {
                    animation-delay: 0.2s;
                }
                
                &:nth-child(3) {
                    animation-delay: 0.4s;
                }
            }
        }
    }
}

.chat-input {
    background: #fff;
    padding: 20px;
    border-top: 1px solid #e4e7ed;
    
    .input-actions {
        display: flex;
        justify-content: flex-end;
        gap: 10px;
        margin-top: 10px;
    }
}

@keyframes fadeIn {
    from {
        opacity: 0;
        transform: translateY(10px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

@keyframes typing {
    0%, 60%, 100% {
        transform: translateY(0);
        opacity: 0.7;
    }
    30% {
        transform: translateY(-10px);
        opacity: 1;
    }
}
</style>

