<template>
    <div>
        <!--    面包屑导航区域-->
        <el-breadcrumb separator-class="el-icon-arrow-right">
            <el-breadcrumb-item :to="{path:'/welcome'}">首页</el-breadcrumb-item>
            <el-breadcrumb-item>研究组管理</el-breadcrumb-item>
            <el-breadcrumb-item>研究组列表</el-breadcrumb-item>
        </el-breadcrumb>

        <div class="all-groups">
            <el-row :gutter="12">
                <el-col :span="6" v-for="(group,index) in groups" :key="index" style="margin-bottom: 15px;">
                    <el-card shadow="hover" >
                        <!-- flex布局下 align调整col组件垂直方向排列：top、middle、bottom；justify调整水平方向上的排列 -->
                        <el-row :gutter="2" type="flex" align="middle">
                            <el-col :span="20" >
                                <el-tooltip effect="light" content="查看研究组详情" placement="top" :enterable=false>
                                    <el-link style="font-size: 15px;" :underline="false" @click="getGroupInfo(group.groupId)">{{group.groupName}}</el-link>                                    </el-tooltip>
                            </el-col>
                            <el-col :span="4">
                                <el-tooltip effect="light" content="加入研究组" placement="top" :enterable=false>
                                    <el-button circle icon="el-icon-plus" @click="joinGroup(group.groupId)"></el-button>
                                </el-tooltip>
                            </el-col>
                        </el-row>
                    </el-card>
                </el-col>
            </el-row>
        </div>

        <!--老师创建研究组按钮-->
        <el-button type="primary" round class="add-group" v-if="type === 'TEACHER' && groupId === 'null'"
                   @click="showAddGroupDialog">老师创建研究组</el-button>

        <!--创建研究组对话框-->
        <el-dialog title="创建研究组" :visible.sync="addGroupDialogVisible" width="50%">
            <el-form ref="addGroupFormRef" :model="addGroupForm" :rules="addGroupFormRules" label-width="100px">
                <el-form-item label="创建者" label-width="100px">
                    <el-input v-model="username" disabled></el-input>
                </el-form-item>
                <el-form-item label="研究组名称" label-width="100px" prop="groupName">
                    <el-input v-model="addGroupForm.groupName" ></el-input>
                </el-form-item>
                <el-form-item label="研究方向" label-width="100px" prop="directions">
                    <el-tag
                            v-for="tag in addGroupForm.directions"
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
                <el-form-item label="简要描述" label-width="100px" prop="description">
                    <el-input :rows="5" type="textarea" v-model="addGroupForm.description" maxlength="300" show-word-limit ></el-input>
                </el-form-item>

            </el-form>

            <span slot="footer" class="dialog-footer">
                <el-button round @click="cancelAddGroup">取消</el-button>
                <el-button round type="primary" @click="submitForm">立即创建</el-button>
            </span>
        </el-dialog>

        <!--研究组详细信息对话框-->
        <el-dialog title="研究组详细信息" :visible.sync="groupInfoDialogVisible" width="50%" >
            <el-form ref="groupInfoFormRef" :model="groupInfoForm"  label-width="100px">
                <el-form-item label="名称" label-width="60px">
                    <el-input v-model="groupInfoForm.groupName" disabled></el-input>
                </el-form-item>
                <el-form-item label="简介" label-width="60px">
                    <el-input v-model="groupInfoForm.description" disabled></el-input>
                </el-form-item>
                <el-form-item label="老师" label-width="60px">
                    <el-button type="text" @click="groupTeacherListVisible = true">查看老师列表</el-button>
                </el-form-item>
                <el-form-item label="学生" label-width="60px">
                    <el-button type="text" @click="groupStudentListVisible = true">查看学生列表</el-button>
                </el-form-item>
            </el-form>

            <!--内层老师列表对话框-->
            <el-dialog width="35%" title="研究组老师列表" :visible.sync="groupTeacherListVisible" append-to-body>
                <el-table :data="groupInfoForm.teacherInfos">
                    <el-table-column property="userId" label="用户ID" width="150"></el-table-column>
                    <el-table-column property="username" label="用户名" width="150"></el-table-column>
                    <el-table-column property="trueName" label="真名" width="150"></el-table-column>
                </el-table>
                <span slot="footer" class="dialog-footer">
                    <el-button round type="primary" @click="groupTeacherListVisible = false">关 闭</el-button>
                </span>
            </el-dialog>

            <!--内层学生列表对话框-->
            <el-dialog width="35%" title="研究组学生列表" :visible.sync="groupStudentListVisible" append-to-body>
                <el-table :data="groupInfoForm.studentInfos">
                    <el-table-column property="userId" label="用户ID" width="150"></el-table-column>
                    <el-table-column property="username" label="用户名" width="150"></el-table-column>
                    <el-table-column property="trueName" label="真名" width="150"></el-table-column>
                </el-table>
                <span slot="footer" class="dialog-footer">
                    <el-button round type="primary" @click="groupStudentListVisible = false">关 闭</el-button>
                </span>
            </el-dialog>

            <span slot="footer" class="dialog-footer">
                <el-button round type="primary" @click="groupInfoDialogVisible = false">关 闭</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        created() {
            this.username = window.sessionStorage.getItem("username");
            this.type = window.sessionStorage.getItem("userType");
            this.id = window.sessionStorage.getItem("userId");
            this.groupId = window.sessionStorage.getItem("groupId");
            this.getAllGroups();
        },
        data(){
            return{
                id: null,
                username: '',
                type: '',
                groupId: null,
                //创建研究组对话框
                addGroupDialogVisible: false,
                addGroupForm:{
                    groupName: '',
                    description: '',
                    directions: []
                },
                addGroupFormRules:{
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
                groups:[],
                //研究组详细信息对话框
                groupInfoDialogVisible: false,
                //研究组老师列表内层对话框
                groupTeacherListVisible: false,
                //研究组学生列表内层对话框
                groupStudentListVisible: false,
                groupInfoForm: {
                    groupId: null,
                    groupName: '',
                    description: '',
                    studentInfos: [],
                    teacherInfos: []
                },
                // 创建研究组输入的研究方向相关数据
                inputVisible: false,
                inputValue: ""
            }
        },
        methods:{
            getAllGroups(){
                this.$axios.get('/researchGroup/simpleInfo/all').then(res => {
                    this.groups = res.data.data;
                }).catch(err => {
                    this.$message.error({message: err, duration: 1500, showClose: true});
                });
            },
            //获取研究组详细信息事件
            getGroupInfo(id){
                this.axios.get('/researchGroup/detailedInfo?groupId=' + id).then(res => {
                    if('errorCode' in res.data){
                        this.$message.error({message:res.data.errorMsg, duration:1500, showClose:true});
                    }else{
                        this.groupInfoForm = res.data.data;
                        this.groupInfoDialogVisible = true;
                    }
                }).catch(err => {
                    this.$message.error({message: err, duration: 1500, showClose: true});
                });
            },
            //更新研究组详细信息对话框
            updateGroupInfoDialog(id){
                this.axios.get('/researchGroup/detailedInfo?groupId=' + id).then(res => {
                    if(res.data !== ""){
                        this.$message.error({message:res.data.errorMsg, duration:1500, showClose:true});
                    }else{
                        this.groupInfoForm = res.data.data;
                    }
                }).catch(err => {
                    this.$message.error({message: err, duration: 1500, showClose: true});
                });
            },
            // 申请加入研究组事件
            async joinGroup(id){
                //弹框询问用户是否加入研究组
                const confirmResult = await this.$confirm('此操作后将申请加入该研究组, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).catch(err => err);//return err的简化
                //如果点击确认按钮 返回confirm字符串
                //如果点击取消按钮 不catch就会报错  catch后返回cancel字符串
                // console.log(confirmResult);
                if(confirmResult !== 'confirm'){
                    this.$message.info({message: "已取消申请！", duration: 1500, showClose: true});
                }else{
                    this.$axios.post('/user/applyToJoinGroup?groupId=' + id + '&userId=' + this.id).then(res => {
                        console.log(res);
                        if(res.data !== ""){
                            this.$message.error({message:res.data.errorMsg, duration:1500, showClose:true});
                        }else{
                            this.$message.success({message: "已申请加入该研究组，请耐心等待回复！", duration: 1500, showClose: true});
                            this.$router.push('/infos');
                        }
                    }).catch(err => {
                        this.$message.error({message: err, duration: 1500, showClose: true});
                    });
                }
            },
            //展示创建研究组对话框
            showAddGroupDialog(){
                this.addGroupForm.description = "";
                this.addGroupForm.groupName = "";
                this.addGroupForm.directions = [];
                this.addGroupDialogVisible = true;
            },
            //没有创建过研究组的老师  创建研究组
            submitForm(){
                this.$refs.addGroupFormRef.validate(valid =>{
                    if(!valid){
                        this.$message.error({message: '创建失败！请重新输入数据',duration: 1500, showClose: true});
                    }else{
                        let form = {};
                        form.creatorId = this.id;
                        form.groupName = this.addGroupForm.groupName;
                        form.description = this.addGroupForm.description;
                        form.directions = this.addGroupForm.directions;
                        form.portrait = null;
                        this.$axios.put('/user/createAndJoinResearchGroup', form).then(res => {
                            if('errorCode' in res.data){
                                this.$message.error({message:res.data.errorMsg, duration:1500, showClose:true});
                                this.cancelAddGroup();
                            }else{
                                this.$message.success({message:"创建成功！", duration:1500, showClose:true});
                                window.sessionStorage.setItem("groupId", res.data.data.id);
                                this.$router.push('/myGroup');
                            }
                        }).catch(err => {
                            this.$message.error({message: err,duration: 1500, showClose: true});
                        });
                    }
                })
            },
            cancelAddGroup(){
                this.$refs.addGroupFormRef.resetFields();
                this.addGroupDialogVisible = false;
            },
            // start
            // 动态加载研究方向标签
            handleClose(tag) {
                this.addGroupForm.directions.splice(this.addGroupForm.directions.indexOf(tag), 1);
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
                    this.addGroupForm.directions.push(inputValue);
                }
                this.inputVisible = false;
                this.inputValue = '';
            }
            // end
        }
    }
</script>

<style lang="less" scoped>
.add-group{
    margin-top: 15px;
}
.all-groups{
    margin-top: 30px;
}

</style>