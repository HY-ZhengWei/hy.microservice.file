export default [
    {
        path: '/fileui/FileList',
        component: (r) => require(['./file/FileList.vue'], r),
        name: '文件管理',
        meta: {
            title: '文件管理',
            requireAuth: false,
            roles: ['admin']
        }
    }
]
