<template>
  <div>
    <div class="wrap">
      <el-card class="inner">
    <el-input style="margin-bottom: 10px;" prefix-icon="el-icon-message" placeholder="请输入正确的邮箱" v-model="email"></el-input>
    <el-input style="" prefix-icon="el-icon-lock" placeholder="请输入新密码" v-model="passwd" show-password></el-input>
    <el-button @click="handleFindPasswd" type="primary" style="margin-top: 20px">点击找回</el-button>
    </el-card>
    </div>
  </div>

</template>

<script>

export default {
  name: "FindPasswd",
  data() {
    return {
      email: '',
      passwd: ''
    }
  },
  methods: {
    handleFindPasswd() {
      var that=this
      this.$axios.post('/user/find', {'email': this.email, 'password': this.passwd}).then(function (res) {
        console.log(res)
        if(res.data==''){
          that.$message.success("请前往邮箱点击确认邮件")
          that.$router.push({path:'/login'})
        }else{
          that.$message.error("该邮箱对应的账号不存在")
        }
      })
    }
  }//191850247@smail.nju.edu.cn
}
</script>

<style scoped>
.wrap{
  width:20% ;
  margin-left: 40%;
  padding-top: 10%}
.inner{
  text-align: center;

  padding: 20px;
}
</style>