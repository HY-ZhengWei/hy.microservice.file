const Mock = require('mockjs')

const userData = Mock.mock({
  data: {
    isLogin: 1,
    telphone: '123456'
  }
})

export default {
  'post|/login/query': option => {
    console.log('api 来到这里！！！。。。')
    return {
      status: 200,
      message: 'success',
      data: userData
    }
  }
}
