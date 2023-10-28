//package com.qiniu.feign.user.fallback;
//
//import com.qiniu.common.domain.R;
//import com.qiniu.feign.user.RemoteUserService;
//import com.qiniu.model.user.domain.dto.RegisterBody;
//import org.springframework.cloud.openfeign.FallbackFactory;
//import org.springframework.stereotype.Component;
//
//@Component
//public class RemoteUserServiceFallback implements FallbackFactory<RemoteUserService> {
//    @Override
//    public RemoteUserService create(Throwable cause) {
//        return new RemoteUserService() {
//            @Override
//            public R<Boolean> register(RegisterBody registerBody) {
//                return R.fail("注册用户失败:" + cause.getMessage());
//            }
//        };
//    }
//}
