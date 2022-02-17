<template>
    <el-container>
        <el-header>
            <div class="left-info">
                <el-page-header @back="goBack" content="PDF阅读"><span>快</span>
                </el-page-header>
            </div>
            <div class="right-info">
                <img class="user-avator" :src="portrait" >
                <el-dropdown trigger="click">
                    <span>{{username}}</span>
                    <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item class="clearfix" @click.native="checkInfos">
                                消息
                            <el-badge class="mark" :value="unread" />
                        </el-dropdown-item>
                        <el-dropdown-item @click.native="getUserDetail">用户信息</el-dropdown-item>
                        <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>
            </div>
        </el-header>
        <el-container class="pdf-viewer-container">
            <iframe
                    :src = pdfSrc
                    style="width: 100%;height: 100%;"
                    name = "pdfFrame"
                    id = "pdfFrame"
                    frameborder="0">
            </iframe>
        </el-container>
    </el-container>
</template>

<script>
export default {
    created(){
        this.id = window.sessionStorage.getItem("userId");
        this.username = window.sessionStorage.getItem("username");
        this.portrait = window.sessionStorage.getItem("portrait");
        this.pdflink = this.$route.query.pdflink;
        this.pdfId = this.$route.query.did;
        // http://119.29.53.191:8081
        // http://localhost:3000
        // http://42.193.37.120:9714
        this.pdfSrc = "http://42.193.37.120:9714/react-pdf-highlighter?"
                    + "url=" + this.pdflink
                    + "&did=" + this.pdfId
                    + "&userId=" + this.id
                    + "&username=" + this.username;
        this.storeUnreadInfoNum();
    },
    mounted() {
        //TODO:{问题：iframe高度不能自适应  状态：已解决}
        const oIframe = document.getElementById('pdfFrame');
        const deviceHeight = document.documentElement.clientHeight;
        oIframe.style.height = (Number(deviceHeight)-60) + 'px'; //数字是页面布局高度差
    },
    data(){
        return{
            pdflink: "http://119.29.53.191:7069/download-file-0.0.1/download/PDF_DOCUMENT/143ff2f1-b6f4-4bb8-a581-1233305ccab1_reading02-10.pdf",
            pdfSrc: "",
            pdfId: null,
            id: null,
            username: "",
            portrait: "",
            inputHome: '',
            unread: 0,
            iframeHeight: 1000

        }
    },
    methods:{
        //TODO：做一个返回上一页的跳转按钮
        goBack(){
            this.$router.push({
                path: '/searchPaper',
                query:{
                    searchStr: ''
                }
            })
        },
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
        //网页加载时保存未读消息数量至store中
        storeUnreadInfoNum(){
            this.$axios.get('/message/unreadMessageNum?userId=' + this.id).then(res => {
                this.unread = res.data.data;
                this.$store.commit('changeUnreadInfoNumInStore', this.unread);
            }).catch(err => {
                this.$message.error({message:err, duration: 1500, showClose: true});
            });
        }
    },
    watch:{
        '$store.state.unreadInfo':{
            handler(){
                this.unread = this.$store.getters.getUnreadInfoNumInStore;
            }
        }
    }

}
</script>

<style scoped lang="less">
.el-header {
    /*position: fixed;*/
    /*top: 0;*/
    /*right: 0;*/
    /*bottom: 0;*/
    /*left: 0;*/
    width: 100%;
    height: 60px;
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

    /*在less或sass这样的预处理器中，无法处理>>>这样的深度作用选择器，可以将>>>换成/deep/作用一样*/
    /*表示希望父组件的样式可以影响得“更深”*/
    .el-page-header /deep/ .el-page-header__content{
        color: #eeeeee;

    }


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
</style>
