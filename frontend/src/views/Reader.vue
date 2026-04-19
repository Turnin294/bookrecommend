<template>
  <div 
    class="min-h-screen transition-colors duration-500 flex flex-col"
    :class="theme === 'sepia' ? 'bg-[#F4ECD8] text-[#5B4636]' : 'bg-[#1A1A1A] text-[#D1D1D1]'"
  >
    <!-- 控制栏 -->
    <header 
      class="fixed top-0 left-0 w-full h-16 px-8 flex items-center justify-between z-50 transition-opacity duration-300 backdrop-blur-md border-b border-current/5"
      :class="showControls ? 'opacity-100' : 'opacity-0 pointer-events-none'"
    >
      <div class="flex items-center gap-4">
        <button @click="$router.back()" class="p-2 hover:bg-black/5 rounded-full">
          <ChevronLeft class="w-6 h-6" />
        </button>
        <h2 class="font-display italic text-lg truncate max-w-[200px]">{{ book?.title }}</h2>
      </div>
      
      <div class="flex items-center gap-6">
        <div class="flex items-center gap-4 bg-black/5 px-4 py-1 rounded-full">
          <button @click="fontSize > 12 && fontSize--" class="text-xl font-bold hover:text-gold">-</button>
          <span class="font-display font-bold w-8 text-center text-sm">{{ fontSize }}px</span>
          <button @click="fontSize < 40 && fontSize++" class="text-xl font-bold hover:text-gold">+</button>
        </div>
        <button @click="toggleTheme" class="p-2 hover:bg-black/5 rounded-full">
          <Sun v-if="theme === 'dark'" class="w-5 h-5" />
          <Moon v-else class="w-5 h-5" />
        </button>
      </div>
    </header>

    <!-- 正文区域 -->
    <main 
      class="flex-1 w-full max-w-4xl mx-auto px-6 py-24 sm:px-16 relative"
      @mousemove="handleMouseMove"
    >
      <div 
        class="font-serif leading-[1.8] space-y-8 select-text whitespace-pre-wrap text-justify break-words overflow-wrap-anywhere"
        :style="{ fontSize: fontSize + 'px' }"
      >
        {{ currentChapterContent }}
      </div>

      <!-- 分页控制 -->
      <div class="mt-20 py-10 border-t border-current/10 flex items-center justify-between">
        <button 
          @click="prevPage" 
          :disabled="currentPage === 0"
          class="px-8 py-3 border border-current/20 hover:border-gold hover:text-gold disabled:opacity-20 disabled:cursor-not-allowed transition-all font-display tracking-widest text-xs uppercase"
        >
          上一页 / PREV
        </button>
        
        <div class="text-[10px] font-bold uppercase tracking-widest opacity-40">
          Page {{ currentPage + 1 }} of {{ totalPages }}
        </div>

        <button 
          @click="nextPage" 
          :disabled="currentPage >= totalPages - 1"
          class="px-8 py-3 border border-current/20 hover:border-gold hover:text-gold disabled:opacity-20 disabled:cursor-not-allowed transition-all font-display tracking-widest text-xs uppercase"
        >
          下一页 / NEXT
        </button>
      </div>
    </main>

    <!-- 底部总进度 -->
    <div class="fixed bottom-0 left-0 w-full h-1 bg-black/5">
      <div 
        class="h-full bg-gold transition-all duration-300" 
        :style="{ width: ((currentPage + 1) / totalPages * 100) + '%' }"
      ></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ChevronLeft, Moon, Sun } from 'lucide-vue-next'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const route = useRoute()
const book = ref(null)
const fontSize = ref(20)
const theme = ref('sepia')
const showControls = ref(true)
const currentPage = ref(0)
const pageSize = 3000 // 每页显示 3000 字，保证渲染流畅

const chapters = computed(() => {
  if (!book.value?.content) return []
  const text = book.value.content
  const res = []
  for (let i = 0; i < text.length; i += pageSize) {
    res.push(text.substring(i, i + pageSize))
  }
  return res
})

const totalPages = computed(() => chapters.value.length)
const currentChapterContent = computed(() => chapters.value[currentPage.value] || '')

const fetchBook = async () => {
  try {
    const res = await axios.get(`/api/v1/books/${route.params.id}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    book.value = res.data.data
  } catch (e) {
    ElMessage.error('读取卷轴失败')
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value - 1) {
    currentPage.value++
    window.scrollTo({ top: 0, behavior: 'smooth' })
  } else {
    recordFinish()
  }
}

const prevPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

const toggleTheme = () => {
  theme.value = theme.value === 'sepia' ? 'dark' : 'sepia'
}

let lastMove = 0
const handleMouseMove = () => {
  showControls.value = true
  lastMove = Date.now()
  // 3秒无操作自动隐藏
}

const recordFinish = async () => {
  try {
    await axios.post('/api/v1/behavior', {
      bookId: route.params.id,
      behaviorType: 4
    }, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    ElMessage.success('阅读达成，思想已升华')
  } catch (e) {}
}

onMounted(fetchBook)
</script>

<style scoped>
.font-serif {
  font-family: 'Libre Baskerville', 'Noto Serif SC', serif;
}
/* 优化字体切换过渡 */
.transition-font-size {
  transition: font-size 0.1s ease-out;
}
</style>
