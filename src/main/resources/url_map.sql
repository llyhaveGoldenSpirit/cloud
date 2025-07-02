# 短链项目建表语句
create table url_map(
                        id int(11) unsigned NOT NULL AUTO_INCREMENT,
                        long_url varchar(250) DEFAULT NULL COMMENT '长链',
                        short_url varchar(10) DEFAULT NULL COMMENT '短链',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        UNIQUE INDEX idx_long_url_short_url(long_url,short_url),
                        INDEX idx_short_url_long_url(short_url,long_url),
                        PRIMARY KEY(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8