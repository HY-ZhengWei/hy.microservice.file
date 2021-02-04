module.exports = {
    publicPath: '/msFile/ms/file',                              // 此处应于 router/index.js 中的根服务保持一致
    devServer: {
        proxy: {
            '/msFileApi/': {
                target: 'http://127.0.0.1/msFile',
                changeOrigin: true,                             // 是否跨域
                secure: true,                                   // 如果是https接口，需要配置这个参数
                pathRewrite: {
                    '^/msFileApi': ''
                }
            },
            '/hxy/': {
                target: 'http://dj9wbr.natappfree.cc',
                changeOrigin: true,                             // 是否跨域
                secure: true                                   // 如果是https接口，需要配置这个参数
            }
        },
    }
}
