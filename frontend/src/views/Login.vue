<template>
  <div class="min-h-screen grid grid-cols-1 lg:grid-cols-2">
    <!-- 左侧：视觉艺术区 -->
    <div class="hidden lg:flex relative bg-ink items-center justify-center p-20 overflow-hidden">
      <!-- 装饰性的背景纹理 -->
      <div class="absolute inset-0 opacity-20 pointer-events-none">
        <div class="absolute top-0 left-0 w-full h-full bg-[radial-gradient(circle_at_50%_50%,_#333,_transparent_70%)]"></div>
      </div>
      
      <div class="relative z-10 max-w-md">
        <h1 class="font-display text-ivory text-7xl mb-8 leading-tight">The Ivory<br/>Archive.</h1>
        <p class="font-serif italic text-gold text-xl leading-relaxed opacity-80 border-l-2 border-gold pl-6">
          “书本是灵魂的镜子，<br/>而在这里，我们将为您寻回丢失的那一面。”
        </p>
        <div class="mt-16 text-ivory/40 text-sm font-sans tracking-widest uppercase">
          Intelligent Book Recommendation System v1.0
        </div>
      </div>
    </div>

    <!-- 右侧：登录操作区 -->
    <div class="flex items-center justify-center bg-ivory p-8">
      <div class="w-full max-w-sm">
        <div class="mb-12">
          <h2 class="font-display text-4xl text-ink mb-2">欢迎回来</h2>
          <p class="text-ink/60 font-sans">请登录您的私人档案馆账户</p>
        </div>

        <el-form :model="form" @submit.prevent="handleLogin" class="space-y-8">
          <div class="space-y-1">
            <label class="text-xs uppercase tracking-widest text-ink/40 font-bold">用户名 / USERNAME</label>
            <el-input 
              v-model="form.username" 
              placeholder="请输入您的用户名"
              input-style="font-size: 1.1rem; padding-top: 0.5rem; padding-bottom: 0.5rem;"
            />
          </div>

          <div class="space-y-1">
            <label class="text-xs uppercase tracking-widest text-ink/40 font-bold">密码 / PASSWORD</label>
            <el-input 
              v-model="form.password" 
              type="password"
              show-password
              placeholder="请输入您的安全密码"
              input-style="font-size: 1.1rem; padding-top: 0.5rem; padding-bottom: 0.5rem;"
            />
          </div>

          <div class="flex items-center justify-between pt-4">
            <button 
              type="submit" 
              class="bg-ink text-ivory px-10 py-3 font-display hover:bg-gold transition-colors duration-300"
              :disabled="loading"
            >
              {{ loading ? '验证中...' : '进入档案馆' }}
            </button>
            <a href="#" class="text-xs text-ink/40 hover:text-gold transition-colors underline-offset-4 underline">忘记密码？</a>
          </div>
        </el-form>

        <div class="mt-12 pt-8 border-t border-ink/5">
          <p class="text-sm text-ink/60">
            还没有账户？ 
            <router-link to="/register" class="text-gold font-bold hover:underline">立即申请加入</router-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const form = reactive({
  username: '',
  password: ''
})

const handleLogin = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('请填写完整的凭证')
    return
  }

  loading.value = true
  try {
    const res = await axios.post('/api/v1/auth/login', form)
    if (res.data.code === 200) {
      localStorage.setItem('token', res.data.data.token)
      localStorage.setItem('role', res.data.data.role)
      ElMessage.success('欢迎回来，' + form.username)
      
      // 根据角色跳转
      if (res.data.data.role === 1) {
        router.push('/admin/dashboard')
      } else {
        router.push('/')
      }
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '服务器连接失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 可以在这里添加局部微调 */
</style>
