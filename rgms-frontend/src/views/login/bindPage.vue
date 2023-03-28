<template>
<div>
  <div>{{text}}</div>
</div>
</template>

<script>
export default {
  name: "bindPage",
  data(){
    return {
      text:'绑定中......'
    }

  },
  created() {
    this.$axios.post('/wx/bound?uid='+window.sessionStorage.getItem('userId')).then(function(res) {
      console.log(res)
      //if(res.data.status==200){
      //this.data.text='绑定成功'}
      //else{
      //this.data.text='绑定失败，请重试'
      //}
    })
  },


}
</script>

<style scoped>

</style>