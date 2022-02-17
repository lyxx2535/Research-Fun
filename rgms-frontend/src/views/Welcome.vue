<template>
    <div>
        <el-menu router  class="el-menu-demo" mode="horizontal" :default-active="$router.currentRoute.path">
            <el-menu-item index="/infos">消息中心</el-menu-item>
            <el-menu-item index="/userPage">用户信息</el-menu-item>
        </el-menu>

        <router-view></router-view>

    </div>
</template>

<script>
    export default {
        name: "Welcome"
    }
</script>

<style scoped>

</style>