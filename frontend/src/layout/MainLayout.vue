<template>
  <div class="flex min-h-screen bg-ivory">
    <!-- 侧边栏：现代极简设计 -->
    <aside class="w-20 lg:w-64 border-r border-ink/5 bg-white/50 backdrop-blur-md flex flex-col h-screen sticky top-0">
      <div class="h-24 flex items-center justify-center border-b border-ink/5">
        <span class="font-display text-2xl tracking-tighter hidden lg:block">象牙档案馆.</span>
        <span class="font-display text-2xl lg:hidden">IA</span>
      </div>
      
      <nav class="flex-1 py-8 space-y-2 px-4">
        <router-link 
          v-for="item in navItems" 
          :key="item.path"
          :to="item.path"
          class="flex items-center gap-4 px-4 py-3 group transition-all rounded-lg"
          :class="$route.path === item.path ? 'bg-ink text-ivory' : 'text-ink/60 hover:bg-ink/5 hover:text-ink'"
        >
          <component :is="item.icon" class="w-5 h-5" />
          <span class="text-sm font-medium tracking-wide hidden lg:block">{{ item.name }}</span>
        </router-link>
      </nav>

      <div class="p-6 border-t border-ink/5">
        <button @click="logout" class="flex items-center gap-4 text-ink/40 hover:text-red-500 transition-colors">
          <LogOut class="w-5 h-5" />
          <span class="text-xs font-bold uppercase tracking-widest hidden lg:block">退出登录</span>
        </button>
      </div>
    </aside>

    <!-- 主内容区 -->
    <main class="flex-1 min-w-0">
      <header class="h-24 bg-ivory/80 backdrop-blur-sm sticky top-0 z-40 px-10 flex items-center justify-between">
        <div class="flex items-center gap-8">
          <div class="font-display italic text-lg text-ink/40">{{ currentPageName }}</div>
        </div>
        
        <div class="flex items-center gap-6">
          <router-link to="/notifications" class="relative text-ink/40 hover:text-ink transition-colors">
            <Bell class="w-5 h-5" />
            <span v-if="unreadCount > 0" class="absolute -top-1 -right-1 w-2 h-2 bg-gold rounded-full"></span>
          </router-link>
          
          <div class="h-8 w-[1px] bg-ink/10"></div>
          
          <div class="flex items-center gap-3 group cursor-pointer" @click="$router.push('/profile')">
            <div class="text-right hidden sm:block">
              <div class="text-xs font-bold tracking-widest uppercase">{{ username }}</div>
              <div class="text-[10px] text-ink/40 uppercase tracking-tighter">荣誉馆员 / MEMBER</div>
            </div>
            <div class="w-10 h-10 rounded-full border-2 border-ink bg-ink/10 overflow-hidden group-hover:border-gold transition-colors">
              <img v-if="avatar" :src="avatar" class="w-full h-full object-cover" />
              <div v-else class="w-full h-full flex items-center justify-center font-display text-ink uppercase">
                {{ username.charAt(0) }}
              </div>
            </div>
          </div>
        </div>
      </header>

      <div class="p-10">
        <router-view v-slot="{ Component }">
          <transition 
            name="fade" 
            mode="out-in"
          >
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { 
  Library, 
  Compass, 
  User, 
  Bell, 
  LogOut,
  Search
} from 'lucide-vue-next'
import axios from 'axios'

const router = useRouter()
const route = useRoute()
const username = ref(localStorage.getItem('username') || 'Member')
const avatar = ref(localStorage.getItem('avatar'))
const unreadCount = ref(0)

const navItems = [
  { name: '智慧推荐', path: '/', icon: Compass },
  { name: '馆藏图书', path: '/books', icon: Library },
  { name: '个人中心', path: '/profile', icon: User },
]

const currentPageName = computed(() => {
  const current = navItems.find(item => item.path === route.path)
  return current ? current.name : 'Archive View'
})

const logout = () => {
  localStorage.clear()
  router.push('/login')
}

onMounted(async () => {
  // 获取未读通知
  try {
    const res = await axios.get('/api/v1/notifications/unread-count', {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    unreadCount.value = res.data.data.count
  } catch (e) {}
})
</script>

<style>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
