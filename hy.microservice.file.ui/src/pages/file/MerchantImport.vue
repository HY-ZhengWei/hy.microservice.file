<!--
@page        批量导入-商家页面
@author      ZhengWei(HY)
@createDate  2020-10-28
@version     v1.0
-->

<template>
    <div>
        <el-dialog title="批量导入商家" :visible="isShowDialog" @close="cancelOnClick">

            <el-form :model="formDatas" ref="formValidate" label-width="100px" :rules="formRules">
                <el-form-item label="上传文件" prop="id">
                    <el-upload
                      class="upload-demo"
                      drag
                      action="/mwcBase/file/upload"
                      :data="uploadDatas"
                      :on-success="fileUploadSuccess"
                      :before-upload="uploadBeforeValidate"
                      :limit="1"
                      :file-list="uploadFiles">
                        <i class="el-icon-upload"></i>
                        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
                        <div class="el-upload__tip" slot="tip">请上传Excel文件(*.xlsx)，且不超过10MB</div>
                    </el-upload>
                </el-form-item>

                <el-form-item label="导入模板">
                    <el-link type="primary" href="/mwcBase/file/showExcel/import/import.Merchant.xlsx" target="_blank">下载导入模板.xlsx</el-link>
                </el-form-item>

            </el-form>

            <div slot="footer" class="dialog-footer">
                <el-button                @click="cancelOnClick" >取 消</el-button>
                <el-button type="primary" @click="confirmOnClick">导 入</el-button>
            </div>

        </el-dialog>
    </div>
</template>

<script>
export default {
    props: {
        isShow:  { type: Boolean, default: false, required: false },        // 是否显示对话框
        succeed: { type: Function,                required: false }         // 成功时的回调方法，方法参数是表单数据
    },

    watch: {
        isShow(i_Value)
        {
            this.isShowDialog = i_Value;
        }
    },

    data() {
        return {
            serviceType: 'merchant',          // 分类编号，如彩课堂、分享家等
            userID: 'HY',
            userName: '郑伟',
            userIcon: '',
            userType: '',
            groupID: '',
            groupInfo: '',
            isShowDialog: false,              // 内部控制是否显示对话框的属性
            uploadFiles: [],                  // 上传文件的列表数据
            formDatas: {},                    // 表单数据
            formRules: {},                    // 表单验证规则
            uploadDatas: {}                   // 上传的额外数据
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
        this.groupID = '' + (new Date()).getTime();
        this.formDatas = {};
        this.uploadDatas = {
            'serviceType': this.serviceType,
            'userid':      this.userID,
            'username':    this.userName,
            'usericon':    this.userIcon,
            'groupid':     this.groupID,
            'groupinfo':   this.groupInfo
        };
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
         * 显示对话框
         *
         * @author      ZhengWei(HY)
         * @createDate  2020-10-28
         * @version     v1.0
         */
        show()
        {
            this.isShowDialog = true;
        },


        /**
         * 隐藏对话框
         *
         * @author      ZhengWei(HY)
         * @createDate  2020-10-23
         * @version     v1.0
         */
        hide()
        {
            this.isShowDialog = false;
        },


        /**
         * 取消按钮的点击事件。
         *
         * @author      ZhengWei(HY)
         * @createDate  2020-10-23
         * @version     v1.0
         */
        cancelOnClick(e)
        {
            this.hide();
        },


        /**
         * 添加数据，确认按钮的点击事件。
         *
         * @author      ZhengWei(HY)
         * @createDate  2020-10-23
         * @version     v1.0
         */
        confirmOnClick(e)
        {
            this.$refs.formValidate.validate((i_Valid) =>
            {
                if ( i_Valid )
                {
                    this.$post('/mwcBase/no/merchant/import', this.formDatas).then(res =>
                    {
                        if ( res && res.success )
                        {
                            this.$message({
                                type: 'success',
                                message: '操作成功'
                            });

                            this.uploadFiles = [];
                            this.formDatas   = {};
                            this.hide();
                            if ( this.succeed !== null && this.succeed !== undefined )
                            {
                                this.succeed(this.formDatas);
                            }
                        }
                        else
                        {
                            this.$message({
                                type: 'fail',
                                message: '请稍后重新尝试'
                            });
                        }
                    });
                }
                else
                {
                    this.$message({
                        type: 'fail',
                        message: '请重新尝试'
                    });
                }
            });
		},


        /**
         * 上传前后的验证。
         *
         * @author      ZhengWei(HY)
         * @createDate  2020-10-28
         * @version     v1.0
         */
        uploadBeforeValidate(i_File)
        {
            const isExcel = i_File.name.toLowerCase().endsWith('.xlsx');
            const isSize  = i_File.size / 1024 / 1024 < 5;

            if ( !isExcel )
            {
                this.$message.error('上传文件只能是 Excel(*.xlsx) 格式!');
            }
            if ( !isSize )
            {
                this.$message.error('上传文件大小不能超过 5MB!');
            }

            return isExcel && isSize;
        },


        /**
         * 批量导入文件上传成功后的事件。
         *
         * @author      ZhengWei(HY)
         * @createDate  2020-10-28
         * @version     v1.0
         */
        fileUploadSuccess(res, file)
        {
            this.formDatas.id  = res.id;
            this.formDatas.url = res.url;
        }
    }
}
</script>

<style>
.avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
}

.avatar-uploader .el-upload:hover {
    border-color: #409EFF;
}

.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    line-height: 178px;
    text-align: center;
}

.avatar {
    width: 178px;
    height: 178px;
    display: block;
}
</style>
