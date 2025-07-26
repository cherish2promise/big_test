import { createRouter, createWebHashHistory } from 'vue-router';

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: '/',
      component: () => import('../components/pages/Index.vue'),
    },
    {
      path: '/users',
      component: () => import('../components/ui/UserGrid.vue'),
    },
    {
      path: '/customerProfiles',
      component: () => import('../components/ui/CustomerProfileGrid.vue'),
    },
    {
      path: '/funeralInfos',
      component: () => import('../components/ui/FuneralInfoGrid.vue'),
    },
    {
      path: '/documentSaves',
      component: () => import('../components/ui/DocumentSaveGrid.vue'),
    },
  ],
})

export default router;
