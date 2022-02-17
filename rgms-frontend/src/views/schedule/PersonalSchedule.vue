<template>
    <div>
        <!--    面包屑导航区域-->
        <el-breadcrumb separator-class="el-icon-arrow-right">
            <el-breadcrumb-item :to="{path:'/welcome'}">首页</el-breadcrumb-item>
            <el-breadcrumb-item>日程管理</el-breadcrumb-item>
            <el-breadcrumb-item>我的日程</el-breadcrumb-item>
        </el-breadcrumb>
        <!--    卡片视图区域-->
        <div class="schedule-list">
            <!-- 日程列表区域:卡片形式-->
            <el-row :gutter="24">
                <el-col :span="24" :offset="0" v-for="(item,index) in tableData" :key="index" style="margin-bottom: 15px;">
                    <el-card shadow="hover">
                        <div :class="testPastSchedule(item.date) ? 'schedule-past' : 'schedule-date'">{{item.date}}</div>
                        <div class="schedule-content" ><i>{{item.content}}</i></div>
                        <div class="schedule-footer">
                            <div v-if="item.state !== '未完成'" class="schedule-state-finished"><i class="el-icon-circle-check" style="width: 90%; font-size:28px; color: green" ></i></div>
                            <div v-else class="schedule-state-unfinished"><i class="el-icon-zoom-in" style="width: 90%; font-size: 28px; color: red;"></i></div>

                            <div class="schedule-actions">
                                <!--可以在过去未完成的日程上新建日程，内容会复制未完成的日程-->
                                <el-tooltip v-if="testPastSchedule(item.date) && testScheduleUnfinished(item.state)" effect="light" content="复制并新建日程" placement="top" :enterable=false>
                                    <el-button type="" icon="el-icon-document-copy"
                                               size="" circle @click="showCopyScheduleDialog(item.scheduleId)"></el-button>
                                </el-tooltip>
                                <!--完成按钮：未来且未完成时显示-->
                                <el-tooltip v-if="(!testPastSchedule(item.date)) && testScheduleUnfinished(item.state)" effect="light" content="完成日程" placement="top" :enterable=false>
                                    <el-button type="" icon="el-icon-check" class="btn-finish-hover-green"
                                               size="" circle @click="finishSchedule(item.scheduleId)"></el-button>
                                </el-tooltip>
                                <!--修改按钮：未来且未完成时显示-->
                                <el-tooltip v-if="(!testPastSchedule(item.date))  && testScheduleUnfinished(item.state)" effect="light" content="修改日程" placement="top" :enterable=false>
                                    <el-button type="" icon="el-icon-edit"
                                               size="" circle @click="showEditDialog(item.scheduleId)"></el-button>
                                </el-tooltip>
                                <!--删除按钮-->
                                <el-tooltip effect="light" content="删除日程" placement="top" :enterable=false>
                                    <el-button type="" icon="el-icon-delete" class="btn-delete-hover-red"
                                               size="" circle="" @click="deleteSchedule(item.scheduleId)"></el-button>
                                </el-tooltip>
                            </div>
                        </div>
                    </el-card>
                </el-col>
            </el-row>

            <!-- 分页栏区域-->
            <el-pagination
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                    :current-page="queryInfo.pageNum"
                    :page-sizes="[5,10,20]"
                    :page-size="queryInfo.pageSize"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="total">
            </el-pagination>
        </div>

        <!--修改用户的对话框 -->
        <el-dialog title="修改日程" :visible.sync="editDialogVisible" width="50%" >
            <el-form ref="editFormRef" :model="editForm" :rules="editFormRules" label-width="100px">
                <el-form-item label="创建者" label-width="60px">
                    <el-input v-model="editForm.constructor" disabled></el-input>
                </el-form-item>
                <el-form-item label="日期" label-width="60px">
                    <el-input v-model="editForm.date" disabled></el-input>
                </el-form-item>
                <!-- 只加上小红色星星只需要  :required="true"-->
                <el-form-item label="状态" label-width="60px">
                    <el-input v-model="editForm.state" disabled></el-input>
                </el-form-item>
                <el-form-item label="内容" label-width="60px" prop="content">
                    <el-input :rows="5" type="textarea" v-model="editForm.content" maxlength="300" show-word-limit ></el-input>
                </el-form-item>
            </el-form>

            <span slot="footer" class="dialog-footer">
                <el-button round @click="editDialogVisible = false">取 消</el-button>
                <el-button round type="primary" @click="editSchedule">确 定</el-button>
            </span>
        </el-dialog>

        <!--复制并新建日程的对话框-->
        <el-dialog title="复制并新建日程" :visible.sync="copyDialogVisible" width="50%" >
            <el-form ref="copyFormRef" :model="copyForm" :rules="copyFormRules" >
                <el-form-item label="日期" required label-width="60px" >
                    <!-- el-date-picker组件获取的日期格式  通过format转换成string格式标准的年月日 value-format="yyyy-MM-dd"  format="yyyy-MM-dd"-->
                    <el-date-picker type="date" placeholder="选择日期" v-model="copyForm.date" style="width: 50%;"
                                    value-format="yyyy-MM-dd"
                                    :picker-options="pickerOptions"
                                    format="yyyy-MM-dd"></el-date-picker>
                </el-form-item>
                <el-form-item label="创建者" label-width="60px">
                    <el-input v-model="copyForm.constructor" disabled></el-input>
                </el-form-item>

                <el-form-item label="状态" label-width="60px">
                    <el-input v-model="copyForm.state" disabled></el-input>
                </el-form-item>
                <el-form-item label="内容" label-width="60px" prop="content">
                    <el-input :rows="5" type="textarea" v-model="copyForm.content" maxlength="300" show-word-limit disabled></el-input>
                </el-form-item>

            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button round @click="cancelCopy">取 消</el-button>
                <el-button round type="primary" @click="copySchedule">确 定</el-button>
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
                    this.id = window.sessionStorage.getItem('userId');
                    this.username = window.sessionStorage.getItem('username');
                    this.userType = window.sessionStorage.getItem('userType');
                    this.getScheduleList();
                }
            }).catch(err => {
                this.$message.error({message: err, duration: 1500, showClose: true});
            })
        },
        data() {
            return {
                id: null,
                username: '',
                groupId: null,
                total:0,
                //学生的个人日程列表
                tableData:[
                    // {
                    //     "scheduleId": 25,
                    //     "date": "2021年4月9日 星期五",
                    //     "content": "cccccc",
                    //     "state": "FINISHED"
                    // }
                ],
                //日程列表的参数对象
                queryInfo:{
                    query: '',
                    //当前的页数
                    pageNum: 1,
                    //每页显示的日程数量
                    pageSize: 10
                },
                //控制修改用户的对话框的显示与隐藏
                editDialogVisible: false,
                //修改日程对话框的表单值
                editForm: {
                    constructor: '',
                    scheduleId: null,
                    date: '',
                    state: '',
                    content: ''
                },
                editFormRules: {
                    content: [
                        { required: true, message: '内容不能为空', trigger: 'blur' },
                        { min: 1, max: 300, message: '长度在 1 到 300 个字符', trigger: 'blur' }
                    ]
                },
                //控制复制日程的对话框的显示与隐藏
                copyDialogVisible: false,
                //复制日程的表单
                copyForm:{
                    constructor: '',
                    scheduleId: null,
                    creatorId: null,
                    date: '',
                    state: '',
                    content: ''
                },
                studentList:[],
                targetList:[],
                //禁止选择当前日期之前的日期
                copyFormRules:{
                    date:[
                       { required: true, message: '内容不能为空', trigger: 'blur' },
                    ]
                },
                pickerOptions:{
                    disabledDate(v) {
                        return v.getTime() < new Date().getTime() - 86400000;
                    }
                }
            }
        },
        methods: {
            //获取日程列表数据
            getScheduleList(){
                this.$axios.get('/schedule/all/user?' +
                    'currentPage=' + (this.queryInfo.pageNum - 1) +
                    '&pageSize=' + this.queryInfo.pageSize +
                    '&userId=' + this.id)
                .then(res => {
                    let sourceTable = res.data.data;
                    this.tableData = sourceTable;
                    for(let i = 0; i < sourceTable.length;i++){
                        if(sourceTable[i].state === "UNFINISHED"){
                            this.tableData[i].state = "未完成";
                        }else if(sourceTable[i].state === "FINISHED"){
                            this.tableData[i].state = "已完成";
                        }
                    }
                    this.total = res.data.total;
                })
                .catch(err => {
                    this.$message.error({message: err, duration: 1500, showClose: true})
                })
                this.studentList = [];
                this.targetList = [];
            },
            //监听 pageSize 值变化的事件
            handleSizeChange(newSize){
                this.queryInfo.pageSize = newSize;
                this.getScheduleList();
            },
            //监听 pageNum 改变的事件
            handleCurrentChange(newPage){
                this.queryInfo.pageNum = newPage;
                this.getScheduleList();
            },
            //点击完成日程按钮触发事件
            async finishSchedule(scheduleId){
                const finishConfirmResult = await this.$confirm('此操作将更改该日程状态为“已完成”，且不可撤销, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).catch(err => err);
                if(finishConfirmResult !== 'confirm'){
                    this.$message.info({message: "已取消更改状态！", duration: 1500, showClose: true});
                }else{
                    this.$axios.post('/schedule/finish?scheduleId=' + scheduleId).then(async res => {
                        if(res.data !== ""){
                            this.$message.warning({message: res.data.errorMsg, duration: 1500, showClose: true})
                        }else{
                            this.getScheduleList();
                            this.$message.success({message: "已完成该日程！", duration: 1500, showClose: true})
                        }
                    }).catch(err => {
                        this.$message.error({message: err, duration: 1500, showClose: true});
                    });
                }
            },
            //展示编辑日程的对话框
            showEditDialog(scheduleId){
                //for循环获取特定scheduleId的日程信息
                this.editForm.constructor = this.username;
                this.editForm.scheduleId = scheduleId;
                for(let i = 0;i < this.tableData.length;i++){
                    if(this.tableData[i].scheduleId === scheduleId){
                        this.editForm.date = this.tableData[i].date;
                        this.editForm.content = this.tableData[i].content;
                        this.editForm.state = this.tableData[i].state;
                    }
                }
                this.editDialogVisible = true;
            },
            //点击编辑日程对话框的 确定 按钮
            editSchedule(){
                this.$refs.editFormRef.validate(async valid =>{
                    //发起修改日程的请求
                    if(valid){
                        let editFormSource = this.editForm;
                        await this.$axios.post('/schedule/edit?content=' + editFormSource.content
                            + '&scheduleId=' + editFormSource.scheduleId)
                        .then(res => {
                            if(res.data !== ""){
                                this.editDialogVisible = false;
                                //修改失败
                                this.$message.error({message: res.data.errorMsg, duration:1500, showClose:true});
                            }else{
                                //关闭对话框
                                this.editDialogVisible = false;
                                //刷新数据列表
                                this.getScheduleList();
                                //提示修改成功
                                this.$message.success({message: "修改成功！", duration: 1500, showClose: true});
                            }
                        })
                        .catch(err => {
                            this.$message.warning({message: err, duration: 1500, showClose: true})
                        })
                    }else{
                        this.$message.warning({message: "数据校验未通过！", duration: 1500, showClose: true})
                    }
                })
            },
            // 普通删除日程
            deleteSchedule(scheduleId){
                this.$axios.post('/schedule/delete?scheduleId=' + scheduleId).then(res => {
                    if(res.status === 200){
                        this.$message.success({message: "删除成功！", duration: 1500, showClose: true});
                        this.getScheduleList();
                    }
                }).catch(err => {
                    this.$message.error({message: err, duration: 1500, showClose: true});
                });

            },
            // 老师删除原来的日程
            deleteTeacherSchedule(scheduleId){
                var form = {
                    scheduleId: scheduleId,
                    stakeHolderIds: []
                };
                //TODO:应该调用老师的删除日程接口。调用有问题，也不发送信息，所以先用普通的
                this.$axios.post('/schedule/delete?scheduleId=' + scheduleId).then(res => {
                    if(res.status === 200){
                        this.$message.success({message: "删除成功！", duration: 1500, showClose: true});
                        this.getScheduleList();
                    }
                }).catch(err => {
                    this.$message.error({message: err, duration: 1500, showClose: true});
                });
            },
            //展示复制日程的对话框
            showCopyScheduleDialog(scheduleId){
                this.generateStudentList();
                //for循环获取特定scheduleId的日程信息
                this.copyForm.creatorId = this.id;
                this.copyForm.constructor = this.username;
                this.copyForm.scheduleId = scheduleId;
                for(let i = 0;i < this.tableData.length;i++){
                    if(this.tableData[i].scheduleId === scheduleId){
                        this.copyForm.content = this.tableData[i].content;
                        this.copyForm.state = this.tableData[i].state;
                    }
                }
                this.copyDialogVisible = true;
            },
            // 点击确定按钮: 复制日程
            copySchedule(){
                // 老师复制，新建日程并删除原日程
                if(this.userType === 'TEACHER'){
                    let copy = {};
                    copy.creatorId = this.copyForm.creatorId;
                    copy.content = this.copyForm.content;
                    copy.date = this.copyForm.date;
                    copy.stakeHolderIds = [];
                    if(copy.date !== ""){
                        this.$axios.post('/schedule/createForTeacher',copy).then(res => {
                            if(res.data !== ""){
                                this.$message.error({message: res.data.errorMsg, duration:1500, showClose:true});
                            }else{
                                this.deleteTeacherSchedule(this.copyForm.scheduleId);
                                this.getScheduleList();
                                this.copyDialogVisible = false;
                                this.$message.success({message: "复制成功！", duration:1500, showClose:true});
                            }
                        }).catch(err => {
                            this.$message.error({message:err, duration:1500,showClose:true});
                        })
                    }else{
                        this.$message.warning({message: "日期不能为空！", duration: 1500, showClose: true});
                    }
                }else{
                    // 学生复制，新建日程并删除原日程
                    let copy = {};
                    copy.content = this.copyForm.content;
                    copy.date = this.copyForm.date;
                    if(copy.date !== ""){
                        this.$axios.post('/schedule/create?userId=' + this.id, copy).then(res => {
                            if(res.data === ""){
                                this.deleteSchedule(this.copyForm.scheduleId);
                                this.getScheduleList();
                                this.copyDialogVisible = false;
                                this.$message.success({message: '添加成功', duration: 1500, showClose: true});
                            }else{
                                this.$message.error({message: res.data.errorMsg, duration: 1500, showClose: true});
                            }
                        },err => {
                            this.$message.error({message: err, duration: 1500, showClose: true});
                        })
                    }else{
                        this.$message.warning({message: "日期不能为空！", duration: 1500, showClose: true});
                    }
                }
            },
            //取消复制
            cancelCopy(){
                this.copyDialogVisible = false;
                this.studentList = [];
                this.targetList = [];
            },
            //学生列表数据生成
            generateStudentList(){
                this.$axios.get('/student/getAllUnderTheTeacher?teacherId=' + this.id).then(res=>{
                    let originList = res.data.data;
                    if(originList.length !== 0){
                        originList.forEach((student, index) =>{
                            this.studentList.push({
                                label: student.username,
                                key: student.userId
                            })
                        })
                    }
                })
            },
            //判断一个日程是否属于过去（时间上小于现在）
            //false: 未来
            //true: 过去
            testPastSchedule(s){
                let d = new Date();
                let dateList = s.split(' ')[0].split('');
                for(let i = 0;i < dateList.length;i++){
                    if(isNaN(dateList[i])){
                        dateList[i] = '-';
                    }
                }
                let newDate = dateList.join('').substring(0,dateList.length-1).split('-');
                if(newDate[0] > d.getFullYear()){
                    return false;
                }else if(newDate[0] < d.getFullYear()){
                    return true;
                } else if(newDate[0] == d.getFullYear() && newDate[1] > (d.getMonth()+1)){
                    return false;
                } else if(newDate[0] == d.getFullYear() && newDate[1] < (d.getMonth()+1)){
                    return true;
                } else if(newDate[0] == d.getFullYear() && newDate[1] == (d.getMonth()+1) && newDate[2] >= d.getDate()){
                    return false;
                } else{
                    return true;
                }
            },
            //判断一个日程是否是未完成
            //false:已完成
            //true: 未完成
            testScheduleUnfinished(s){
                if(s === "未完成"){
                    return true;
                }else{
                    return false;
                }
            }
        },
        //过滤器：当一行内容溢出时将超过50个字符的部分改成省略号
        filters: {
            ellipsis(value) {
                if (!value) return "";
                if (value.length > 50) {
                    return value.slice(0, 50) + "...";
                }
                return value;
            }
        }
    }
</script>

<style lang="less" scoped>
.state-finished{
    color: forestgreen;
}
.schedule-list{
    margin-top: 20px;
}
.schedule-date{
    color: #2880b8;
    font-size: 20px;
    font-weight: 400;
}
.schedule-past{
    color: #D3D3D3;
    font-size: 20px;
    font-weight: 400;
}
.schedule-content{
    margin-top: 15px;
    font-size:16px;
    font-weight: lighter;
}
.schedule-footer{
    margin-top: 15px;
    display: flex;
    flex-direction: row;
    justify-content: flex-start;
    align-items: center;
}
.schedule-state{
    width: 85%;
    font-size:14px;
}
.schedule-state-finished{
    width: 85%;
}
.schedule-state-unfinished{
    width: 85%;
}
.schedule-actions{
    width: 15%;
    display: flex;
    flex-direction: row;
    justify-content: flex-end;
}

</style>