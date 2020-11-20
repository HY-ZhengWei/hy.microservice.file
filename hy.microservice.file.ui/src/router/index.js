import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)
let routes = [];
const routerContext = require.context('../pages', true, /pages\.js$/)    /* 扫描pages文件夹，将路由信息整合 */


routerContext.keys().forEach(route =>
{
    // 如果是根目录的 index.js、 不做任何处理
    if (route.startsWith('./index.js'))
    {
        return
    }

    // 兼容 import export 和 require module.export 两种规范 Es modules commonjs
    const routerModule = routerContext(route)
    routes = [...(routerModule.default || routerModule),
        {
            path: '/',
            name: '默认首页',
            component: (r) => require(['./index.vue'], r)
        }];
});



export default new Router({
  mode: 'history',
  base: '/msFile/ms/file',          // 根服务
  routes: routes
})
