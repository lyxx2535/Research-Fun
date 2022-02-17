<template>
    <div>
        <el-page-header @back="goBackToPaperUploaded" content="文献解析">
        </el-page-header>
        <!--后台初步解析后的文献-->
        <div class="execute-pdf-container">
            <el-form label-position="left" class="edit-user-info-form" v-loading="executedLoading"
                     ref="executedPaperFormRef" :model="executedPaperForm" label-width="80px">
                <el-form-item label="标题">
                    <el-popover v-model="titlePopoverVisible" title="添加标题" min-width="200px" :placement="'right-end'" trigger="manual">
                        <p style="width: 300px;">{{titleStr}}</p>
                        <div style="text-align: right; margin: 0">
                            <el-button size="mini" type="text" @click="cancelPopoverSelected">取消</el-button>
                            <el-button type="primary" size="mini" @click="confirmTitleSelected(titleStr)">确定</el-button>
                        </div>
                        <el-card slot="reference">
                            <span  class="" @mousedown="titleMouseDown" @mouseup="titleMouseUp($event)" >{{executedPaperForm.title}}</span>
                        </el-card>
                    </el-popover>
                </el-form-item>

                <el-form-item label="作者">
                    <el-input disabled="" v-model="extraAuthorInfo" style="margin-bottom: 15px;"></el-input>
                    <el-popover v-model="authorsPopoverVisible" title="添加作者" min-width="200px" :placement="'right-end'" trigger="manual">
                        <p>{{authorStr}}</p>
                        <div style="text-align: right; margin: 0">
                            <el-button size="mini" type="text" @click="cancelPopoverSelected">取消</el-button>
                            <el-button type="primary" size="mini" @click="confirmAuthorSelected(authorStr)">确定</el-button>
                        </div>
                        <el-card slot="reference">
                            <span  class="" @mousedown="authorMouseDown" @mouseup="authorMouseUp($event)" >{{executedPaperForm.authors}}</span>
                        </el-card>
                    </el-popover>
                </el-form-item>

                <el-form-item label="关键词">
                    <el-input disabled="" v-model="extraKeywordInfo" style="margin-bottom: 15px;"></el-input>
                    <el-popover v-model="keywordsPopoverVisible" title="添加关键词" min-width="200px" :placement="'right-end'" trigger="manual">
                        <p>{{keywordStr}}</p>
                        <div style="text-align: right; margin: 0">
                            <el-button size="mini" type="text" @click="cancelPopoverSelected">取消</el-button>
                            <el-button type="primary" size="mini" @click="confirmKeywordSelected(keywordStr)">确定</el-button>
                        </div>
                        <el-card slot="reference">
                            <span  class="" @mousedown="keywordMouseDown" @mouseup="keywordMouseUp($event)" >{{executedPaperForm.keywords}}</span>
                        </el-card>
                    </el-popover>
                </el-form-item>

                <el-form-item label="首页">
                    <el-popover v-model="firstPagePopoverVisible" title="添加指定内容" width="500px" placement="right-end" trigger="manual">
                        <p style="width: 500px">{{firstPageStr}}</p>
                        <div style="text-align: right; margin: 0">
                            <el-button size="mini" type="text" @click="cancelPopoverSelected">取消</el-button>
                            <el-button type="primary" size="mini" @click="confirmTitleSelected(firstPageStr)" icon="el-icon-circle-plus-outline">标题</el-button>
                            <el-button type="primary" size="mini" @click="confirmAuthorSelected(firstPageStr)" icon="el-icon-circle-plus-outline">作者</el-button>
                            <el-button type="primary" size="mini" @click="confirmKeywordSelected(firstPageStr)" icon="el-icon-circle-plus-outline">关键词</el-button>
                            <el-button type="primary" size="mini" @click="confirmAbstractSelected(firstPageStr)" icon="el-icon-circle-plus-outline">摘要</el-button>
                        </div>
                        <el-card slot="reference">
                            <span  class="" @mousedown="firstPageMouseDown" @mouseup="firstPageMouseUp($event)" >{{executedPaperForm.firstPage}}</span>
                        </el-card>
                    </el-popover>
                </el-form-item>
            </el-form>

            <el-divider direction="vertical" class="form-divider"></el-divider>

            <!--用户确认的文献表单-->
            <el-form label-position="left" class="edit-user-info-form"
                     ref="PaperFormRef" :model="paperForm" :rules="paperFormRules" label-width="80px">
                <el-form-item label="标题" prop="title">
                    <el-input class="" type="textarea" :rows="5" v-model="paperForm.title"></el-input>
                </el-form-item>
                <el-form-item label="作者" prop="authors">
                    <el-card>
                        <el-tag
                                v-for="tag in paperForm.authors"
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
                <el-form-item label="关键词" prop="keywords">
                    <el-card>
                        <el-tag
                                v-for="tag in paperForm.keywords"
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
                <el-form-item label="研究方向" prop="research_direction">
                    <el-select v-model="paperForm.research_direction" placeholder="请选择论文研究方向">
                        <el-option
                                v-for="(item,index) in groupDirectionOptions"
                                :key="index"
                                :label="item"
                                :value="item">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="摘要" prop="abstract">
                    <el-input class="" type="textarea" :rows="25" v-model="paperForm.abstract"></el-input>
                </el-form-item>
                <el-form-item  class="submit-btns">
                    <el-button round @click="resetPaperForm">重 置</el-button>
                    <el-button round type="primary" @click="submitPaper">确认上传</el-button>
                 </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script>
    export default {
        name: "ExecutePaperAbstract",
        created(){
            //取值 ====> this.$route.query.name,页面传参
            this.filepath = this.$route.query.file;
            this.id = window.sessionStorage.getItem("userId");
            this.groupId = window.sessionStorage.getItem("groupId");
            this.getGroupDirections();
            this.showExecutedPaperAbstract();
        },
        data(){
            return{
                filepath: "",
                groupId: null,
                id: null,
                //初步解析的文献（后台第一次解析后的结果）
                executedPaperForm:{
                    title:"",
                    authors:"",
                    keywords:"",
                    firstPage:"",
                },
                //用户确认后的文献
                paperForm:{
                    title:"",
                    authors:[],
                    keywords:[],
                    abstract:"",
                    research_direction: ""
                },
                paperFormRules:{
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
                    ]
                },
                //解析表格加载
                executedLoading: false,
                txtTest: "",
                //标题选中字符串
                titleStr: "",
                //作者选中字符串
                authorStr: "",
                //关键词选中字符串
                keywordStr: "",
                //摘要选中字符串
                abstractStr: "",
                //首页内容用户选中字符串
                firstPageStr:"",
                extraAuthorInfo: "注：一次选中一位作者",
                extraKeywordInfo: "注：一次选中一个关键词",
                //标题气泡是否可见
                titlePopoverVisible: false,
                //作者气泡是否可见
                authorsPopoverVisible: false,
                //关键词气泡是否可见
                keywordsPopoverVisible: false,
                //首页气泡是否可见
                firstPagePopoverVisible: false,
                x:0,
                y:0,
                positionStyle:{top:'20px',left:'20px'},
                // 用户所在组的研究方向
                groupDirectionOptions: [],
                inputAuthorVisible: false,
                inputAuthorValue: "",
                inputKeywordVisible: false,
                inputKeywordValue: "",
            }
        },
        methods:{
            getGroupDirections(){
                if(this.groupId !== null){
                    this.$axios.get('/researchGroup/directions?groupId=' + this.groupId).then(res => {
                        this.groupDirectionOptions = res.data.data;                    
                    }).catch(err => {
                        this.$message.error({message: err, duration: 1500, showClose: true});
                    });
                }else{
                    this.$message.error({message: "请先加入研究组！", duration:1500, showClose:true});
                    this.$router.push('/allGroups');
                }
            },
            showExecutedPaperAbstract(){            
                this.executedLoading = true;
                this.$axios.get('/file/excutepdf?fileUrl=' + this.filepath).then(res => {
                    if('errorCode' in res.data){
                        this.$message.error({message: res.data.errorMsg, duration:1500, showClose:true});
                    }else{
                        if(res.data.msg === 'success'){
                            this.$message.success({message: '解析成功！请手动保存文献信息！', duration: 2000, showClose: true});
                        }else{
                            this.$message.error({message: '解析失败！', duration: 2000, showClose: true});
                        }
                        this.executedPaperForm.title = res.data.data[0] !== null ? res.data.data[0] : "";
                        this.executedPaperForm.authors = res.data.data[1] !== null ? res.data.data[1] : "";
                        this.executedPaperForm.keywords = res.data.data[2] !== null ? res.data.data[2] : "";
                        this.executedPaperForm.firstPage = res.data.data[3] !== null ? res.data.data[3] : "";
                        this.executedLoading = false;
                    }
                }).catch(err => {
                    this.$message.error({message: err, duration:1500, showClose:true});
                })
            },
            //按下鼠标，选中文字
            titleMouseDown(){},
            authorMouseDown(){},
            keywordMouseDown(){},
            firstPageMouseDown(){},
            //抬起鼠标，显示气泡确认框
            titleMouseUp(event){
                var txt = ""; //存储选中文字
                if(window.getSelection){
                    txt = window.getSelection().toString();
                }else{
                    txt = document.selection.createRange().text;
                }
                if(txt){
                    this.titleStr = txt;
                    this.titlePopoverVisible = true;
                }
            },
            authorMouseUp(event){
                var txt = ""; //存储选中文字
                if(window.getSelection){
                    txt = window.getSelection().toString();
                }else{
                    txt = document.selection.createRange().text;
                }
                if(txt){
                    this.authorStr = txt;
                    this.authorsPopoverVisible = true;
                }
            },
            keywordMouseUp(event){
                var txt = ""; //存储选中文字
                if(window.getSelection){
                    txt = window.getSelection().toString();
                }else{
                    txt = document.selection.createRange().text;
                }
                if(txt){
                    this.keywordStr = txt;
                    this.keywordsPopoverVisible = true;
                }
            },
            firstPageMouseUp(event){
                var txt=""; //存储选中文字
                if(window.getSelection){
                    txt = window.getSelection().toString();
                }else{
                    txt = document.selection.createRange().text;
                }
                if(txt){
                    this.firstPageStr = txt;
                    this.firstPagePopoverVisible = true;
                }
            },
            //标题填入确定
            confirmTitleSelected(s){
                this.paperForm.title = s;
                this.titlePopoverVisible = false;
                this.firstPagePopoverVisible = false;
            },
            //作者填入确定
            confirmAuthorSelected(s){
                this.paperForm.authors.push(s);
                this.authorsPopoverVisible = false;
                this.firstPagePopoverVisible = false;
            },
            //关键词填入确定
            confirmKeywordSelected(s){
                this.paperForm.keywords.push(s);
                this.keywordsPopoverVisible = false;
                this.firstPagePopoverVisible = false;
            },
            //摘要填入确定
            confirmAbstractSelected(s){
                this.paperForm.abstract = s;
                this.firstPagePopoverVisible = false;
                this.firstPagePopoverVisible = false;
            },
            //删除作者气泡
            closeAuthorTag(index){
                this.paperForm.authors.splice(index,1);
            },
            //删除关键词气泡
            closeKeywordTag(index){
                this.paperForm.keywords.splice(index,1);
            },
            //取消气泡选中
            cancelPopoverSelected(){
                window.getSelection().removeAllRanges();
                this.titlePopoverVisible = this.titlePopoverVisible === true ? false : false;
                this.authorsPopoverVisible = this.authorsPopoverVisible === true ? false : false;
                this.keywordsPopoverVisible = this.keywordsPopoverVisible === true ? false : false;
                this.firstPagePopoverVisible = this.firstPagePopoverVisible === true ? false : false;
            },
            resetPaperForm(){
                this.$refs.paperFormRef.resetFields();
            },
            //确认上传文献信息
            submitPaper(){
                this.$refs.PaperFormRef.validate(valid => {
                    if(valid){
                        if(this.paperForm.title === "" ||
                            this.paperForm.authors.length === 0 ||
                            this.paperForm.keywords.length === 0 ||
                            this.paperForm.abstract === "" ||
                            this.paperForm.research_direction === ""){
                            this.$message.error({message: "存在表单数据项为空，请重新填写！", duration:1500, showClose:true});
                        }else{
                            let doc = {
                                abstract: "",
                                authors: [],
                                date: "",
                                did: 0,
                                doi: 0,
                                keywords: [],
                                pdflink: "",
                                title: "",
                                userid: 0,
                                research_direction: ""
                            };
                            doc.title = this.paperForm.title;
                            doc.authors = this.paperForm.authors;
                            doc.keywords = this.paperForm.keywords;
                            doc.pdflink = decodeURIComponent(this.filepath);
                            doc.userid = this.id;
                            doc.abstract = this.paperForm.abstract;
                            doc.research_direction = this.paperForm.research_direction;
                            this.$axios.put('/file/advanceSaveDocumentinfo',doc).then(res => {
                                if('errorCode' in res.data){
                                    this.$message.error({message: res.data.errorMsg, duration:1500, showClose:true});
                                }else{
                                    this.$message.success({message: "上传成功！", duration:1500, showClose:true});
                                    this.$router.push({
                                        path: '/searchPaper',
                                        query:{
                                            searchStr : ""
                                        }
                                    })
                                }
                            }).catch(err => {
                                this.$message.error({message: err, duration:1500, showClose:true});
                            })
                        }
                    }else{
                        this.$message.error({message: "文献信息验证未通过！请重新提交", duration:1500, showClose:true});
                    }
                })
            },
            goBackToPaperUploaded(){
                this.$router.push('/paperUploaded');
            },
            // start 编辑作者信息
            handleCloseAuthors(tag) {
                this.paperForm.authors.splice(this.paperForm.authors.indexOf(tag), 1);
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
                    this.paperForm.authors.push(inputValue);
                }
                this.inputAuthorVisible = false;
                this.inputAuthorValue = '';
            },
            // end
            // start 编辑关键词信息
            handleCloseKeywords(tag) {
                this.paperForm.keywords.splice(this.paperForm.keywords.indexOf(tag), 1);
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
                    this.paperForm.keywords.push(inputValue);
                }
                this.inputKeywordVisible = false;
                this.inputKeywordValue = '';
            }
            // end
        }
    }
</script>

<style lang="less" scoped>
.execute-pdf-container{
    margin-top: 15px;
    height: 100%;
    display: flex;
    flex-direction: row;
    justify-content: space-around;
}
.el-divider--vertical{
    display: inline-block;
    width: 1px;
    height: 1000px;
    margin: 0 8px;
    vertical-align: middle;
    position: relative;
}
.test{
    display: flex;
    justify-content: space-between;
    flex-direction: column;
}
.title-executed{
    border-radius: 3px;
    height: 100%;
    width: 80%
}
.el-form{
    width: 40%;
}
.el-tag{
    margin-right: 10px;
}
.submit-btns{
    display: flex;
    flex-direction: row;
    justify-content: flex-end;
}
</style>