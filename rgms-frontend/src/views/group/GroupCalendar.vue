<template>
    <div style="position: relative">
        <!--    面包屑导航区域-->
        <el-breadcrumb separator-class="el-icon-arrow-right">
            <el-breadcrumb-item :to="{path:'/welcome'}">首页</el-breadcrumb-item>
            <el-breadcrumb-item>研究组管理</el-breadcrumb-item>
            <el-breadcrumb-item>研究组日程表</el-breadcrumb-item>
        </el-breadcrumb>

        <el-select class="select_student" size="mini" @change="handleSelectorChange()"
                   v-model="value" placeholder="请选择">
            <el-option
                    v-for="item in options"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
                <span style="float: left">{{ item.label }}</span>
                <span style="float: right; color: #8492a6; font-size: 13px">{{ item.membership }}</span>
            </el-option>
        </el-select>

        <!-- 日历区域-->
        <el-calendar v-model="calendarVal" @change="listenMonthChange">
            <template
                    slot="dateCell"
                    slot-scope="{date, data}">
                <div style="position: relative; text-align: center;" @click="ifThisMonth(data.day)">
                    {{ data.day.split('-').slice(2).join('').replace(/\b(0+)/gi,"")}}
                    <div v-for="item in scheduleList" :key="item">
                        <!--去除上一个月下一个月多余的日期-->
                        <div v-if="data.day.split('-').slice(1)[0] === month ">
                            <div v-if="(item.dayOfMonth < 10 ? '0' + item.dayOfMonth : '' + item.dayOfMonth)
                            .indexOf(data.day.split('-').slice(2).join('')) !== -1" :class="'has-schedule'"
                                 @click="showOneDaySchedules(item.dayOfMonth)">
                                {{item.dayOfMonth}}
                                <p>有日程</p>
                            </div>
                        </div>
                    </div>
                </div>
            </template>
        </el-calendar>

        <!-- 查看有日程的一天的所有日程 -->
        <el-dialog :title="dateTitleStr" :visible.sync="showSchedulesDialog" width="50%" >
            <el-collapse  accordion v-if="value === 0">
                <div v-for="(item, index) in dateScheduleList" :key="index">
                    <el-collapse-item style="padding-bottom: 0" :title="item.userInfo.username">
                        <el-form :model="item" label-width="100px">
                            <el-form-item label="内容" label-width="60px">
                                <span style="color: #2b4b6b;">{{item.content}}</span>
                            </el-form-item>
                            <el-form-item label="状态" label-width="60px">
                                <p :class="item.state === 'UNFINISHED' ? 'schedule-unfinished' : 'schedule-finished'">
                                    {{item.state === "UNFINISHED" ? "未完成" : "已完成"}}</p>
                            </el-form-item>
                        </el-form>

                    </el-collapse-item>
                </div>
            </el-collapse>

            <el-form v-else :model="dateMemberSchedule" label-width="100px">
                <el-form-item label="内容" label-width="60px">
                    <span style="color: #2b4b6b;">{{dateMemberSchedule.content}}</span>
                </el-form-item>
                <el-form-item label="状态" label-width="60px">
                    <p :class="dateMemberSchedule.state === 'UNFINISHED' ? 'schedule-unfinished' : 'schedule-finished'">
                        {{dateMemberSchedule.state === "UNFINISHED" ? "未完成" : "已完成"}}</p>
                </el-form-item>
            </el-form>

            <span slot="footer" class="dialog-footer">
                <el-button type="primary" round @click="showSchedulesDialog = false">关 闭</el-button>
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
                    this.userId = window.sessionStorage.getItem("id");

                    this.options = [
                        {value: 0, label: "所有组员", membership:"All"},
                    ];
                    //已经加入研究组：获得今日所在月份的日程表
                    this.getGroupMemberList();
                    this.year = this.calendarVal.getFullYear();
                    this.month = this.handleTime(this.calendarVal.getMonth() + 1);
                    this.getScheduleListByMonth(this.calendarVal.getFullYear(),this.calendarVal.getMonth() + 1);
                }
            }).catch(err => {
                this.$message.error({message: err, duration: 1500, showClose: true});
            })
        },
        mounted(){
            this.$nextTick(() => {
                // 点击前一个月
                let prevBtn = document.querySelector(
                    ".el-calendar__button-group .el-button-group>button:nth-child(1)"
                );
                prevBtn.addEventListener("click", e => {
                    let d = this.calendarVal;
                    this.year = d.getFullYear();
                    this.month = this.handleTime(d.getMonth() + 1);
                    if(this.value === 0){
                        this.getScheduleListByMonth(d.getFullYear(),d.getMonth() + 1);
                    }else{
                        this.getMemberSchedules(d.getFullYear(),d.getMonth()+1, this.value);
                    }
                    let resDate = d.getFullYear() + '-' + this.handleTime((d.getMonth() + 1));
                })
            });

            this.$nextTick(() => {
                // 点击后一个月
                let prevBtn = document.querySelector(
                    ".el-calendar__button-group .el-button-group>button:nth-child(3)"
                );
                prevBtn.addEventListener("click", e => {
                    let d = this.calendarVal;
                    this.year = d.getFullYear();
                    this.month = this.handleTime(d.getMonth() + 1);
                    if(this.value === 0){
                        this.getScheduleListByMonth(d.getFullYear(),d.getMonth() + 1);
                    }else{
                        this.getMemberSchedules(d.getFullYear(),d.getMonth()+1,this.value)
                    }
                    let resDate = d.getFullYear() + '-' + this.handleTime((d.getMonth() + 1));
                })
            });

            this.$nextTick(() => {
                // 点击今天
                let prevBtn = document.querySelector(
                    ".el-calendar__button-group .el-button-group>button:nth-child(2)"
                );
                prevBtn.addEventListener("click", e => {
                    let d = this.calendarVal;
                    this.year = d.getFullYear();
                    this.month = this.handleTime(d.getMonth() + 1);
                    if(this.value === 0){
                        this.getScheduleListByMonth(d.getFullYear(),d.getMonth() + 1);
                    }else{
                        this.getMemberSchedules(d.getFullYear(),d.getMonth()+1,this.value)
                    }
                    let resDate = d.getFullYear() + '-' + this.handleTime((d.getMonth() + 1));
                })
            });

        },
        data(){
            return{
                userId: null,
                groupId: null,
                calendarVal: new Date(),
                scheduleList: [],
                year: null,
                month: null,//03
                showSchedulesDialog: false,
                dateTitleStr: "",
                dateScheduleList: [],
                dateMemberSchedule:{},
                options: [],
                value: 0
            }
        },
        methods:{
            getGroupMemberList(){
                this.$axios.get('/researchGroup/detailedInfo?groupId=' + this.groupId).then(res => {
                    var teachers = res.data.data.teacherInfos;
                    teachers.forEach(item => {
                        this.options.push({
                            value: item.userId,
                            label: item.username,
                            membership: 'Teacher'
                        })
                    });
                    var students = res.data.data.studentInfos;
                    students.forEach(item => {
                        this.options.push({
                            value: item.userId,
                            label: item.username,
                            membership: 'Student'
                        })
                    })
                }).catch(err => {
                    this.$message.error({message: err, duration:1500, showClose:true});
                })
            },
            //根据年份和月份获得该月该研究组的所有日程
            getScheduleListByMonth(year, month){
                this.$axios.get('/schedule/all/group?groupId='+ this.groupId +'&month='+ month +'&year=' + year).then(res => {
                    if('errorCode' in res.data){
                        this.$message.warning({message: res.data.errorMsg, duration:1500, showClose:true});
                    }else{
                        this.scheduleList = res.data.data;
                    }
                }).catch(err => {
                    this.$message.error({message: err, duration: 1500, showClose: true});
                })
            },
            // 根据年份和月份获得某个成员的某年某月全部日程
            getMemberSchedules(year, month, memberId){
               this.$axios.get('/schedule/all/user/month?month='+ month + '&userId=' + memberId + '&year=' + year).then(res => {
                   if('errorCode' in res.data){
                       this.$message.warning({message: res.data.errorMsg, duration:1500, showClose:true});
                   }else{
                       this.scheduleList = res.data.data;
                   }
               }).catch(err => {
                   this.$message.error({message: err, duration: 1500, showClose: true}); 
               });
            },
            // 展示某天的日程对话框
            showOneDaySchedules(d){
                this.dateTitleStr = this.year + '年' + this.month.replace(/\b(0+)/gi,"") + '月' + d + '日';
                if(this.value === 0){
                    let list = [];
                    for(let i = 0;i < this.scheduleList.length;i++){
                        if(this.scheduleList[i].dayOfMonth === d){
                            list.push(this.scheduleList[i]);
                        }
                    }
                    this.dateScheduleList = list;
                }else{
                    let li = {};
                    for(let i = 0;i < this.scheduleList.length;i++){
                        if(this.scheduleList[i].dayOfMonth === d){
                            li = this.scheduleList[i];
                            break;
                        }
                    }
                    this.dateMemberSchedule = li;
                }
                this.showSchedulesDialog = true;
            },
            // 处理下拉框值的变化
            handleSelectorChange(){
                if(this.value === 0){
                    this.getScheduleListByMonth(this.calendarVal.getFullYear(),this.calendarVal.getMonth() + 1);
                }else{
                    this.getMemberSchedules(this.calendarVal.getFullYear(),this.calendarVal.getMonth() + 1, this.value);
                }
            },
            ifThisMonth(data){
                let year = data.split('-')[0];
                let month = data.split('-')[1];
                if(year !== this.year || month !== this.month){
                    this.year = year;
                    this.month = month;
                    if(this.value === 0){
                        this.getScheduleListByMonth(year,this.handleTimeString(month));
                    }else{
                        this.getMemberSchedules(year,this.handleTimeString(month));
                    }
                }
            },
            // 处理时间：数字9=>字符串'09'
            handleTime(s) {
                return s < 10 ? '0' + s : '' + s
            },
            // 反向处理时间：字符串'09'=>数字9
            handleTimeString(s){
                return s.length === 2 ? s - '' : s.split('')[1];
            },
            listenMonthChange(){
                this.year = this.calendarVal.getFullYear();
                this.month = this.handleTime(this.calendarVal.getMonth() + 1);
                this.getScheduleListByMonth(this.calendarVal.getFullYear(),this.calendarVal.getMonth() + 1);
            },
        },
    }
</script>

<style lang="less" scoped>
.el-calendar-table:not(.is-range) td.next, .el-calendar-table:not(.is-range) td.prev{
    pointer-events: none;
}
.is-selected{
    background-color: #2b4b6b;
}
.has-schedule{
    /*#f2f8fe*/
    position:absolute;
    background-color: #87CEFA !important;
    width:100%;
    height: 100%;
    padding: 0;
    left: 0;
    top:0;

}
/**日期div的样式*/
.el-calendar-day{
    height: 100%;
    padding: 2px;
    text-align: center;
}
.el-calendar-table .el-calendar-day > div {
    height: 100%;
    padding:0;
}
.calendar-day-form{
    text-align: center;
}
.schedule-unfinished{
    margin-top: 0px;
    color: red;
}
.schedule-finished{
    margin-top: 0px;
    color: green;
}
.select_student{
    position: absolute;
    left: 140px;
    top: 40px;

}
</style>