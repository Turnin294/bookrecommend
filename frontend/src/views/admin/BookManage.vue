<template>
  <div class="space-y-8 bg-white p-10 border border-ink/5 shadow-soft">
    <div class="flex items-center justify-between">
      <div class="space-y-1">
        <h3 class="font-display text-2xl text-ink">图书编目 / BOOKS</h3>
        <p class="text-xs text-ink/40 uppercase tracking-widest">录入、上传馆藏封面及正文全本</p>
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
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑馆藏' : '新增馆藏'" width="700px">
      <el-form :model="form" label-position="top" class="grid grid-cols-2 gap-x-6 gap-y-4">
        <el-form-item label="书名" class="col-span-2">
          <el-input v-model="form.title" />
        </el-form-item>
        
        <!-- 封面上传 -->
        <el-form-item label="封面图片 / COVER" class="col-span-1">
          <el-upload
            class="cover-uploader"
            action="/api/v1/admin/books/upload?type=cover"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            :headers="uploadHeaders"
          >
            <img v-if="form.cover" :src="form.cover" class="w-full h-40 object-cover border" />
            <div v-else class="w-full h-40 border border-dashed flex flex-col items-center justify-center text-ink/20">
              <Plus class="w-8 h-8" />
              <span class="text-[10px]">点击上传封面</span>
            </div>
          </el-upload>
        </el-form-item>

        <!-- 正文 TXT 上传 -->
        <el-form-item label="全本内容 / FULL TEXT" class="col-span-1">
          <el-upload
            drag
            action="#"
            :auto-upload="false"
            :on-change="handleTextFileChange"
            :limit="1"
            accept=".txt"
          >
            <div class="el-upload__text">
              拖拽 .txt 文件或 <em>点击上传全本</em>
            </div>
          </el-upload>
          <div class="mt-2 text-[10px] text-ink/40">已录入 {{ form.content?.length || 0 }} 字</div>
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
        
        <el-form-item label="描述" class="col-span-2">
          <el-input v-model="form.description" type="textarea" rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveBook">提交存档</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, computed } from 'vue'
import { Plus } from 'lucide-vue-next'
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
  content: '',
  stock: 0,
  status: 1,
  tags: []
})

const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token')}`
}))

const fetchBooks = async () => {
  try {
    const res = await axios.get('/api/v1/admin/books', {
      params: { page: 1, size: 20 },
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    books.value = res.data.data.list
  } catch (e) {}
}

const handleCoverSuccess = (res: any) => {
  if (res.code === 200) {
    form.cover = res.data
    ElMessage.success('封面上传成功')
  }
}

// 处理 TXT 文件读取 (工业级编码自适应方案)
const handleTextFileChange = (file: any) => {
  const reader = new FileReader()
  reader.onload = async (e: any) => {
    const uint8Array = new Uint8Array(e.target.result)
    
    // 尝试 UTF-8 解码，如果抛错或包含乱码，则切换到 GBK
    try {
      const decoder = new TextDecoder('utf-8', { fatal: true })
      form.content = decoder.decode(uint8Array)
      ElMessage.success('全本内容已解析 (UTF-8)')
    } catch (err) {
      // UTF-8 解析失败，尝试 GBK
      const gbkDecoder = new TextDecoder('gbk')
      form.content = gbkDecoder.decode(uint8Array)
      ElMessage.success('已自动识别 GBK 编码并修复乱码')
    }
  }
  // 以二进制方式读取，方便后续精准转码
  reader.readAsArrayBuffer(file.raw)
}

const openDialog = (book: any) => {
  if (book) {
    Object.assign(form, book)
  } else {
    Object.assign(form, { id: null, title: '', author: '', isbn: '', categoryId: 1, cover: '', description: '', content: '', stock: 0, status: 1, tags: [] })
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

<style scoped>
.cover-uploader :deep(.el-upload) {
  width: 100%;
}
</style>
