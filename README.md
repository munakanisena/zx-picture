# 惠眸图界

[![CI-Server](https://github.com/munakanisena/zx-picture/workflows/CI-Server/badge.svg)](https://github.com/munakanisena/zx-picture/actions/workflows/CI-Server.yml)
[![CI-Web](https://github.com/munakanisena/zx-picture/workflows/CI-Web/badge.svg)](https://github.com/munakanisena/zx-picture/actions/workflows/CI-Web.yml)


> [!WARNING]
>
> 注意：本项目主要是个人学习开发和部署使用。很多功能无法保证，并且代码写的挺烂的。仅做个人学习使用。

## 部署

假如你想部署试一试我这烂项目的话。

下载项目：

```bash
> git clone https://github.com/munakanisena/zx-picture.git
> cd zx-picture
```

在项目根目录(docker-compose.yml文件同级)创建并编辑 `.env` 文件，内容如下:

```bash
#mysql数据库配置
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/zx_picture?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=123456

#redis 配置
SPRING_REDIS_HOST=redis
SPRING_REDIS_PASSWORD=123456
REDIS_PASSWORD=123456

SPRING_MAIL_USERNAME= 	#发送的邮件账号
SPRING_MAIL_PASSWORD= 	#授权码

#腾讯云 对象存储配置
COS_CLIENT_SECRET_ID=
COS_CLIENT_SECRET_KEY=
COS_CLIENT_BUCKET_NAME=
COS_CLIENT_HOST=
COS_CLIENT_REGION=

ALIYUN_BAILIAN_SERET_KEY=	#阿里云百炼扩图

#mysql配置
MYSQL_ROOT_PASSWORD=123456
MYSQL_DATABASE=zx_picture

#注: SPRING_REDIS_PASSWORD和REDIS_PASSWORD密码一致.MYSQL_ROOT_PASSWORD和SPRING_DATASOURCE_PASSWORD需要一致
```

打开 `docker-compose.yml` 文件，酌情修改。

运行 `docker compose up [-d]` (`-d` 为后台运行)。