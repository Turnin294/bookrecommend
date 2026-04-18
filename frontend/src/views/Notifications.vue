<template>
  <div class="max-w-3xl mx-auto space-y-10">
    <div class="flex items-center justify-between border-b border-ink/5 pb-6">
      <h2 class="font-display text-4xl text-ink">通知消息 / LOGS</h2>
      <button @click="markAllRead" class="text-[10px] font-bold uppercase tracking-widest text-gold hover:underline">
        全部标记为已读
      </button>
    </div>

    <div v-if="notifications.length === 0" class="py-20 text-center text-ink/20 italic font-serif">
      “空谷传声，静候佳音”
    </div>

    <div v-else class="space-y-6">
      <div 
        v-for="n in notifications" 
        :key="n.id"
        class="p-6 border border-ink/5 bg-white shadow-inner-soft group transition-all"
        :class="n.isRead === 0 ? 'border-l-4 border-l-gold' : 'opacity-60'"
      >
        <div class="flex justify-between items-start mb-2">
          <div class="flex items-center gap-3">
            <span class="text-[10px] font-bold uppercase tracking-widest px-2 py-0.5" 
                  :class="n.type === 2 ? 'bg-gold text-ink' : 'bg-ink text-ivory'">
              {{ n.type === 2 ? 'Recommend' : 'System' }}
            </span>
            <h4 class="font-bold text-ink">{{ n.title }}</h4>
          </div>
          <span class="text-[10px] text-ink/30 font-sans tracking-tighter">{{ n.createdAt }}</span>
        </div>
        <p class="text-sm text-ink/60 leading-relaxed">{{ n.content }}</p>
        <div v-if="n.isRead === 0" class="mt-4">
          <button @click="markRead(n.id)" class="text-[10px] font-bold uppercase tracking-widest text-ink/40 hover:text-gold">
            标记已读
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const notifications = ref([])

const fetchNotifications = async () => {
  try {
    const res = await axios.get('/api/v1/notifications', {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    notifications.value = res.data.data.list
  } catch (e) {}
}

const markRead = async (id: number) => {
  try {
    await axios.put(`/api/v1/notifications/${id}/read`, {}, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    fetchNotifications()
  } catch (e) {}
}

const markAllRead = async () => {
  try {
    await axios.put(`/api/v1/notifications/read-all`, {}, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    ElMessage.success('已全部标记为已读')
    fetchNotifications()
  } catch (e) {}
}

onMounted(fetchNotifications)
</script>
