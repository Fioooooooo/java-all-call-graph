CREATE TABLE if not exists jacg_lambda_method_info_{appName} (
  call_id int NOT NULL COMMENT '方法调用序号，从1开始',
  lambda_callee_class_name varchar(255) NOT NULL COMMENT 'Lambda表达式被调用方类名',
  lambda_callee_method_name varchar(200) NOT NULL COMMENT 'Lambda表达式被调用方方法名',
  lambda_callee_full_method text NOT NULL COMMENT 'Lambda表达式被调用方完整方法（类名+方法名+参数）',
  lambda_next_class_name varchar(255) DEFAULT NULL COMMENT 'Lambda表达式下一个被调用类名',
  lambda_next_method_name varchar(200) DEFAULT NULL COMMENT 'Lambda表达式下一个被调用方法名',
  lambda_next_full_method text DEFAULT NULL COMMENT 'Lambda表达式下一个被调用完整方法（类名+方法名+参数）',
  lambda_next_is_stream tinyint DEFAULT NULL COMMENT '下一个被调用方法是否为Stream，1:是，0:否',
  lambda_next_is_intermediate tinyint DEFAULT NULL COMMENT '下一个被调用方法是否为Stream的intermediate（中间）操作，1:是，0:否',
  lambda_next_is_terminal tinyint DEFAULT NULL COMMENT '下一个被调用方法是否为Stream的terminal（终端）操作，1:是，0:否',
  PRIMARY KEY (call_id),
  INDEX idx_lmi_ecn_{appName}(lambda_callee_class_name),
  INDEX idx_lmi_ncn_{appName}(lambda_next_class_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Lambda表达式方法信息表';