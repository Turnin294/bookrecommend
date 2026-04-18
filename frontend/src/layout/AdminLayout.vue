<template>
  <div class="flex min-h-screen bg-ivory">
    <aside class="w-64 border-r border-ink/5 bg-ink text-ivory flex flex-col sticky top-0 h-screen">
      <div class="p-8 border-b border-white/10 flex items-center gap-3">
        <div class="w-8 h-8 bg-gold rounded-sm flex items-center justify-center font-display text-ink font-bold">A</div>
        <span class="font-display tracking-widest text-lg">Admin Center</span>
      </div>
      
      <nav class="flex-1 p-6 space-y-2">
        <router-link 
          v-for="item in adminNav" 
          :key="item.path"
          :to="item.path"
          class="flex items-center gap-4 px-4 py-3 rounded transition-colors"
          :class="$route.path === item.path ? 'bg-white/10 text-gold' : 'text-ivory/50 hover:text-ivory hover:bg-white/5'"
        >
          <component :is="item.icon" class="w-5 h-5" />
          <span class="text-xs font-bold uppercase tracking-widest">{{ item.name }}</span>
        </router-link>
      </nav>

      <div class="p-8 border-t border-white/10 space-y-4">
        <router-link to="/" class="text-[10px] font-bold uppercase tracking-widest text-gold hover:underline flex items-center gap-2">
          <ChevronLeft class="w-3 h-3" /> 返回档案馆首页
        </router-link>
      </div>
    </aside>

    <main class="flex-1 overflow-x-hidden">
      <header class="h-20 bg-white border-b border-ink/5 px-10 flex items-center justify-between sticky top-0 z-50">
        <h2 class="font-display text-xl text-ink tracking-wide capitalize">{{ $route.name }}</h2>
        <div class="text-[10px] font-bold uppercase tracking-widest text-ink/40">系统管理权限已激活</div>
      </header>
      
      <div class="p-10">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { 
  LayoutDashboard, 
  Users, 
  BookOpen, 
  ChevronLeft 
} from 'lucide-vue-next'

const adminNav = [
  { name: '数据看板', path: '/admin/dashboard', icon: LayoutDashboard },
  { name: '用户管理', path: '/admin/users', icon: Users },
  { name: '图书管理', path: '/admin/books', icon: BookOpen },
]
</script>
