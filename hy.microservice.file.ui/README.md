# foreend

> 这是一个通用的前端项目，这是一个通用的vue前端，
> 已经集成了手机组件vant，pc组件elment-ui，报表呈现echart，以上api用法，请自行百度
## note
### 开发项目时，针对各自的项目，在child下新建一个目录，新建目录下可自定义目录层级，详见实例demo
### 大体分为assets（存放图片等）、router（路由）、store（全局状态）、views（页面）
### 其中view中的页面由三部分组成：*.vue,*.js,*.scss(编译好的css）,建议严格按照这个来开发。
### 实例路由：/user,/emp,/bdemo,/demo
### require('./utils/mock') // 增加mock模块，假数据使用,测试真实后台，将此注释掉

## Build Setup

``` bash

# 第一次启动项目时，使用以下命令
# 事先请安装nodejs以及npm，由于国内npm命令较慢，建议使用阿里代理，cnpm
# 如何代理请百度
cnpm install

# 开发时，运行如下命令 localhost:8080
cnpm run dev

# 将项目静态打包，得到一个dist文件夹，将dist改名为app，
# 将dist\static\WEB-INF文件夹移动到上一级目录，放到tomcat下，
# 需要注意的是，打包的项目是静态的，开发代理的接口如果跨域，访问不了
# 需要用ngix做代理，或者将静态包和后端放在同一个web容器下。
cnpm run build

# build for production and view the bundle analyzer report
npm run build --report

# run unit tests
npm run unit

# run all tests
npm test
```

For a detailed explanation on how things work, check out the [guide](http://vuejs-templates.github.io/webpack/) and [docs for vue-loader](http://vuejs.github.io/vue-loader).
