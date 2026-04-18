<template>
  <div class="space-y-10">
    <!-- 顶部核心数据卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
      <div v-for="(val, key) in stats" :key="key" class="bg-white p-8 border border-ink/5 flex flex-col items-center shadow-soft">
        <div class="text-[10px] font-bold uppercase tracking-widest text-ink/30 mb-2">{{ key }}</div>
        <div class="font-display text-4xl text-ink leading-none">{{ val }}</div>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-10">
      <!-- 趋势图 -->
      <div class="bg-white p-10 border border-ink/5 shadow-soft">
        <h3 class="font-display text-2xl text-ink mb-10 border-b border-ink/5 pb-4">行为活跃趋势 / ACTIVITY</h3>
        <div ref="trendChart" class="h-80 w-full"></div>
      </div>

      <!-- 热度排行 -->
      <div class="bg-white p-10 border border-ink/5 shadow-soft">
        <h3 class="font-display text-2xl text-ink mb-10 border-b border-ink/5 pb-4">图书热度排行 / POPULARITY</h3>
        <div class="space-y-6">
          <div v-for="(book, index) in hotBooks" :key="book.id" class="flex items-center gap-6 group">
            <div class="font-display text-3xl italic text-ink/10 group-hover:text-gold transition-colors">{{ index + 1 }}</div>
            <div class="flex-1 border-b border-ink/5 pb-4">
              <div class="text-[10px] font-bold uppercase tracking-widest text-gold mb-1">{{ book.author }}</div>
              <div class="font-serif text-lg text-ink line-clamp-1">{{ book.title }}</div>
            </div>
            <div class="text-[10px] font-bold uppercase tracking-widest text-ink/40">{{ book.hot_score }} pts</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 分类占比 -->
    <div class="bg-white p-10 border border-ink/5 shadow-soft">
      <h3 class="font-display text-2xl text-ink mb-10 border-b border-ink/5 pb-4">馆藏分类分布 / COLLECTION</h3>
      <div ref="pieChart" class="h-80 w-full"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import * as echarts from 'echarts'
import axios from 'axios'

const trendChart = ref<HTMLElement | null>(null)
const pieChart = ref<HTMLElement | null>(null)
const hotBooks = ref([])
const stats = reactive({
  '浏览次数': 0,
  '收藏次数': 0,
  '评分次数': 0,
  '完读次数': 0
})

const initCharts = (trendData: any, pieData: any) => {
  if (trendChart.value) {
    const chart = echarts.init(trendChart.value)
    chart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: trendData.map((d: any) => d.date), axisLine: { lineStyle: { color: '#eee' } } },
      yAxis: { type: 'value', splitLine: { lineStyle: { type: 'dashed' } } },
      series: [{ 
        data: trendData.map((d: any) => d.count), 
        type: 'line', 
        smooth: true, 
        color: '#C5A059',
        areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#C5A05933' }, { offset: 1, color: '#C5A05900' }]) }
      }]
    })
  }

  if (pieChart.value) {
    const chart = echarts.init(pieChart.value)
    chart.setOption({
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 4, borderColor: '#fff', borderWidth: 2 },
        label: { show: true, fontFamily: 'serif' },
        data: pieData.map((d: any) => ({ name: d.name, value: d.bookCount })),
        color: ['#1A1A1A', '#C5A059', '#E5E5E5', '#333', '#D4B47B']
      }]
    })
  }
}

onMounted(async () => {
  const token = localStorage.getItem('token')
  const auth = { Authorization: `Bearer ${token}` }
  
  try {
    // 获取行为统计
    const resBehavior = await axios.get('/api/v1/admin/analysis/behavior?startDate=2024-01-01&endDate=2026-12-31', { headers: auth })
    const bData = resBehavior.data.data
    stats['浏览次数'] = bData.totalBrowse || 0
    stats['收藏次数'] = bData.totalFavorite || 0
    stats['评分次数'] = bData.totalRating || 0
    stats['完读次数'] = bData.totalRead || 0
    
    // 获取热门图书
    const resHot = await axios.get('/api/v1/analysis/hot-books?limit=5', { headers: auth })
    hotBooks.value = resHot.data.data

    // 获取分类分布
    const resCat = await axios.get('/api/v1/admin/analysis/categories', { headers: auth })
    
    initCharts(bData.dailyTrend || [], resCat.data.data || [])
  } catch (e) {}
})
</script>
