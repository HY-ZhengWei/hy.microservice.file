const Mock = require('mockjs')

const demoList = Mock.mock({
  status: 200,
  message: 'success',
  data: {
    total: 100,
    'rows|10': [{
      id: '@guid',
      name: '@cname',
      imgUrl: 'https://img.yzcdn.cn/vant/cat.jpeg',
      'age|20-30': 23,
      'job|1': ['前端工程师', '后端工程师', 'UI工程师', '需求工程师'],
      'describe|10-30': '@cword(1)'
    }]
  }
})

export default {
  'get|/parameter/query': option => {
    console.log('api 来到这里！！！。。。')
    return {
      status: 200,
      message: 'success',
      data: demoList
    }
  }
}
