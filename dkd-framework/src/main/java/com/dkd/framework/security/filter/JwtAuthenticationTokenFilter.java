package com.dkd.framework.security.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.dkd.common.core.domain.model.LoginUser;
import com.dkd.common.utils.SecurityUtils;
import com.dkd.common.utils.StringUtils;
import com.dkd.framework.web.service.TokenService;

/**
 * token过滤器 验证token有效性
 *
 * @author ruoyi
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // 从请求中获取登录用户信息
        LoginUser loginUser = tokenService.getLoginUser(request);
        // 如果登录用户不为空且当前安全上下文中没有认证信息，则进行后续处理
        if (StringUtils.isNotNull(loginUser) && StringUtils.isNull(SecurityUtils.getAuthentication())) {
            // 验证登录用户的token是否有效
            tokenService.verifyToken(loginUser);
            // 创建一个UsernamePasswordAuthenticationToken对象，设置用户信息和权限
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            // 设置认证信息的详细信息
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // 将认证信息设置到安全上下文中
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        // 继续执行过滤链中的下一个过滤器
        chain.doFilter(request, response);
    }
}
