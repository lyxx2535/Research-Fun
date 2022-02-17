# Docker image for springboot file run
# VERSION 0.0.1
# 基础镜像使用java
FROM openjdk:8

# VOLUME 指定了临时文件目录为/tmp。
# 其效果是在主机 /var/lib/docker 目录下创建了一个临时文件，并链接到容器的/tmp
VOLUME /tmp

# 将jar包添加到容器中并更名为rgms.jar和download-file.jar
ADD rgms-0.0.1.jar /rgms.jar

RUN /bin/sh -c 'mkdir /usr/local/rgms-resources'
RUN /bin/sh -c 'mkdir /usr/local/rgms-resources/portrait'
RUN /bin/sh -c 'mkdir /usr/local/rgms-resources/document'
RUN /bin/sh -c 'mkdir /usr/local/rgms-resources/comment-img'
RUN /bin/sh -c 'chmod -R 777 /usr/local/rgms-resources'

# 运行
ENTRYPOINT ["nohup", "java","-jar","/rgms.jar", "&"]
