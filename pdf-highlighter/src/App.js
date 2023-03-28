// @flow
/* eslint import/no-webpack-loader-syntax: 0 */

import React, { Component } from "react";
import PDFWorker from "worker-loader!pdfjs-dist/lib/pdf.worker";
import axios from "axios";

import {
  PdfLoader,
  PdfHighlighter,
  Tip,
  Highlight,
  Popup,
  AreaHighlight,
  setPdfWorker
} from "react-pdf-highlighter";

import testHighlights from "./test-highlights";

import Spinner from "./Spinner";
import Sidebar from "./Sidebar";

import type {
  T_Highlight,
  T_NewHighlight
} from "react-pdf-highlighter/src/types";

import "./style/App.css";

setPdfWorker(PDFWorker);

type Props = {};

// 批注个人信息数据格式
type UserInfoInHighlight = {
  // 批注日期
  date: String, 
  // 文献id
  did: Number,
  // 批注者头像
  portrait: String,
  // 批注用户id
  userid: Number,
  // 批注者用户名
  username: String
}


// 修改展示时的批注数据格式
type State = {
  url: string,
  highlights: Array<T_Highlight>,
  //从后台的数据中拆分出用户信息，保持pdf组件原有数据格式不变
  userInfos: Array<UserInfoInHighlight>,
  replyList: Array<ReplyWithPageInfo>
};

const getNextId = () => String(Math.random()).slice(2);

const parseIdFromHash = () =>
  document.location.hash.slice("#highlight-".length);

const resetHash = () => {
  document.location.hash = "";
};

const HighlightPopup = ({ comment }) =>
  comment.text ? (
    <div className="Highlight__popup">
      {comment.emoji} {comment.text}
    </div>
  ) : null;

const PRIMARY_PDF_URL = "https://arxiv.org/pdf/1708.08021.pdf";
const SECONDARY_PDF_URL = "https://arxiv.org/pdf/1604.02480.pdf";

const searchParams = new URLSearchParams(document.location.search);
const storage = window.sessionStorage;

// 获取pdf的url
const initialUrl = searchParams.get("url") || PRIMARY_PDF_URL;
// 获取文献id
const initialDocId = searchParams.get("did") || 0;
// 获取用户id
const initialUserId = searchParams.get("userId") || 0;
// 获取用户名
const initialUsername = searchParams.get("username") || "";
//  "&commentContent=" + this.commentContent
//  "&position=" + this.position
//  "&commentId=" + this.commentId;
//获取跳转的文本内容
const initialCommentContent = searchParams.get("commentContent") || "";
//获取跳转的地址
const initialPosition = searchParams.get("position") || "";
//获取跳转的评论id
const initialCommentId = searchParams.get("commentId") || 0;

const updateHash = highlight => {
  document.location.hash = `highlight-${highlight.id}`;
  console.log(document.location);
};

class App extends Component<Props, State> {
  state = {
    url: initialUrl,
    highlights: testHighlights[initialUrl]
      ? [...testHighlights[initialUrl]]
      : [],
    userInfoList: [],
    readerInfo: {
      readerId: initialUserId,
      readerName: initialUsername
    }
  };

  state: State;

  componentDidMount() {
    // 加载文献时导入文献的批注和回复，加载侧边栏
    this.getHighlightsFromServer();

    window.addEventListener(
      "hashchange",
      this.scrollToHighlightFromHash,
      false
    );

    updateHash({//传这些可以索引地址
      "content": initialCommentContent,
      "position": initialPosition,
      "id" : initialCommentId
    });
  }

  // 从后端获得批注
  getHighlightsFromServer = () =>{
    var highlightsInCircle: Array<T_Highlight> = [];
    var userInfosInCircle: Array<UserInfoInHighlight> = [];

    axios.get('http://42.193.37.120:9712/comment/getCompletecommentinfo?did=' + initialDocId).then(res=>{
      // console.log(res);
      var commentList = res.data.data;
    
      commentList.forEach((element,index) => {
        // 批注信息
        var highlight: T_Highlight = {};
        highlight.content = element.content; 
        highlight.comment = element.comment;
        highlight.position = element.position;
        highlight.id = element.id + '';
        highlightsInCircle.push(highlight);
        // 批注者信息
        var userInfos: UserInfoInHighlight = {};
        userInfos = element.userInfos;
        userInfosInCircle.push(userInfos);

      });

      this.setState({
        highlights: highlightsInCircle,
        userInfoList: userInfosInCircle,
      },
      ()=>{
        // console.log(this.state);
      });
    }).catch(err=>err);

  }

  resetHighlights = () => {
    this.setState({
      highlights: []
    });
  };

  toggleDocument = () => {
    const newUrl =
      this.state.url === PRIMARY_PDF_URL ? SECONDARY_PDF_URL : PRIMARY_PDF_URL;

    this.setState({
      url: newUrl,
      highlights: testHighlights[newUrl] ? [...testHighlights[newUrl]] : []
    });
  };

  scrollViewerTo = (highlight: any) => {};

  scrollToHighlightFromHash = () => {
    const highlight = this.getHighlightById(parseIdFromHash());

    if (highlight) {
      this.scrollViewerTo(highlight);
    }
  };
  
  getHighlightById(id: string) {
    const { highlights } = this.state;

    return highlights.find(highlight => highlight.id === id);
  }

  // 添加高亮和批注，保存到服务器
  addHighlight(highlight: T_NewHighlight) {
    const { highlights } = this.state;
    
    var highlightUpload : Object = highlight;
    if('text' in highlightUpload.content){
      highlightUpload.content['image'] = null;
      // highlight.content['image'] = null;
    }else{
      highlightUpload.content['text'] = null;
      // highlight.content['text'] = null;
    }

    var extraInfo = {
      did: initialDocId,
      userid: initialUserId
    }

    highlightUpload['infos'] = extraInfo;
    // console.log(highlight);

    axios.put('http://42.193.37.120:9712/comment/saveCompleteCommentinfo',highlightUpload).then(res=>{
      // console.log(res);
      if(res.data.data === "Save Success"){
        this.getHighlightsFromServer();
      }else{
        alert("添加失败！");
      }

    }).catch(err=>err);

  }

  updateHighlight(highlightId: string, position: Object, content: Object) {
    console.log("Updating highlight", highlightId, position, content);

    this.setState({
      highlights: this.state.highlights.map(h => {
        const {
          id,
          position: originalPosition,
          content: originalContent,
          ...rest
        } = h;
        return id === highlightId
          ? {
              id,
              position: { ...originalPosition, ...position },
              content: { ...originalContent, ...content },
              ...rest
            }
          : h;
      })
    });
  }

  // 删除批注
  deleteComment = (commentId) => {
    axios.get('http://42.193.37.120:9712/comment/deletecomment?commentid=' + commentId).then(res=>{
      // console.log(res);
      this.getHighlightsFromServer();
    }).catch(err => err);
    // console.log(commentId);
  }

  render() {
    const { url, highlights, userInfoList, readerInfo } = this.state;

    // console.log(highlights);

    return (
      <div className="App" style={{ display: "flex", height: "100vh" }}>
       
        <div
          style={{
            height: "100vh",
            width: "75vw",
            position: "relative"
          }}
        >
          <PdfLoader url={url} beforeLoad={<Spinner />}>
            {pdfDocument => (
              <PdfHighlighter
                pdfDocument={pdfDocument}
                enableAreaSelection={event => event.altKey}
                onScrollChange={resetHash}
                // pdfScaleValue="page-width"
                scrollRef={scrollTo => {
                  this.scrollViewerTo = scrollTo;

                  this.scrollToHighlightFromHash();
                }}
                onSelectionFinished={(
                  position,
                  content,
                  hideTipAndSelection,
                  transformSelection
                ) => (
                  <Tip
                    onOpen={transformSelection}
                    onConfirm={comment => {
                      this.addHighlight({ content, position, comment });

                      hideTipAndSelection();
                    }}
                  />
                )}
                highlightTransform={(
                  highlight,
                  index,
                  setTip,
                  hideTip,
                  viewportToScaled,
                  screenshot,
                  isScrolledTo
                ) => {
                  const isTextHighlight = !Boolean(
                    highlight.content && highlight.content.image
                  );

                  const component = isTextHighlight ? (
                    <Highlight
                      isScrolledTo={isScrolledTo}
                      position={highlight.position}
                      comment={highlight.comment}
                    />
                  ) : (
                    <AreaHighlight
                      highlight={highlight}
                      onChange={boundingRect => {
                        this.updateHighlight(
                          highlight.id,
                          { boundingRect: viewportToScaled(boundingRect) },
                          { image: screenshot(boundingRect) }
                        );
                      }}
                    />
                  );

                  return (
                    <Popup
                      popupContent={<HighlightPopup {...highlight} />}
                      onMouseOver={popupContent =>
                        setTip(highlight, highlight => popupContent)
                      }
                      onMouseOut={hideTip}
                      key={index}
                      children={component}
                    />
                  );
                }}
                highlights={highlights}
              />
            )}
          </PdfLoader>
        </div>
        <Sidebar
          highlights={highlights}
          userInfos={userInfoList}
          readerInfo={readerInfo}
          deleteComment={this.deleteComment}
          // resetHighlights={this.resetHighlights}
          // toggleDocument={this.toggleDocument}
        />
      </div>
    );
  }
}

export default App;
