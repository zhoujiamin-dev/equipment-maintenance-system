import { createRouter, createWebHistory } from 'vue-router'
import EquipmentView from '../views/EquipmentView.vue'
import WorkOrderView from '../views/WorkOrderView.vue'
import AiAnalysisView from '../views/AiAnalysisView.vue'
import LoginView from '../views/LoginView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),

  routes: [
    {
      path: '/',
      redirect: '/equipment',
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/equipment',
      name: 'equipment',
      component: EquipmentView,
    },
    {
      path: '/work-orders',
      name: 'work-orders',
      component: WorkOrderView,
    },
    {
      path: '/ai-analysis',
      name: 'ai-analysis',
      component: AiAnalysisView,
    },
  ],
})

router.beforeEach((to) => {
  const token = localStorage.getItem('token')

  if (to.path !== '/login' && !token) {
    return '/login'
  }

  if (to.path === '/login' && token) {
    return '/equipment'
  }
})

export default router
