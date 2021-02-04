<!--
@page        公司申请管理
@author      ZhengWei(HY)
@createDate  2020-11-03
@version     v1.0
-->

<template>
  <div>
    <el-row>
      <el-col :span="20" :push="2">
        <div>
          <el-form :inline="true">
            <el-form-item style="float: left; justify-content : center;" label="查询公司名称:">
              <div style="display: flex;">
                <el-input v-model="queryKey" placeholder="查询公司......"></el-input>
                <el-button type="primary" icon="el-icon-search" @click="loadListDatas" style="margin-left: 1.25rem;">
                  查询
                </el-button>
              </div>
            </el-form-item>
            <el-form-item style="float: right">
              <el-button type="primary" size="medium" icon="el-icon-edit-outline" @click="openDialogAddOnClick"   >申请</el-button>
            </el-form-item>
          </el-form>
        </div>

        <div class="table">
          <el-table :data="tableDatas" border style="width: 100%">
            <el-table-column type="index" label="序号" align="center" width="60" fixed="left" v-loading="listLoading">
            </el-table-column>

            <el-table-column label="公司名称" align="center">
              <template slot-scope="scope">
                <a href="#" @click="openDialogDetailOnClick(scope.$index, scope.row)">{{ scope.row.corpname }}</a>
              </template>
            </el-table-column>

            <el-table-column label="公司性质" align="center">
              <template slot-scope="scope">
                <span>{{ scope.row.corptype }}</span>
              </template>
            </el-table-column>

            <el-table-column label="公司Logo" align="center">
              <template slot-scope="scope">
                <img v-if="scope.row.logo_url" class="logoIcon" :src="scope.row.logo_url" alt="-" />
                <img v-else                    class="logoIcon"  src="/mwcBase/static/manwucai/Null.png" alt="-" />
              </template>
            </el-table-column>

            <el-table-column label="营业执照" align="center">
              <template slot-scope="scope">
                <img v-if="scope.row.blis_url" class="logoIcon" :src="scope.row.blis_url" alt="-" />
                <img v-else                    class="logoIcon"  src="/mwcBase/static/manwucai/Null.png" alt="-" />
              </template>
            </el-table-column>

            <el-table-column label="联系人" align="center">
              <template slot-scope="scope">
                <span>{{ scope.row.name }}</span>
              </template>
            </el-table-column>

            <el-table-column label="电话" align="center">
              <template slot-scope="scope">
                <span>{{ scope.row.phone }}</span>
              </template>
            </el-table-column>

            <el-table-column label="innerid" align="center" width="120" v-if="false">
              <template slot-scope="scope">
                <span>{{ scope.row.innerid }}</span>
              </template>
            </el-table-column>

            <el-table-column label="操作" align="center">
              <template slot-scope="scope">
                <el-tooltip class="item" effect="dark" content="编辑" placement="top">
                    <el-button type="primary" icon="el-icon-edit" circle @click="openDialogEditOnClick(scope.$index, scope.row)"></el-button>
                </el-tooltip>

                <el-tooltip class="item" effect="dark" content="审核" placement="top">
                    <el-button :type="scope.row.auditResult == '1' ? 'success' : 'warning'" icon="el-icon-check" circle @click="openDialogVerifyOnClick(scope.$index, scope.row)"></el-button>
                </el-tooltip>

                <el-tooltip class="item" effect="dark" content="删除" placement="top">
                    <el-button type="danger" icon="el-icon-delete" circle @click="deleteOnClick(scope.$index, scope.row)"></el-button>
                </el-tooltip>
              </template>
            </el-table-column>
          </el-table>

          <pagination
            v-show="pagination.total>0"
            :total="pagination.total"
            :page.sync="pagination.page"
            :limit.sync="pagination.limit"
            @pagination="loadListDatas" />
        </div>
      </el-col>
    </el-row>

    <MerchantSave   ref="dialogSave"   :isShow="false" :succeed="loadListDatas"></MerchantSave>
    <MerchantImport ref="dialogImport" :isShow="false" :succeed="loadListDatas"></MerchantImport>
  </div>
</template>

<script>
import MerchantSave   from './MerchantSave.vue'
import MerchantImport from './MerchantImport.vue'
import Pagination     from '@/components/Pagination'

export default {
    components: {
        MerchantSave,
        MerchantImport,
        Pagination
    },

    data() {
        return {
            tableDatas: [],                      // 列表数据
            queryKey: '',                        // 查询框中的查询关键字信息
            pagination: {
                page: 1,                         // 分页，第几页
                limit: 50,                       // 分页，每页显示几行数据
                total: 0                         // 分页，共有多少条数据
            },
            listLoading: true
        }
    },

    /**
     * 在实例初始化之后，数据观测 (data observer) 和 event/watcher 事件配置之前被调用。
     */
    beforeCreate()
    {
        console.log('beforeCreate');
    },

    /**
     * 在实例创建完成后被立即调用
     */
    created()
    {
        this.loadListDatas();
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
        // this.$nextTick(function () {});
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

    },

    methods: {
        /**
         * 加载列表数据
         *
         * @author      ZhengWei(HY)
         * @createDate  2020-10-23
         * @version     v1.0
         */
        loadListDatas()
        {
            this.$post('/hxy/e/warning/queryList',
                    {
                        corpname:     this.queryKey,
                        startIndex:  (this.pagination.page - 1) * this.pagination.limit,
                        pagePerCount: this.pagination.limit
                    })
                    .then(res =>
                    {
                        console.log('列表数据', res)
                        console.log('列表数据', res.datas)
                        console.log('分页数据', res.total)
                        this.tableDatas       = res.datas;
                        this.pagination.total = res.total;
                        this.listLoading      = false;
                    });
        },


        /**
         * 删除一行数据
         *
         * @author      ZhengWei(HY)
         * @createDate  2020-10-28
         * @version     v1.0
         */
        deleteOnClick(index, row)
        {
            this.$confirm('此操作将永久删除条记录, 是否继续?', '提示',
            {
                confirmButtonText: '确定',
                cancelButtonText:  '取消',
                type: 'warning'
            })
            .then(() =>
            {
                this.$post('/mwcBase/no/merchant/delete',
                        {
                            innerid: row.innerid
                        })
                        .then(res =>
                        {
                            this.$message({
                                type: 'success',
                                message: '删除成功!'
                            });

                            this.loadListDatas();
                        });
            })
            .catch(() =>
            {
                this.$message({
                    type: 'info',
                    message: '取消删除'
                });
            });
        },


        /**
         * 打开添加数据的对话窗口
         *
         * @author      ZhengWei(HY)
         * @createDate  2020-10-27
         * @version     v1.0
         */
        openDialogAddOnClick(e)
        {
            this.$refs.dialogSave.show();
        },


        /**
         * 打开批量导入数据的对话窗口
         *
         * @author      ZhengWei(HY)
         * @createDate  2020-10-28
         * @version     v1.0
         */
        openDialogImportOnClick(e)
        {
            this.$refs.dialogImport.show();
        },


        /**
         * 打开编辑数据的对话窗口
         *
         * @author      ZhengWei(HY)
         * @createDate  2020-10-28
         * @version     v1.0
         */
        openDialogEditOnClick(index, row)
        {
            this.$refs.dialogSave.show(row);
        },


        /**
         * 仅查看数据的对话窗口
         *
         * @author      ZhengWei(HY)
         * @createDate  2020-10-28
         * @version     v1.0
         */
        openDialogDetailOnClick(index, row)
        {
            this.$refs.dialogSave.showOnlyRead(row);
        },


        /**
         * 审核数据的对话窗口
         *
         * @author      ZhengWei(HY)
         * @createDate  2020-10-29
         * @version     v1.0
         */
        openDialogVerifyOnClick(index, row)
        {
            this.$refs.dialogSave.showVerify(row);
        }
    }
}
</script>

<style>
h1 {
    font-size: 30px;
    color: blue;
    text-align: center;
    margin: 0 auto;
    padding-bottom: 5px;
    border-bottom: 2px solid #E9E9EB;
    width: 300px;
}

.logoIcon {
    width: 96px;
    height: 96px;
}
</style>
