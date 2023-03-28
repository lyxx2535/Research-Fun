// @flow
import React from "react";
import {AutoComplete, Button} from 'element-react'
import {Popover} from 'element-react'
import {Pagination} from 'element-react'
import {Loading} from 'element-react'
// import {Input} from 'element-react'
import 'element-theme-default'

import type { T_Highlight } from "react-pdf-highlighter/src/types";
import axios from "axios";
import Reply from "./Reply.js"//引入回复组件
import Spinner from "./Spinner.js";

type T_ManuscriptHighlight = T_Highlight;

// 批注个人信息数据格式
type UserInfoInHighlight = {
    // 批注日期
    date: String,
    // 文献id
    did: String,
    // 批注者头像
    portrait: String,
    // 批注用户id
    userid: String,
    // 批注者用户名
    username: String
}

// 回复数据格式 Reply.js中的item就包括这些属性
type ReplyStructure = {
    // 批注Id
    commentid: Number,
    // 回复内容
    content: String,
    // 日期
    date: String,
    // 回复本身的id
    id: Number,
    // 头像
    portrait: String,
    // 回复哪一条id
    replyid: Number,
    // 回复种类
    type: Number,
    // 用户id
    userid: Number,
    // 用户名
    username: String
}

type Props = {
    highlights: Array<T_ManuscriptHighlight>,
    userInfos: Array<UserInfoInHighlight>,
    rederInfo: {
        readerId: Number,
        readerName: String
    },
    replyContent: {
        currentPage: 0,
        pageSize: 5,
        total: 0,
        replyList: []
    }
    // resetHighlights: () => void,
    // toggleDocument: () => void
};


const updateHash = highlight => {
    document.location.hash = `highlight-${highlight.id}`;
    console.log(document.location);
};


class Sidebar extends React.Component {//先在这里定义方法

    constructor(props){
        super(props);
        this.state = {
            isToastShow: false, // 是否展示 Toast
            toastText: '', // Toast 文字内容
            conbineList: [],
            readerInfo: {
                readerId: 0,
                readerName: ""
            },
            // 评论回复：同一页面一次只能最多弹出一个回复框
            currentPage: 1,
            pageSize: 5,
            total: 0,
            replyList: [],
            input: "",
            loading: false,
            translation_res: "等待翻译中......",
            groupId: -1,//用户当前所在的研究组
            teacherList: [],
            studentList: [],
            memberNameList: [],//用户当前所在研究组的所有成员姓名
            memberIdList: [],//用户当前所在研究组的所有成员id
            receiverId: -1,//接收方id
            remindMsg: "",//留言的内容
        };
    }

    componentDidMount(){
        this.initSidebarState();
    }

    // 踩坑！！！2021-04-07
    // 官方文档  https://reactjs.org/docs/react-component.html#setstate
    // componentDidUpdate()发生更新后立即调用。初始渲染不调用此方法。
    // 组件更新后，可以以此为契机在DOM上进行操作。只要将当前的道具与以前的道具进行比较，这也是一个进行网络请求的好地方（例如，如果道具未更改，则可能不需要网络请求）。
    // 您可以setState()立即调用它，componentDidUpdate()但请注意，它必须像上面的示例中那样包装，否则会导致无限循环!!!(坑)
    componentDidUpdate(prevProps){
        if(this.props.highlights !== prevProps.highlights ||
            this.props.userInfos !== prevProps.userInfos ||
            this.props.readerInfo !== prevProps.readerInfo){
            this.initSidebarState();
        }
    }

    // 重组props更新成自身组件的state
    initSidebarState(){
        var highlightsInstance = this.props.highlights;
        var userInfosInstance = this.props.userInfos;
        var readerInfo = this.props.readerInfo;
        // console.log(highlightsInstance);

        var li = [];
        for(let i = 0;i < highlightsInstance.length;i++){
            var element = {};
            element['content'] = highlightsInstance[i].content;
            element['comment'] = highlightsInstance[i].comment;
            element['position'] = highlightsInstance[i].position;
            element['id'] = highlightsInstance[i].id;
            element['infos'] = userInfosInstance[i];
            // 评论的回复
            li.push(element);
        }
        this.setState({
            conbineList : li,
            readerInfo: readerInfo,
        },()=>{
            // console.log(this.state);
        })
        this.getMemberList(this.props.readerInfo.readerId)
    }

    // 设置 Toast 属性
    handleToastShow = (toastText, showTime) => {
        this.setState({
            isToastShow: true,
            toastText
        });
        // 定时销毁 Toast Dom
        setTimeout(() => {
            this.setState({
                isToastShow: false,
                toastText: ""
            })
        }, showTime)
    }
    // 添加回复，更新state，重新渲染Reply组件
    addReply = (commentId) => {
    var replyBody = {
        commentid: commentId,
        content: this.state.input,
        date: "",
        id: 1,
        portrait: "",
        replyid: null,
        type: 1,
        userid: this.state.readerInfo.readerId,
        username: this.state.readerInfo.readerName
    };
    //评论后重新渲染评论区
    axios.put('http://42.193.37.120:9712/comment/savereplyinfo',replyBody).then(res=>{
    if(res.data.msg === "Save Reply Success"){
    this.getRepliesByCommentId(commentId);
}
}).catch(err=>err);
}

    // 添加提醒，更新state
    sendNotice = (comment, content, position, id) => {
        //     "content": item.content,
        //     "comment": item.comment,
        //     "position": item.position,
        //     "id" : item.id
        if(this.state.receiverId == -1){
            this.setState({
                receiverId: this.state.memberIdList[0],
            }, ()=>{
                var noticeBody = {
                    sendId: this.props.readerInfo.readerId,//发送方id
                    sendName: this.state.readerInfo.readerName,//发送方姓名
                    receiverId: this.state.receiverId,//接收方id
                    comment: comment.text,//评论内容
                    notice: this.state.remindMsg,//留言内容
                    did: this.props.userInfos[0].did,//文献id
                    content: JSON.stringify(content),//文献标记内容
                    position: JSON.stringify(position),//文献标记内容的位置
                    id: id,//评论的id
                };
                console.log(noticeBody)
                //发送提醒后清除部分变量
                axios.post('http://42.193.37.120:9712/comment/at', noticeBody).then(res=>{
                    if(res.data.msg === "success"){
                        this.handleToastShow('已发！', 1000)
                        this.cleanNotice();
                    }
                }).catch(err=>err);
            })
        }else{
            var noticeBody = {
                sendId: this.props.readerInfo.readerId,//发送方id
                sendName: this.state.readerInfo.readerName,//发送方姓名
                receiverId: this.state.receiverId,//接收方id
                comment: comment.text,//评论内容
                notice: this.state.remindMsg,//留言内容
                did: this.props.userInfos[0].did,//文献id
                content: JSON.stringify(content),//文献标记内容
                position: JSON.stringify(position),//文献标记内容的位置
                id: id,//评论的id
            };
            console.log(noticeBody)
            //发送提醒后清除部分变量
            axios.post('http://42.193.37.120:9712/comment/at', noticeBody).then(res=>{
                if(res.data.msg === "success"){
                    this.handleToastShow('已发！', 1000)
                    this.cleanNotice();
                }
            }).catch(err=>err);
        }
    }

cleanNotice(){
   this.setState({
       receiverId: -1,
   })
}
handleInputChange = e => {
    this.setState({
        input: e.target.value
    })
}

handleMsgChange = e => {
    this.setState({
        remindMsg: e.target.value
    }, ()=>{
        console.log(this.state.remindMsg)
    })
}

handleCurrentChange = (page, commentId) => {
    this.setState({
        currentPage: page
    },()=>{
        this.getRepliesByCommentId(commentId)
    })
}

// 点击展示回复按钮 调用加载批注接口
showRepliesOnClick = (commentId) => {
    this.setState({
        currentPage: 1
    });
    this.getRepliesByCommentId(commentId);
}

// 加载某个批注的回复
getRepliesByCommentId = (commentId) => {
    this.setState({
        loading: true
    })
    var nowPage = this.state.currentPage - 1;
    axios.post('http://42.193.37.120:9712/comment/getreply?' +
        'commentid=' + commentId + '&currentPage=' + nowPage + '&pageSize=' + this.state.pageSize).then(res=>{
        this.setState({
            replyList: res.data.data,
            total: res.data.total,
            loading: false
        })
    }).catch(err => err);
}

//翻译图片或文本
getTranslation = (content) => {
    this.setState({
        translation_res: "等待翻译中......",
    })
    if(content.text != null){
        axios.get('http://42.193.37.120:9712/trans/text?query=' + content.text).then(res=>{
            this.setState({
                translation_res: res.data.data,
            })
        }).catch(err => err);
    }
    if(content.image != null){
        axios.get('http://42.193.37.120:9712/trans/ocr?url=' + content.image).then(res=>{
            this.setState({
                translation_res: res.data.data.sumDst,
            })
        }).catch(err => err);
    }
    // console.log(this.state.translation_res)
}

//获得所在研究组和所有成员
getMemberList  = (userid) => {
    if(this.state.memberNameList.length == 0 && userid != null){
        axios.get("http://42.193.37.120:9712/user/groupId?userId=" + userid).then(res=>{
            this.setState({
                groupId: res.data.data,
            }, () => {
                if(this.state.memberNameList.length == 0){
                    axios.get("http://42.193.37.120:9712/researchGroup/detailedInfo?groupId=" + this.state.groupId).then(res=>{
                        this.setState({
                            teacherList: res.data.data.teacherInfos,
                            studentList: res.data.data.studentInfos
                        }, () => {
                            if (this.state.memberNameList.length == 0 && this.state.teacherList.length !== 0) {
                                this.state.teacherList.forEach((member, index) => {
                                    this.state.memberNameList.push(member.username);
                                    this.state.memberIdList.push(member.userId)
                                });
                            }
                            if (this.state.studentList.length !== 0) {
                                this.state.studentList.forEach((member, index) => {
                                    this.state.memberNameList.push(member.username);
                                    this.state.memberIdList.push(member.userId)
                                });
                            }
                            console.log(this.state.memberNameList)
                            console.log(this.state.memberIdList)
                        });
                    }).catch(err => err);
                }
            });
        }).catch(err => err);
    }else{
        console.log(this.state.memberNameList)
        console.log(this.state.memberIdList)
    }
}

//选择被提醒的成员
getPickedMember = (e) => {
    //获取被选中的值
    console.log(e.target.value);
    this.state.memberNameList.forEach((member, index) => {
        if(e.target.value == member){
            this.setState({
                //默认值改变
                receiverId: this.state.memberIdList[index]
            }, () => {
                console.log(this.state.receiverId)
            })
        }
    });
}

render(){
    const {conbineList, readerInfo, replyList, isToastShow, toastText} = this.state;

    return (//右边批注区的内容
        <div className="sidebar" style={{ width: "25vw" }}>
        <div className="description" style={{ padding: "1rem" }}>
            <p>
                <small>按住Option(Alt)键可绘制矩形框批注图片</small>
            </p>
        </div>


        <ul className="sidebar__highlights">
            {conbineList.map((item, index) => (
                <li key={index} className="sidebar__li">
                    <div style={{ display: "flex", flexDirection: "column",}}
                         className="sidebar__highlight" onClick={() => {
                            updateHash({//传这些可以索引地址
                                "content": item.content,
                                "comment": item.comment,
                                "position": item.position,
                                "id" : item.id
                            });
                         }}>
                    {/* 头像、用户名、时间、页码 */}
                    <div style={{
                        display: 'flex',
                        flexDirection: 'row',
                        justifyContent: 'flex-start',
                        alignItems: 'center'
                    }}>
                    <img src={item.infos.portrait} style={{
                        height: '20px',
                        width: '20px',
                        border: '1px solid #eee',
                        borderRadius: '50%',
                        boxShadow: '0 0 10px #ddd',
                        backgroundColor: '#fff'
                    }}></img>
                    <strong style={{marginLeft: '0.5rem', fontSize: "0.8rem"}}>{item.infos.username}</strong>
                    <div style={{width: "80%"}}>
                        <div className="highlight__location">
                            {item.infos.date.slice(0,16)}&nbsp;&nbsp;Page {item.position.pageNumber}
                        </div>
                    </div>
                    </div>
                    {/* 批注对象信息，包括图片和文字*/}
                        {item.content.text ? (
                            <blockquote style={{ marginTop: "0.5rem", fontSize: "0.5rem"}}>
                                {item.content.text.slice(0,100).trim()}...
                            </blockquote>
                        ) : null}
                        {item.content.image ? (
                            <div className="highlight__image" style={{ marginTop: "0.5rem" }}>
                                <img src={item.content.image} alt={"Screenshot"} />
                            </div>
                        ) : null}
                        {/* 评论信息 */}
                        <span style={{
                            marginTop: '10px',
                            color: '#2b4b6b',
                            fontSize: '1.5rem',
                            fontWeight: '500'
                        }}>{item.comment.text}</span>
                    </div>
                    {/* 操作区、按钮 */}
                    <div className="sidebar__highlight__buttons">
                        {/* 展示评论的pop */}
                        <Popover  style={{width: '23vw'}} placement="bottom" title="评论" trigger="click" content={(
                            // 回复区
                            <div>
                                {/* 回复展示区 */}
                                <Loading loading={this.state.loading} style={{minHeight: '10px'}}>
                                    <div><Reply replyList={replyList}/></div>
                                </Loading>
                                {/* 分页 */}
                                {this.state.total != 0 ? (
                                    <Pagination layout="prev, pager, next" small={true}
                                                total={this.state.total}
                                                currentPage={this.state.currentPage}
                                                pageSize={this.state.pageSize}
                                                style={{textAlign: "center"}}
                                                onCurrentChange={(currentPage) => this.handleCurrentChange(currentPage, item.id)}/>)
                                    : null}
                                {/* 回复输入框和按钮 */}
                                <div style={{
                                    display: 'flex',
                                    flexDirection: 'row',
                                    justifyContent: 'start',
                                    marginTop: '1rem'
                                }}>
                                    <input
                                        placeholder="请输入评论内容"
                                        style={{width: '70%'}}
                                        onChange={ e => this.handleInputChange(e)}
                                    />
                                    <Button type="primary" icon="edit" style={{marginLeft: '0.5rem'}}
                                            onClick={() => this.addReply(item.id)}>回复</Button>
                                </div>
                            </div>
                        )}>
                            <Button size="mini" onClick={()=> this.showRepliesOnClick(item.id)} >展示评论</Button>
                        </Popover>
                        {/* 翻译选中文本或图片的pop */}
                        <Popover  style={{width: '23vw'}} placement="bottom" title="翻译" trigger="click" content={(
                            <div>
                                <div>{this.state.translation_res}</div>
                            </div>
                        )}>
                            <Button size="mini" style={{marginLeft: '1rem'}} onClick={()=> this.getTranslation(item.content)} >翻译</Button>
                        </Popover>
                        {/* 提醒研究组某位用户查看该条评论 */}
                        <Popover  style={{width: '23vw'}} placement="bottom" title="提醒" trigger="click" content={(
                            <div>
                                选择提醒成员：
                                <select style={{ width: 200 }} defaultValue="请选择提醒成员" onChange={(e)=>this.getPickedMember(e)}>
                                    {
                                        // 遍历option
                                        this.state.memberNameList.map((ele,index)=>{
                                            return(
                                                <option key={index}>{ele}</option>
                                            )
                                        })
                                    }
                                </select>
                                {/* 留言输入框和按钮 */}
                                <div style={{
                                    display: 'flex',
                                    flexDirection: 'row',
                                    justifyContent: 'start',
                                    marginTop: '1rem'
                                }}>
                                    <input
                                        placeholder="请输入提醒内容"
                                        style={{width: '70%'}}
                                        onChange={ e => this.handleMsgChange(e)}
                                    />
                                    <Button type="primary" icon="edit" style={{marginLeft: '0.5rem'}}
                                            onClick={() => this.sendNotice(item.comment, item.content, item.position, item.id)}>发送</Button>
                                    <div>
                                        <div className="toast-wrap">
                                            <div className="toast-mask" />
                                            <div className="toast-text">{toastText}</div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        )}>
                            <Button size="mini" style={{marginLeft: '1rem'}}>提醒</Button>
                        </Popover>
                        {/* 删除评论的pop 鼠标放上去即显示*/}
                        {item.infos.userid == readerInfo.readerId ? (
                            <Popover placement="bottom" title="删除批注" width="100" trigger="hover" content={(
                                <div>
                                    <p>确定删除这段批注吗？</p>
                                    <div style={{textAlign: 'right'}}>
                                        <Button type="primary" size="mini" onClick={() => this.props.deleteComment(item.id)}>确定</Button>
                                    </div>
                                </div>
                            )}>
                                {/* 按钮键 */}
                                <Button size="mini" icon="delete" style={{marginLeft: '1rem'}}>删除</Button>
                            </Popover>
                        ): null}
                    </div>
                </li>
            ))}
        </ul>
</div>
);
}
}

export default Sidebar;
