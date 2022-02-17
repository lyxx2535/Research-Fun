<template>
    <div>
        <div class="" style="margin-top: 15px">

            <el-row :gutter="24">
                <el-col :span="24" :offset="0" v-for="(item,index) in allInfos" :key="index" style="margin-bottom: 15px;">

                    <el-card shadow="hover" >
                        <div class="info-content" v-if="(item.body).indexOf('{sender}') !== -1">
                            <div>{{item.body.split('{sender}')[0].replace(/用户/,'')}}</div>
                            <div class="highlight-sender">{{item.senderInfo.username}}</div>
                            <div>{{item.body.split('{sender}')[1]}}</div>
                        </div>
                        <div class="info-content" v-else>{{item.body}}</div>
                        <div class="info-time"><i>{{item.date}}</i></div>
                        <div class="info-footer">
                                <i class="el-icon-circle-check" style="width: 90%; font-size:28px; color: green" v-if="item.state !== 'UNREAD'"></i>
                                <i class="el-icon-zoom-in" style="width: 90%; font-size: 28px; color: red;" v-if="item.state === 'UNREAD'"></i>

                                <div class="read-btns">
                                <!--阅读消息按钮-->
                                <el-tooltip v-if="item.type !== 'APPLY_TO_JOIN_GROUP' && item.type !== 'APPLY_TO_EXIT_GROUP'" effect="light" content="确认消息" placement="top" :enterable=false>
                                    <el-button class="btn-finish-hover-green" v-if="item.state === 'UNREAD'"
                                               circle icon="el-icon-check" @click="readOneInfo(item.id,item.state)">
                                    </el-button>
                                </el-tooltip>
                                <!--同意加入小组按钮-->
                                <el-tooltip v-if="item.type === 'APPLY_TO_JOIN_GROUP'" effect="light" content="同意加入研究组" placement="top" :enterable=false>
                                    <el-button type="" icon="el-icon-plus" class="btn-finish-hover-green"
                                               size="" circle  @click="agreeJoinGroup(item.senderInfo.userId, item.id)"></el-button>
                                </el-tooltip>
                                <!--不同意加入小组按钮-->
                                <el-tooltip v-if="item.type === 'APPLY_TO_JOIN_GROUP'" effect="light" content="拒绝加入研究组" placement="top" :enterable=false>
                                    <el-button type="" icon="el-icon-close" class="btn-delete-hover-red"
                                               size="" circle @click="refuseJoinGroup(item.senderInfo.userId, item.id)"></el-button>
                                </el-tooltip>
                                <!--同意退出小组按钮-->
                                <el-tooltip v-if="item.type === 'APPLY_TO_EXIT_GROUP'" effect="light" content="同意退出研究组" placement="top" :enterable=false>
                                    <el-button type="" icon="el-icon-minus" class="btn-finish-hover-green"
                                               size="" circle  @click="agreeGetOutGroup(item.senderInfo.userId, item.id)"></el-button>
                                </el-tooltip>
                                <!--不同意退出小组按钮-->
                                <el-tooltip v-if="item.type === 'APPLY_TO_EXIT_GROUP'" effect="light" content="拒绝退出研究组" placement="top" :enterable=false>
                                    <el-button type="" icon="el-icon-close" class="btn-delete-hover-red"
                                               size="" circle @click="refuseGetOutGroup(item.senderInfo.userId, item.id)"></el-button>
                                </el-tooltip>
                                <!--查看日程按钮-->
                                <el-tooltip v-if="item.type === 'SCHEDULE_RELATIVE'" effect="light" content="查看日程" placement="top" :enterable=false>
                                    <el-button circle icon="el-icon-zoom-in" @click="checkOneSchedule(item.extraInfoId)">
                                    </el-button>
                                </el-tooltip>
                                <!--删除消息按钮-->
                                <el-tooltip  v-if="item.type !== 'APPLY_TO_JOIN_GROUP' && item.type !== 'APPLY_TO_EXIT_GROUP'" effect="light" content="删除消息" placement="top" :enterable=false>
                                    <el-button class="btn-delete-hover-red" circle
                                               icon="el-icon-delete" @click="deleteOneInfo(item.id)">
                                    </el-button>
                                </el-tooltip>

                            </div>
                        </div>
                    </el-card>
                </el-col>
            </el-row>

            <!-- 分页栏区域 -->
            <el-pagination
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                    :current-page="pageNum"
                    :page-sizes="[5,10,20]"
                    :page-size="pageSize"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="total">
            </el-pagination>

            <!--查看额外日程的对话框 -->
            <el-dialog title="修改日程" :visible.sync="extraScheduleDialogVisible" width="50%" >
                <el-form ref="extraScheduleInfoRef" :model="extraScheduleInfo" label-width="100px">
                    <el-form-item label="日期" label-width="60px">
                        <span >{{extraScheduleInfo.date}}</span>
                    </el-form-item>
                    <!-- 只加上小红色星星只需要  :required="true"-->
                    <el-form-item label="状态" label-width="60px">
                        <span>{{extraScheduleInfo.state}}</span>
                    </el-form-item>
                    <el-form-item label="内容" label-width="60px">
                        <el-input :rows="5" type="textarea" v-model="extraScheduleInfo.content" maxlength="300" show-word-limit ></el-input>
                    </el-form-item>
                </el-form>

                <span slot="footer" class="dialog-footer">
                <el-button round @click="extraScheduleDialogVisible = false">确 定</el-button>
            </span>
            </el-dialog>

        </div>
    </div>
</template>

<script>
    export default {
        created() {
            this.id = window.sessionStorage.getItem("userId");
            this.getAllInfos();
        },
        data() {
            return {
                id: null,
                total: 0,
                pageNum: 1,
                pageSize: 10,
                allInfos: [
                    // {
                    //     body: "用户{sender}加入了研究组",
                    //     date: "2021年3月9日 星期二 下午08时17分00秒",
                    //     id: 183,
                    //     senderInfo: {userId: 10, username: "学生2", trueName: null},
                    //     state: "UNREAD"
                    // },
                ],
                unreadInfoNum: 0,
                scheduleIdSendByInfo: null,
                extraScheduleInfo:{
                    // "content": "string",
                    // "date": "string",
                    // "scheduleId": 0,
                    // "state": "UNFINISHED"
                },
                extraScheduleDialogVisible: false
            }
        },
        methods: {
            //获取自己的所有信息
            getAllInfos() {
                this.$axios.get('/message?currentPage='+  (this.pageNum-1) +
                    '&pageSize=' + this.pageSize +'&userId=' + this.id).then(res => {
                    this.allInfos = res.data.data;
                    this.total = res.data.total;
                    this.getUnreadInfoNum();
                }).catch(err => {
                    this.$message.error({message: err, duration: 1500, showClose: true});
                });
            },
            //将某个消息标记为已读
            readOneInfo(id,s) {
                if(s === "UNREAD"){
                    this.$axios.post('/message/markRead?messageId=' + id).then(res => {
                        if (res.data.data === "success") {
                            this.getAllInfos();
                            this.getUnreadInfoNum();
                            this.$message.success({message: "成功阅读该消息！", duration: 1500, showClose: true});
                        } else {
                            this.$message.error({message: "请重新阅读！", duration: 1500, showClose: true});
                        }
                    }).catch(err => {
                        this.$message.error({message: err, duration: 1500, showClose: true});
                    });
                }else{
                    this.$message.info({message:"此消息已经被阅读过！", duration:1500,showClose:true});
                }
            },
            //删除某个消息
            deleteOneInfo(id) {
                this.$axios.post('/message/delete?messageId=' + id).then(res => {
                    if(res.data.data === "success"){
                        this.getAllInfos();
                        this.$message.success({message: "删除成功！", duration: 1500, showClose: true});
                    }else{
                        this.$message.error({message: "删除失败！", duration: 1500, showClose: true});
                    }
                }).catch(err => {
                    this.$message.error({message: err, duration: 1500, showClose: true});
                })
            },

            // TODO:同意加入研究组!!创建者不一定是自己
            agreeJoinGroup(applierId,infoId){
                this.$axios.post('/user/approveToJoinGroup?messageId=' + infoId).then(res => {
                    this.$message.success({message: "该学生已经成功加入你的研究组！", duration: 1500, showClose: true});
                    this.getAllInfos();
                }).catch(err => {
                   this.$message.error({message: err, duration: 1500, showClose: true});
                });
            },

            // TODO:拒绝加入研究组!!创建者不一定是自己
            refuseJoinGroup(applierId,infoId){
                this.$axios.post('/user/refuseToJoinGroup?applier=' + applierId + '&groupCreator=' + this.id).then(res => {
                    this.$message.success({message: "已拒绝该学生加入你的研究组！", duration: 1500, showClose: true});
                    this.getAllInfos();
                }).catch(err => {
                   this.$message.error({message: err, duration: 1500, showClose: true});
                })
            },
            // 同意退出研究组
            agreeGetOutGroup(applierId, infoId){
                this.$axios.post('/user/approveToExitGroup?messageId=' + infoId).then(res => {
                    if(res.data.data === 'success'){
                        this.$message.success({message: "该学生已经退出你的研究组！", duration: 1500, showClose: true});
                    }else{
                        this.$message.warning({message: res.data.errorMsg, duration: 1500, showClose: true});
                    }
                    this.getAllInfos();
                }).catch(err => {
                    this.$message.error({message: err, duration: 1500, showClose: true});
                })
            },
            // 拒绝退出研究组
            refuseGetOutGroup(applierId, infoId){
                this.$axios.post('/user/refuseToExitGroup?messageId=' + infoId).then(res => {
                    if(res.data.data === 'success'){
                        this.$message.success({message: "已拒绝！该学生依然在你的研究组中", duration: 2000, showClose: true});
                    }else{
                        this.$message.warning({message: res.data.errorMsg, duration: 2000, showClose: true});
                    }
                    this.getAllInfos();
                }).catch(err => {
                    this.$message.error({message: err, duration: 2000, showClose: true});
                })
            },
            // 查看发送消息中的日程信息
            checkOneSchedule(scheduleId){
                this.$axios.get('/schedule?scheduleId=' + scheduleId).then(res => {
                    if('errorCode' in res.data){
                        this.$message.error({message: res.data.errorMsg, duration: 1500, showClose: true});
                    }else{
                        let obj = res.data.data;
                        obj.state = this.handleScheduleStateString(obj.state);
                        this.extraScheduleInfo = obj;
                        this.extraScheduleDialogVisible = true;
                    }
                }).catch(err => {
                    this.$message.error({message: err, duration: 2000, showClose: true});
                });
            },
            //监听 pageSize 值变化的事件
            handleSizeChange(newSize){
                this.pageSize = newSize;
                this.getAllInfos();
            },
            //监听 页码值 改变的事件
            handleCurrentChange(newPage){
                this.pageNum = newPage;
                this.getAllInfos();
            },
            // 获得未读消息数量
            getUnreadInfoNum(){
                this.$axios.get('/message/unreadMessageNum?userId=' + this.id).then(res => {
                    this.unreadInfoNum = res.data.data;
                }).catch(err => {
                    this.$message.error({message: err, duration: 1500, showClose: true});
                });
            },
            handleScheduleStateString(s){
                if(s === "DELETED"){
                    return "已删除";
                }else if(s === "UNFINISHED"){
                    return "未完成";
                }else{
                    return "已完成";
                }
            }
        },
        watch:{
            //监听消息数量的变化
            unreadInfoNum: {
                handler(){
                    this.$store.commit('changeUnreadInfoNumInStore', this.unreadInfoNum);
                }
            }

        }
    }
</script>

<style scoped lang="less">
    .info-state-unread{
        color: darkred;
        width: 90%;
        font-size: 14px;
    }
    .info-state-read{
        color: darkgreen;
        width: 90%;
        font-size: 14px;
    }

    .info-content{
        color: #1E1E1E;
        font-size: 20px;
        font-weight: 400;
        display: flex;
        flex-direction: row;
    }
    .info-time{
        margin-top: 15px;
        font-size:14px;
        font-weight: lighter;
    }
    .highlight-sender{
        /*color: #434B77;*/
        color: #2880b8;
        font:italic bold 20px/28px arial,sans-serif;
    }
    .info-footer{
        margin-top: 15px;
        display: flex;
        flex-direction: row;
        justify-content: flex-start;
        align-items: center;
    }
    .read-btns{
        width: 15%;
        margin-left: 10%;
        display: flex;
        flex-direction: row;
        justify-content: flex-end;


    }
</style>