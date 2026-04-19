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

    <div class="grid grid-cols-1 lg:grid-cols-12 gap-16 items-start">
      <!-- 左侧：封面展示 -->
      <div class="lg:col-span-5 flex justify-center lg:justify-start sticky top-32">
        <div 
          class="relative group cursor-pointer inline-block"
          @click="$router.push(`/books/${book.id}/read`)"
        >
          <div class="absolute -inset-4 bg-gold/5 blur-2xl group-hover:bg-gold/10 transition-colors"></div>
          <img 
            :src="book.cover" 
            class="relative w-72 lg:w-96 shadow-[20px_20px_60px_-15px_rgba(0,0,0,0.3)] border border-white/20 transition-transform duration-700 group-hover:scale-[1.02]"
          />
          <div class="absolute inset-0 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity bg-black/20 backdrop-blur-[2px]">
             <div class="bg-ivory text-ink px-6 py-2 font-display tracking-widest text-xs uppercase shadow-xl">开始阅读 / READ</div>
          </div>
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

        <div class="flex flex-wrap gap-3">
          <span v-for="tag in book.tags" :key="tag" class="px-4 py-1.5 border border-ink/10 text-[10px] font-bold uppercase tracking-widest text-ink/60 cursor-default">
            {{ tag }}
          </span>
        </div>

        <div class="space-y-4">
          <h3 class="text-xs font-bold uppercase tracking-widest text-ink/20 border-b border-ink/5 pb-2">图书简介 / DESCRIPTION</h3>
          <p class="font-serif italic text-lg leading-relaxed text-ink/80 first-letter:text-5xl first-letter:font-display first-letter:float-left first-letter:mr-3 first-letter:mt-1">
            {{ book.description || '该书暂无详细描述。' }}
          </p>
        </div>

        <!-- 操作区 -->
        <div class="pt-8 space-y-6">
          <button 
            @click="$router.push(`/books/${book.id}/read`)"
            class="w-full bg-gold text-ink py-5 font-display text-xl tracking-[0.3em] hover:bg-ink hover:text-ivory transition-all duration-500 shadow-xl shadow-gold/20 flex items-center justify-center gap-4 group"
          >
            <BookOpen class="w-6 h-6 group-hover:rotate-12 transition-transform" />
            开始阅读 / START READING
          </button>

          <div class="flex items-center gap-6">
            <button 
              @click="handleAction(2)"
              class="flex-1 border px-8 py-4 font-display text-sm tracking-widest transition-all flex items-center justify-center gap-3 group"
              :class="isFavorited ? 'bg-ink text-ivory border-ink' : 'border-ink/10 text-ink hover:bg-ink/5'"
            >
              <Bookmark class="w-4 h-4" :class="isFavorited ? 'fill-gold stroke-gold' : 'group-hover:fill-current'" />
              {{ isFavorited ? '已加入收藏' : '加入收藏' }}
            </button>
            
            <el-popover placement="top" :width="240" trigger="click">
              <template #reference>
                <button class="w-14 h-14 border border-ink/10 flex items-center justify-center hover:border-gold hover:text-gold transition-colors" :class="userScore > 0 ? 'text-gold border-gold/40 bg-gold/5' : ''">
                  <Star class="w-5 h-5" :class="userScore > 0 ? 'fill-current' : ''" />
                </button>
              </template>
              <div class="flex flex-col gap-4 p-2">
                <div class="text-[10px] font-bold uppercase tracking-widest text-ink/40 text-center">给此书评分与简评</div>
                <div class="flex justify-center"><el-rate v-model="ratingValue" /></div>
                <el-input v-model="commentValue" type="textarea" :rows="3" placeholder="写下你的简评..." class="text-xs" />
                <button @click="handleRateClick" class="w-full bg-ink text-ivory py-2 text-[10px] uppercase tracking-widest hover:bg-gold transition-colors">提交评价</button>
              </div>
            </el-popover>
            
            <button @click="handleAction(4)" class="w-14 h-14 border border-ink/10 flex items-center justify-center hover:border-gold transition-colors" :class="isFinished ? 'text-green-600 border-green-200 bg-green-50' : 'hover:text-gold'">
              <CheckCircle class="w-5 h-5" :class="isFinished ? 'fill-current' : ''" />
            </button>
          </div>
        </div>

        <!-- 关联推荐 -->
        <div class="pt-20 border-t border-ink/5">
          <h3 class="text-[10px] font-bold uppercase tracking-[0.3em] text-ink/30 mb-8 text-center">馆员还在看 / READERS ALSO LIKED</h3>
          <div v-if="relatedBooks.length === 0" class="py-10 text-center text-ink/10 italic text-sm">“曲高和寡，暂无关联”</div>
          <div v-else class="grid grid-cols-2 sm:grid-cols-5 gap-8">
            <div v-for="item in relatedBooks" :key="item.id" @click="$router.push(`/books/${item.id}`)" class="group cursor-pointer space-y-3">
              <div class="aspect-[3/4] overflow-hidden border border-ink/5">
                <img :src="item.cover" class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500" />
              </div>
              <div class="text-[9px] font-bold uppercase text-gold line-clamp-1">{{ item.author }}</div>
              <div class="text-[11px] font-display text-ink leading-tight line-clamp-1 group-hover:text-gold transition-colors">{{ item.title }}</div>
            </div>
          </div>
        </div>

        <!-- 读者书评 -->
        <div class="pt-20 space-y-8">
          <h3 class="text-xs font-bold uppercase tracking-widest text-ink/20 border-b border-ink/5 pb-2">读者书评 / REVIEWS</h3>
          <div v-if="publicReviews.length === 0" class="py-10 text-center text-ink/10 italic font-serif text-sm">“石沉大海，尚无回响。”</div>
          <div v-else class="space-y-6">
            <div v-for="rev in publicReviews" :key="rev.id" class="p-6 bg-white/40 border border-ink/5 relative overflow-hidden group hover:bg-white/80 transition-colors">
              <div class="flex items-start gap-4">
                <div class="w-10 h-10 rounded-full bg-ink/5 flex items-center justify-center font-display text-sm font-bold">{{ rev.username.charAt(0) }}</div>
                <div class="flex-1 space-y-2">
                  <div class="flex justify-between items-center">
                    <span class="text-xs font-bold uppercase tracking-widest">{{ rev.username }}</span>
                    <el-rate :model-value="rev.score" disabled size="small" />
                  </div>
                  <p class="font-serif italic text-sm text-ink/70 leading-relaxed">“{{ rev.comment }}”</p>
                  <div class="text-[9px] text-ink/20 uppercase tracking-tighter">{{ rev.createdAt }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { Bookmark, Star, CheckCircle, BookOpen, ChevronLeft } from 'lucide-vue-next'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const route = useRoute()
const loading = ref(true)
const book = ref(null)
const isFavorited = ref(false)
const isFinished = ref(false)
const userScore = ref(0)
const ratingValue = ref(0)
const commentValue = ref('')
const publicReviews = ref([])
const relatedBooks = ref([])

const fetchBook = async () => {
  loading.value = true
  try {
    const res = await axios.get(`/api/v1/books/${route.params.id}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    if (res.data.code === 200) {
        book.value = res.data.data
        fetchUserBehavior()
        fetchPublicReviews()
        fetchRelatedBooks()
        recordBehavior(1)
    }
  } catch (e) {} finally { loading.value = false }
}

const fetchUserBehavior = async () => {
  try {
    const res = await axios.get('/api/v1/behavior/my', { headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } })
    const bId = parseInt(route.params.id as string)
    isFavorited.value = res.data.data.favorites.some((i: any) => i.bookId === bId)
    isFinished.value = res.data.data.finished.some((i: any) => i.bookId === bId)
    const rate = res.data.data.ratings.find((i: any) => i.bookId === bId)
    if (rate) { userScore.value = rate.score; ratingValue.value = rate.score; commentValue.value = rate.comment; }
  } catch(e) {}
}

const fetchPublicReviews = async () => {
  try {
    const res = await axios.get(`/api/v1/behavior/book/${route.params.id}/reviews`, { headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } })
    publicReviews.value = res.data.data
  } catch (e) {}
}

const fetchRelatedBooks = async () => {
  try {
    const res = await axios.get(`/api/v1/books/${route.params.id}/related`, { headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } })
    relatedBooks.value = res.data.data
  } catch (e) {}
}

const recordBehavior = async (type: number, score: number | null = null, comment: string | null = null) => {
  try {
    await axios.post('/api/v1/behavior', { bookId: route.params.id, behaviorType: type, score, comment }, { headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } })
  } catch (e) {}
}

const handleRateClick = () => {
    if (ratingValue.value === 0) return ElMessage.warning('请先选择星级')
    userScore.value = ratingValue.value
    recordBehavior(3, ratingValue.value, commentValue.value)
    ElMessage.success('评价成功！')
    fetchPublicReviews()
}

const handleAction = (type: number) => {
  if (type === 2) isFavorited.value = !isFavorited.value
  if (type === 4) isFinished.value = true
  recordBehavior(type)
  ElMessage.success(type === 2 ? (isFavorited.value ? '已加入收藏' : '已取消收藏') : '已标记为完读')
}

onMounted(fetchBook)
</script>

<style scoped>
p::after { content: ""; display: table; clear: both; }
</style>
