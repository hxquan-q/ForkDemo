import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/ApiTestSimple.vue')
  },
  {
    path: '/api-test',
    name: 'ApiTest',
    component: () => import('../views/ApiTestSimple.vue')
  },
  {
    path: '/concurrency-test',
    name: 'ConcurrencyTest',
    component: () => import('../views/ConcurrencyTest.vue')
  },
  {
    path: '/service-status',
    name: 'ServiceStatus',
    component: () => import('../views/ServiceStatus.vue')
  },
  {
    path: '/docs',
    name: 'ApiDocs',
    component: () => import('../views/ApiDocs.vue')
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
