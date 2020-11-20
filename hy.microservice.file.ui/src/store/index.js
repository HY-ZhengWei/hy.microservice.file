import Vue from 'vue'
import Vuex from 'vuex'
// import price from './module/price'
// import Teststate from './module/Teststate'
// import actions from './actions'
// import getters from './getters'
// import mutations from './mutations'

Vue.use(Vuex)

const stateModule = require.context('../', true, /(\w+State)\.js$/)

const arrMoudule = {}
for (const key of stateModule.keys()) {
  const fileKey = key.replace(/\.\/|\.js/g, '')
  const arr = fileKey.split('/')
  let storeKey = ''
  if (arr) {
    storeKey = arr[arr.length - 1]
  }
  console.log('key.....................', key)
  arrMoudule[storeKey] = stateModule(key).default
}

export default new Vuex.Store({
  strict: true,
  modules: arrMoudule
})
