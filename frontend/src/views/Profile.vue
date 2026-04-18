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
        <p class="text-xs font-bold uppercase tracking-widest text-gold italic">Archives Member Since 2024</p>
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
        
        <div class="flex flex-wrap gap-3">
          <div 
            v-for="tag in allTags" 
            :key="tag"
            @click="toggleTag(tag)"
            class="px-5 py-2 text-xs font-bold uppercase tracking-widest cursor-pointer transition-all border"
            :class="user.interests?.includes(tag) ? 'bg-ink text-ivory border-ink' : 'bg-transparent text-ink/30 border-ink/10 hover:border-ink/40'"
          >
            {{ tag }}
          </div>
        </div>
      </div>
    </section>

    <!-- 基本信息 -->
    <section class="grid grid-cols-1 md:grid-cols-2 gap-10">
      <div class="space-y-4">
        <label class="text-[10px] font-bold uppercase tracking-widest text-ink/40">邮箱地址 / EMAIL</label>
        <el-input v-model="user.email" placeholder="your@email.com" />
      </div>
      <div class="space-y-4">
        <label class="text-[10px] font-bold uppercase tracking-widest text-ink/40">账户角色 / ROLE</label>
        <div class="text-sm font-display py-2 border-b border-ink/5">{{ user.role === 1 ? 'Administrator' : 'General Member' }}</div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const user = ref({
  username: '',
  email: '',
  avatar: '',
  role: 0,
  interests: [] as string[]
})

const allTags = ['科幻', '历史', '哲学', '艺术', '技术', '心理学', '文学', '传记', '悬疑', '经济']

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

const toggleTag = (tag: string) => {
  const idx = user.value.interests.indexOf(tag)
  if (idx > -1) user.value.interests.splice(idx, 1)
  else user.value.interests.push(tag)
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

onMounted(fetchUser)
</script>
