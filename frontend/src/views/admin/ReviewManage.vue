<template>
  <div class="space-y-8 bg-white p-10 border border-ink/5 shadow-soft">
    <div class="flex items-center justify-between">
      <div class="space-y-1">
        <h3 class="font-display text-2xl text-ink">评价审核 / REVIEWS</h3>
        <p class="text-xs text-ink/40 uppercase tracking-widest">监控、管理及清理全馆书籍的读者评价</p>
      </div>
    </div>

    <el-table :data="reviews" style="width: 100%">
      <el-table-column label="读者 / USER" width="180">
        <template #default="scope">
          <div class="flex items-center gap-2">
            <div class="w-6 h-6 rounded-full bg-gold/20 flex items-center justify-center text-[10px] font-bold">
              {{ scope.row.username.charAt(0) }}
            </div>
            <span class="text-sm font-bold">{{ scope.row.username }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="关联图书 / BOOK" width="220">
        <template #default="scope">
          <div class="text-xs font-serif italic text-ink/60 line-clamp-1">《{{ scope.row.bookTitle }}》</div>
        </template>
      </el-table-column>

      <el-table-column label="星级" width="120">
        <template #default="scope">
          <el-rate :model-value="scope.row.score" disabled size="small" />
        </template>
      </el-table-column>

      <el-table-column prop="comment" label="评价内容" />

      <el-table-column prop="createdAt" label="时间" width="180" />

      <el-table-column label="操作" fixed="right" width="100">
        <template #default="scope">
          <el-button 
            type="danger" 
            link 
            size="small" 
            @click="handleDelete(scope.row.id)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="flex justify-center pt-6">
      <el-pagination
        v-model:current-page="page"
        :page-size="size"
        layout="prev, pager, next"
        :total="total"
        @current-change="fetchReviews"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const reviews = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(15)

const fetchReviews = async () => {
  try {
    const res = await axios.get('/api/v1/admin/users/reviews', {
      params: { page: page.value, size: size.value },
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    reviews.value = res.data.data.list
    total.value = res.data.data.total
  } catch (e) {}
}

const handleDelete = (id: number) => {
  ElMessageBox.confirm('确定要永久删除这条评价吗？此操作不可撤销。', '警告', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning',
    confirmButtonClass: 'el-button--danger'
  }).then(async () => {
    try {
      await axios.delete(`/api/v1/admin/users/reviews/${id}`, {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      })
      ElMessage.success('评价已清除')
      fetchReviews()
    } catch (e) {}
  })
}

onMounted(fetchReviews)
</script>
