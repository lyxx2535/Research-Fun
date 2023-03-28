<template>
  <div>这是重定向页面</div>
</template>

<script>
import {mapMutations} from "vuex";

export default {
  ...mapMutations(['changeLogin']),
  name: "Redirect",
  data() {
    return {
      CODE: '',
      STATE: ''
    }

  },
  created() {
    const _this = this;

    // var currentURL = window.location.href
    // var searchParams = new URLSearchParams(currentURL);
    var code = this.$route.query.code
    var state = this.$route.query.state

    //if(searchParams.has('code')&&searchParams.has('state')){
    if (code !== undefined && state !== undefined) {

      this.CODE = code
      this.STATE = state
      this.$axios.post('/wx/webLogin', {code: this.CODE, state: this.STATE}).then(function (res) {
// this.$alert("code = " + code + " state = " + state)
        console.log(res)
        if (res.data.msg !== "success") {
          _this.$alert(res.data.errorMsg);
          _this.$router.push('/login');
        } else {
          let tokenStr = res.data.data.token;
          let idStr = res.data.data.userId;
          let typeStr = res.data.data.userType;
          window.sessionStorage.setItem("token", tokenStr);
          window.sessionStorage.setItem("userId", idStr);
          window.sessionStorage.setItem("userType", typeStr);
          //_this.changeLogin({token:tokenStr});
          _this.$router.push('/infos');
        }
      })
    } else {
      _this.$alert("缺少参数")
    }
  }
}
</script>

<style scoped>

</style>