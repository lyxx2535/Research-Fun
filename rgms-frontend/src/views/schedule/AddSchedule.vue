<template>
    <div>
        <!--    面包屑导航区域-->
        <el-breadcrumb separator-class="el-icon-arrow-right">
            <el-breadcrumb-item :to="{path:'/welcome'}">首页</el-breadcrumb-item>
            <el-breadcrumb-item>日程管理</el-breadcrumb-item>
            <el-breadcrumb-item>创建日程</el-breadcrumb-item>
        </el-breadcrumb>
        <!--    卡片视图区域-->
        <el-card shadow="hover">
            <el-form :model="ruleForm" :rules="rules" ref="ruleFormRef" label-width="70px" class="demo-ruleForm">
                <el-form-item label="日期" required>
                    <el-col :span="11">
                        <!-- el-date-picker组件获取的日期格式  通过format转换成string格式标准的年月日 value-format="yyyy-MM-dd"  format="yyyy-MM-dd"-->
                        <el-form-item prop="date">
                            <el-date-picker type="date" placeholder="选择日期" v-model="ruleForm.date" style="width: 50%;"
                                            value-format="yyyy-MM-dd"
                                            :picker-options="pickerOptions"
                                            format="yyyy-MM-dd">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>
                </el-form-item>
                <el-form-item label="内容" prop="content">
                    <!-- :rows="5"控制textarea的大小（高度）   -->
                    <el-input type="textarea" v-model="ruleForm.content" maxlength="300"
                              :rows="5" show-word-limit>
                    </el-input>
                </el-form-item>

                <el-form-item label="发送给" v-if="userType === 'TEACHER'">
                    <el-transfer
                            :titles="['我的学生', '接收消息']"
                            filterable
                            filter-placeholder="请输入学生用户名"
                            v-model="targetList"
                            :data="studentList">
                    </el-transfer>

                </el-form-item>
                <el-form-item>
                    <el-button round type="primary" @click="submitForm">立即创建</el-button>
                    <el-button round @click="resetForm">重置</el-button>
                </el-form-item>
            </el-form>
        </el-card>
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
                    this.id = window.sessionStorage.getItem('userId');
                    this.userType = window.sessionStorage.getItem('userType');
                    if(this.userType === 'TEACHER'){
                        this.generateStudentList();
                    }
                }
            }).catch(err => {
                this.$message.error({message: err, duration: 1500, showClose: true});
            })

        },
        data() {
            return {
                id: '',
                groupId: 0,
                userType: '',
                ruleForm:{
                    date:'',
                    content: ''
                },
                rules: {
                    date: [
                        { required: true, message: '日期不能为空', trigger: 'blur' },
                    ],
                    content: [
                        { required: true, message: '内容不能为空', trigger: 'blur' }
                    ]
                },
                studentList:[],
                targetList:[],
                //禁止选择当前日期之前的日期
                pickerOptions:{
                    disabledDate(v) {
                        return v.getTime() < new Date().getTime() - 86400000;
                    }
                }
            };
        },
        methods: {
            //学生列表数据生成
            generateStudentList(){
                this.$axios.get('/researchGroup/detailedInfo?groupId=' + this.groupId).then(res => {
                    let originList = res.data.data.studentInfos;
                    if(originList.length !== 0){
                        originList.forEach((student, index) =>{
                            this.studentList.push({
                                label: student.username,
                                key: student.userId
                            })
                        })
                    }
                }).catch(err => {
                    this.$message.error({message: err, duration: 1500, showClose: true});
                })
            },
            //创建日程
            submitForm() {
                this.$refs.ruleFormRef.validate(valid => {
                    if(valid){
                        //老师创建日程
                        if(this.userType === 'TEACHER'){
                            let teacherSchedule = {};
                            teacherSchedule.creatorId = this.id;
                            teacherSchedule.date = this.ruleForm.date;
                            teacherSchedule.content = this.ruleForm.content;
                            teacherSchedule.stakeHolderIds = this.targetList;

                            this.$axios.post('/schedule/createForTeacher', teacherSchedule).then(res => {
                                if(res.data !== ""){
                                    this.$message.error({message: res.data.errorMsg, duration:1500, showClose:true});
                                }else{
                                    this.$message.success({message: "创建成功！", duration:1500, showClose:true});
                                    this.$router.push('/personalSchedule');
                                }
                            }).catch(err => {
                                this.$message.error({message: err, duration: 1500, showClose: true});
                            });
                        }
                        //学生创建日程
                        else{
                            this.$axios.post('/schedule/create?userId='+ this.id, this.ruleForm).then(res => {
                                if(res.data === ""){
                                    this.$message.success({message: '添加成功', duration: 1500, showClose: true});
                                    this.$router.push('/personalSchedule');
                                }else{
                                    this.$message.error({message: res.data.errorMsg, duration: 1500, showClose: true});
                                }
                            }, err => {
                                this.$message.error({message: err, duration: 1500, showClose: true});
                            })
                        }

                    }
                    //valid验证失败
                    else{
                        this.$message.warning({message: '添加失败！', duration: 1500, showClose: true});
                        return false;
                    }
                });
            },
            //重置表单
            resetForm() {
                this.$refs.ruleFormRef.resetFields();
                this.studentList = [];
                this.targetList = [];
                this.generateStudentList();
            },
        }
    }
</script>

<style scoped lang="less">

</style>