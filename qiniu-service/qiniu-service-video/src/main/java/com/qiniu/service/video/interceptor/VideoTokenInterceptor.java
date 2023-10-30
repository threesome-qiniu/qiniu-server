package com.qiniu.service.video.interceptor;

import com.qiniu.model.user.domain.User;
import com.qiniu.model.user.dto.UserThreadLocalUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 功能：
 * 作者：lzq
 * 日期：2023/10/29 17:12
 */
public class VideoTokenInterceptor implements HandlerInterceptor {

    /**
     * 得到header中的用户信息，并且存入到当前线程中
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("userId");
        if (userId != null) {
            //存入到当前线程中
            User user = new User();
            user.setUserId(Long.valueOf(userId));
            UserThreadLocalUtil.setUser(user);
        }
        return true;
    }

    /**
     * 清理线程中的数据
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserThreadLocalUtil.clear();
    }
}