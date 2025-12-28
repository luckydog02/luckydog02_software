# SpringBoot+Vue 医疗系统项目服务器部署配置文档

# 一、部署环境说明

## 1. 服务器信息

- 服务器公网IP：47.122.117.145（阿里云服务器）

- 操作系统：Linux

- 配置：2核4G内存

- 登录账号：root

- 初始密码：123456

## 2. 所需软件及版本

|软件名称|版本要求|用途|
|---|---|---|
|JDK|1.8|运行SpringBoot后端|
|Nginx|1.24.0+|部署Vue前端、反向代理|
|MySQL|5.7+|数据库存储（兼容TDSQL-C MySQL）|
|Xshell|8.0+|服务器远程连接|
|Xftp|8.0+|本地文件上传至服务器|


- Xshell/Xftp免费版：从官方地址下载（https://www.xshell.com/zh/free-for-home-school/），仅支持非商业用途

# 二、部署前准备

## 1. 本地项目配置修改

### （1）Vue前端配置

1. 项目根目录新建环境配置文件
       

    - 开发环境：`.env.developmentVUE_APP_BASEURL='http://localhost:9281'`

    - 生产环境：`.env.productionVUE_APP_BASEURL='http://47.122.117.145:9281'`

2. 全局变量配置（main.js）
        `Vue.prototype.$baseUrl = process.env.VUE_APP_BASEURL`

3. 请求封装修改（request.js）
       `const request = axios.create({
    baseURL: process.env.VUE_APP_BASEURL, // 关联环境变量
    timeout: 30000
})`

4. 替换全局接口地址：项目中全局搜索 `localhost:9281`，替换为`$baseUrl`
        

5. 登录校验增强（Manager.vue）：防止未登录访问首页
        `mounted() {
  if (!this.user.id) { // 无用户信息时跳转登录页
    this.$router.push('/login')
  }
},`

6. 打包前端项目：执行命令生成dist文件夹
        `npm run build`

### （2）SpringBoot后端配置

1. 打包项目：使用Maven命令生成jar包
        `mvn clean package`

2. 配置文件修改（application.yml）：将数据库连接、服务端口等配置更新为服务器信息
       `server:
  port: 9281 # 保持端口一致
spring:
  datasource:
    url: jdbc:mysql://47.122.117.145:3306/DB_Hospital?useSSL=false&characterEncoding=utf8
    username: root
    password: 123456 # 后续数据库配置的密码`

## 2. 服务器远程连接

1. 打开Xshell，创建新会话，配置如下：
        

    - 主机：47.122.117.145

    - 端口：22（默认SSH端口）

    - 用户名：root

    - 密码：123456

2. 连接成功后，通过Xftp上传以下文件至服务器：
        

    - 前端dist文件夹

    - 后端jar包

    - 软件安装包（jdk-8u371-linux-x64.tar.gz、nginx-1.24.0.tar.gz、mysql-5.7.42-el7-x86_64.tar.gz）上传至/tmp目录

    - 数据库脚本（DB_hospital.sql）

# 三、服务器环境搭建

## 1. 安装JDK 1.8

1. 解压安装包至指定目录
        `tar -zxvf /tmp/jdk-8u371-linux-x64.tar.gz -C /usr/local/
mv /usr/local/jdk1.8.0_371 /usr/local/java`

2. 配置环境变量：编辑/etc/profile文件
        `vi /etc/profile`

3. 在文件末尾添加以下内容
        `export JAVA_HOME=/usr/local/java
export PATH=$JAVA_HOME/bin:$PATH`

4. 生效配置并验证
        `source /etc/profile
java -version # 显示jdk版本即安装成功`

## 2. 安装Nginx

1. 安装依赖库
        `yum install gcc-c++ -y
yum install -y pcre pcre-devel
yum install -y zlib zlib-devel
yum install -y openssl openssl-devel`

2. 解压并编译安装
        `mkdir /usr/local/nginx
tar -zxvf /tmp/nginx-1.24.0.tar.gz -C /usr/local/nginx
cd /usr/local/nginx/nginx-1.24.0
./configure --with-http_stub_status_module --with-http_ssl_module
make && make install`

3. Nginx基本命令（目录：/usr/local/nginx/sbin）
        `./nginx # 启动
./nginx -s stop # 停止
./nginx -s reload # 重启
ps -ef | grep nginx # 查看运行状态`

## 3. 安装MySQL 5.7

1. 卸载系统自带MariaDB
        `rpm -qa | grep mariadb # 查看已安装的MariaDB
yum remove -y 【查询结果中的MariaDB包名】 # 卸载所有相关包`

2. 解压并配置MySQL
        `mkdir /data/mysql # 创建数据存储目录
tar -zxvf /tmp/mysql-5.7.42-el7-x86_64.tar.gz -C /usr/local
mv /usr/local/mysql-5.7.42-el7-x86_64 /usr/local/mysql`

3. 创建用户组并授权
        `groupadd mysql
useradd -r -g mysql mysql
chown -R mysql.mysql /usr/local/mysql
chown -R mysql.mysql /data/mysql`

4. 初始化MySQL并记录临时密码
        `cd /usr/local/mysql
/usr/local/mysql/bin/mysqld --user=mysql --basedir=/usr/local/mysql/ --datadir=/data/mysql --initialize`> 注意：记录输出日志中的临时密码（如：osGMitUO<56.）

5. 配置MySQL服务
       `cp /usr/local/mysql/support-files/mysql.server /etc/init.d/mysql
ln -s /usr/local/mysql/bin/mysql /usr/bin # 配置快捷命令
ln -s /usr/lib64/libtinfo.so.6.1 /usr/lib64/libtinfo.so.5 # 兼容依赖
ln -s /usr/lib64/libncurses.so.6.1 /usr/lib64/libncurses.so.5`

6. 编辑MySQL配置文件（/etc/my.cnf）
        `vi /etc/my.cnf`配置内容如下：`[mysqld]
datadir=/data/mysql
basedir=/usr/local/mysql
socket=/tmp/mysql.sock
user=mysql
port=3306
character-set-server=utf8
symbolic-links=0
[mysqld_safe]
log-error=/var/log/mysqld.log
pid-file=/var/run/mysqld/mysqld.pid`

7. 启动MySQL并设置开机自启
        `service mysql start # 启动服务
        chkconfig mysql on # 开机自启`

8. 登录MySQL并修改密码、配置权限
        `mysql -uroot -p # 输入临时密码登录`执行以下SQL命令：`SET PASSWORD = PASSWORD('123456'); # 修改密码为123456
use mysql;
update user set host = '%' where user = 'root' and host = 'localhost'; # 允许远程连接
flush privileges; # 刷新权限
exit; # 退出`

9. 导入数据库脚本：使用Navicat远程连接服务器MySQL（主机：47.122.117.145，端口3306，用户名root，密码123456），创建DB_hospital数据库并导入脚本

# 四、项目部署

## 1. 后端部署（SpringBoot）

1. 创建服务目录
        `mkdir -p /home/server/hospital/java`

2. 通过Xftp将后端jar包上传至`/home/server/hospital/java`目录

3. 创建启动脚本（start.sh）
        `cd /home/server/hospital/java
vi start.sh`脚本内容：`NAME=springboot-0.0.1-SNAPSHOT.jar # 替换为实际jar包名称
nohup java -jar $NAME > server.log 2>&1 &
echo 'start success'`

4. 创建停止脚本（stop.sh）
        `vi stop.sh`脚本内容：`PORT=9281
pid=`netstat -tnlp | grep $PORT | grep -v grep | awk '{print $7}' | awk -F/ '{print $1}'`
if [ ${pid} ]; then
    kill -9 $pid
    echo "kill $PORT"
else
    echo 'stop success!'
fi`

5. 脚本赋权并去除特殊符号
        `chmod +x *
sed -i 's/\r//' start.sh
sed -i 's/\r//' stop.sh`

6. 启动后端服务并查看日志
        `./start.sh
tail -100f server.log # 实时查看启动日志，无报错即启动成功`

## 2. 前端部署（Vue）

1. 创建前端目录
        `mkdir -p /home/server/hospital/vue`

2. 通过Xftp将dist文件夹上传至`/home/server/hospital/vue`目录

3. 配置Nginx：编辑`/usr/local/nginx/conf/nginx.confvi /usr/local/nginx/conf/nginx.conf`

4. 修改server配置块：
        `server {
    listen       80;
    server_name  47.122.117.145; # 服务器公网IP

    location / {
        root   /home/server/hospital/vue/dist; # 前端dist目录路径
        index  index.html index.htm;
        try_files $uri $uri/ /index.html; # 解决Vue路由刷新404问题
    }

}`

5. 重启Nginx使配置生效
        `/usr/local/nginx/sbin/nginx -s reload`

# 五、防火墙配置

开放80端口（Nginx）、9281端口（后端服务）、3306端口（MySQL）


# 六、验证部署

1. 前端访问：打开浏览器输入 `http://47.122.117.145`，可看到系统登录页

2. 后端接口验证：访问 `http://47.122.117.145:9281/xxx`（替换为实际接口），返回正常响应

3. 数据库连接验证：通过Navicat成功连接服务器MySQL，可查看DB_hospital数据库表结构及数据

4. 系统功能验证：注册账号并登录，测试挂号、排班查询、订单提交等核心功能

# 七、常用维护命令

|操作场景|命令|
|---|---|
|启动后端|/home/server/hospital/java/start.sh|
|停止后端|/home/server/hospital/java/stop.sh|
|查看后端日志|tail -100f /home/server/hospital/java/server.log|
|重启Nginx|/usr/local/nginx/sbin/nginx -s reload|
|启动MySQL|service mysql start|
|停止MySQL|service mysql stop|
|查看服务器端口占用|netstat -tnlp|
# 八、注意事项

1. 服务器安全：定期更新系统补丁，修改默认密码，限制SSH登录IP（可选）

2. 数据备份：定期备份MySQL数据库，避免数据丢失

3. 日志管理：定期清理后端日志和Nginx日志，防止磁盘空间占满

4. 资源监控：通过阿里云控制台查看服务器CPU、内存、带宽使用情况，按需扩容


