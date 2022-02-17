import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        // 存储token
        token: sessionStorage.getItem('token') ? sessionStorage.getItem('token') : '',
        // 存储未读消息数量
        unreadInfo: 0,
        userId: 0,
        username: "",
        userType: "",
        groupId: 0,
        portrait: ""
    },
    getters:{
        // get未读消息数量
        getUnreadInfoNumInStore: state => {
            return state.unreadInfo;
        },
        getUserId: state => {
            return state.userId;
        },
        getUsername: state => {
            return state.username;
        },
        getUserType: state => {
            return state.userType;
        },
        getGroupId: state => {
            return state.groupId;
        },
        getPortrait: state => {
            return state.portrait;
        }

    },
    mutations: {
        // 修改token，并将token存入sessionStorage
        changeLogin(state,user){
            this.state.token = user.token;
            sessionStorage.setItem('token', user.token);
        },
        // 修改未读消息数量
        changeUnreadInfoNumInStore(state, newVal){
            state.unreadInfo = newVal;
        },
        setUserId(state, newVal){
            state.userId = newVal;
        },
        setUsername(state, newVal){
            state.username = newVal;
        },
        setUserType(state, newVal){
            state.userType = newVal;
        },
        setGroupId(state, newVal){
            state.groupId = newVal;
        },
        setPortrait(state, newVal){
            state.portrait = newVal;
        }
    },
    actions: {
    },
    modules: {
    }
})
