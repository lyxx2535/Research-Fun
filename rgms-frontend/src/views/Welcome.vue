<template>
    <div>
        <el-menu router  class="el-menu-demo" mode="horizontal" :default-active="$router.currentRoute.path">

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