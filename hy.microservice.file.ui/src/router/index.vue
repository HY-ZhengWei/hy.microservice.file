<!--
@page        默认首页
@author      ZhengWei(HY)
@createDate  2020-11-02
@version     v1.0
-->

<template>
    <div>

        

    </div>
</template>

<script>
export default {
    data() {
        return {

        }
    },

    /**
     * 在实例初始化之后，数据观测 (data observer) 和 event/watcher 事件配置之前被调用。
     */
    beforeCreate()
    {

    },

    /**
     * 在实例创建完成后被立即调用
     */
    created()
    {

    },

    /**
     * 在挂载开始之前被调用：相关的 render 函数首次被调用。
     */
    beforeMount()
    {

    },

    /**
     * 实例被挂载后调用
     * 注意 mounted 不会保证所有的子组件也都一起被挂载。如果你希望等到整个视图都渲染完毕，可以在 mounted 内部使用 vm.$nextTick
     */
    mounted()
    {
        this.$router.push('/fileui/FileList');
    },

    /**
     * 数据更新时调用
     */
    beforeUpdate()
    {

    },

    /**
     * 当这个钩子被调用时，组件 DOM 已经更新，所以你现在可以执行依赖于 DOM 的操作
     */
    updated()
    {

    },

    /**
     * 组件激活时调用
     */
    activated()
    {

    },

    /**
     * 组件停用时调用
     */
    deactivated()
    {

    },

    /**
     * 实例销毁之前调用
     */
    beforeDestroy()
    {

    },

    /**
     * 实例销毁后调用
     */
    destroyed()
    {

    }
}
</script>

<style>

</style>
