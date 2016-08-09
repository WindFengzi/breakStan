put_break_rule_info @ INSERT INTO BR_BREAK_RULE_INFO
           (break_rule_id
           ,node_id
           ,org_id
           ,user_id
           ,camera_id
           ,break_rule_content
           ,pic_name
           ,pic_name_other
           ,pic_time
           ,break_rule_type
           ,update_time
           ,submit_time
           ,Longitude
           ,Latitude)
     VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?);

get_remote_sql_struct @ SELECT     
			a.name,  
			dtype=case  
				when   b.name='int'   then   'int'  
		 when   b.name='date'   then   'date'  
		 when   b.name='datetime'   then   'datetime'  
			when   b.name='varchar'   then   'varchar('+ cast(a.length as varchar(20)) + ')' 
			when   b.name='numeric'   then   'numeric('+ cast(COLUMNPROPERTY(a.id,a.name,'PRECISION') as varchar(20)) + ','+ cast(isnull(COLUMNPROPERTY(a.id,a.name,'Scale'),0) as varchar(20)) + ')'  
			else   ''    
		 	end  
		 FROM   syscolumns   a    
		 left   join   systypes   b   on   a.xtype=b.xusertype    
		 inner   join   sysobjects   d   on   a.id=d.id     and   d.xtype='U'   and     d.name<>'dtproperties'    
		 left   join   syscomments   e   on   a.cdefault=e.id    
		 left   join   sys.extended_properties g   on   a.id=g.major_id   and   a.colid=g.minor_id           
		 left   join   sys.extended_properties f   on   d.id=f.major_id   and   f.minor_id   =0  
		where   d.name='mytablename'         
		order   by   a.id,a.colorder  ;
