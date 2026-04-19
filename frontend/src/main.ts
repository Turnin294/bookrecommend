import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import 'element-plus/dist/index.css'
import './style.css'
import App from './App.vue'
import { createRouter, createWebHistory } from 'vue-router'

const app = createApp(App)

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: () => import('./views/Login.vue'), name: 'Login' },
    { path: '/register', component: () => import('./views/Register.vue'), name: 'Register' },
    { path: '/books/:id/read', component: () => import('./views/Reader.vue'), name: 'Reader' },
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
        { path: 'reviews', component: () => import('./views/admin/ReviewManage.vue'), name: 'ReviewManage' },
      ]
    }
  ]
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  if (to.path === '/login' || to.path === '/register') {
    next()
  } else if (!token) {
    next('/login')
  } else if (to.path.startsWith('/admin') && role !== '1') {
    next('/')
  } else {
    next()
  }
})

app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: zhCn })

app.mount('#app')
