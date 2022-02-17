<template>
<div>
    <!--    面包屑导航区域-->
    <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{path:'/welcome'}">首页</el-breadcrumb-item>
        <el-breadcrumb-item>文献管理</el-breadcrumb-item>
        <el-breadcrumb-item>我上传的文献</el-breadcrumb-item>
    </el-breadcrumb>

    <!--  搜索框 和 上传按钮      -->
    <div class="upload-header">
        <el-select v-model="direction" placeholder="请选择研究方向"
                   @change="getPapersUploadedByMyself"
                   style="width: 120px; margin-right: 10px">
               <el-option v-for="(item,index) in directionOptions" :key="index" :value="item.value" :label="item.label"></el-option>
        </el-select>
        <!--按照官方文档那样发现，最左边的选择器里面的字显示不出来，解决方法是在为el-select设置宽度，就可以正常显示了-->
        <el-input placeholder="请输入内容" @change="getPapersUploadedByMyself"
                  v-model="input" class="input-with-select">
            <el-select v-model="queryType" slot="prepend" placeholder="请选择" style="width: 100px;">
                   <el-option v-for="(item,index) in options" :key="index" :value="item.value" :label="item.label"></el-option>
            </el-select>
            <el-button slot="append" icon="el-icon-search" @click="getPapersUploadedByMyself" style="width: 80px;"></el-button>
        </el-input>

        <el-button round type="primary" icon="el-icon-upload" @click="showUploadDialog" style="margin-left: 30px">上传文献</el-button>

    </div>

    <!--论文列表-->
    <el-row :gutter="24" style="margin-top: 20px">
        <el-col :span="24" :offset="0" v-for="(item,index) in papers" :key="index" style="margin-bottom: 15px;">
            <el-card shadow="hover">
                <div class="paper-title-box">
                    <el-link class="paper-title" @click="jumpToPaperDetail(item.did)">{{item.title}}</el-link>
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

    <!--上传文献的对话框-->
    <el-dialog title="上传文献" :visible.sync="uploadPaperDialog" width="50%" @close="cancelUpload">
        <el-form v-loading="loading">
            <el-form-item>
                <el-upload
                        class="upload-demo"
                        ref="upload"
                        drag
                        action=""
                        accept="application/pdf"
                        :on-preview="handlePreview"
                        :on-remove="handleRemove"
                        :on-change="changeFile"
                        :file-list="fileList"
                        :auto-upload="false"
                        :limit="1"
                        show-file-list
                        multiple>
                    <i class="el-icon-upload"></i>
                    <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
                    <div class="el-upload__tip" slot="tip">注：一次只能上传一个pdf文献，且不能超过100MB</div>
                </el-upload>
            </el-form-item>
            <el-form-item class="upload-dialog-footer">
                <el-button round @click="cancelUpload">取 消</el-button>
                <el-button round type="primary" @click="uploadPaper">解 析</el-button>
            </el-form-item>
        </el-form>
    </el-dialog>
</div>
</template>

<script>
    export default {
        created() {
            this.$axios.get('/user/groupId?userId=' + window.sessionStorage.getItem("userId")).then(res => {
                if(res.data.data === null){
                    this.$message.warning({message: '请先加入研究组！', duration: 1500, showClose: true});
                    this.$router.push('/allGroups');
                }else{
                    this.groupId = res.data.data;
                    this.id = window.sessionStorage.getItem("userId");
                    this.getGroupDirections();
                    this.getPapersUploadedByMyself();
                }
            }).catch(err => {
                this.$message.error({message: err, duration: 1500, showClose: true});
            })
        },
        data(){
            return{
                id: null,
                groupId: null,
                uploadPaperDialog: false,
                fileList:[],
                select: "",
                pdfUploadFilepath: "",
                // 文献
                pdfFile:null,
                loading: false,
                input: "",
                options:[
                    {value : 1, label: "标题"},
                    {value : 2, label: "关键词"},
                    {value : 3, label: "作者"},
                    // {value : 4, label: "上传者"},
                ],
                queryType: 1,
                direction: "null",
                directionOptions:[
                    {value : "null", label: "所有"}
                ],
                total: 0,
                pageNum: 1,
                pageSize: 10,
                papers:[]
            }
        },
        methods: {
            getGroupDirections(){
                if(this.groupId !== null){
                    this.$axios.get('/researchGroup/directions?groupId=' + this.groupId).then(res => {
                        var list = res.data.data;
                        var options = [
                            {value : "null", label: "所有"}
                        ];
                        list.forEach(item => {
                            options.push({value: item, label: item});
                        });
                        this.directionOptions = options;
                    }).catch(err => {
                        this.$message.error({message: err, duration: 1500, showClose: true});
                    });
                    // 默认研究方向
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
            showUploadDialog(){
                this.uploadPaperDialog = true;
            },
            handlePreview(file){
            },
            //上传的文件移除后的钩子函数
            handleRemove(file, fileList){
            },
            // 取消上传，清空文件列表
            cancelUpload(){
                this.$refs.upload.clearFiles();
                this.uploadPaperDialog = false;
                this.pdfFile = null;
            },
            //点击上传按钮事件
            uploadPaper(){
                this.loading = true;
                this.$axios.post('/file/upload?fileType=PDF_DOCUMENT',this.pdfFile).then(res=>{
                    if('errorCode' in res.data){
                        this.$message.error({message: res.data.errorMsg, duration:1500, showClose:true});
                    }else{
                        this.pdfUploadFilepath = res.data.data;
                        this.loading = false;

                        this.$router.push({
                            path: '/executePaperAbstract',
                            query:{
                                file: encodeURIComponent(this.pdfUploadFilepath)
                            }
                        })
                    }
                }).catch(err => {
                    this.$message.error({message: err, duration: 1500, showClose: true});
                })
            },
            changeFile(file, fileList){
                let upload = new FormData();
                upload.append("file", file.raw);
                this.pdfFile = upload;
            },
            // 获得我上传的文献
            getPapersUploadedByMyself(){
                this.$axios.post('/search/searchdocbyuploader?'
                    + 'currentPage=' + (this.pageNum - 1)
                    + '&direction=' + this.direction
                    + '&keyword=' + this.input
                    + '&pageSize=' + this.pageSize
                    + '&searcherid=' + this.id
                    + '&type=' + this.queryType
                    + '&uploaderid=' + this.id).then(res => {
                    this.papers = res.data.data;
                    this.total = res.data.total;
                }).catch(err => {
                    this.$message.error({message: err, duration:1500, showClose:true});
                })
            },
            //监听 pageSize 值变化的事件
            handleSizeChange(newSize){
                this.pageSize = newSize;
                this.getPapersUploadedByMyself();
            },
            //监听 页码值 改变的事件
            handleCurrentChange(newPage){
                this.pageNum = newPage;
                this.getPapersUploadedByMyself();
            },
            jumpToPaperDetail(id){
                this.$router.push({
                    path: '/paperDetail',
                    query: {
                        did: id
                    }
                })
            }
        }
    }
</script>

<style scoped lang="less">
.upload-header{
    display: flex;
    justify-content: flex-start;
    margin-top: 20px;
}
.input-with-select{
    width: 60%;
    margin-right: 20px;

}
.input-with-select .el-input-group__prepend {
    background-color: #fff;
}
.upload-dialog-footer{
    display: flex;
    flex-direction: row;
    justify-content: flex-end;
}
.paper-title{
    font-size: 27px;
    font-family: tahoma,arial,Hiragino Sans GB,\5b8b\4f53,sans-serif;
    color: #161616;
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