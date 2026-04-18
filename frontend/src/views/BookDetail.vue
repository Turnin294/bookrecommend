<template>
  <div v-if="loading" class="animate-pulse flex flex-col items-center py-20">
    <div class="w-64 h-96 bg-ink/5 mb-8"></div>
    <div class="w-48 h-6 bg-ink/5"></div>
  </div>

  <div v-else-if="book" class="max-w-6xl mx-auto py-10">
    <!-- 顶部导航 -->
    <div class="mb-12 flex items-center gap-4 text-[10px] font-bold uppercase tracking-widest text-ink/40">
      <router-link to="/books" class="hover:text-gold">馆藏图书</router-link>
      <span>/</span>
      <span class="text-gold">{{ book.categoryName }}</span>
      <span>/</span>
      <span class="text-ink/20 italic font-serif capitalize">{{ book.title }}</span>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-12 gap-16">
      <!-- 左侧：封面展示 -->
      <div class="lg:col-span-5 flex justify-center lg:justify-start">
        <div class="relative group">
          <div class="absolute -inset-4 bg-gold/5 blur-2xl group-hover:bg-gold/10 transition-colors"></div>
          <img 
            :src="book.cover || 'https://via.placeholder.com/300x400?text=The+Archive'" 
            class="relative w-72 lg:w-96 shadow-[20px_20px_60px_-15px_rgba(0,0,0,0.3)] border border-white/20 transition-transform duration-700 hover:scale-[1.02]"
          />
        </div>
      </div>

      <!-- 右侧：详细信息 -->
      <div class="lg:col-span-7 space-y-10">
        <div class="space-y-4">
          <div class="text-xs font-bold uppercase tracking-[0.3em] text-gold">{{ book.author }}</div>
          <h1 class="font-display text-5xl lg:text-6xl text-ink leading-tight">{{ book.title }}</h1>
          <div class="flex items-center gap-6 pt-4 text-[10px] font-bold uppercase tracking-widest text-ink/40">
            <span>ISBN: {{ book.isbn }}</span>
            <span class="w-1 h-1 bg-ink/20 rounded-full"></span>
            <span>出版日期: {{ book.publishDate }}</span>
            <span class="w-1 h-1 bg-ink/20 rounded-full"></span>
            <span>库存: {{ book.stock }}</span>
          </div>
        </div>

        <!-- 标签 -->
        <div class="flex flex-wrap gap-3">
          <span 
            v-for="tag in book.tags" 
            :key="tag"
            class="px-4 py-1.5 border border-ink/10 text-[10px] font-bold uppercase tracking-widest text-ink/60 hover:border-gold hover:text-gold cursor-default transition-colors"
          >
            {{ tag }}
          </span>
        </div>

        <!-- 简介 -->
        <div class="space-y-4">
          <h3 class="text-xs font-bold uppercase tracking-widest text-ink/20 border-b border-ink/5 pb-2">图书简介 / DESCRIPTION</h3>
          <p class="font-serif italic text-lg leading-relaxed text-ink/80 first-letter:text-5xl first-letter:font-display first-letter:float-left first-letter:mr-3 first-letter:mt-1">
            {{ book.description || '该书暂无详细描述，档案馆正在抓紧录入中。' }}
          </p>
        </div>

        <!-- 操作区 -->
        <div class="pt-8 flex items-center gap-6">
          <button 
            @click="handleAction(2)"
            class="flex-1 bg-ink text-ivory px-8 py-4 font-display text-lg tracking-widest hover:bg-gold transition-colors flex items-center justify-center gap-3 group"
          >
            <Bookmark class="w-5 h-5 group-hover:fill-current" />
            加入收藏
          </button>
          
          <button 
            @click="handleAction(3)"
            class="w-16 h-16 border border-ink/10 flex items-center justify-center hover:border-gold hover:text-gold transition-colors"
            title="评分"
          >
            <Star class="w-6 h-6" />
          </button>
          
          <button 
            @click="handleAction(4)"
            class="w-16 h-16 border border-ink/10 flex items-center justify-center hover:border-gold hover:text-gold transition-colors"
            title="标记已读"
          >
            <CheckCircle class="w-6 h-6" />
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { Bookmark, Star, CheckCircle } from 'lucide-vue-next'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const route = useRoute()
const loading = ref(true)
const book = ref(null)

const fetchBook = async () => {
  loading.value = true
  try {
    const res = await axios.get(`/api/v1/books/${route.params.id}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    book.value = res.data.data
    
    // 关键：自动记录“浏览”行为 (behaviorType=1)
    recordBehavior(1)
  } catch (e) {
    ElMessage.error('获取图书详情失败')
  } finally {
    loading.value = false
  }
}

const recordBehavior = async (type: number, score: number | null = null) => {
  try {
    await axios.post('/api/v1/behavior', {
      bookId: route.params.id,
      behaviorType: type,
      score: score
    }, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
  } catch (e) {}
}

const handleAction = (type: number) => {
  const messages = {
    2: '已加入您的私人收藏',
    3: '感谢您的评分！',
    4: '已标记为完读'
  }
  recordBehavior(type)
  ElMessage.success(messages[type as keyof typeof messages])
}

onMounted(fetchBook)
</script>

<style scoped>
/* 针对首字下沉的微调 */
p::after {
  content: "";
  display: table;
  clear: both;
}
</style>
