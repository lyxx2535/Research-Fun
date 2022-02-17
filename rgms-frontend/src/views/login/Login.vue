<template>
    <div class="login-container">
        <div class="login-box">
            <!--头像区域-->
            <div class="avatar-box">
                <img src="../../assets/sys-logo.png" alt="">
            </div>
            <!--表单区域-->
            <el-form ref="loginFormRef" :model="loginForm" :rules="loginFormRules" label-width="0px" class="login-form">
                <el-form-item prop="username">
                    <el-input v-model="loginForm.username" prefix-icon="el-icon-user" clearable=""></el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input v-model="loginForm.password" prefix-icon="el-icon-lock" type="password" show-password clearable></el-input>
                </el-form-item>
                <!--按钮区域-->
                <el-form-item class="btns">
                    <el-button type="primary" round @click="login">登录</el-button>
                    <el-button round @click="toRegister">注册</el-button>
                </el-form-item>
            </el-form>
        </div>

    </div>
</template>
<script>
    import {mapMutations} from "vuex";
    export default {
        data() {
            return {
                loginForm: {
                    username: '',
                    password: ''

                },
                loginFormRules:{
                    username:[
                        { required: true, message: '请输入用户名称', trigger: 'blur' },
                        { min: 3, max: 10, message: '长度在 3 到 10 个字符', trigger: 'blur' }
                    ],
                    password:[
                        { required: true, message: '请输入用户密码', trigger: 'blur' },
                        { min: 5, max: 15, message: '长度在 5 到 15 个字符', trigger: 'blur' }
                    ]
                }
            }
        },
        methods: {
            ...mapMutations(['changeLogin']),
            login() {
                this.$refs.loginFormRef.validate(async valid=>{//异步函数
                    if(!valid){
                        this.$message.error({message: "登录失败！请重新输入用户数据！", duration:1500,showClose:true});
                    }else{
                        const _this = this;
                        const result = await this.$axios.post('/user/login',this.loginForm);
                        //直接获取数据，result.data为服务器返回的数据，重命名为res
                        // console.log(result);
                        //1、将登录成功之后的token，保存到客户端的sessionStorage中
                        //   1.1、项目中除了登录之外的其他API接口，必须在登录之后才可以访问
                        //   1.2、token当中只应在当前网站打开期间生效，所以将token保存在sessionStorage中
                        // 2、通过编程式导航跳转到后台主页，路由地址是/***
                        // console.log('errorCode' in result.data);
                        if('errorCode' in result.data){
                            this.$message.error({message: result.data.errorMsg, duration:1500,showClose:true});
                        }else{
                            let tokenStr = result.data.data.token;
                            let idStr = result.data.data.userId;
                            let typeStr = result.data.data.userType;
                            window.sessionStorage.setItem("token",tokenStr);
                            window.sessionStorage.setItem("userId",idStr);
                            window.sessionStorage.setItem("userType",typeStr);
                            _this.changeLogin({token:tokenStr});
                            this.$router.push('/infos');
                        }
                    }
                })
            },
            toRegister(){
                this.$router.push('/register');
            }
        }
    }
</script>

<style lang="less" scoped>
.login-container{
    background-color: #2b4b6b;
    height: 100%;
}

.login-box{
    width: 450px;
    height: 300px;
    background-color: white;
    border-radius: 5px;
    position: absolute;
    left: 50%;
    top: 50%;
    /*横轴、纵轴各移动50%*/
    transform:translate(-50%,-50%);

    .avatar-box{
        height: 100px;
        width: 100px;
        border: 1px solid #eee;
        border-radius: 50%;
        padding:10px;
        box-shadow: 0 0 10px #ddd;
        position: absolute;
        left: 50%;
        transform:translate(-50%,-50%);
        background-color: #fff;

        img{
            width: 100%;
            height: 100%;
            border-radius: 50%;
            background-color: #eee;

        }
    }

    .btns{
        display: flex;
        justify-content: flex-end;
    }
    .login-form{
        position: absolute;
        bottom: 0;
        width: 100%;
        padding: 0 20px;
        -webkit-box-sizing: border-box;
        -moz-box-sizing: border-box;
        box-sizing: border-box;

    }
}

</style>