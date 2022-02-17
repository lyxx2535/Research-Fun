<template>

    <el-container class="home-container">
        <el-header >
            <div class="left-info">
                <img src="../assets/sys-logo.png">
                <span>研究组学术过程管理系统</span>
                <el-input clearable="" placeholder="请输入内容" v-model="inputHome" size="small">
                    <el-button slot="append" icon="el-icon-search" size="mini" @click="searchFromHeader"></el-button>
                </el-input>
            </div>
            <div class="right-info">
                <img class="user-avator" :src="portrait">
                <el-dropdown trigger="click">
                    <span>{{username}}</span>
                    <el-dropdown-menu slot="dropdown" >
                        <el-dropdown-item class="clearfix" @click.native="checkInfos">
                                消息
                            <el-badge class="mark" :value="unreadInfoNumber" />
                        </el-dropdown-item>
                        <el-dropdown-item @click.native="getUserDetail">用户信息</el-dropdown-item>
                        <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>
            </div>
        </el-header>
        <el-container>
            <el-aside style="width: 250px">
                <el-menu router :default-openeds="['1','2','3']" :default-active="$router.path">
                    <el-submenu index="1">
                        <template slot="title"><i class="el-icon-user"></i>研究组管理</template>
                        <el-menu-item index="/myGroup" >我的研究组</el-menu-item>
                        <el-menu-item index="/groupCalendar"  >研究组日程表</el-menu-item>
                    </el-submenu>

                    <el-submenu index="2">
                        <template slot="title"><i class="el-icon-document-checked"></i>日程管理</template>
                        <el-menu-item index="/personalSchedule">我的日程</el-menu-item>
                        <el-menu-item index="/addSchedule" >创建日程</el-menu-item>
                    </el-submenu>

                    <el-submenu index="3">
                        <template slot="title"><i class="el-icon-notebook-2"></i>文献管理</template>
                        <el-menu-item index="/searchPaper?searchStr=" >搜索文献</el-menu-item>
                        <el-menu-item index="/paperUploaded"  >我上传的文献</el-menu-item>
                    </el-submenu>

                </el-menu>
            </el-aside>
            <el-main>
                <router-view> </router-view>
            </el-main>
        </el-container>
    </el-container>

</template>

<script>
    export default {
        created() {
            this.id = window.sessionStorage.getItem('userId');
            if(window.sessionStorage.getItem('userType') === "STUDENT"){
                this.$axios.get('/student/detailedInfo?studentId=' + this.id).then(res => {
                    this.username = res.data.data.username;
                    this.userType = res.data.data.userType;
                    this.portrait = res.data.data.portrait;
                    window.sessionStorage.setItem("username", this.username);
                    window.sessionStorage.setItem("portrait",res.data.data.portrait);
                    //单独存储研究组id
                    if(res.data.data.groupInfo === null){
                        window.sessionStorage.setItem("groupId" , null);
                    }else{
                        window.sessionStorage.setItem("groupId" , res.data.data.groupInfo.groupId);
                    }
                }).catch(err => {
                    this.$message.error({message: err, duration: 1500, showClose: true});
                })
            }else{
                this.axios.get('/teacher/detailedInfo?teacherId=' + this.id).then(res => {
                    this.username = res.data.data.username;
                    this.userType = res.data.data.userType;
                    this.portrait = res.data.data.portrait;
                    window.sessionStorage.setItem("username",this.username);
                    window.sessionStorage.setItem("portrait",res.data.data.portrait);
                    //单独存储研究组id
                    if(res.data.data.groupInfo === null){
                        window.sessionStorage.setItem("groupId" , null);
                    }else{
                        window.sessionStorage.setItem("groupId" , res.data.data.groupInfo.groupId);
                    }
                }).catch(err => {
                    this.$message.error({message: err, duration: 1500, showClose: true});
                })
            }
            this.storeUnreadInfoNum();
        },
        data() {
            return {
                id: null,
                username: '',
                userType: '',
                portrait: '',
                inputHome: "",
                unreadInfoNumber: 0,
                groupId: null,
            }
        },
        methods:{
            logout(){
                window.sessionStorage.clear();
                this.$router.push('login');
            },
            //查看我的消息
            checkInfos(){
                this.$router.push('/infos');
            },
            //查看用户详细信息
            getUserDetail(){
                this.$router.push('/userPage');
            },
            //通过Header栏的小搜索框进入搜索文献页面
            searchFromHeader(){
                if(window.sessionStorage.getItem('groupId') !== 'null'){
                    let ipt = this.inputHome;
                    this.inputHome = '';
                    this.$router.push({
                        path: '/searchPaper',
                        query:{
                            searchStr: ipt,
                        }
                    })
                }else{
                    this.$message.warning({message:'请先加入研究组！', duration: 1500, showClose: true});
                    this.inputHome = '';
                }
            },
            //网页加载时保存未读消息数量至store中
            storeUnreadInfoNum(){
                this.$axios.get('/message/unreadMessageNum?userId=' + this.id).then(res => {
                    this.unreadInfoNumber = res.data.data;
                    this.$store.commit('changeUnreadInfoNumInStore', this.unreadInfoNumber);
                }).catch(err => {
                    this.$message.error({message:err, duration: 1500, showClose: true});
                });
            },
        },
        watch:{
            '$store.state.unreadInfo':{
                handler(){
                    this.unreadInfoNumber = this.$store.getters.getUnreadInfoNumInStore;
                }
            }
        }
    };
</script>

<style lang="less" scoped>
.user-avator{
    height: 50px;
    width: 50px;
    border: 1px solid #eee;
    border-radius: 50%;
    box-shadow: 0 0 10px #ddd;
    background-color: #fff;
}
.el-header {
    //深蓝色：主题色
    background-color: #2b4b6b;
    display: flex;
    justify-content: space-between;
    padding-left: 0;
    align-items: center;
    color: #ffffff;
    font-size: 20px;
}
.left-info{
    height: 100%;
    margin-left: 15px;
    text-align: left;
    font-size: 22px;
    font-weight: lighter;
    display: flex;
    justify-content: space-between;
    align-items: center;

    img{
        height: 50px;
        width: 50px;
        border: 1px solid #eee;
        border-radius: 50%;
        box-shadow: 0 0 10px #ddd;
        background-color: #fff;
    }
    span{
        margin-left: 10px;
        width: 300px
    }
    .el-input{
        width: 250px;
        height: 30px;
    }
}
.right-info{
    height: 100%;
    margin-right: 15px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    img{
        height: 50px;
        width: 50px;
        border: 1px solid #eee;
        border-radius: 50%;
        box-shadow: 0 0 10px #ddd;
        background-color: #fff;
    }

    span{
        margin-left: 10px;
        color: #ffffff;
        font-weight: lighter;
    }
}

.el-aside {
    background-color: #E9EEF3;
    width: 150px;
    .el-menu{
        background-color: #E9EEF3;
    }
}
.el-main{
    background-color: #ffffff;
}
.home-container{
    height: 100%;
}
</style>
