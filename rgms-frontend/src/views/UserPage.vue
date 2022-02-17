<template>
    <div>
        <el-form label-position="left" class="edit-user-info-form"
                 ref="userInfoRef" :model="userInfo" :rules="userInfoRules" label-width="60px">
            <el-form-item label="头像">
                <el-upload
                        class="avatar-uploader"
                        action=""
                        :auto-upload="false"
                        accept=".jpg,.jpeg,.png,.JPG,.JPEG"
                        :show-file-list="false"
                        :on-change="getImageLocalUrl"
                        :on-success="handleAvatarSuccess"
                        :before-upload="beforeAvatarUpload">
                    <img v-if="userInfo.portrait" :src="userInfo.portrait" class="avatar">
                    <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                </el-upload>
            </el-form-item>
            <el-form-item label="用户名">
                <el-input style="width: 100%" v-model="userInfo.username" disabled></el-input>
            </el-form-item>
            <el-form-item label="真名" prop="trueName">
                <el-input style="width: 100%" v-model="userInfo.trueName"></el-input>
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
                <el-input style="width: 100%" v-model="userInfo.email" ></el-input>
            </el-form-item>
            <el-form-item class="update-user-info-footer">
                <el-button round type="primary" @click="updateUser">立即更新</el-button>
                <el-button round @click="cancelEdit">取 消</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
    export default {
        data(){
            //验证邮箱的规则
            let checkEmail = (rule, value, callback)=>{
                //正则表达式
                const regEmail = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)/;
                if(regEmail.test(value)){
                    return callback();
                }
                callback(new Error("请输入合法的邮箱！"))
            }
            return{
                id: null,
                type: "",
                userInfo: {
                },
                imgFile: {},
                userInfoRules:{
                    trueName: [
                        { min: 3, max: 10, message: '长度在 3 到 10 个字符', trigger: 'blur' }
                    ],
                    email:[
                        { validator: checkEmail, trigger:'blur'}
                    ]
                },
            }
        },
        created(){
            this.id = window.sessionStorage.getItem("userId");
            this.type = window.sessionStorage.getItem("userType");
            this.getUser();
        },
        methods:{
            //获得用户信息
            getUser(){
                this.imgFile = null;
                if(this.type === "TEACHER"){
                    this.$axios.get('/teacher/detailedInfo?teacherId=' + this.id).then(res => {
                        this.userInfo = res.data.data;
                    }).catch(err => {
                        this.$message.error({message: err, duration: 1500, showClose: true});
                    });
                }else{
                    // 学生要多获取一步：默认研究方向
                    this.$axios.get('/student/detailedInfo?studentId=' + this.id).then(res => {
                        this.userInfo = res.data.data;
                    }).catch(err => {
                        this.$message.error({message: err, duration: 1500, showClose: true});
                    });
                }
            },
            //上传头像，保存本地地址和文件
            getImageLocalUrl(file,fileList){
                this.userInfo.portrait = URL.createObjectURL(file.raw);
                this.imgFile = file.raw;
            },
            testAvator(file, fileList){
            },
            handleAvatarSuccess(res, file) {
            },
            beforeAvatarUpload(file) {
                const isJPG = file.type === 'image/jpeg' || 'image/png';
                const isLt2M = file.size / 1024 / 1024 < 2;

                if (!isJPG) {
                    this.$message.error('上传头像图片只能是 JPG/JPEG/PNG 格式!');
                }
                if (!isLt2M) {
                    this.$message.error('上传头像图片大小不能超过 2MB!');
                }
                return isJPG && isLt2M;
            },
            //取消编辑
            cancelEdit(){
                this.getUser();
            },
            //上传更新
            updateUser(){
                let upload = new FormData();
                upload.append("file", this.imgFile);

                if(this.imgFile === null){
                    let updateForm = {};
                    updateForm.userId = this.id;
                    updateForm.email = this.userInfo.email;
                    updateForm.trueName = this.userInfo.trueName;
                    updateForm.portrait = this.userInfo.portrait;
                    this.$axios.put('/user/userTrueNameAndEmailAndPortrait', updateForm).then(res => {
                        window.sessionStorage.setItem("portrait", updateForm.portrait);
                        history.go(0);
                        this.$message.success({message:"更新成功！", duration:1500, showClose:true});
                    }).catch(err => {
                        this.$message.error({message: err, duration: 1500, showClose: true});
                    });
                }else{
                    this.$axios.post('/file/upload?fileType=PORTRAIT',upload).then(async result => {
                        if('errorCode' in result){
                            this.$message.warning({message: result.errorMsg, duration: 1500, showClose: true});
                        }else{                                
                            this.userInfo.portrait = result.data.data;
                            let updateForm = {};
                            updateForm.userId = this.id;
                            updateForm.email = this.userInfo.email;
                            updateForm.trueName = this.userInfo.trueName;
                            updateForm.portrait = this.userInfo.portrait;
                            let _this = this;
                            this.$axios.put('/user/userTrueNameAndEmailAndPortrait', updateForm).then(res=>{                                    
                                window.sessionStorage.setItem("portrait", updateForm.portrait);
                                history.go(0);
                                _this.$message.success({message:"更新成功！", duration:1500, showClose:true});
                            }).catch(err=>err);
                        }
                    }).catch(err => {
                        this.$message.error({message: err, duration: 1500, showClose: true});
                    });
                }
            }
        }
    }
</script>

<style scoped lang="less">
.edit-user-info{
    margin-left: 15px;
    width: 20px;
    height: 20px;
}
.edit-user-info-form{
    margin-left: 15px;
    width: 60%;
}
.update-user-info-footer{
    display: flex;
    flex-direction: row;
    justify-content: flex-start;
}
</style>