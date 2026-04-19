<template>
  <div class="flex gap-12">
    <!-- 左侧：分类导航 -->
    <aside class="w-48 shrink-0 space-y-8">
      <div>
        <h3 class="text-[10px] font-bold uppercase tracking-[0.3em] text-ink/30 mb-6">分类检索 / CATEGORIES</h3>
        <ul class="space-y-4">
          <li 
            class="cursor-pointer text-sm transition-colors"
            :class="!selectedCategory ? 'text-gold font-bold underline underline-offset-8' : 'text-ink/60 hover:text-ink'"
            @click="selectCategory(null)"
          >
            全部馆藏
          </li>
          <li 
            v-for="cat in categories" 
            :key="cat.id"
            class="group"
          >
            <div 
              class="cursor-pointer text-sm transition-colors py-1"
              :class="selectedCategory === cat.id ? 'text-gold font-bold underline underline-offset-8' : 'text-ink/60 hover:text-ink'"
              @click="selectCategory(cat.id)"
            >
              {{ cat.name }}
            </div>
            <!-- 子分类 (简易处理) -->
            <ul v-if="cat.children && cat.children.length > 0" class="ml-4 mt-2 space-y-2 border-l border-ink/5 pl-4">
              <li 
                v-for="sub in cat.children" 
                :key="sub.id"
                class="text-xs cursor-pointer transition-colors"
                :class="selectedCategory === sub.id ? 'text-gold' : 'text-ink/40 hover:text-ink'"
                @click.stop="selectCategory(sub.id)"
              >
                {{ sub.name }}
              </li>
            </ul>
          </li>
        </ul>
      </div>

      <!-- 热门搜索 / HOT SEARCH -->
      <div>
        <h3 class="text-[10px] font-bold uppercase tracking-[0.3em] text-ink/30 mb-6">热门搜索 / HOT SEARCH</h3>
        <div class="flex flex-wrap gap-2">
          <span 
            v-for="tag in ['科幻', '刘慈欣', '历史', '哲学', 'Java', '东野圭吾', '推理']" 
            :key="tag"
            @click="searchByTag(tag)"
            class="px-3 py-1 bg-ink/5 text-[10px] uppercase tracking-wider text-ink/60 hover:bg-gold hover:text-ivory cursor-pointer transition-colors"
          >
            {{ tag }}
          </span>
        </div>
      </div>
    </aside>

    <!-- 右侧：主内容 -->
    <div class="flex-1 space-y-8">
      <!-- 搜索与工具栏 -->
      <div class="flex items-center justify-between border-b border-ink/5 pb-6">
        <div class="flex-1 max-w-md relative">
          <Search class="absolute left-0 top-1/2 -translate-y-1/2 w-4 h-4 text-ink/20" />
          <input 
            v-model="queryParams.keyword"
            @keyup.enter="fetchBooks"
            placeholder="搜索书名、作者或 ISBN..." 
            class="w-full bg-transparent pl-8 pr-4 py-2 text-sm focus:outline-none placeholder:text-ink/20 font-serif italic"
          />
        </div>
        <div class="text-[10px] font-bold uppercase tracking-widest text-ink/40">
          Showing {{ books.length }} of {{ total }} Books
        </div>
      </div>

      <!-- 图书列表 -->
      <div v-if="loading" class="grid grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-x-8 gap-y-12">
        <div v-for="i in 8" :key="i" class="space-y-4">
          <div class="aspect-[3/4] bg-ink/5 animate-pulse"></div>
          <div class="h-4 w-3/4 bg-ink/5 animate-pulse"></div>
        </div>
      </div>

      <div v-else-if="books.length > 0" class="grid grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-x-8 gap-y-12">
        <div 
          v-for="book in books" 
          :key="book.id"
          class="group cursor-pointer"
          @click="goToDetail(book.id)"
        >
          <div class="relative aspect-[3/4] mb-4 overflow-hidden border border-ink/5">
            <img 
              :src="book.cover || 'https://via.placeholder.com/300x400?text=The+Archive'" 
              class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105" 
            />
            <div class="absolute inset-0 bg-ink opacity-0 group-hover:opacity-10 transition-opacity"></div>
          </div>
          <div class="space-y-1">
            <div class="text-[10px] uppercase tracking-widest text-gold font-bold">{{ book.author }}</div>
            <h4 class="font-display text-lg text-ink line-clamp-1 group-hover:text-gold transition-colors">{{ book.title }}</h4>
            <div class="flex gap-2">
              <span v-for="tag in book.tags?.slice(0, 2)" :key="tag" class="text-[9px] text-ink/30 border border-ink/10 px-1 italic">
                #{{ tag }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="py-20 text-center space-y-4">
        <div class="font-display italic text-2xl text-ink/20">“虚无缥缈，未有所获”</div>
        <p class="text-xs text-ink/40 uppercase tracking-widest">未能找到匹配的馆藏图书</p>
      </div>

      <!-- 分页 (ElementPlus 风格定制) -->
      <div class="flex justify-center pt-10">
        <el-pagination
          v-model:current-page="queryParams.page"
          :page-size="queryParams.size"
          layout="prev, pager, next"
          :total="total"
          @current-change="fetchBooks"
          class="archive-pagination"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from 'lucide-vue-next'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(true)

// ...

const goToDetail = (id: any) => {
  if (!id) {
    ElMessage.error('书籍索引失效')
    return
  }
  router.push(`/books/${id}`)
}
const categories = ref([])
const books = ref([])
const total = ref(0)
const selectedCategory = ref(null)

const queryParams = reactive({
  keyword: '',
  categoryId: null,
  page: 1,
  size: 12
})

const fetchCategories = async () => {
  try {
    const res = await axios.get('/api/v1/categories', {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    categories.value = res.data.data
  } catch (e) {}
}

const fetchBooks = async () => {
  loading.value = true
  try {
    const { keyword, categoryId, page, size } = queryParams
    const res = await axios.get('/api/v1/books', {
      params: { keyword, categoryId, page, size },
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    books.value = res.data.data.list
    total.value = res.data.data.total
  } catch (e) {
    ElMessage.error('获取图书失败')
  } finally {
    loading.value = false
  }
}

const selectCategory = (id: any) => {
  selectedCategory.value = id
  queryParams.categoryId = id
  queryParams.page = 1
  fetchBooks()
}

const searchByTag = (tag: string) => {
  queryParams.keyword = tag
  queryParams.page = 1
  fetchBooks()
}

onMounted(() => {
  fetchCategories()
  fetchBooks()
})
</script>

<style>
.archive-pagination .el-pager li {
  background: transparent !important;
  font-family: 'Playfair Display', serif;
  color: #1A1A1A;
}
.archive-pagination .el-pager li.is-active {
  color: #C5A059 !important;
  font-weight: bold;
  text-decoration: underline;
}
.archive-pagination .btn-next, .archive-pagination .btn-prev {
  background: transparent !important;
}
</style>
