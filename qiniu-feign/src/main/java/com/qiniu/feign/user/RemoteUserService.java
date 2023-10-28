//package com.qiniu.feign.user;
//
//import com.qiniu.common.constant.ServiceNameConstants;
//import com.qiniu.common.domain.R;
//import com.qiniu.feign.user.fallback.RemoteUserServiceFallback;
//import com.qiniu.model.user.domain.dto.RegisterBody;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//
///**
// * RemoteUserService
// *
// * @AUTHOR: roydon
// * @DATE: 2023/10/27
// **/
//@FeignClient(contextId = "remoteUserService", value = ServiceNameConstants.USER_SERVICE, fallbackFactory = RemoteUserServiceFallback.class)
//public interface RemoteUserService {
//
//    /**
//     * 用户注册
//     *
//     * @param registerBody
//     * @return
//     */
//    @PostMapping("/user/register")
//    R<Boolean> register(@RequestBody RegisterBody registerBody);
//}
