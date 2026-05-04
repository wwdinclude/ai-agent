<template>
  <div class="love-master-container">
    <div class="header">
      <div class="back-button" @click="goBack">返回</div>
      <h1 class="title">AI恋爱大师</h1>
      <div class="chat-id">会话ID: {{ chatId }}</div>
    </div>
    
    <div class="content-wrapper">
      <div class="chat-area">
        <ChatRoom 
          :messages="messages" 
          :connection-status="connectionStatus"
          ai-type="love"
          @send-message="sendMessage"
        />
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useHead } from '@vueuse/head'
import ChatRoom from '../components/ChatRoom.vue'
import { chatWithLoveApp } from '../api'

// 设置页面标题和元数据
useHead({
  title: 'AI恋爱大师 - AI超级智能体应用平台',
  meta: [
    {
      name: 'description',
      content: 'AI恋爱大师是AI超级智能体应用平台的专业情感顾问，帮你解答各种恋爱问题，提供情感建议'
    },
    {
      name: 'keywords',
      content: 'AI恋爱大师,情感顾问,恋爱咨询,AI聊天,情感问题,AI智能体'
    }
  ]
})

const router = useRouter()
const messages = ref([])
const chatId = ref('')
const connectionStatus = ref('disconnected')
let eventSource = null

// 生成随机会话ID
const generateChatId = () => {
  return 'love_' + Math.random().toString(36).substring(2, 10)
}

// 添加消息到列表
const addMessage = (content, isUser) => {
  messages.value.push({
    content,
    isUser,
    time: new Date().getTime()
  })
}

const cleanResponse = (rawData) => {
  let cleaned = rawData

  cleaned = cleaned.replace(/Step \d+: 工具: \w+ 完成了它的任务！结果:/g, '|||')
  
  const parts = cleaned.split('|||')
  let usefulContent = parts.pop() || ''
  
  if (!usefulContent.trim() && parts.length > 0) {
    usefulContent = parts.join('')
  }
  
  usefulContent = usefulContent.replace(/<[^>]*>/g, '')
  
  usefulContent = usefulContent.replace(/\\["']/g, '')
  usefulContent = usefulContent.replace(/["']/g, '')
  
  usefulContent = usefulContent.replace(/\s*=\s*["'][^"']*["']/g, '')
  
  usefulContent = usefulContent.replace(/\$\{[^}]+\}/g, '')
  
  usefulContent = usefulContent.replace(/var\s+\w+\s*=/g, '')
  usefulContent = usefulContent.replace(/function\s*\([^)]*\)\s*\{[^}]*\}/g, '')
  
  usefulContent = usefulContent.replace(/<!--[\s\S]*?-->/g, '')
  
  usefulContent = usefulContent.replace(/\/\*[\s\S]*?\*\//g, '')
  
  usefulContent = usefulContent.replace(/\{[^}]*\}/g, (match) => {
    if (match.includes('title') && match.includes('link')) {
      const titleMatch = match.match(/title["']?:\s*["']([^"']+)["']/)
      const snippetMatch = match.match(/snippet["']?:\s*["']([^"']+)["']/)
      if (titleMatch && snippetMatch) {
        return `【${titleMatch[1]}】\n${snippetMatch[1]}\n\n`
      } else if (titleMatch) {
        return `【${titleMatch[1]}】\n`
      }
    }
    return ''
  })
  
  usefulContent = usefulContent.replace(/\s{2,}/g, ' ').trim()
  
  usefulContent = usefulContent.replace(/。{2,}/g, '。')
  usefulContent = usefulContent.replace(/\n{3,}/g, '\n\n')
  
  const chinesePattern = /[\u4e00-\u9fa5]{2,}/g
  const matches = usefulContent.match(chinesePattern)
  if (matches && matches.length > 0) {
    return usefulContent || '暂无有效信息'
  }
  
  return '暂无有效信息'
}

const sendMessage = (message) => {
  addMessage(message, true)

  if (eventSource) {
    eventSource.close()
  }

  const aiMessageIndex = messages.value.length
  addMessage('', false)

  connectionStatus.value = 'connecting'
  eventSource = chatWithLoveApp(message, chatId.value)

  let accumulatedData = ''

  eventSource.onmessage = (event) => {
    const data = event.data
    if (data && data !== '[DONE]') {
      accumulatedData += data
    }

    if (data === '[DONE]') {
      const cleanedContent = cleanResponse(accumulatedData)
      if (aiMessageIndex < messages.value.length) {
        messages.value[aiMessageIndex].content = cleanedContent
      }
      connectionStatus.value = 'disconnected'
      eventSource.close()
    }
  }

  eventSource.onerror = (error) => {
    console.error('SSE Error:', error)
    connectionStatus.value = 'error'
    eventSource.close()
  }
}

// 返回主页
const goBack = () => {
  router.push('/')
}

// 页面加载时添加欢迎消息
onMounted(() => {
  // 生成聊天ID
  chatId.value = generateChatId()
  
  // 添加欢迎消息
  addMessage('欢迎来到AI恋爱大师，请告诉我你的恋爱问题，我会尽力给予帮助和建议。', false)
})

// 组件销毁前关闭SSE连接
onBeforeUnmount(() => {
  if (eventSource) {
    eventSource.close()
  }
})
</script>

<style scoped>
.love-master-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #fff9f9;
  overflow: hidden;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background-color: #ff6b8b;
  color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 10;
}

.back-button {
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  transition: opacity 0.2s;
}

.back-button:hover {
  opacity: 0.8;
}

.back-button:before {
  content: '←';
  margin-right: 8px;
}

.title {
  font-size: 20px;
  font-weight: bold;
  margin: 0;
}

.chat-id {
  font-size: 14px;
  opacity: 0.8;
}

.content-wrapper {
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow: hidden; /* 关键：防止内容溢出 */
}

.chat-area {
  flex: 1;
  padding: 16px;
  overflow-y: auto; /* 关键：只让聊天区域垂直滚动 */
  overflow-x: hidden;
  position: relative;
  /* 移除原来的min-height和margin-bottom，改用flex自动计算 */
}

/* 响应式样式 */
@media (max-width: 768px) {
  .header {
    padding: 12px 16px;
  }
  
  .title {
    font-size: 18px;
  }
  
  .chat-id {
    font-size: 12px;
  }
  
  .chat-area {
    padding: 12px;
  }
}

@media (max-width: 480px) {
  .header {
    padding: 10px 12px;
  }
  
  .back-button {
    font-size: 14px;
  }
  
  .title {
    font-size: 16px;
  }
  
  .chat-id {
    display: none;
  }
  
  .chat-area {
    padding: 8px;
  }
}
</style> 