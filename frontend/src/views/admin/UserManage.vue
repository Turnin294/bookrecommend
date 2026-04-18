<template>
  <div class="space-y-8 bg-white p-10 border border-ink/5 shadow-soft">
    <div class="flex items-center justify-between">
      <div class="space-y-1">
        <h3 class="font-display text-2xl text-ink">用户档案库 / USERS</h3>
        <p class="text-xs text-ink/40 uppercase tracking-widest">管理所有档案馆成员及其访问权限</p>
      </div>
      <div class="w-64">
        <el-input v-model="keyword" placeholder="搜索用户名或邮箱..." @keyup.enter="fetchUsers" />
      </div>
    </div>

    <el-table :data="users" style="width: 100%" class="archive-table">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="成员" width="200">
        <template #default="scope">
          <div class="flex items-center gap-3">
            <div class="w-8 h-8 rounded-full bg-ink/5 flex items-center justify-center font-display text-xs">
              {{ scope.row.username.charAt(0) }}
            </div>
            <span class="font-bold">{{ scope.row.username }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="email" label="邮箱" />
      <el-table-column label="角色">
        <template #default="scope">
          <span class="text-[10px] font-bold uppercase tracking-widest px-2 py-1" 
                :class="scope.row.role === 1 ? 'bg-gold text-ink' : 'bg-ink/5 text-ink/40'">
            {{ scope.row.role === 1 ? 'Admin' : 'Member' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="状态">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" effect="plain">
            {{ scope.row.status === 1 ? '正常' : '已禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="150">
        <template #default="scope">
          <el-button 
            v-if="scope.row.status === 1" 
            size="small" 
            type="danger" 
            link
            @click="updateStatus(scope.row.id, 0)"
          >禁用</el-button>
          <el-button 
            v-else 
            size="small" 
            type="success" 
            link
            @click="updateStatus(scope.row.id, 1)"
          >启用</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="flex justify-center pt-6">
      <el-pagination
        v-model:current-page="page"
        :page-size="size"
        layout="prev, pager, next"
        :total="total"
        @current-change="fetchUsers"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const users = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const keyword = ref('')

const fetchUsers = async () => {
  try {
    const res = await axios.get(`/api/v1/admin/users`, {
      params: { keyword: keyword.value, page: page.value, size: size.value },
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    users.value = res.data.data.list
    total.value = res.data.data.total
  } catch (e) {}
}

const updateStatus = async (userId: number, status: number) => {
  try {
    await axios.put(`/api/v1/admin/users/${userId}/status`, { status }, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    ElMessage.success('操作成功')
    fetchUsers()
  } catch (e) {}
}

onMounted(fetchUsers)
</script>
