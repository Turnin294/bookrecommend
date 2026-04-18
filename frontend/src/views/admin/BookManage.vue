<template>
  <div class="space-y-8 bg-white p-10 border border-ink/5 shadow-soft">
    <div class="flex items-center justify-between">
      <div class="space-y-1">
        <h3 class="font-display text-2xl text-ink">图书编目 / BOOKS</h3>
        <p class="text-xs text-ink/40 uppercase tracking-widest">录入、更新馆藏图书及分类信息</p>
      </div>
      <el-button type="primary" @click="openDialog(null)">新增馆藏图书</el-button>
    </div>

    <!-- 图书列表 -->
    <el-table :data="books" style="width: 100%">
      <el-table-column label="图书" width="300">
        <template #default="scope">
          <div class="flex items-center gap-4">
            <img :src="scope.row.cover" class="w-10 h-14 object-cover border border-ink/5 shadow-sm" />
            <div class="min-w-0">
              <div class="text-[10px] font-bold uppercase tracking-widest text-gold">{{ scope.row.author }}</div>
              <div class="font-serif text-sm text-ink line-clamp-1">{{ scope.row.title }}</div>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="isbn" label="ISBN" width="150" />
      <el-table-column prop="stock" label="库存" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">
            {{ scope.row.status === 1 ? '已上架' : '已下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right">
        <template #default="scope">
          <el-button size="small" type="primary" link @click="openDialog(scope.row)">编辑</el-button>
          <el-button 
            size="small" 
            :type="scope.row.status === 1 ? 'info' : 'success'" 
            link 
            @click="toggleStatus(scope.row)"
          >
            {{ scope.row.status === 1 ? '下架' : '上架' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑馆藏' : '新增馆藏'" width="600px">
      <el-form :model="form" label-position="top" class="grid grid-cols-2 gap-x-6 gap-y-4">
        <el-form-item label="书名" class="col-span-2">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="form.author" />
        </el-form-item>
        <el-form-item label="ISBN">
          <el-input v-model="form.isbn" />
        </el-form-item>
        <el-form-item label="分类 ID">
          <el-input-number v-model="form.categoryId" class="w-full" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="form.stock" class="w-full" />
        </el-form-item>
        <el-form-item label="封面 URL" class="col-span-2">
          <el-input v-model="form.cover" />
        </el-form-item>
        <el-form-item label="描述" class="col-span-2">
          <el-input v-model="form.description" type="textarea" rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveBook">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const books = ref([])
const dialogVisible = ref(false)
const form = reactive({
  id: null,
  title: '',
  author: '',
  isbn: '',
  categoryId: 1,
  cover: '',
  description: '',
  stock: 0,
  status: 1,
  tags: []
})

const fetchBooks = async () => {
  try {
    const res = await axios.get('/api/v1/admin/books', {
      params: { page: 1, size: 20 },
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    books.value = res.data.data.list
  } catch (e) {}
}

const openDialog = (book: any) => {
  if (book) {
    Object.assign(form, book)
  } else {
    Object.assign(form, { id: null, title: '', author: '', isbn: '', categoryId: 1, cover: '', description: '', stock: 0, status: 1, tags: [] })
  }
  dialogVisible.value = true
}

const saveBook = async () => {
  const token = localStorage.getItem('token')
  const auth = { Authorization: `Bearer ${token}` }
  try {
    if (form.id) {
      await axios.put(`/api/v1/admin/books/${form.id}`, form, { headers: auth })
    } else {
      await axios.post('/api/v1/admin/books', form, { headers: auth })
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    fetchBooks()
  } catch (e) {}
}

const toggleStatus = async (book: any) => {
  const newStatus = book.status === 1 ? 0 : 1
  try {
    await axios.put(`/api/v1/admin/books/${book.id}/status`, { status: newStatus }, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    ElMessage.success('状态已更新')
    fetchBooks()
  } catch (e) {}
}

onMounted(fetchBooks)
</script>
