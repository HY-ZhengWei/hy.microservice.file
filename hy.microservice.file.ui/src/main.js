import Vue from 'vue'
import 'normalize.css/normalize.css' // a modern alternative to CSS resets
import ElementUI from 'element-ui'
import './styles/element-variables.scss'
import './assets/css/style.css'
import './assets/iconfont.css'
// import 'element-ui/lib/theme-chalk/index.css'
import App from './App.vue'
import { post, fetch, patch, put, postForm } from './utils/api'
import router from './router'
import store from './store'
require('./utils/mock') // 增加mock模块，假数据使用,测试真实后台，将此注释掉
Vue.use(ElementUI)

// 定义全局变量
Vue.prototype.$post = post
Vue.prototype.$fetch = fetch
Vue.prototype.$patch = patch
Vue.prototype.$put = put
Vue.prototype.$postForm = postForm


router.beforeEach((to, from, next) =>
{
    window.document.title = to.meta.title === undefined ? 'hy.microservice.file' : to.meta.title;

    console.log('拦截登录开始：requireAuth=' + to.meta.requireAuth);
    // 判断跳转的路由是否需要登录
    if ( to.meta.requireAuth )
    {
        let v_IsLogin       = false;
        let v_UserLoginTime = localStorage.getItem('UserLoginTime');
        if ( v_UserLoginTime )
        {
            let v_Now  = (new Date()).getTime();
            let v_Diff = v_Now - v_UserLoginTime;

            if ( v_Diff < 5 * 60 * 1000 )   // 5分钟过期时长
            {
                v_IsLogin = true;
            }
        }

        console.log('是否已登录：' + v_IsLogin);

        if ( v_IsLogin )
        {
            // 已登录。vuex.state判断token是否存在
            next();
        }
        else
        {
            post('/calc/login/loginInfo.page', {}).then(res =>
            {
                console.log('是否有登录会话信息：' + res.paramObj.userCode + ' - ' + res.paramObj.userName + ' - ' + res.paramObj.loginAccount);
                if ( res && res.value && res.paramObj )
                {
                    localStorage.setItem('UserLoginTime', (new Date()).getTime());
                    next();
                }
                else
                {
                    console.log('前往登录页面');
                    next({
                        path: '/',
                        query: {
                            redirect: to.fullPath
                        }
                    });  // 将跳转的路由path作为参数，登录成功后跳转到该路由
                }
            });
        }
    }
    else
    {
        next();
    }
});

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  components: { App },
  template: '<App/>',
  render: h => h(App)
})
