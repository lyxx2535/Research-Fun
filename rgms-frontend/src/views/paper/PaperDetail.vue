<template>
    <div style="min-height: 1000px">
        <el-page-header @back="goBackToLastPage" content="文献详情"></el-page-header>
        <el-divider></el-divider>
        <div class="paper-detail-container">
            <div class="paper-detail-title"><span>{{paper.title}}</span></div>
            <div class="paper-detail-authors">
                <ul class="clearfix">
                    <li v-for="(author, index) in paper.authors" :key="index">
                        <a >{{author}}</a>
                    </li>
                </ul>
            </div>
            <div class="paper-detail-publish">
                <i class="el-icon-collection" style="padding-right: 5px;font-size: 20px"></i>
                <i style="padding-right: 10px">{{paper.publisher}}</i>
                <i>{{paper.publishdate}}</i>
            </div>
            <div class="paper-detail-abstract">{{paper.abstract}}</div>
            <div class="paper-detail-keywords">
                <div class="keyword-title">Keywords：</div>
                <div class="keyword-content">
                    <ul class="clearfix">
                        <li v-for="(keyword, index) in paper.keywords" :key="index">
                            <a>{{keyword}}</a>
                        </li>
                    </ul>
                </div>
            </div>
            <div class="paper-detail-upload">
                <div class="upload-title"> <i class="el-icon-upload"></i></div>
                <div class="upload-username"><a>{{paper.username}}</a></div>
                <div class="upload-date">{{paper.date}}</div>
            </div>
            <div class="paper-detail-btns">
                <el-button icon="el-icon-reading" type="read" @click="showPdfViewer">PDF阅读</el-button>
                <el-button v-if="id == paper.userid" icon="el-icon-edit" type="edit" @click="editPdf">编辑文献</el-button>
<!--                <el-button v-if="id == paper.userid" icon="el-icon-delete" type="delete" @click="deletePdf">删除文献</el-button>-->
            </div>
        </div>

        <el-dialog title="修改文献" :visible.sync="editPaperDialog" width="50%">
            <el-form ref="" :model="editPaper" :rules="editPaperRules" >
                <el-form-item label="标题" label-width="80px" prop="title">
                    <el-input v-model="editPaper.title" type="textarea" :rows="3"></el-input>
                </el-form-item>
                <el-form-item label="作者" prop="authors" label-width="80px">
                    <el-card>
                        <el-tag
                                v-for="tag in editPaper.authors"
                                :key="tag"
                                closable
                                :disable-transitions="false"
                                @close="handleCloseAuthors(tag)">
                            {{tag}}
                        </el-tag>
                        <el-input
                                class="input-new-tag"
                                v-if="inputAuthorVisible"
                                v-model="inputAuthorValue"
                                ref="saveAuthorTagInput"
                                size="small"
                                @keyup.enter.native="handleInputAuthorConfirm"
                                @blur="handleInputAuthorConfirm"
                        >
                        </el-input>
                        <el-button v-else class="button-new-tag" size="small" @click="showAuthorInput">+ 作者</el-button>
                    </el-card>
                </el-form-item>

                <el-form-item label="关键词" prop="keywords" label-width="80px">
                    <el-card>
                        <el-tag
                                v-for="tag in editPaper.keywords"
                                :key="tag"
                                closable
                                :disable-transitions="false"
                                @close="handleCloseKeywords(tag)">
                            {{tag}}
                        </el-tag>
                        <el-input
                                class="input-new-tag"
                                v-if="inputKeywordVisible"
                                v-model="inputKeywordValue"
                                ref="saveKeywordTagInput"
                                size="small"
                                @keyup.enter.native="handleInputKeywordConfirm"
                                @blur="handleInputKeywordConfirm"
                        >
                        </el-input>
                        <el-button v-else class="button-new-tag" size="small" @click="showKeywordInput">+ 关键词</el-button>
                    </el-card>
                </el-form-item>
                <el-form-item label="研究方向" prop="research_direction" label-width="80px">
                    <el-select v-model="editPaper.research_direction">
                        <el-option
                                v-for="(item,index) in groupDirectionOptions"
                                :key="index"
                                :label="item"
                                :value="item">
                        </el-option>
                    </el-select>
                </el-form-item>
                 <el-form-item label="发表刊物" label-width="80px" prop="publisher">
                    <el-input v-model="editPaper.publisher" ></el-input>
                </el-form-item>
                 <el-form-item label="发表日期" label-width="80px" prop="publishdate">
                    <el-date-picker 
                        type="date" 
                        placeholder="选择日期" 
                        v-model="editPaper.publishdate" 
                        style="width: 50%;"
                        value-format="yyyy-MM-dd"
                        format="yyyy-MM-dd">
                    </el-date-picker>
                </el-form-item>
                <el-form-item label="摘要" label-width="80px" prop="abstract">
                    <el-input v-model="editPaper.abstract" type="textarea" :rows="5"></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button round @click="editPaperDialog = false">取 消</el-button>
                <el-button round type="primary" @click="updateNewPaper">更 新</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "PaperDetail",
        created() {
            this.id = window.sessionStorage.getItem("userId");
            this.did = this.$route.query.did;
            this.groupId = window.sessionStorage.getItem('groupId');
            this.getGroupResearchDirections();
            this.getPaperDetail();
        },
        data(){
            return{
                did: null,
                id: null,
                groupId: null,
                // 文献详情的关键词列表有问题
                paper: {},
                editPaperDialog: false,
                editPaper:{
                    // "abstract": "string",
                    // "authors": ["Sam", "Kate"],
                    // "date": "2021-01-01",
                    // "did": 1,
                    // "doi": 1,
                    // publishdate: null,
                    // publisher: null,
                    // "groupid": 0,
                    // "keywords": ["deep learning", "Kate"],
                    // "pdflink": "http://119.29.53.191:7069/download-file-0.0.1/download?fileType=PDF_DOCUMENT&filePath=/usr/local/rgms-resources/document/5299eb18-a0e7-440e-aa3e-26331937af81_test.pdf",
                    // "research_direction": "face dection",
                    // "title": "deep learning tests",
                    // "userid": 5,
                    // "username": "李明"
                },
                editPaperRules:{
                    title: [
                        { required: true, message: '标题不能为空', trigger: 'blur' },
                    ],
                    authors:[
                        { required: true, message: '作者列表不能为空', trigger: 'blur' },
                    ],
                    keywords:[
                        { required: true, message: '关键词列表不能为空', trigger: 'blur' },
                    ],
                    abstract: [
                        { required: true, message: '摘要不能为空', trigger: 'blur' },
                    ],
                    research_direction: [
                        { required: true, message: '研究方向不能为空', trigger: 'blur' },
                    ],
                    publisher: [
                        { required: true, message: '发表杂志名称不能为空', trigger: 'blur' },
                    ],
                    publishdate: [
                        { required: true, message: '发表时间不能为空', trigger: 'blur' },
                    ],
                },
                groupDirectionOptions:[],
                inputAuthorVisible: false,
                inputAuthorValue: "",
                inputKeywordVisible: false,
                inputKeywordValue: "",
            }
        },
        methods:{
            // 获得用户所在小组的研究方向
            getGroupResearchDirections(){
                this.$axios.get('/researchGroup/directions?groupId=' + this.groupId).then(res=>{
                    this.groupDirectionOptions = res.data.data;
                }).catch(err=>err);
            },
            // 获得文献详细信息
            getPaperDetail(){
                this.$axios.post('/search/searchcompletedoc?did=' + this.did).then(res=>{
                    this.paper = res.data.data;
                    console.log(this.paper);
                }).catch(err=>err);
            },
            // 跳转到pdf阅读页面
            showPdfViewer(){
                this.$router.push({
                    path: '/paperViewer',
                    query:{
                        pdflink: this.paper.pdflink,
                        did: this.paper.did
                    }
                });
            },
            //页头返回上一页
            goBackToLastPage(){
                this.$router.go(-1);
            },
            // 点击编辑文献信息按钮
            editPdf(){
                // 深拷贝 偷懒方法
                let doc = JSON.stringify(this.paper);
                this.editPaper = JSON.parse(doc);
                this.editPaperDialog = true;
            },
            // 上传更新
            updateNewPaper(){
                // console.log(this.editPaper);
                this.$axios.put('/file/modifydocumentinfo', this.editPaper).then(res => {
                    if(res.data.data === 'Modify Success!'){
                        this.getPaperDetail();
                        this.$message.success({message: '修改成功！', duration: 1500, showClose:true});
                    }else{
                        this.$message.error({message: '修改失败！', duration: 1500, showClose: true});
                    }
                }).catch(err => {
                    this.$message.error({message: err, duration: 1500, showClose: true});
                });
                this.editPaperDialog = false;
            },
            // 删除文献
            deletePdf(){

            },
            // start 编辑作者信息
            handleCloseAuthors(tag) {
                this.editPaper.authors.splice(this.editPaper.authors.indexOf(tag), 1);
            },
            showAuthorInput() {
                this.inputAuthorVisible = true;
                this.$nextTick(_ => {
                    this.$refs.saveAuthorTagInput.$refs.input.focus();
                });
            },
            handleInputAuthorConfirm() {
                let inputValue = this.inputAuthorValue;
                if (inputValue) {
                    this.editPaper.authors.push(inputValue);
                }
                this.inputAuthorVisible = false;
                this.inputAuthorValue = '';
            },
            // end
            // start 编辑关键词信息
            handleCloseKeywords(tag) {
                this.editPaper.keywords.splice(this.editPaper.keywords.indexOf(tag), 1);
            },
            showKeywordInput() {
                this.inputKeywordVisible = true;
                this.$nextTick(_ => {
                    this.$refs.saveKeywordTagInput.$refs.input.focus();
                });
            },
            handleInputKeywordConfirm() {
                let inputValue = this.inputKeywordValue;
                if (inputValue) {
                    this.editPaper.keywords.push(inputValue);
                }
                this.inputKeywordVisible = false;
                this.inputKeywordValue = '';
            }
            // end


        }
    }
</script>

<style lang="less" scoped>
.paper-detail-container{
    margin-left: 20px;
    margin-top: 30px;
    display: flex;
    flex-direction: column;
}
.paper-detail-title {
    width: 90%;
    line-height: 50px;
    text-align: left;


    span{
        color: #161616;
        font-size: 50px;
        font-weight: 600;
        font-family: 'brandon',serif;

    }
}
.paper-detail-authors{
    width: 90%;
    margin-top: 20px;

    ul{
        li{
            float: left;
            margin-right: 5px;
            a{
                color: #83a7cf;
                font-size: 16px;
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
.paper-detail-abstract{
    width: 90%;
    margin: 20px 0;
    color: #a0a0a0;
    font-size: 14px;
    font-family: 'Hiragino Sans GB',sans-serif;
    font-weight: 100;
}
.paper-detail-keywords{
    width: 90%;
    margin-bottom: 20px;

    .keyword-title{
        width: 55px;
        height: 24px;
        margin-right: 15px;
        font-size: 14px;
        line-height: 24px;
        color: #777;
        float: left;
    }

    .keyword-content{
        padding-left: 3px;
        box-sizing: border-box;
        width: 80%;
        display: block;

        ul{
            li{
                min-width: 56px;
                margin: 0 3px 5px;
                padding: 0 3px;
                box-sizing: border-box;
                height: 24px;
                line-height: 24px;
                font-size: 14px;
                text-align: center;
                border: 1px solid #a0a0a0;
                border-radius: 2px;
                float: left;

                a{
                    color: #000;

                }
                a:hover{
                    text-decoration: underline;
                }
            }
        }
    }
}
.paper-detail-upload{
    width: 90%;
    margin-bottom: 20px;

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

    .upload-date{
        height: 24px;
        margin-right: 15px;
        font-size: 14px;
        line-height: 24px;
        color: #777;
        float: left;
    }

}
.paper-detail-btns{
    width: 90%;
    margin-top:20px;

    /*TODO:修改el-button颜色样式*/
    .el-button--read{
        background-color: #83a7cf;
        color: #ffffff;
        border: 1px solid #fff;
    }
    .el-button--read:hover{
        background-color: #fff;
        color: #83a7cf;
        border: 1px solid #83a7cf;
    }

    .el-button--edit:hover{
        color: #83a7cf;
        background: #fff;
        border-color: #83a7cf
    }
    .el-button--delete:hover{
        color: red;
        background: #fff;
        border-color: red
    }
}

.el-tag + .el-tag {
    margin-left: 10px;
}
.button-new-tag {
    margin-left: 10px;
    height: 32px;
    line-height: 30px;
    padding-top: 0;
    padding-bottom: 0;
}
.input-new-tag {
    width: 90px;
    margin-left: 10px;
    vertical-align: bottom;
}
.paper-detail-publish{
    margin-top: 20px;
    font-size: 16px;
    line-height: 24px;
    color: #333;
}
</style>