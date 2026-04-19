<template>
  <div class="max-w-4xl mx-auto space-y-16">
    <section class="flex items-center gap-10">
      <div class="w-32 h-32 rounded-full border-4 border-ink bg-ink/5 overflow-hidden">
        <img v-if="user.avatar" :src="user.avatar" class="w-full h-full object-cover" />
        <div v-else class="w-full h-full flex items-center justify-center font-display text-4xl text-ink uppercase">
          {{ user.username?.charAt(0) }}
        </div>
      </div>
      <div class="space-y-2">
        <h2 class="font-display text-4xl text-ink">{{ user.username }}</h2>
        <p class="text-xs font-bold uppercase tracking-widest text-gold italic">入馆年份：2024</p>
      </div>
    </section>

    <!-- 兴趣偏好：推荐算法的核心输入 -->
    <section class="space-y-8 bg-white/40 p-10 border border-ink/5 backdrop-blur-sm">
      <div class="flex items-center justify-between border-b border-ink/5 pb-4">
        <h3 class="font-display text-2xl text-ink">兴趣画像 / INTERESTS</h3>
        <button 
          @click="saveProfile" 
          class="text-[10px] font-bold uppercase tracking-[0.2em] px-6 py-2 bg-ink text-ivory hover:bg-gold transition-colors"
        >
          保存更改 / SAVE
        </button>
      </div>

      <div class="space-y-6">
        <p class="text-xs text-ink/40 font-serif italic">选出您感兴趣的标签，我们将根据这些标签在浩瀚书海中为您指引方向。</p>
        
        <div class="flex flex-wrap gap-4">
          <div 
            v-for="tag in allTags" 
            :key="tag"
            @click="toggleTag(tag)"
            class="relative px-6 py-2.5 text-xs font-bold uppercase tracking-[0.2em] cursor-pointer transition-all duration-300 border select-none flex items-center gap-2 group overflow-hidden"
            :class="user.interests?.includes(tag) 
              ? 'bg-gold text-ink border-gold shadow-lg shadow-gold/20 scale-105' 
              : 'bg-transparent text-ink/30 border-ink/10 hover:border-ink/40 hover:text-ink hover:scale-105 active:scale-95'"
          >
            <!-- 选中状态的小勾选 -->
            <transition name="scale-fade">
              <Check v-if="user.interests?.includes(tag)" class="w-3 h-3" />
            </transition>
            
            {{ tag }}
            
            <!-- 悬浮时的流动光效 -->
            <div class="absolute inset-0 bg-white/20 translate-x-[-100%] group-hover:translate-x-[100%] transition-transform duration-700 ease-in-out"></div>
          </div>
        </div>
      </div>
    </section>

    <!-- 个人资产/足迹 -->
    <section class="space-y-8">
      <el-tabs v-model="activeTab" class="archive-tabs">
        <el-tab-pane label="我的收藏 / FAVORITES" name="favorites">
          <div class="grid grid-cols-2 sm:grid-cols-4 gap-6 pt-6">
            <div v-if="myBehaviors.favorites.length === 0" class="col-span-full py-10 text-center text-ink/20 italic font-serif">
              “书架空空，虚位以待”
            </div>
            <div 
              v-else
              v-for="item in myBehaviors.favorites" 
              :key="item.id" 
              class="group cursor-pointer space-y-2"
              @click="goToDetail(item.bookId)"
            >
              <img :src="item.bookCover" class="w-full aspect-[3/4] object-cover border border-ink/5 group-hover:shadow-md transition-shadow" />
              <div class="text-xs font-bold line-clamp-1">{{ item.bookTitle }}</div>
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="我的评价 / RATINGS" name="ratings">
          <div v-if="myBehaviors.ratings.length === 0" class="py-10 text-center text-ink/20 italic font-serif">“字字珠玑，尚无评价”</div>
          <div v-else class="space-y-4 pt-6">
            <div v-for="item in myBehaviors.ratings" :key="item.id" class="flex gap-4 p-4 bg-white/40 border border-ink/5 cursor-pointer hover:bg-white/80 transition-colors" @click="goToDetail(item.bookId)">
              <img :src="item.bookCover" class="w-16 h-24 object-cover border border-ink/5" />
              <div class="flex-1 space-y-1">
                <div class="flex justify-between items-start">
                  <div class="font-bold text-sm">{{ item.bookTitle }}</div>
                  <el-rate :model-value="item.score" disabled size="small" />
                </div>
                <div class="text-xs text-ink/60 font-serif italic mt-2">"{{ item.comment || '无短评' }}"</div>
                <div class="text-[10px] text-ink/30 mt-2">{{ item.createdAt }}</div>
              </div>
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="已读记录 / FINISHED" name="finished">
          <div v-if="myBehaviors.finished.length === 0" class="py-10 text-center text-ink/20 italic font-serif">“书海浩瀚，静候启航”</div>
          <div v-else class="grid grid-cols-2 sm:grid-cols-4 gap-6 pt-6">
            <div 
              v-for="item in myBehaviors.finished" 
              :key="item.id" 
              class="group cursor-pointer space-y-2"
              @click="goToDetail(item.bookId)"
            >
              <div class="relative">
                 <img :src="item.bookCover" class="w-full aspect-[3/4] object-cover border border-ink/5 group-hover:shadow-md transition-shadow" />
                 <div class="absolute top-2 right-2 bg-green-500 text-white text-[8px] px-1.5 py-0.5 rounded-sm uppercase tracking-widest">已读</div>
              </div>
              <div class="text-xs font-bold line-clamp-1">{{ item.bookTitle }}</div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </section>

    <!-- 基本信息 -->
    <section class="grid grid-cols-1 md:grid-cols-2 gap-10">
      <div class="space-y-4">
        <label class="text-[10px] font-bold uppercase tracking-widest text-ink/40">邮箱地址 / EMAIL</label>
        <el-input v-model="user.email" placeholder="请输入您的电子邮箱" />
      </div>
      <div class="space-y-4">
        <label class="text-[10px] font-bold uppercase tracking-widest text-ink/40">账户角色 / ROLE</label>
        <div class="text-sm font-display py-2 border-b border-ink/5">{{ user.role === 1 ? '系统管理员 / ADMIN' : '正式馆员 / MEMBER' }}</div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Check } from 'lucide-vue-next'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const activeTab = ref('favorites')
const allTags = ['科幻', '历史', '哲学', '艺术', '技术', '心理学', '文学', '传记', '悬疑', '经济']

const user = ref({
  username: '',
  email: '',
  avatar: '',
  role: 0,
  interests: [] as string[]
})

const myBehaviors = ref({
  favorites: [],
  ratings: [],
  finished: []
})

const fetchUser = async () => {
  try {
    const res = await axios.get('/api/v1/user/me', {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    user.value = res.data.data
  } catch (e) {
    ElMessage.error('获取用户信息失败')
  }
}

const fetchBehaviors = async () => {
  try {
    const res = await axios.get('/api/v1/behavior/my', {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    myBehaviors.value = res.data.data
  } catch (e) {}
}

const toggleTag = (tag: string) => {
  const idx = user.value.interests.indexOf(tag)
  if (idx > -1) user.value.interests.splice(idx, 1)
  else user.value.interests.push(tag)
}

const goToDetail = (item: any) => {
  // 暴力识别：支持 bookId (驼峰), book_id (下划线), id (记录ID)
  // 这里的 item 如果直接传 ID 也要处理，如果是对象也要处理
  let id = null
  if (typeof item === 'object') {
    id = item.bookId || item.book_id || item.id
  } else {
    id = item
  }

  if (!id) {
    console.error('Invalid Item Data:', item)
    ElMessage.error('书籍索引失效，请刷新页面')
    return
  }
  router.push(`/books/${id}`)
}

const saveProfile = async () => {
  try {
    await axios.put('/api/v1/user/me', {
      email: user.value.email,
      avatar: user.value.avatar,
      interests: user.value.interests,
      favoriteCategories: []
    }, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    ElMessage.success('配置已保存，正在刷新您的推荐列表...')
    
    // 保存后顺便触发一下推荐刷新
    await axios.post('/api/v1/recommend/refresh', {}, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

onMounted(() => {
  fetchUser()
  fetchBehaviors()
})
</script>

<style scoped>
/* 针对个人中心特定的 Tabs 样式微调 */
:deep(.archive-tabs .el-tabs__item) {
  font-family: 'Playfair Display', serif;
  font-weight: bold;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  font-size: 12px;
}
</style>
