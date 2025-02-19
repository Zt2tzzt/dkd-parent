package com.dkd.web.controller.system;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.dkd.common.constant.Constants;
import com.dkd.common.core.domain.AjaxResult;
import com.dkd.common.core.domain.entity.SysMenu;
import com.dkd.common.core.domain.entity.SysUser;
import com.dkd.common.core.domain.model.LoginBody;
import com.dkd.common.utils.SecurityUtils;
import com.dkd.framework.web.service.SysLoginService;
import com.dkd.framework.web.service.SysPermissionService;
import com.dkd.system.service.ISysMenuService;

/**
 * 登录验证
 *
 * @author ruoyi
 */
@RestController
public class SysLoginController {
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody) {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo() {
        // 获取当前登录用户信息
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        // 创建一个成功的AjaxResult对象
        AjaxResult ajax = AjaxResult.success();
        // 将用户信息、角色和权限放入AjaxResult对象中
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        // 返回包含用户信息、角色和权限的AjaxResult对象
        return ajax;
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters() {
        // 获取当前用户的ID
        Long userId = SecurityUtils.getUserId();
        // 根据用户ID查询该用户有权限访问的菜单树
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        // 构建菜单，转为前端所需要的路由格式，并返回成功的结果
        return AjaxResult.success(menuService.buildMenus(menus));
    }
}
