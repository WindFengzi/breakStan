Create table  if not exists t_sys_setting
(
	 set_group varchar(40) ,
	 set_name  varchar(100),
     set_value varchar(100),
	 is_enable int,
	 PRIMARY KEY (set_group ,set_name)  
);

Create table  if not exists t_save_break_law
(
	
	 prop_id varchar(40) ,
	 option_id  varchar(100),
     project_id varchar(100),
	 break_law_desc	 int 
);



