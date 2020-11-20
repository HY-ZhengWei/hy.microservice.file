<!--
@page        添加、编辑、查看明细、审核-商家页面
@author      ZhengWei(HY)
@createDate  2020-10-28
@version     v1.0
-->

<template>
    <div>
        <el-dialog :title="(isVerify ? '审核' : (isReadOnly ? '查看' : titleForStart)) + titleForEnd" :visible="isShowDialog" @close="cancelOnClick">

            <el-form :model="formDatas" ref="formValidate" label-width="100px" :rules="formRules">
                <el-form-item label="公司名称" prop="corpname">
                    <el-input :readonly="isReadOnly" v-model="formDatas.corpname" placeholder="公司名称"></el-input>
                </el-form-item>

                <el-form-item label="公司简介" prop="brief">
                    <el-input :readonly="isReadOnly" v-model="formDatas.brief" type="textarea" :rows="3" placeholder="公司简介" show-word-limit></el-input>
                </el-form-item>

                <el-form-item label="纳税人号" prop="computerNum">
                    <el-input :readonly="isReadOnly" v-model="formDatas.computerNum" placeholder="纳税人识别号"></el-input>
                </el-form-item>

                <el-form-item label="登录名" prop="loginName">
                    <el-input :readonly="isReadOnly" v-model="formDatas.loginName" placeholder="登录名"></el-input>
                </el-form-item>

                <el-form-item label="公司性质" prop="corptype">
                    <el-select :disabled="isReadOnly" v-model="formDatas.corptype" placeholder="请选择">
                        <el-option v-for="ctItem in corpTypes" :key="ctItem.name" :label="ctItem.name" :value="ctItem.name"></el-option>
                    </el-select>
                </el-form-item>

                <el-form-item label="角色分类" prop="roleType">
                    <el-select :disabled="isReadOnly" v-model="formDatas.roleType" placeholder="请选择">
                        <el-option v-for="roleItem in roleTypes" :key="roleItem.id" :label="roleItem.name" :value="roleItem.id"></el-option>
                    </el-select>
                </el-form-item>

                <el-form-item label="管理分类" prop="adminType">
                    <el-select :disabled="isReadOnly" v-model="formDatas.adminType" placeholder="请选择">
                        <el-option v-for="adminItem in adminTypes" :key="adminItem.id" :label="adminItem.name" :value="adminItem.id"></el-option>
                    </el-select>
                </el-form-item>

                <el-form-item label="公司Logo" prop="logo_url">
                    <el-upload
                      :disabled="isReadOnly"
                      class="avatar-uploader"
                      action="/mwcBase/file/upload"
                      :data="uploadDatas"
                      :show-file-list="false"
                      :on-success="logoUploadSuccess"
                      :before-upload="uploadBeforeValidate">
                      <img v-if="isShowLogoImg" ref="logoURLImg" :src="formDatas.logo_url" class="avatar">
                      <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                    </el-upload>
                </el-form-item>

                <el-form-item label="营业执照" prop="blis_url">
                    <el-upload
                      :disabled="isReadOnly"
                      class="avatar-uploader"
                      action="/mwcBase/file/upload"
                      :data="uploadDatas"
                      :show-file-list="false"
                      :on-success="blisUploadSuccess"
                      :before-upload="uploadBeforeValidate">
                      <img v-if="isShowBlisImg" ref="blisURLImg" :src="formDatas.blis_url" class="avatar">
                      <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                    </el-upload>
                </el-form-item>

                <el-form-item label="品牌认证书" prop="certificate_url">
                    <el-upload
                      :disabled="isReadOnly"
                      class="avatar-uploader"
                      action="/mwcBase/file/upload"
                      :data="uploadDatas"
                      :show-file-list="false"
                      :on-success="certificateUploadSuccess"
                      :before-upload="uploadBeforeValidate">
                      <img v-if="isShowCertificateImg" ref="certificateURLImg" :src="formDatas.certificate_url" class="avatar">
                      <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                    </el-upload>
                </el-form-item>

                <el-form-item label="主营行业" prop="industry">
                    <el-input :readonly="isReadOnly" v-model="formDatas.industry" placeholder="主营行业"></el-input>
                </el-form-item>

                <el-form-item label="主营产品" prop="mainProduct">
                    <el-input :readonly="isReadOnly" v-model="formDatas.mainProduct" type="textarea" :rows="3" placeholder="主营产品"></el-input>
                </el-form-item>

                <el-form-item label="园区分类" prop="parkType">
                    <el-input :readonly="isReadOnly" v-model="formDatas.parkType" placeholder="园区分类"></el-input>
                </el-form-item>

                <el-form-item label="入驻类型" prop="settlementType">
                    <el-input :readonly="isReadOnly" v-model="formDatas.settlementType" placeholder="入驻类型"></el-input>
                </el-form-item>

                <el-form-item label="线下场馆分类" prop="venueType">
                    <el-input :readonly="isReadOnly" v-model="formDatas.venueType" placeholder="线下场馆分类"></el-input>
                </el-form-item>

                <el-form-item label="银行账号" prop="bank_no">
                    <el-input :readonly="isReadOnly" v-model="formDatas.bank_no" placeholder="银行账号"></el-input>
                </el-form-item>

                <el-form-item label="银行账户姓名" prop="bank_user">
                    <el-input :readonly="isReadOnly" v-model="formDatas.bank_user" placeholder="银行账户姓名" maxlength="20" show-word-limit></el-input>
                </el-form-item>

                <el-form-item label="联系人姓名" prop="name">
                    <el-input :readonly="isReadOnly" v-model="formDatas.name" placeholder="联系人姓名" minlength="2" maxlength="20" show-word-limit></el-input>
                </el-form-item>

                <el-form-item label="身份证号" prop="idcard">
                    <el-input :readonly="isReadOnly" v-model="formDatas.idcard" placeholder="身份证号" minlength="18" maxlength="18" show-word-limit></el-input>
                </el-form-item>

                <el-form-item label="电话" prop="phone">
                    <el-input :readonly="isReadOnly" v-model="formDatas.phone" placeholder="电话" minlength="8" maxlength="30" show-word-limit></el-input>
                </el-form-item>

                <el-form-item label="工龄" prop="experience">
                    <el-input :readonly="isReadOnly" v-model="formDatas.experience" placeholder="工龄"></el-input>
                </el-form-item>

                <el-form-item label="OpenID" prop="openid">
                    <el-input :readonly="isReadOnly" v-model="formDatas.openid" placeholder="微信小程序openid"></el-input>
                </el-form-item>

                <el-divider><i class="el-icon-collection-tag"></i></el-divider>

                <el-form-item label="数据编号">
                    <el-input :readonly="true" :value="formDatas.innerid"></el-input>
                </el-form-item>

                <el-form-item label="商家编号">
                    <el-input :readonly="true" :value="formDatas.user_id"></el-input>
                </el-form-item>

                <el-form-item label="审核状态">
                    <el-input :readonly="true" :value="formDatas.auditResult == '1' ? '通过' : '未通过'"></el-input>
                </el-form-item>

                <el-form-item label="最后审核时间">
                    <el-input :readonly="true" :value="formDatas.auditTimeStr"></el-input>
                </el-form-item>

            </el-form>

            <div slot="footer" class="dialog-footer">
                <!-- 添加、编辑模式 -->
                <div v-if="!isReadOnly && !isVerify">
                    <el-button                @click="cancelOnClick" >取 消</el-button>
                    <el-button type="primary" @click="confirmOnClick">保 存</el-button>
                </div>

                <!-- 查看模式 -->
                <div v-if="isReadOnly && !isVerify">
                    <el-button type="primary" @click="cancelOnClick">关 闭</el-button>
                </div>

                <!-- 审核模式 -->
                <div v-if="isVerify">
                    <el-button                                                    @click="cancelOnClick">取 消</el-button>
                    <el-button type="danger"  v-if="formDatas.auditResult == '1'" @click="rejectOnClick">驳 回</el-button>
                    <el-button type="success" v-else                              @click="agreeOnClick" >通 过</el-button>
                </div>
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
            isReadOnly: false,                // 是否为只读模式
            isVerify: false,                  // 是否为审核模式
            titleForStart: '',                // 标题的前半部
            titleForEnd: '商家信息',           // 标题的后半部
            isShowDialog: false,              // 内部控制是否显示对话框的属性
            isShowLogoImg: false,             // 是否显示公司Logo
            isShowBlisImg: false,             // 是否显示营业执照
            isShowCertificateImg: false,      // 是否显示营业执照
            formDatas: {},                    // 表单数据
            formRules: {                      // 表单验证规则
                computerNum: [{ required: true,  message:  '请填写纳税人识别号',   trigger: 'blur' }],
                loginName:   [{ required: true,  message:  '请填写登录名',        trigger: 'blur' }],
                corpname:    [{ required: true,  message:  '请填写公司名称',      trigger: 'blur' }],
                corptype:    [{ required: true,  message:  '请选择公司性质',      trigger: 'blur' }],
                roleType:    [{ required: true,  message:  '请选择角色分类',      trigger: 'blur' }],
                adminType:   [{ required: true,  message:  '请选择管理分类',      trigger: 'blur' }],
                name:        [
                              { required: true,  message:  '请填写联系人姓名',    trigger: 'blur' },
                              { min: 2, max: 20, message:  '请填写真实的姓名',    trigger: 'blur' }],
                idcard:      [
                              { required: true,   message: '请填写身份证号',      trigger: 'blur' },
                              { min: 18, max: 18, message: '请填写真实的身份证号', trigger: 'blur' }],
                phone:       [
                              { required: true,  message:  '请联系电话',          trigger: 'blur' },
                              { min: 8, max: 30, message:  '请填写正确的电话1',    trigger: 'blur' }]
            },
            uploadDatas: {},                  // 上传的额外数据
            corpTypes: [                      // 企业性质
                { id: 'CT001', name: '企业' },
                { id: 'CT002', name: '个体户' }
            ],
            roleTypes: [                      // 角色分类
                { id: '1', name: '品牌商' },
                { id: '2', name: '代理商' },
                { id: '3', name: '设计师' },
                { id: '4', name: '管家' },
                { id: '5', name: '施工队' },
                { id: '6', name: '装修公司' }
            ],
            adminTypes: [                     // 管理分类
                { id: '2', name: '商户管理员' },
                { id: '3', name: '商户工作人员' }
            ]
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
         * 显示对话框（审核+只读模式）
         *
         * @author      ZhengWei(HY)
         * @createDate  2020-10-29
         * @version     v1.0
         */
        showVerify(i_FormDatas)
        {
            this.show(i_FormDatas);
            this.isReadOnly = true;
            this.isVerify   = true;
        },

        /**
         * 显示对话框（只读模式）
         *
         * @author      ZhengWei(HY)
         * @createDate  2020-10-27
         * @version     v1.0
         */
        showOnlyRead(i_FormDatas)
        {
            this.show(i_FormDatas);
            this.isReadOnly = true;
            this.isVerify   = false;
        },


        /**
         * 显示对话框
         *
         * @author      ZhengWei(HY)
         * @createDate  2020-10-23
         * @version     v1.0
         */
        show(i_FormDatas)
        {
            if ( i_FormDatas === null || i_FormDatas === undefined || i_FormDatas === '' )
            {
                this.titleForStart = '添加';
                this.formDatas     = {};
            }
            else
            {
                this.titleForStart = '编辑';
                this.formDatas     = i_FormDatas;

                if ( this.formDatas.logo_url )
                {
                    this.isShowLogoImg = true;
                }
                else
                {
                    this.isShowLogoImg = false;
                }

                if ( this.formDatas.blis_url )
                {
                    this.isShowBlisImg = true;
                }
                else
                {
                    this.isShowBlisImg = false;
                }

                if ( this.formDatas.certificate_url )
                {
                    this.isShowCertificateImg = true;
                }
                else
                {
                    this.isShowCertificateImg = false;
                }
            }

            this.isShowDialog = true;
            this.isReadOnly   = false;
            this.isVerify     = false;
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
                    delete this.formDatas.auditTime;

                    this.$post('/mwcBase/no/merchant/' + (this.formDatas.innerid ? 'update' : 'save'), this.formDatas).then(res =>
                    {
                        if ( res && res.success )
                        {
                            this.$message({
                                type: 'success',
                                message: '操作成功'
                            });

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
                        message: '请填写真实有效的信息'
                    });
                }
            });
		},


        /**
         * 同步用户数据。
         *
         * @author      ZhengWei(HY)
         * @createDate  2020-10-30
         * @version     v1.0
         */
        syncMerchant(i_SuccessCallBack)
        {
            this.$post('/manwucai/rest/user/saveMerchantInfo',
            {
                body: {
                    frontCardUrl       : '',
                    backCardUrl        : '',
                    merchantLicenseUrl : this.formDatas.blis_url,
                    merchantName       : this.formDatas.corpname,
                    merchantIndustryI  : (this.formDatas.industry    ? this.formDatas.industry    : ''),
                    merchantProduct    : (this.formDatas.mainProduct ? this.formDatas.mainProduct : ''),
                    merchantWebsite    : '',
                    computerNum        : this.formDatas.computerNum,
                    merchantCardID     : '',
                    merchantOtherCer   : '',
                    merchantType       : (this.formDatas.corptype === '企业' ? '1' : '0'),
                    identypeCode       : this.formDatas.roleType,
                    userType           : this.formDatas.adminType,
                    mobilePhone        : this.formDatas.phone,
                    sex                : '0',
                    telPhone           : '',
                    nickName           : '',
                    realName           : this.formDatas.name,
                    idCardNum          : this.formDatas.idcard,
                    email              : '',
                    loginName          : this.formDatas.loginName,
                    userId             : (this.formDatas.user_id ? this.formDatas.user_id : '')
                }
            })
            .then(res =>
            {
                if ( res && res.result && res.body )
                {
                    i_SuccessCallBack(res);
                }
                else
                {
                    this.$message({
                        type: 'fail',
                        message: '请稍后重新尝试'
                    });
                }
            });
        },


        /**
         * 审核通过。
         *
         * @author      ZhengWei(HY)
         * @createDate  2020-10-29
         * @version     v1.0
         */
        agreeOnClick(e)
        {
            this.$refs.formValidate.validate((i_Valid) =>
            {
                if ( i_Valid )
                {
                    var v_This = this;
                    this.syncMerchant(function(i_MerchantRes)
                    {
                        v_This.formDatas.user_id = i_MerchantRes.body.userId;

                        v_This.$post('/mwcBase/no/merchant/verify',
                        {
                            innerid: v_This.formDatas.innerid,
                            user_id: v_This.formDatas.user_id,
                            auditResult: '1'
                        })
                        .then(res =>
                        {
                            if ( res && res.success )
                            {
                                v_This.$message({
                                    type: 'success',
                                    message: '操作成功'
                                });

                                v_This.hide();
                                if ( v_This.succeed !== null && v_This.succeed !== undefined )
                                {
                                    v_This.succeed(v_This.formDatas);
                                }
                            }
                            else
                            {
                                v_This.$message({
                                    type: 'fail',
                                    message: '请稍后重新尝试'
                                });
                            }
                        });
                    });
                }
                else
                {
                    this.$message({
                        type: 'fail',
                        message: '请填写真实有效的信息，再重新审核尝试'
                    });
                }
            });
        },


        /**
         * 审核驳回。
         *
         * @author      ZhengWei(HY)
         * @createDate  2020-10-29
         * @version     v1.0
         */
        rejectOnClick(e)
        {
            this.$post('/mwcBase/no/merchant/verify',
            {
                innerid: this.formDatas.innerid,
                auditResult: '0'
            })
            .then(res =>
            {
                if ( res && res.success )
                {
                    this.$message({
                        type: 'success',
                        message: '操作成功'
                    });

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
        },


        /**
         * 上传前后的验证。
         *
         * @author      ZhengWei(HY)
         * @createDate  2020-10-27
         * @version     v1.0
         */
        uploadBeforeValidate(i_File)
        {
            const isJPG  = (i_File.type === 'image/jpeg') || (i_File.type === 'image/png');
            const isSize = i_File.size / 1024 / 1024 < 5;

            if ( !isJPG )
            {
                this.$message.error('上传图片只能是 JPG 格式!');
            }
            if ( !isSize )
            {
                this.$message.error('上传图片大小不能超过 5MB!');
            }

            return isJPG && isSize;
        },


        /**
         * 公司Logo上传成功后的事件。
         *
         * @author      ZhengWei(HY)
         * @createDate  2020-10-27
         * @version     v1.0
         */
        logoUploadSuccess(res, file)
        {
            this.isShowLogoImg      = true;
            this.formDatas.logo_url = res.url;

            if ( this.$refs.logoURLImg )
            {
                this.$refs.logoURLImg.src = this.formDatas.logo_url;
            }
        },


        /**
         * 营业执照上传成功后的事件。
         *
         * @author      ZhengWei(HY)
         * @createDate  2020-10-28
         * @version     v1.0
         */
        blisUploadSuccess(res, file)
        {
            this.isShowBlisImg      = true;
            this.formDatas.blis_url = res.url;

            if ( this.$refs.blisURLImg )
            {
                this.$refs.blisURLImg.src = this.formDatas.blis_url;
            }
        },


        /**
         * 品牌认证书上传成功后的事件。
         *
         * @author      ZhengWei(HY)
         * @createDate  2020-10-28
         * @version     v1.0
         */
        certificateUploadSuccess(res, file)
        {
            this.isShowCertificateImg      = true;
            this.formDatas.certificate_url = res.url;

            if ( this.$refs.certificateURLImg )
            {
                this.$refs.certificateURLImg.src = this.formDatas.certificate_url;
            }
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
