import Vue from 'vue'
import App from '../App.vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import PersonalSchedule from '../views/schedule/PersonalSchedule'
import AddSchedule from '../views/schedule/AddSchedule'
import GroupCalendar from '../views/group/GroupCalendar'
import Login from '../views/login/Login'
import Welcome from '../views/Welcome'
import Register from '../views/login/Register'
import AllGroups from '../views/group/AllGroups'
import MyGroup from '../views/group/MyGroup'
import Infos from '../views/Infos'
import UserPage from '../views/UserPage'
import PaperUploaded from '../views/paper/PaperUploaded'
import ExecutePaperAbstract from '../views/paper/ExecutePaperAbstract'
import SearchPaper from '../views/paper/SearchPaper'
import PaperDetail from '../views/paper/PaperDetail'
import PaperViewer from '../views/PaperViewer.vue'

Vue.use(VueRouter);
axios.defaults.withCredentials=false;

const routes = [
    {path: '/', redirect: '/login', show:false},
    {path: '/login', name: '登录', component: Login, show:false},
    {path: '/register', name: '注册', component: Register, show:false},
    {path: '/paperViewer', name: 'PDF阅读', component: PaperViewer, show:false},
    {
        path: '/home',
        component: Home,
        name: "我的日程",
        show: true,
        children:[
            {
                path:'/personalSchedule',
                name: "个人日程",
                component: PersonalSchedule
            },
            {
                path:'/addSchedule',
                name: "添加日程",
                component: AddSchedule
            },
            {
                path: '/groupCalendar',
                name: "小组日程计划表",
                component: GroupCalendar
            },
            {
                path: '/allGroups',
                name: '所有研究组',
                component: AllGroups
            },
            {
                path: '/myGroup',
                name: '我的研究组',
                component: MyGroup
            },
            {
                path: '/welcome',
                name: "欢迎界面",
                component: Welcome,
                children: [
                    {
                        path: '/infos' ,
                        name: '我的消息',
                        component: Infos
                    },
                    {
                        path: '/userPage',
                        name: '用户信息',
                        component: UserPage
                    }
                ]
            },
            {
                path: '/paperUploaded',
                name: '我上传的文献',
                component: PaperUploaded
            },
            {
                path: '/executePaperAbstract',
                name: '解析PDF文献',
                component: ExecutePaperAbstract
            },
            {
                path: '/searchPaper',
                name: '搜索文献',
                component: SearchPaper
            },
            {
                path: '/paperDetail',
                name: '文献详情',
                component: PaperDetail
            },
        ]
    },

]

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
});

//挂载路由导航守卫
router.beforeEach((to,from,next) => {
    //to：将要访问的路径
    //from：代表从哪个路径跳转而来
    //next：是一个函数，表示放行
    //next()放行   next('/login')强制跳转
    if(to.path === '/login' || to.path === '/register') return next()
    //从sessionStorage中获取到保存的token值
    const tokenStr = window.sessionStorage.getItem('token');
    //没有token，强制跳转到登录页面
    if(!tokenStr) return next('/login')
    next()

});

// 解决ElementUI导航栏中的vue-router在3.0版本以上重复点菜单报错问题
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err)
}

export default router
