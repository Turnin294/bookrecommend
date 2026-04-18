import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './style.css'
import App from './App.vue'
import { createRouter, createWebHistory } from 'vue-router'

const app = createApp(App)

// 基础路由配置
const router = createRouter({
  history: createWebHistory(),
  routes: [
    { 
      path: '/login', 
      component: () => import('./views/Login.vue'),
      name: 'Login'
    },
    { 
      path: '/', 
      component: () => import('./layout/MainLayout.vue'),
      children: [
        { path: '', component: () => import('./views/Home.vue'), name: 'Home' },
        { path: 'books', component: () => import('./views/BookList.vue'), name: 'Books' },
        { path: 'books/:id', component: () => import('./views/BookDetail.vue'), name: 'BookDetail' },
        { path: 'profile', component: () => import('./views/Profile.vue'), name: 'Profile' },
        { path: 'notifications', component: () => import('./views/Notifications.vue'), name: 'Notifications' },
      ]
    },
    {
      path: '/admin',
      component: () => import('./layout/AdminLayout.vue'),
      children: [
        { path: 'dashboard', component: () => import('./views/admin/Dashboard.vue'), name: 'AdminDashboard' },
        { path: 'users', component: () => import('./views/admin/UserManage.vue'), name: 'UserManage' },
        { path: 'books', component: () => import('./views/admin/BookManage.vue'), name: 'BookManage' },
      ]
    }
  ]
})

// 路由守卫 (简单演示，后期完善)
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.name !== 'Login' && !token) next({ name: 'Login' })
  else next()
})

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

app.mount('#app')
