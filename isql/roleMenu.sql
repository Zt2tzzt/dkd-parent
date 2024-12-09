-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('工单角色', '2019', '1', 'role', 'manage/role/index', 1, 0, 'C', '0', '0', 'manage:role:list', '#', 'admin', sysdate(), '', null, '工单角色菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('工单角色查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'manage:role:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('工单角色新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'manage:role:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('工单角色修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'manage:role:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('工单角色删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'manage:role:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('工单角色导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'manage:role:export',       '#', 'admin', sysdate(), '', null, '');