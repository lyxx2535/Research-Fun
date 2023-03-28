/*
Navicat MySQL Data Transfer

Source Server         : 大创项目腾讯云
Source Server Version : 50737
Source Host           : 42.193.37.120:9712
Source Database       : researchfun

Target Server Type    : MYSQL
Target Server Version : 50737
File Encoding         : 65001

Date: 2022-02-23 16:41:14
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_author
-- ----------------------------
DROP TABLE IF EXISTS `t_author`;
CREATE TABLE `t_author` (
  `auid` bigint(20) NOT NULL AUTO_INCREMENT,
  `aname` varchar(255) DEFAULT NULL COMMENT '作者姓名',
  PRIMARY KEY (`auid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_comment
-- ----------------------------
DROP TABLE IF EXISTS `t_comment`;
CREATE TABLE `t_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `did` bigint(20) DEFAULT NULL,
  `comment_type` int(11) DEFAULT NULL COMMENT '0是文字 1是图片',
  `content` varchar(255) DEFAULT NULL COMMENT '批注内容',
  `comment_emoji` varchar(255) DEFAULT NULL COMMENT '批注的表情',
  `comment_text` varchar(255) DEFAULT NULL COMMENT '批注对应的文本',
  `date` datetime DEFAULT NULL COMMENT '批注日期',
  `pagenumber` int(11) DEFAULT NULL COMMENT '批注在文献的页码',
  `userid` bigint(20) DEFAULT NULL COMMENT '添加批注的用户',
  PRIMARY KEY (`id`),
  KEY `did` (`did`),
  KEY `userid` (`userid`),
  CONSTRAINT `t_comment_ibfk_1` FOREIGN KEY (`did`) REFERENCES `t_doc` (`did`),
  CONSTRAINT `t_comment_ibfk_2` FOREIGN KEY (`userid`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_direction
-- ----------------------------
DROP TABLE IF EXISTS `t_direction`;
CREATE TABLE `t_direction` (
  `did` bigint(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL COMMENT '研究方向名字',
  PRIMARY KEY (`did`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_doc
-- ----------------------------
DROP TABLE IF EXISTS `t_doc`;
CREATE TABLE `t_doc` (
  `did` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL COMMENT '文献标题',
  `abstract` varchar(8191) DEFAULT NULL COMMENT '文献摘要',
  `pdflink` varchar(2047) DEFAULT NULL COMMENT '文献下载路径',
  `date` datetime DEFAULT NULL COMMENT '上传时间',
  `userid` bigint(20) DEFAULT NULL COMMENT '上传者id',
  `research_direction` varchar(255) DEFAULT NULL COMMENT '研究方向',
  `groupid` bigint(20) DEFAULT NULL COMMENT '研究组id',
  `publisher` varchar(255) DEFAULT NULL COMMENT '出版机构',
  `publishdate` datetime DEFAULT NULL COMMENT '出版日期',
  PRIMARY KEY (`did`),
  KEY `userid` (`userid`),
  KEY `groupid` (`groupid`),
  CONSTRAINT `groupid` FOREIGN KEY (`groupid`) REFERENCES `t_researchgroup` (`id`),
  CONSTRAINT `userid` FOREIGN KEY (`userid`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_doc_author
-- ----------------------------
DROP TABLE IF EXISTS `t_doc_author`;
CREATE TABLE `t_doc_author` (
  `auid` bigint(20) DEFAULT NULL,
  `did` bigint(20) DEFAULT NULL,
  KEY `作者id` (`auid`),
  KEY `文献id` (`did`),
  CONSTRAINT `作者id` FOREIGN KEY (`auid`) REFERENCES `t_author` (`auid`),
  CONSTRAINT `文献id` FOREIGN KEY (`did`) REFERENCES `t_doc` (`did`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_doc_keyword
-- ----------------------------
DROP TABLE IF EXISTS `t_doc_keyword`;
CREATE TABLE `t_doc_keyword` (
  `kid` bigint(20) DEFAULT NULL COMMENT '关键词id',
  `did` bigint(20) DEFAULT NULL COMMENT '文献id',
  KEY `kid` (`kid`),
  KEY `did` (`did`),
  CONSTRAINT `did` FOREIGN KEY (`did`) REFERENCES `t_doc` (`did`),
  CONSTRAINT `kid` FOREIGN KEY (`kid`) REFERENCES `t_keyword` (`kid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_keyword
-- ----------------------------
DROP TABLE IF EXISTS `t_keyword`;
CREATE TABLE `t_keyword` (
  `kid` bigint(20) NOT NULL AUTO_INCREMENT,
  `kname` varchar(255) NOT NULL COMMENT '关键字',
  PRIMARY KEY (`kid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_message
-- ----------------------------
DROP TABLE IF EXISTS `t_message`;
CREATE TABLE `t_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `body` varchar(255) DEFAULT NULL,
  `date` datetime(6) DEFAULT NULL COMMENT '消息日期',
  `extra_info_id` bigint(20) DEFAULT NULL COMMENT '附加信息id',
  `recipient` bigint(255) DEFAULT NULL COMMENT '消息接受者',
  `sender` bigint(255) DEFAULT NULL COMMENT '消息发送者',
  `state` int(11) DEFAULT '0' COMMENT '消息状态',
  `type` int(11) DEFAULT '1' COMMENT '消息类型',
  `extra_info` varchar(255) DEFAULT NULL COMMENT '附加信息，不对外展示，仅用于方便逻辑处理',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_rectangle
-- ----------------------------
DROP TABLE IF EXISTS `t_rectangle`;
CREATE TABLE `t_rectangle` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cid` bigint(20) DEFAULT NULL COMMENT '位置信息对应的批注id',
  `height` double DEFAULT NULL COMMENT '高度',
  `width` double DEFAULT NULL COMMENT '宽度',
  `type` int(11) DEFAULT NULL COMMENT '类型',
  `x1` double DEFAULT NULL COMMENT '左边界',
  `x2` double DEFAULT NULL COMMENT '右边界',
  `y1` double(255,0) DEFAULT NULL COMMENT '上边界',
  `y2` double(255,0) DEFAULT NULL COMMENT '下边界',
  PRIMARY KEY (`id`),
  KEY `cid` (`cid`),
  CONSTRAINT `t_rectangle_ibfk_1` FOREIGN KEY (`cid`) REFERENCES `t_comment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_reply
-- ----------------------------
DROP TABLE IF EXISTS `t_reply`;
CREATE TABLE `t_reply` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cid` bigint(20) DEFAULT NULL COMMENT '评论对应的批注的id',
  `content` varchar(255) DEFAULT NULL COMMENT '评论对应的批注',
  `date` datetime DEFAULT NULL COMMENT '日期',
  `replyid` bigint(20) DEFAULT NULL COMMENT '回复的评论id',
  `type` int(255) DEFAULT NULL COMMENT '0是评论 1是回复评论',
  `userid` bigint(20) DEFAULT NULL COMMENT '写评论的用户信息',
  PRIMARY KEY (`id`),
  KEY `cid` (`cid`),
  KEY `userid` (`userid`),
  CONSTRAINT `t_reply_ibfk_1` FOREIGN KEY (`cid`) REFERENCES `t_comment` (`id`),
  CONSTRAINT `t_reply_ibfk_2` FOREIGN KEY (`userid`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_researchgroup
-- ----------------------------
DROP TABLE IF EXISTS `t_researchgroup`;
CREATE TABLE `t_researchgroup` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `creator_id` bigint(20) DEFAULT NULL,
  `portrait` varchar(255) DEFAULT NULL COMMENT '头像地址',
  `directions` varchar(255) DEFAULT NULL COMMENT '研究方向，不同的研究方向用;隔开',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_schedule
-- ----------------------------
DROP TABLE IF EXISTS `t_schedule`;
CREATE TABLE `t_schedule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` varchar(255) DEFAULT NULL COMMENT '日程内容',
  `date` datetime DEFAULT NULL COMMENT '日程日期',
  `state` int(11) DEFAULT NULL COMMENT '日程状态',
  `user_id` bigint(20) DEFAULT NULL COMMENT '创建者id',
  `group_id` bigint(20) DEFAULT NULL COMMENT '对应研究组的日程',
  `accepter_id` bigint(20) DEFAULT NULL COMMENT '日程的接收者id',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `group_id` (`group_id`),
  KEY `accepter_id` (`accepter_id`),
  CONSTRAINT `accepter_id` FOREIGN KEY (`accepter_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `group_id` FOREIGN KEY (`group_id`) REFERENCES `t_researchgroup` (`id`),
  CONSTRAINT `user_id` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_student
-- ----------------------------
DROP TABLE IF EXISTS `t_student`;
CREATE TABLE `t_student` (
  `uid` bigint(20) NOT NULL AUTO_INCREMENT,
  `default_research_direction` varchar(255) DEFAULT NULL COMMENT '默认研究方向',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_teacher
-- ----------------------------
DROP TABLE IF EXISTS `t_teacher`;
CREATE TABLE `t_teacher` (
  `uid` bigint(20) NOT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `true_name` varchar(255) DEFAULT NULL,
  `portrait` varchar(255) DEFAULT NULL COMMENT '头像地址',
  `user_type` varchar(255) DEFAULT NULL COMMENT '用户类别',
  `group_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_user_group
-- ----------------------------
DROP TABLE IF EXISTS `t_user_group`;
CREATE TABLE `t_user_group` (
  `uid` bigint(20) DEFAULT NULL COMMENT '用户id',
  `gid` bigint(20) DEFAULT NULL COMMENT '研究组id',
  KEY `uid` (`uid`),
  KEY `gid` (`gid`),
  CONSTRAINT `gid` FOREIGN KEY (`gid`) REFERENCES `t_researchgroup` (`id`),
  CONSTRAINT `uid` FOREIGN KEY (`uid`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `t_user_openid`;
CREATE TABLE `t_user_openid` (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                `user_id` bigint(20) NOT NULL COMMENT '用户id',
                                `open_id` varchar(255) DEFAULT NULL COMMENT 'open_id',
                                PRIMARY KEY (`id`),
                                KEY `openid_userid` (`user_id`),
                                CONSTRAINT `openid_userid` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;