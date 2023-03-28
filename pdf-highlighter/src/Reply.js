// Reply组件：回复列表 + 分页 + 输入框 + 回复按钮

import React from "react";
import {Button} from 'element-react'
import {Input} from 'element-react'
import 'element-theme-default'

import type { T_Highlight } from "react-pdf-highlighter/src/types";
import axios from "axios";

type Props = {

}

class Reply extends React.Component {
    constructor(props){
        super(props);
    }

    render(){
        const replyList = this.props.replyList;

        return (//评论区的格式
            <div >
                <ul style={{listStyle: 'none', padding:'0'}}>
                    {replyList.map((item,index) => (
                        <li
                            key={index}
                            style={{marginBottom: '1rem', borderBottom: '0px solid rgb(119, 119, 119)'}}
                        >
                            <div style={{
                                    display: 'flex',
                                    flexDirection: 'row',
                                    justifyContent: 'flex-start',
                                    alignItems: 'center',
                                    marginBottom: '0.5rem'
                                }}>
                                <img src={item.portrait} style={{
                                    height: '15px',
                                    width: '15px',
                                    border: '1px solid #eee',
                                    borderRadius: '50%',
                                    boxShadow: '0 0 10px #ddd',
                                    backgroundColor: '#fff'
                                }}></img>
                                <strong style={{marginLeft: '0.5rem', fontSize: "0.8rem"}}>{item.username}</strong>
                                <div style={{width: "80%"}}>
                                    <div className="highlight__location">
                                    {item.date.slice(0,16)}
                                    </div>
                                </div>
                            </div>
                            <div style={{fontSize: '1rem', color: '#2b4b6b', marginLeft: '1.5rem', marginBottom: '0.5rem'}}>
                                <span>{item.content}</span>
                            </div>
                        </li>
                    ))}
                </ul>
            </div>
        )
    }
}

export default Reply;