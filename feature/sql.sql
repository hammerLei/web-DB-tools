-- 用户表
create table database_user_info
(
  id                        int auto_increment
    primary key,
  account                   varchar(20) default ''             null
  comment '用户名',
  password                  varchar(100) default ''            null
  comment '密码',
  last_login_time           datetime default CURRENT_TIMESTAMP null,
  create_time               datetime default CURRENT_TIMESTAMP null,
  update_time               datetime default CURRENT_TIMESTAMP null,
  level                     int(4) default '0'                 null,
  last_login_ip             varchar(40) default ''             null
  comment '最后登录的机器ip',
  last_update_password_time datetime default CURRENT_TIMESTAMP null
  comment '最后一次更新密码时间',
  version                   int default '0'                    null
  comment '修改版本,亦可用于乐观锁',
  email                     varchar(40) default ''             null
  comment '个人邮箱',
  online_level              int(4) default '0'                 null,
  status                    int(4) default '1'                 null,
  default_schema            varchar(20) default 'db_test1'    null,
  after_login_redirect      varchar(50)                        null,
  constraint database_user_info_account_uindex
  unique (account)
)
  charset = latin1;


-- 用户SQL收藏表
create table database_user_favorite
(
  id                  int auto_increment
    primary key,
  user_id             int default '0'                    null
  comment 'database_user_info表对应的id',
  favorite_sql_detail varchar(5000) default ''           null,
  name                varchar(100) default ''            null
  comment '给收藏sql取个名字吧',
  create_time         datetime default CURRENT_TIMESTAMP null
  comment '创建时间',
  update_time         datetime default CURRENT_TIMESTAMP null
  comment '更新时间',
  delete_flag         int(2) default '0'                 null
  comment '是否删除,0:否，1：是',
  client_ip           varchar(40) default ''             null
  comment '客户端ip',
  order_sort          int(4) default '0'                 null
  comment '排序'
)
  charset = utf8;


-- sql执行历史表
create table database_user_sql_history
(
  id          bigint auto_increment
    primary key,
  user_id     int default '0'                    null
  comment 'database_user_info表对应的id',
  sql_detail  varchar(2000) default ''           null
  comment '执行的SQL详情',
  create_time datetime default CURRENT_TIMESTAMP null
  comment '记录创建时间即执行SQL时间',
  client_ip   varchar(40) default ''             null
  comment '执行sql时登录的客户端ip',
  server_ip   varchar(40) default ''             null
  comment '执行SQL的服务端ip',
  env         varchar(20) default ''             null
  comment '执行的数据库环境：test、stage、production'
)
  charset = utf8;

create index user_id
  on database_user_sql_history (user_id);


-- 菜单表
create table db_tools_menus
(
  id           int(8) auto_increment
    primary key,
  title        varchar(30)                        null
  comment '菜单名',
  name         varchar(30)                        null
  comment '菜单名',
  page_url     varchar(50)                        null
  comment 'url地址，完整url或者uri，uri为本站域名地址',
  icon         varchar(50)                        null
  comment '菜单图标',
  open_type    int(2) default '1'                 null
  comment '1:弹框打开本域名内地址，2:弹框ifream打开',
  extend       tinyint(1) default '0'             null
  comment '是否可以展开',
  parent_id    int(8) default '0'                 null
  comment '父菜单id',
  max_open     int(2) default '-1'                null,
  is_desk      tinyint(1) default '0'             null
  comment '是否放置在桌面',
  user_id      int(8) default '-1'                null
  comment '用户自定义添加菜单用户id',
  is_necessary tinyint(1) default '0'             null
  comment '是否是系统菜单',
  is_public    tinyint(2) default '0'             null
  comment '是否公共菜单',
  create_time  datetime default CURRENT_TIMESTAMP null,
  update_time  datetime default CURRENT_TIMESTAMP null,
  client_ip    varchar(50)                        null
  comment '客户端ip',
  sort         int(4) default '10000'             null
)
  comment '数据库工具菜单表'
  charset = utf8;

-- 菜单用户信息关联表
create table menu_auth_manage
(
  id          int(8) auto_increment
    primary key,
  menu_id     int(8)                             not null
  comment 'db_tools_menus表中id',
  group_id    int(8)                             not null
  comment 'role_group表中id',
  create_time datetime default CURRENT_TIMESTAMP null
)
  comment '菜单对应用户组表';


-- 角色组表
create table role_group
(
  id             int(6) auto_increment
    primary key,
  role_name      varchar(50)                        not null
  comment '用户组名',
  description    varchar(200)                       null
  comment '用户组描述',
  create_date    datetime default CURRENT_TIMESTAMP null,
  last_oper_time datetime default CURRENT_TIMESTAMP null,
  data_state     tinyint(1) default '1'             null
  comment '状态'
)
  charset = utf8;

-- 用户角色组关联关系表
create table user_group
(
  id          int(8) auto_increment
    primary key,
  user_id     int(8)                             not null
  comment '用户表中的id',
  group_id    int(8)                             not null
  comment 'role_group表中的id',
  create_time datetime default CURRENT_TIMESTAMP null
)
  comment '用户组对应关系表';






