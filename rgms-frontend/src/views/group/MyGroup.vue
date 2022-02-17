<template>
    <div>
        <!--    面包屑导航区域-->
        <el-breadcrumb separator-class="el-icon-arrow-right">
            <el-breadcrumb-item :to="{path:'/welcome'}">首页</el-breadcrumb-item>
            <el-breadcrumb-item>研究组管理</el-breadcrumb-item>
            <el-breadcrumb-item>我的研究组</el-breadcrumb-item>
        </el-breadcrumb>

        <!--研究组详情卡片-->
        <div v-loading="loading" class="group-info-box">
            <div class="group-title-box">
                <span class="group-title">{{myGroup.groupName}}</span>
                <div class="get-out-group">
                    <el-dropdown>
                        <el-link>查看<i class="el-icon-view el-icon--right"></i></el-link>
                        <el-dropdown-menu slot="dropdown">
                            <el-dropdown-item @click.native="showEditDialog" v-if="userType === 'TEACHER'">编辑研究组信息</el-dropdown-item>
<!--                            <el-dropdown-item @click.native="getAllGroups">查看所有研究组</el-dropdown-item>-->
                            <el-dropdown-item @click.native="getOutGroup" v-if="userType === 'STUDENT'">退出研究组</el-dropdown-item>
                            <el-dropdown-item @click.native="deleteGroup" v-if="userType === 'TEACHER'">删除研究组</el-dropdown-item>
                        </el-dropdown-menu>
                    </el-dropdown>
                </div>
            </div>
            <div class="group-des" style="margin-top: 15px; margin-bottom: 15px;">{{myGroup.description}}</div>
            <!--研究方向tag-->
            <div><el-tag v-for="(tag,index) in myGroup.directions" :key="index">{{tag}}</el-tag></div>
            <!--研究组老师列表-->
            <div class="list-title">老师</div>
            <el-row :gutter="20"  >
                <el-col :span="4" v-for="teacherInfo in myGroup.teacherInfos" :key="teacherInfo" style="margin-bottom: 15px;">
                    <div class="grid-content bg-purple">
                    <el-avatar  :size="80" :src="teacherInfo.portrait">{{teacherInfo.username}} </el-avatar>
                    <div style="font-size: 18px; margin-top: 15px;" >{{teacherInfo.username}}</div></div>
                </el-col>
            </el-row>
            <div class="list-title">学生</div>
            <!--研究组学生列表-->
            <el-row :gutter="20"  >
                <el-col :span="4" v-for="studentInfo in myGroup.studentInfos" :key="studentInfo" style="margin-bottom: 15px;">
                    <div class="grid-content bg-purple">
                        <el-avatar  :size="80" :src="studentInfo.portrait">{{studentInfo.username}} </el-avatar>
                        <div style="font-size: 18px; margin-top: 15px;" >{{studentInfo.username}}</div>
                    </div>
                </el-col>
            </el-row>
        </div>

        <el-dialog title="修改研究组信息" :visible.sync="editDialogVisible" width="50%">
            <el-form ref="editGroupFormRef" :model="editGroupForm" :rules="editGroupFormRules" >
                <el-form-item label="创建者" label-width="80px">
                    <el-input v-model="myGroup.creatorInfo.username" disabled></el-input>
                </el-form-item>
                <el-form-item label="组名" label-width="80px" required="" prop="groupName">
                    <el-input v-model="editGroupForm.groupName" ></el-input>
                </el-form-item>
                <el-form-item label="研究方向" label-width="80px" prop="directions">
                    <el-tag
                            v-for="tag in editGroupForm.directions"
                            :key="tag"
                            closable
                            :disable-transitions="false"
                            @close="handleClose(tag)">
                        {{tag}}
                    </el-tag>
                    <el-input
                            class="input-new-tag"
                            v-if="inputVisible"
                            v-model="inputValue"
                            ref="saveTagInput"
                            size="small"
                            @keyup.enter.native="handleInputConfirm"
                            @blur="handleInputConfirm"
                    >
                    </el-input>
                    <el-button v-else class="button-new-tag" size="small" @click="showInput">+ 研究方向</el-button>
                </el-form-item>
                <el-form-item label="简介" label-width="80px" required="" prop="description">
                    <el-input :rows="5" type="textarea" maxlength="300" show-word-limit v-model="editGroupForm.description" ></el-input>
                </el-form-item>

            </el-form>

            <span slot="footer" class="dialog-footer">
                <el-button round @click="editDialogVisible = false">取 消</el-button>
                <el-button round type="primary" @click="updateGroupInfo">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        created(){
            this.$axios.get('/user/groupId?userId=' + window.sessionStorage.getItem("userId")).then(res => {
                if(res.data.data === null){
                    this.$message.warning({message: '请先加入研究组！', duration: 1500, showClose: true});
                    this.$router.push('/allGroups');
                }else{
                    this.groupId = res.data.data;
                    this.id = window.sessionStorage.getItem("userId");
                    this.userType = window.sessionStorage.getItem("userType");
                    this.getMyGroup();
                }
            }).catch(err => {
                this.$message.error({message: err, duration: 1500, showClose: true});
            })
        },
        data(){
            return{
                loading: false,
                userType: '',
                id: null,
                myGroup: {
                    creatorInfo: {
                        userId: null,
                        trueName: "",
                        username: "",
                        portrait: ""
                    },
                    groupName:"",
                    description: "",
                    directions: [],
                    studentInfos: [],
                    teacherInfos: []
                },
                editDialogVisible: false,
                editGroupForm: {
                    id: null,
                    groupName: '',
                    description: '',
                    directions: [],
                    portrait: '',
                },
                editGroupFormRules: {
                    groupName: [
                        { required: true, message: '组名不能为空', trigger: 'blur' },
                        { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
                    ],
                    description: [
                        { required: true, message: '描述不能为空', trigger: 'blur' },
                        { min: 1, max: 300, message: '长度在 1 到 300 个字符', trigger: 'blur' }
                    ],
                    directions: [
                        { required: true, message: '研究方向不能为空', trigger: 'blur' },
                    ]
                },
                // 编辑研究组输入的研究方向相关数据
                inputVisible: false,
                inputValue: ""

            }
        },
        methods:{
            //获取我的研究组数据
            getMyGroup(){
                //先判断是否在组内  是：展示研究组信息  否：展示研究组列表
                this.loading = true;
                this.$axios.get('/researchGroup/detailedInfo?groupId=' + this.groupId).then(res => {
                    this.myGroup = res.data.data;
                }).catch(err => {
                    this.$message.error({message: err, duration: 1500, showClose: true});
                });
                this.loading = false;
            },
            //点击退出小组按钮事件
            async getOutGroup(){
                const confirmResult = await this.$confirm('此操作将永久退出该研究组, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).catch(err => err);
                //如果点击确认按钮 返回confirm字符串
                //如果点击取消按钮
                // 不catch就会报错  catch后返回cancel字符串
                // console.log(confirmResult);
                if(confirmResult !== 'confirm'){
                    this.$message.info({message: "已取消退出！", duration: 1500, showClose: true});
                }else{
                    this.$axios.post('/user/applyToExitGroup?userId=' + this.id).then(res => {
                        if(res.data.data === "success"){
                            this.$message.success({message: "申请退出研究组成功！请耐心等待回复", duration: 2000, showClose: true});
                        }else{
                            this.$message.error({message: res.data.errorMsg, duration: 2000, showClose: true});
                        }
                    }).catch(err => {
                        this.$message.error({message: err, duration: 1500, showClose: true});
                    });
                }
            },
            getAllGroups(){
                this.$router.push('/allGroups');
            },
            //展示编辑小组信息的对话框
            showEditDialog(){
                this.editGroupForm.id = this.myGroup.groupId;
                this.editGroupForm.groupName = this.myGroup.groupName;
                this.editGroupForm.description = this.myGroup.description;
                this.editGroupForm.directions = this.myGroup.directions;
                this.editDialogVisible = true;
            },
            //更新研究组信息
            updateGroupInfo(){
                this.$axios.put('/researchGroup/groupInfo', this.editGroupForm).then(res => {
                    if(res.data === ""){
                        this.getMyGroup();
                        this.$message.success({message: '修改成功！', duration:1500, showClose:true});
                    }else{
                        this.$message.error({message:res.data.errorMsg, duration:1500, showClose:true});
                    }
                }).catch(err => {
                    this.$message.error({message:err, duration:1500, showClose:true});
                });
                this.editDialogVisible = false;
            },
            //老师删除研究组
            async deleteGroup(){
                const confirmResult = await this.$confirm('此操作将永久删除并退出该研究组, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).catch(err => err);
                //如果点击确认按钮 返回confirm字符串
                //如果点击取消按钮
                // 不catch就会报错  catch后返回cancel字符串
                // console.log(confirmResult);
                if(confirmResult !== 'confirm'){
                    this.$message.info({message: "已取消删除！", duration: 1500, showClose: true});
                }else{
                    this.$axios.delete('/user/researchGroup?groupId='+ this.myGroup.groupId +'&userId=' + this.id).then(res => {
                        if(res.data !== ""){
                            this.$message.success({message:res.data.errorMsg, duration:1500, showClose:true});
                        }else{
                            this.$message.success({message:'删除成功！请重新创建并加入研究组', duration:1500, showClose:true});
                            this.$router.push('/allGroups');
                            window.sessionStorage.setItem("groupId",null);
                        }
                    }).catch(err=>{
                        this.$message.error({message:err, duration:1500, showClose:true});
                    })
                }
            },
            // start 编辑研究组信息
            // 动态加载研究方向标签
            handleClose(tag) {
                this.editGroupForm.directions.splice(this.editGroupForm.directions.indexOf(tag), 1);
            },
            showInput() {
                this.inputVisible = true;
                this.$nextTick(_ => {
                    this.$refs.saveTagInput.$refs.input.focus();
                });
            },
            handleInputConfirm() {
                let inputValue = this.inputValue;
                if (inputValue) {
                    this.editGroupForm.directions.push(inputValue);
                }
                this.inputVisible = false;
                this.inputValue = '';
            }
            // end
        }
    }
</script>

<style scoped>
.group-title-box{
    display: flex;
    flex-direction: row;
    justify-content: space-around;
}
.group-title{
    width: 80%;
    font-size: 80px;
    font-weight: bold;
    color: #2b4b6b;
    font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
}
.get-out-group{
    width: 20%;
    display: flex;
    flex-direction: row;
    justify-content: flex-start;
    align-items: center;
}
.group-des{
    margin-top: 15px;
    font-size: 18px;
    font-weight: 100;
    font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
    color: #a0a0a0;
}
.list-title{
    margin-top: 15px;
    font-size: 20px;
    font-weight: 200;
    font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
    color: #2b4b6b;
}
.grid-content {
    border-radius: 4px;
    min-height: 36px;
    display: flex;
    flex-direction: column;
    justify-content: space-around;
    align-items: center;
}
.bg-purple {

}
    .group-info-box{
        margin-left: 30px;
        margin-top: 15px;
    }
    .not-in-group{
        margin-left: 30px;
    }


</style>