<template>
    <div>
        <!--    面包屑导航区域-->
        <el-breadcrumb separator-class="el-icon-arrow-right">
            <el-breadcrumb-item :to="{path:'/welcome'}">首页</el-breadcrumb-item>
            <el-breadcrumb-item>文献管理</el-breadcrumb-item>
            <el-breadcrumb-item>搜索文献</el-breadcrumb-item>
            <el-link v-if="userType === 'STUDENT'" icon="el-icon-edit" style="margin-left: 5px; font-size:13px" @click="showEditDefaultDirectionDialog">修改默认研究方向</el-link>

        </el-breadcrumb>

        <!--  搜索框  -->
        <div class="search-paper-header">
            <el-select v-model="direction" @change="getPapersWithType"
                       placeholder="请选择研究方向" style="width: 120px; margin-right: 10px">
                   <el-option v-for="(item,index) in directionOptions" :key="index" :value="item.value" :label="item.label"></el-option>
            </el-select>
            <!--按照官方文档那样发现，最左边的选择器里面的字显示不出来，解决方法是在为el-select设置宽度，就可以正常显示了-->
            <el-input placeholder="请输入内容" v-model="input" class="search-paper-input"
                        @change="getPapersWithType">
                <el-select v-model="queryType" slot="prepend"
                           placeholder="请选择" style="width: 100px; ">
                       <el-option v-for="(item,index) in options" :key="index" :value="item.value" :label="item.label"></el-option>
                </el-select>
                <el-button slot="append" icon="el-icon-search" style="width: 100px;" @click="getPapersWithType()"></el-button>
            </el-input>
            <el-tooltip>
            </el-tooltip>

        </div>

        <!--论文列表-->
        <el-row :gutter="24" style="margin-top: 20px">
            <el-col :span="24" :offset="0" v-for="(item,index) in papers" :key="index" style="margin-bottom: 15px;">
                <el-card shadow="hover">
                    <div class="paper-title-box">
                        <el-link class="paper-title" @click="jumpToPaperDetail(item.did)">{{item.title}}</el-link>
                    </div>
                    <div class="paper-publisher">
                        <i class="el-icon-collection" style="padding-right: 5px;font-size: 20px"></i>
                        <i style="padding-right: 10px">{{item.publisher}}</i>
                        <i>{{item.publishdate}}</i>
                    </div>
                    <div class="paper-authors">
                        <ul class="clearfix">
                            <li v-for="(author, index) in item.authors" :key="index"><a >{{author}}</a></li>
                        </ul>
                    </div>
                    <div class="paper-keywords" >
                        <ul class="clearfix">
                            <li v-for="(keyword, index) in item.keywords" :key="index"><a ><i>{{keyword}}</i></a></li>
                        </ul>
                    </div>
                    <div class="paper-upload">
                        <div class="paper-upload-left">
                            <div class="upload-title"> <i class="el-icon-upload"></i></div>
                            <div class="upload-username"><a>{{item.username}}</a></div>
                            <div class="upload-date">{{item.date}}</div>
                        </div>
                        <div class="paper-upload-right">
                            <i class="el-icon-location-outline" :class="item.research_direction.length > 10 ? 'direction-color-red' : 'direction-color-blue'">
                                {{item.research_direction}}</i>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <!--分页栏区域-->
        <el-pagination
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :current-page="pageNum"
                :page-sizes="[5,10,20]"
                :page-size="pageSize"
                layout="total, sizes, prev, pager, next, jumper"
                :total="total"
                style="margin-top: 30px;">
        </el-pagination>

        <el-dialog title="编辑默认研究方向" :visible.sync="showEditDefaultDialog" width="50%" >
            <el-form label-width="100px">
                <el-form-item label="默认研究方向">
                    <el-select v-model="defaultDirection" @change="getPapersWithType"
                               placeholder="请选择研究方向" style="width: 120px; margin-right: 10px">
                           <el-option v-for="(item,index) in studentDirectionOptions" :key="index" :value="item.value" :label="item.label"></el-option>
                    </el-select>
                </el-form-item>
            </el-form>

            <span slot="footer">
                <el-button round type="primary" @click="showEditDefaultDialog = false">取 消</el-button>
                <el-button round @click="updateDefaultDialog">更 新</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    import {Loading} from 'element-ui';

    export default {
        name: "SearchPaper",
        created() {
            this.userType = window.sessionStorage.getItem("userType");
            this.$axios.get('/user/groupId?userId=' + window.sessionStorage.getItem("userId")).then(res => {
                if(res.data.data === null){
                    this.$message.warning({message: '请先加入研究组！', duration: 1500, showClose: true});
                    this.$router.push('/allGroups');
                }else{
                    this.groupId = res.data.data;
                    this.id = window.sessionStorage.getItem("userId");
                    this.input = this.$route.query.searchStr;

                    this.getGroupDirections();
                    this.getPapersWithType();
                }
            }).catch(err => {
                this.$message.error({message: err, duration: 1500, showClose: true});
            });
        },
        data(){
            return{
                id: null,
                userType: '',
                groupId: null,
                input: '1',
                // searchLoading: false,
                options:[
                    {value : 1, label: "标题"},
                    {value : 2, label: "关键词"},
                    {value : 3, label: "作者"},
                    {value : 4, label: "上传者"},
                ],
                queryType: 1,
                directionOptions: [
                    {value: "null", label: "所有"}
                ],
                direction: "null",
                total: 0,
                pageNum: 1,
                pageSize: 10,
                papers: [
                    // {
                    //     abstract: "We study the performance of ‘integral channel features’ for image classification tasks, focusing in particular on pedestrian detection. The general idea behind integral chan- nel features is that multiple registered image channels are computed using linear and non-linear transformations of the input image, and then features such as local sums, his- tograms, and Haar features and their various generalizations are efficiently computed using integral images. Such features have been used in recent literature for a variety of tasks – indeed, variations appear to have been invented independently multiple times. Although integral channel features have proven effective, little effort has been devoted to analyzing or optimizing the features themselves. In this work we present a unified view of the relevant work in this area and perform a detailed experimental evaluation. We demonstrate that when designed properly, integral channel features not only outperform other features including histogram of oriented gradient (HOG), they also (1) naturally integrate heterogeneous sources of information, (2) have few parameters and are insen- sitive to exact parameter settings, (3) allow for more accurate spatial localization during detection, and (4) result in fast detectors when coupled with cascade classifiers.",
                    //     date: "2021-03-21",
                    //     authors:["David","Tom","Li Chuanyi"],
                    //     keywords:["Deep learning", "Big Data", "Android"],
                    //     did: 8,
                    //     doi: null,
                    //     pdflink: "http://119.29.53.191:7069/download-file-0.0.1/download/PDF_DOCUMENT/143ff2f1-b6f4-4bb8-a581-1233305ccab1_reading02-10.pdf",
                    //     title: "deep learning tests",
                    //     userid: 5,
                    //     username: "zhangsan"
                    //     publishdate: null,
                    //     publisher: null,
                    // },
                ],
                showEditDefaultDialog: false,
                defaultDirection: '',
                studentDirectionOptions:[]
            }
        },
        methods:{
            getGroupDirections(){
                if(this.groupId !== null){
                    this.$axios.get('/researchGroup/directions?groupId=' + this.groupId).then(res => {
                        var list = res.data.data;
                        list.forEach(item => {
                            this.directionOptions.push({value: item, label: item});
                        });
                    }).catch(err => {
                        this.$message.error({message: err, duration: 1500, showClose: true});
                    });
                    if(window.sessionStorage.getItem('userType') === 'STUDENT'){
                        this.$axios.get('/student/defaultResearchDirection?studentId=' + this.id).then(res => {
                            if(res.data.data){
                                this.direction = res.data.data;
                            }
                        }).catch(err => {
                            this.$message.error({message: err, duration: 1500, showClose: true});
                        });
                    }
                }
            },
            //根据不同的搜索方式获得文献
            getPapersWithType(){
                // let loadingInstance = Loading.service({
                //     target: "el-row"
                // });
                this.$axios.post('/search/searchdocinfo?currentPage='+ (this.pageNum-1)
                                + '&keyword=' + this.input
                                + '&pageSize=' + this.pageSize
                                + '&type=' + this.queryType
                                + '&searcherid=' + this.id
                                + '&direction=' + this.direction
                ).then(res => {
                    if('errorCode' in res.data){
                        this.$message.error({message: res.data.errorMsg, duration: 1500, showClose: true});
                        // this.$nextTick(() => { // 以服务的方式调用的 Loading 需要异步关闭
                        //     loadingInstance.close();
                        // });
                    }else{
                        console.log(res.data.data);
                        this.papers = res.data.data;
                        this.total = res.data.total;
                        // this.$nextTick(() => { // 以服务的方式调用的 Loading 需要异步关闭
                        //     loadingInstance.close();
                        // })
                    }   
                }).catch(err => {
                    this.$message.error({message: err, duration: 1500, showClose: true});
                })
            },
            //监听 pageSize 值变化的事件
            handleSizeChange(newSize){
                this.pageSize = newSize;
                this.getPapersWithType();
            },
            //监听 页码值 改变的事件
            handleCurrentChange(newPage){
                this.pageNum = newPage;
                this.getPapersWithType();
            },
            jumpToPaperDetail(id){
                this.$router.push({
                    path: '/paperDetail',
                    query: {
                        did: id
                    }
                })
            },
            showEditDefaultDirectionDialog(){
                let ops = new Array();
                this.directionOptions.forEach(item=>{
                    if(item.value !== 'null'){
                        ops.push(item);
                    }
                });
                this.studentDirectionOptions = ops;

                this.$axios.get('/student/defaultResearchDirection?studentId=' + this.id).then(res => {
                    this.defaultDirection = res.data.data;
                }).catch(err => {
                    this.$message.error({message: err, duration: 1500, showClose: true});
                });
                this.showEditDefaultDialog = true;
            },
            updateDefaultDialog(){
                this.$axios.put('/student/defaultResearchDirection?newDirection=' + this.defaultDirection + '&studentId=' + this.id).then(res => {
                    if(res.data.data === 'success'){
                        this.showEditDefaultDialog = false;
                        this.$message.success({message: "修改成功！", duration: 1500, showClose: true});
                        this.direction = this.defaultDirection;
                    }
                }).catch(err => {
                    this.$message.error({message: err, duration: 1500, showClose: true});
                })
            }
        },
        //监听路由参数变化：非常有用！！！
        watch: {
            '$route' (to , from){
                if(to.path === from.path && to.query.searchStr !== from.query.searchStr){
                    this.input = to.query.searchStr;
                    this.getPapersWithType();
                }
                //  to , from 分别表示从哪跳转到哪，都是一个对象
                // to.path   ( 表示的是要跳转到的路由的地址 eg:  /home );
                // to.query.id 提取id进行http请求数据更新页面
            }
        }
    }
</script>

<style lang="less" scoped>
.search-paper-header{
    margin-top: 20px;
}
.search-paper-input{
    width: 75%;
}
.paper-title{
    font-size: 27px;
    font-family: tahoma,arial,Hiragino Sans GB,\5b8b\4f53,sans-serif;
    color: #161616;
}
.paper-publisher{
    font-size: 16px;
    margin-top: 10px;
    color: #666
}
.el-link.el-link--default {
    color: #161616;
}
.paper-keywords{
    margin-top: 5px;
    margin-bottom: 5px;

    ul{
        li{
            float: left;
            margin-right: 5px;
            a{
                color: #8c939d;
                font-size: 14px;
                i{
                    font-weight: 100;
                }
            }
            a:after{
                content: ','
            }
            a:hover{
                text-decoration: underline;
                text-underline: #83a7cf;
            }
        }
        li:last-child{
            a:after{
                content:''
            }
        }
    }
}
.paper-authors{
    margin-top: 5px;;
    ul{
        li{
            float: left;
            margin-right: 5px;
            a{
                color: #83a7cf;
                font-size: 14px;
            }
            a:after{
                content: ','
            }
            a:hover{
                text-decoration: underline;
                text-underline: #83a7cf;
            }
        }
        li:last-child{
            a:after{
                content:''
            }
        }
    }
}
.paper-upload{
    width: 100%;
    margin-top: 5px;
    display: flex;
    flex-direction: row;
    justify-content: space-between;

    .paper-upload-left{

        .upload-title{
            height: 24px;
            margin-right: 15px;
            font-size: 24px;
            line-height: 24px;
            color: #777;
            float: left;
        }
        .upload-username{
            height: 24px;
            margin-right: 15px;
            font-size: 14px;
            line-height: 24px;
            color: #777;
            float: left;

            a{
                color: #83a7cf;
            }
            a:hover{
                text-decoration: underline;
                text-underline: #83a7cf;
            }
        }
        .upload-date {
            height: 24px;
            margin-right: 15px;
            font-size: 14px;
            line-height: 24px;
            color: #777;
            float: left;
        }
    }
    .paper-upload-right{
        .direction-color-red{
            font-size: 16px;
            font-weight: 300;
            color: #DC143C;
        }
        .direction-color-red:hover{
            text-decoration: underline;
            text-underline: #DC143C;
        }
        .direction-color-blue{
            font-size: 16px;
            font-weight: 300;
            color: #83a7cf;
        }
        .direction-color-blue:hover{
            text-decoration: underline;
            text-underline: #83a7cf;
        }
    }
}

</style>