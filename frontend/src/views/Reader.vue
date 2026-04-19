<template>
  <div 
    class="min-h-screen transition-colors duration-500 flex flex-col"
    :class="theme === 'sepia' ? 'bg-[#F4ECD8] text-[#5B4636]' : 'bg-[#1A1A1A] text-[#D1D1D1]'"
  >
    <!-- 顶部控制栏 (悬浮触发) -->
    <header 
      class="fixed top-0 left-0 w-full h-16 px-8 flex items-center justify-between z-50 transition-opacity duration-300 backdrop-blur-md"
      :class="showControls ? 'opacity-100' : 'opacity-0 pointer-events-none'"
    >
      <div class="flex items-center gap-4">
        <button @click="$router.back()" class="p-2 hover:bg-black/5 rounded-full">
          <ChevronLeft class="w-6 h-6" />
        </button>
        <h2 class="font-display italic text-lg">{{ book?.title }}</h2>
      </div>
      
      <div class="flex items-center gap-6">
        <!-- 字体调节 -->
        <div class="flex items-center gap-3 bg-black/5 px-4 py-1 rounded-full">
          <span class="text-xs uppercase font-bold tracking-widest opacity-40">Size</span>
          <button @click="fontSize > 12 && fontSize--" class="p-1 hover:text-gold">-</button>
          <span class="font-display font-bold w-4 text-center">{{ fontSize }}</span>
          <button @click="fontSize < 32 && fontSize++" class="p-1 hover:text-gold">+</button>
        </div>

        <!-- 主题切换 -->
        <button @click="toggleTheme" class="p-2 hover:bg-black/5 rounded-full">
          <Sun v-if="theme === 'dark'" class="w-5 h-5" />
          <Moon v-else class="w-5 h-5" />
        </button>
      </div>
    </header>

    <!-- 正文区域 -->
    <main 
      class="flex-1 max-w-3xl mx-auto px-6 py-24 sm:px-12"
      @mousemove="handleMouseMove"
      @click="showControls = !showControls"
    >
      <div 
        class="font-serif leading-relaxed space-y-8 select-none whitespace-pre-wrap text-justify"
        :style="{ fontSize: fontSize + 'px' }"
      >
        {{ book?.content }}
      </div>
      
      <!-- 完读触发器 (可见度侦测) -->
      <div ref="endRef" class="h-20 flex items-center justify-center mt-20 border-t border-current/10 italic opacity-40">
        “全书完 · THE END”
      </div>
    </main>

    <!-- 底部进度条 -->
    <div class="fixed bottom-0 left-0 w-full h-1 bg-black/5">
      <div 
        class="h-full bg-gold transition-all duration-300" 
        :style="{ width: scrollProgress + '%' }"
      ></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { ChevronLeft, Moon, Sun } from 'lucide-vue-next'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const route = useRoute()
const book = ref(null)
const fontSize = ref(20)
const theme = ref('sepia')
const showControls = ref(true)
const scrollProgress = ref(0)
const endRef = ref<HTMLElement | null>(null)
let lastMove = 0

const fetchBook = async () => {
  try {
    const res = await axios.get(`/api/v1/books/${route.params.id}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    book.value = res.data.data
  } catch (e) {
    ElMessage.error('无法载入古卷')
  }
}

const toggleTheme = () => {
  theme.value = theme.value === 'sepia' ? 'dark' : 'sepia'
}

const handleMouseMove = () => {
  showControls.value = true
  lastMove = Date.now()
  setTimeout(() => {
    if (Date.now() - lastMove >= 3000) {
      showControls.value = false
    }
  }, 3000)
}

const updateProgress = () => {
  const winScroll = document.documentElement.scrollTop
  const height = document.documentElement.scrollHeight - document.documentElement.clientHeight
  scrollProgress.value = (winScroll / height) * 100
}

// 自动打卡完读
const observer = new IntersectionObserver((entries) => {
  if (entries[0].isIntersecting && book.value) {
    recordFinish()
  }
}, { threshold: 1.0 })

const recordFinish = async () => {
  try {
    await axios.post('/api/v1/behavior', {
      bookId: route.params.id,
      behaviorType: 4 // 完读
    }, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    ElMessage.success('恭喜完读，智慧+1')
  } catch (e) {}
}

onMounted(() => {
  fetchBook()
  window.addEventListener('scroll', updateProgress)
  if (endRef.value) observer.observe(endRef.value)
})

onUnmounted(() => {
  window.removeEventListener('scroll', updateProgress)
  observer.disconnect()
})
</script>

<style scoped>
.font-serif {
  font-family: 'Libre Baskerville', 'Noto Serif SC', serif;
}
</style>
