<template>
  <div class="home-container">
    <div class="starfield"></div>
    <div class="scanlines"></div>
    
    <div class="content">
      <div class="header">
        <div class="title-section">
          <h1 class="main-title">AI智能助手应用平台</h1>
          <p class="main-subtitle">探索AI的无限可能，体验智能对话</p>
        </div>
      </div>
      
      <div class="terminal-container">
        <div class="terminal">
          <div class="terminal-header">
            <div class="terminal-dots">
              <span class="dot red"></span>
              <span class="dot yellow"></span>
              <span class="dot green"></span>
            </div>
            <div class="terminal-title">yupi-terminal</div>
          </div>
          <div class="terminal-body" ref="terminalBody">
            <div 
              class="command-line" 
              v-for="(line, index) in terminalLines" 
              :key="index" 
              :style="{ animationDelay: `${index * 0.3}s` }"
              @animationend="scrollToBottom"
            >
              <span class="prompt">root@yupi:~$</span>
              <span class="command">{{ line.command }}</span>
              <span class="output" v-if="line.output">{{ line.output }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="cards-container">
        <div class="app-card love-card" @click="navigateTo('/love-master')">
          <div class="card-border"></div>
          <div class="card-inner">
            <div class="card-icon love-icon">
              <span class="icon-emoji">❤️</span>
              <div class="icon-glow"></div>
            </div>
            <h3 class="card-title">AI恋爱大师</h3>
            <p class="card-desc">智能情感顾问，帮你解答恋爱烦恼</p>
            <button class="card-button">
              <span class="button-text">立即体验</span>
              <span class="button-arrow">→</span>
            </button>
          </div>
        </div>
        
        <div class="app-card super-card" @click="navigateTo('/super-agent')">
          <div class="card-border"></div>
          <div class="card-inner">
            <div class="card-icon super-icon">
              <span class="icon-emoji">🤖</span>
              <div class="icon-glow"></div>
            </div>
            <h3 class="card-title">AI超级智能体</h3>
            <p class="card-desc">全能型AI助手，解决各类专业问题</p>
            <button class="card-button">
              <span class="button-text">立即体验</span>
              <span class="button-arrow">→</span>
            </button>
          </div>
        </div>
      </div>
      
      <div class="footer">
        <div class="footer-text">© AI超级智能体应用平台</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useHead } from '@vueuse/head'

useHead({
  title: 'AI超级智能体应用平台',
  meta: [
    { name: 'description', content: 'AI超级智能体应用平台提供AI恋爱大师和AI超级智能体服务' },
    { name: 'keywords', content: 'AI智能体,AI应用,AI恋爱大师,AI助手,智能对话,' }
  ]
})

const router = useRouter()
const terminalBody = ref(null)

const terminalLines = [
  { command: './launch-ai-platform.sh', output: '' },
  { command: '', output: '正在初始化 AI 引擎...' },
  { command: '', output: '加载智能助手模型...' },
  { command: '', output: '连接服务器...' },
  { command: '', output: 'AI 平台启动成功！选择下方应用开始体验' }
]

const scrollToBottom = () => {
  nextTick(() => {
    if (terminalBody.value) {
      terminalBody.value.scrollTop = terminalBody.value.scrollHeight
    }
  })
}

const navigateTo = (path) => {
  router.push(path)
}
</script>

<style scoped>


.home-container {
  min-height: 100vh;
  background: #0a0a0f;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.starfield {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: 
    radial-gradient(2px 2px at 20px 30px, #fff, transparent),
    radial-gradient(2px 2px at 40px 70px, rgba(255,255,255,0.8), transparent),
    radial-gradient(1px 1px at 90px 40px, #fff, transparent),
    radial-gradient(2px 2px at 160px 120px, rgba(255,255,255,0.9), transparent),
    radial-gradient(1px 1px at 230px 80px, #fff, transparent),
    radial-gradient(2px 2px at 300px 150px, rgba(255,255,255,0.7), transparent),
    radial-gradient(1px 1px at 370px 60px, #fff, transparent),
    radial-gradient(2px 2px at 450px 200px, rgba(255,255,255,0.8), transparent);
  background-repeat: repeat;
  background-size: 500px 250px;
  animation: twinkle 5s ease-in-out infinite;
  z-index: 1;
  pointer-events: none;
}

@keyframes twinkle {
  0%, 100% { opacity: 0.8; }
  50% { opacity: 1; }
}

.scanlines {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: repeating-linear-gradient(
    0deg,
    rgba(0, 0, 0, 0.15),
    rgba(0, 0, 0, 0.15) 1px,
    transparent 1px,
    transparent 2px
  );
  z-index: 2;
  pointer-events: none;
  opacity: 0.2;
}

.content {
  flex: 1;
  position: relative;
  z-index: 3;
}

.header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 20px 30px;
  text-align: center;
}

.title-section {
  position: relative;
}

.main-title {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  font-size: 3.2rem;
  font-weight: 800;
  color: #fff;
  margin: 0 0 16px;
  text-shadow: 
    0 0 10px rgba(0, 212, 255, 0.8),
    0 0 20px rgba(0, 212, 255, 0.6),
    0 0 40px rgba(0, 212, 255, 0.4);
  letter-spacing: 2px;
  animation: titleGlow 3s ease-in-out infinite;
}

@keyframes titleGlow {
  0%, 100% { 
    text-shadow: 
      0 0 10px rgba(0, 212, 255, 0.8),
      0 0 20px rgba(0, 212, 255, 0.6);
  }
  50% { 
    text-shadow: 
      0 0 20px rgba(0, 212, 255, 1),
      0 0 40px rgba(0, 212, 255, 0.8),
      0 0 60px rgba(0, 212, 255, 0.6);
  }
}

.main-subtitle {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  font-size: 1.1rem;
  font-weight: 400;
  color: rgba(255, 255, 255, 0.7);
  margin: 0;
  letter-spacing: 1px;
}

.terminal-container {
  padding: 15px;
  display: flex;
  justify-content: center;
  max-height: 140px;
}

.terminal {
  width: 100%;
  max-width: 700px;
  background: #1a1a2e;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 0 30px rgba(0, 240, 255, 0.2);
  border: 1px solid rgba(0, 240, 255, 0.15);
}

.terminal-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  background: #252542;
  border-bottom: 1px solid rgba(0, 240, 255, 0.15);
}

.terminal-dots {
  display: flex;
  gap: 6px;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.dot.red { background: #ff5f56; }
.dot.yellow { background: #ffbd2e; }
.dot.green { background: #27c93f; }

.terminal-title {
  font-family: 'Share Tech Mono', monospace;
  color: rgba(255, 255, 255, 0.6);
  font-size: 12px;
  letter-spacing: 1px;
}

.terminal-body {
  padding: 12px 16px;
  font-family: 'Share Tech Mono', monospace;
  font-size: 12px;
  line-height: 1.6;
  max-height: 80px;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: #00d4ff #1a1a2e;
}

.command-line {
  opacity: 0;
  animation: typeIn 0.4s ease-out forwards;
}

@keyframes typeIn {
  to { opacity: 1; }
}

.prompt {
  color: #00ff88;
  margin-right: 12px;
}

.command {
  color: #00d4ff;
}

.output {
  color: rgba(255, 255, 255, 0.8);
  display: block;
  margin-left: 24px;
  animation: blinkText 0.8s steps(1) infinite;
}

.output:last-child {
  color: #00ff88;
  animation: none;
}

@keyframes blinkText {
  0%, 50% { opacity: 1; }
  51%, 100% { opacity: 0; }
}

.cards-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 40px;
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.app-card {
  position: relative;
  width: 360px;
  aspect-ratio: 3/4;
  border-radius: 20px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.card-border {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 20px;
  padding: 2px;
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.3), rgba(144, 0, 255, 0.3));
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  opacity: 0;
  transition: opacity 0.4s;
}

.app-card:hover .card-border {
  opacity: 1;
}

.card-inner {
  position: relative;
  width: 100%;
  height: 100%;
  background: rgba(20, 20, 35, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 18px;
  padding: 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.app-card:hover {
  transform: translateY(-15px);
}

.love-card:hover {
  box-shadow: 
    0 20px 60px rgba(255, 0, 122, 0.3),
    0 0 40px rgba(255, 0, 122, 0.2);
}

.super-card:hover {
  box-shadow: 
    0 20px 60px rgba(0, 212, 255, 0.3),
    0 0 40px rgba(0, 212, 255, 0.2);
}

.card-icon {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  margin-bottom: 24px;
}

.icon-emoji {
  font-size: 3.5rem;
  z-index: 2;
  position: relative;
}

.icon-glow {
  position: absolute;
  top: -10px;
  left: -10px;
  right: -10px;
  bottom: -10px;
  border-radius: 50%;
  animation: iconPulse 2s ease-in-out infinite;
}

.love-icon {
  background: linear-gradient(135deg, #ff007a, #ff5722);
  box-shadow: 0 0 30px rgba(255, 0, 122, 0.5);
}

.love-icon .icon-glow {
  background: radial-gradient(circle, rgba(255, 0, 122, 0.4) 0%, transparent 70%);
}

.super-icon {
  background: linear-gradient(135deg, #00b2ff, #4f56ff);
  box-shadow: 0 0 30px rgba(0, 178, 255, 0.5);
}

.super-icon .icon-glow {
  background: radial-gradient(circle, rgba(0, 178, 255, 0.4) 0%, transparent 70%);
}

@keyframes iconPulse {
  0%, 100% { transform: scale(1); opacity: 0.5; }
  50% { transform: scale(1.1); opacity: 0.8; }
}

.card-title {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  font-size: 1.5rem;
  font-weight: 700;
  color: #fff;
  margin: 0 0 12px;
  text-shadow: 0 0 10px rgba(0, 212, 255, 0.5);
}

.card-desc {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  font-size: 0.95rem;
  font-weight: 400;
  color: rgba(255, 255, 255, 0.6);
  text-align: center;
  margin: 0 0 auto;
  line-height: 1.6;
}

.card-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 30px;
  background: linear-gradient(90deg, #0088ff, #00b2ff);
  border: none;
  border-radius: 30px;
  color: #fff;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s;
  margin-top: 20px;
  box-shadow: 0 4px 15px rgba(0, 136, 255, 0.4);
}

.card-button:hover {
  transform: scale(1.05);
  box-shadow: 0 8px 25px rgba(0, 136, 255, 0.6);
}

.button-arrow {
  transition: transform 0.3s;
}

.card-button:hover .button-arrow {
  transform: translateX(5px);
}

.footer {
  text-align: center;
  padding: 40px 20px;
}

.footer-text {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.4);
  letter-spacing: 1px;
}

@media (max-width: 768px) {
  .main-title {
    font-size: 2.5rem;
    letter-spacing: 2px;
  }
  
  .main-subtitle {
    font-size: 1rem;
  }
  
  .cards-container {
    gap: 30px;
  }
  
  .app-card {
    width: 100%;
    max-width: 400px;
  }
}

@media (max-width: 480px) {
  .header {
    padding: 40px 15px 20px;
  }
  
  .main-title {
    font-size: 1.8rem;
    letter-spacing: 1px;
  }
  
  .main-subtitle {
    font-size: 0.9rem;
  }
  
  .terminal-body {
    font-size: 12px;
    padding: 16px;
  }
  
  .card-icon {
    width: 80px;
    height: 80px;
  }
  
  .icon-emoji {
    font-size: 3rem;
  }
  
  .card-title {
    font-size: 1.3rem;
  }
  
  .card-button {
    padding: 12px 24px;
    font-size: 0.9rem;
  }
}
</style>