<template>
  <div class="min-h-screen grid grid-cols-1 lg:grid-cols-2">
    <!-- 左侧：视觉艺术区 (同登录页保持一致) -->
    <div class="hidden lg:flex relative bg-ink items-center justify-center p-20 overflow-hidden">
      <div class="absolute inset-0 opacity-20 pointer-events-none">
        <div class="absolute top-0 left-0 w-full h-full bg-[radial-gradient(circle_at_50%_50%,_#333,_transparent_70%)]"></div>
      </div>
      <div class="relative z-10 max-w-md">
        <h1 class="font-display text-ivory text-7xl mb-8 leading-tight">Join The<br/>Archive.</h1>
        <p class="font-serif italic text-gold text-xl leading-relaxed opacity-80 border-l-2 border-gold pl-6">
          “开启您的私人阅读纪元。”
        </p>
      </div>
    </div>

    <!-- 右侧：注册操作区 -->
    <div class="flex items-center justify-center bg-ivory p-8">
      <div class="w-full max-w-sm">
        <div class="mb-12">
          <h2 class="font-display text-4xl text-ink mb-2">申请加入</h2>
          <p class="text-ink/60 font-sans">填写以下信息，即刻开启智能推荐之旅</p>
        </div>

        <el-form :model="form" @submit.prevent="handleRegister" class="space-y-6">
          <div class="space-y-1">
            <label class="text-xs uppercase tracking-widest text-ink/40 font-bold">用户名 / USERNAME</label>
            <el-input v-model="form.username" placeholder="建议使用常用笔名" />
          </div>

          <div class="space-y-1">
            <label class="text-xs uppercase tracking-widest text-ink/40 font-bold">邮箱 / EMAIL</label>
            <el-input v-model="form.email" placeholder="用于找回密码及接收通知" />
          </div>

          <div class="space-y-1">
            <label class="text-xs uppercase tracking-widest text-ink/40 font-bold">安全密码 / PASSWORD</label>
            <el-input v-model="form.password" type="password" show-password placeholder="至少 6 位字符" />
          </div>

          <button 
            type="submit" 
            class="w-full bg-ink text-ivory py-4 font-display hover:bg-gold transition-colors duration-300 mt-6"
            :disabled="loading"
          >
            {{ loading ? '正在建立档案...' : '提交加入申请' }}
          </button>
        </el-form>

        <div class="mt-8 text-center">
          <p class="text-sm text-ink/60">
            已有档案？ 
            <router-link to="/login" class="text-gold font-bold hover:underline">返回登录</router-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const form = reactive({
  username: '',
  password: '',
  email: ''
})

const handleRegister = async () => {
  if (!form.username || !form.password || !form.email) {
    ElMessage.warning('请完整填写每一项')
    return
  }
  
  loading.value = true
  try {
    const res = await axios.post('/api/v1/auth/register', form)
    if (res.data.code === 200) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>
