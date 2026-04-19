<template>
  <div class="space-y-12">
    <!-- 顶部欢迎语 -->
    <section class="max-w-2xl">
      <h2 class="font-display text-5xl text-ink mb-6">为您策划。</h2>
      <p class="text-ink/60 leading-relaxed italic border-l-4 border-gold pl-6">
        基于您的阅读偏好和近期行为，档案馆为您精选了以下馆藏。每一本书都是一次跨越时空的对话。
      </p>
    </section>

    <!-- 推荐列表 -->
    <div v-if="loading" class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-10">
      <div v-for="i in 6" :key="i" class="h-64 bg-ink/5 animate-pulse"></div>
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-10">
      <div 
        v-for="book in recommendations" 
        :key="book.bookId"
        class="group cursor-pointer"
        @click="goToDetail(book)"
      >
        <div class="relative aspect-[3/4] overflow-hidden bg-ink/5 mb-6">
          <img 
            :src="book.cover || 'https://via.placeholder.com/300x400?text=The+Archive'" 
            class="w-full h-full object-cover transition-transform duration-700 group-hover:scale-110"
          />
          <!-- 匹配度角标 -->
          <div class="absolute top-4 right-4 bg-white/90 backdrop-blur-md px-3 py-1 text-[10px] font-bold tracking-widest uppercase border border-ink/5">
            {{ (book.score * 100).toFixed(0) }}% 契合
          </div>
          <!-- 悬浮遮罩 -->
          <div class="absolute inset-0 bg-ink/60 opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex flex-col justify-end p-8 text-ivory">
            <div class="text-xs font-bold uppercase tracking-widest text-gold mb-2">推荐理由 / REASON</div>
            <p class="font-serif italic text-sm">{{ book.reason }}</p>
          </div>
        </div>
        
        <div class="space-y-2">
          <div class="text-xs uppercase tracking-[0.2em] text-gold font-bold">{{ book.author }}</div>
          <h3 class="font-display text-2xl group-hover:text-gold transition-colors line-clamp-1">{{ book.title }}</h3>
          <div class="text-[10px] text-ink/40 uppercase tracking-widest">{{ book.algorithm }} 智慧算法</div>
        </div>
      </div>
    </div>

    <!-- 刷新按钮 -->
    <div class="flex justify-center pt-10">
      <button 
        @click="fetchRecommendations"
        class="group flex flex-col items-center gap-4 text-ink/40 hover:text-ink transition-colors"
      >
        <div class="w-12 h-12 rounded-full border border-ink/10 flex items-center justify-center group-hover:border-gold group-hover:rotate-180 transition-all duration-700">
          <RefreshCw class="w-5 h-5" />
        </div>
        <span class="text-[10px] font-bold uppercase tracking-widest">刷新推荐列表 / REFRESH</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { RefreshCw } from 'lucide-vue-next'
import axios from 'axios'
import { ElMessage } from 'element-plus'

import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(true)
const recommendations = ref([])

const goToDetail = (book: any) => {
  const id = book.bookId || book.id
  if (!id) {
    console.error('Invalid Book ID:', book)
    ElMessage.error('书籍索引失效，请刷新页面')
    return
  }
  router.push(`/books/${id}`)
}

const fetchRecommendations = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/v1/recommend?size=6', {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    recommendations.value = res.data.data
  } catch (e: any) {
    ElMessage.error('无法加载推荐列表')
  } finally {
    loading.value = false
  }
}

onMounted(fetchRecommendations)
</script>
