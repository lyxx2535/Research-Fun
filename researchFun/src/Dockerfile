# Docker image for springboot file run
# VERSION 0.0.1
# 基础镜像使用java
FROM openjdk:8

MAINTAINER lqf <201830168@smail.nju.edu.cn>

# VOLUME 指定了临时文件目录为/tmp。
# 其效果是在主机 /var/lib/docker 目录下创建了一个临时文件，并链接到容器的/tmp
# 设置挂载数据卷


# 将jar包添加到容器中
ADD researchFun.jar /researchFun.jar

# 在构建的容器内部里新建文件夹
RUN /bin/sh -c 'mkdir /usr/local/researchfun'
RUN /bin/sh -c 'mkdir /usr/local/researchfun/resources'
RUN /bin/sh -c 'mkdir /usr/local/researchfun/resources/portrait'
RUN /bin/sh -c 'mkdir /usr/local/researchfun/resources/document'
RUN /bin/sh -c 'mkdir /usr/local/researchfun/resources/comment-img'
RUN /bin/sh -c 'mkdir /usr/local/researchfun/resources/voice'
# 修改权限 可读可写可执行
RUN /bin/sh -c 'chmod -R 777 /usr/local/researchfun'

# 先创建文件 再挂载
# 手动挂载
# VOLUME /usr/local/reseachfun/resources

#EXPOSE 9000

# 运行
#ENTRYPOINT ["nohup", "java","-jar","/researchFun.jar", "&"]
ENTRYPOINT ["java","-jar","/researchFun.jar"]