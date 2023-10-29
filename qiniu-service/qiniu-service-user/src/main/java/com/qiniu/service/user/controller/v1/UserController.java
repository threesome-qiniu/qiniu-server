package com.qiniu.service.user.controller.v1;

import cn.hutool.core.util.PhoneUtil;
import com.qiniu.common.constant.Constants;
import com.qiniu.common.domain.R;
import com.qiniu.common.exception.CustomException;
import com.qiniu.common.service.RedisCache;
import com.qiniu.common.service.RedisService;
import com.qiniu.common.utils.EmailUtils;
import com.qiniu.common.utils.file.PathUtils;
import com.qiniu.common.utils.string.StringUtils;
import com.qiniu.model.common.enums.HttpCodeEnum;
import com.qiniu.model.user.domain.User;
import com.qiniu.model.user.domain.dto.LoginUserDTO;
import com.qiniu.model.user.domain.dto.RegisterBody;
import com.qiniu.model.user.domain.dto.UpdatePasswordDTO;
import com.qiniu.model.user.domain.dto.UserThreadLocalUtil;
import com.qiniu.service.user.constants.QiniuUserOssConstants;
import com.qiniu.service.user.constants.UserCacheConstants;
import com.qiniu.service.user.service.IUserService;
import com.qiniu.starter.file.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.SystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * UserController
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/24
 **/
@Slf4j
@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    private IUserService userService;

    @Resource
    private RedisService redisService;

    /**
     * 登录
     *
     * @param loginUserDTO
     * @return
     */
    @PostMapping("/login")
    public R<Map<String, String>> login(@RequestBody LoginUserDTO loginUserDTO) {
        log.debug("登录用户：{}", loginUserDTO);
        String token = userService.login(loginUserDTO);
        Map<String, String> map = new HashMap<>();
        map.put(Constants.TOKEN, token);
        return R.ok(map);
    }

    /**
     * 注册
     *
     * @param registerBody
     * @return
     */
    @PostMapping("/register")
    public R<Boolean> register(@RequestBody RegisterBody registerBody) {
        log.debug("注册用户：{}", registerBody);
        boolean b = userService.register(registerBody);
        return R.ok(b);
    }

    /**
     * 更新用户信息
     *
     * @param user
     * @return
     */
    @PutMapping("/update")
    public R<User> save(@RequestBody User user) {
        // 校验邮箱
        if (StringUtils.isNotEmpty(user.getEmail()) && !EmailUtils.isValidEmail(user.getEmail())) {
            throw new CustomException(HttpCodeEnum.EMAIL_VALID_ERROR);
        }
        // 校验手机号
        if (StringUtils.isNotEmpty(user.getTelephone()) && !PhoneUtil.isPhone(user.getTelephone())) {
            throw new CustomException(HttpCodeEnum.TELEPHONE_VALID_ERROR);
        }
        return R.ok(userService.updateUserInfo(user));
    }

    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    @GetMapping("/{userId}")
    public R<User> userInfoById(@PathVariable Long userId) {
        User user = userService.queryById(userId);
        user.setPassword(null);
        user.setSalt(null);
        return R.ok(user);
    }

    /**
     * 通过token获取用户信息
     *
     * @return
     */
    @GetMapping("/userinfo")
    public R<User> userInfo() {
        Long userId = UserThreadLocalUtil.getUser().getUserId();
        if (StringUtils.isNull(userId)) {
            R.fail(HttpCodeEnum.NEED_LOGIN.getCode(), "请先登录");
        }
        User userCache = redisService.getCacheObject(UserCacheConstants.USER_INFO_PREFIX + userId);
        if (StringUtils.isNotNull(userCache)) {
            return R.ok(userCache);
        }
        User user = userService.queryById(userId);
        user.setPassword(null);
        user.setSalt(null);
        // 设置缓存
        redisService.setCacheObject(UserCacheConstants.USER_INFO_PREFIX + userId, user);
        redisService.expire(UserCacheConstants.USER_INFO_PREFIX + userId, UserCacheConstants.USER_INFO_EXPIRE_TIME, TimeUnit.SECONDS);
        return R.ok(user);
    }

    /**
     * 修改密码
     *
     * @param dto
     * @return
     */
    @PostMapping("/updatepass")
    public R<?> updatePass(@RequestBody UpdatePasswordDTO dto) {
        return R.ok(userService.updatePass(dto));
    }


    @Resource
    private FileStorageService fileStorageService;

    @PostMapping("/avatar")
    public R<String> avatar(@RequestParam("file") MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        //对原始文件名进行判断
        if (originalFilename.endsWith(".png")
                || originalFilename.endsWith(".jpg")
                || originalFilename.endsWith(".jpeg")
                || originalFilename.endsWith(".webp")) {
            String filePath = PathUtils.generateFilePath(originalFilename,file);
            String url = fileStorageService.uploadImgFile(file, QiniuUserOssConstants.PREFIX_URL, filePath);
            return R.ok(url);
        } else {
            throw new CustomException(HttpCodeEnum.FILE_TYPE_ERROR);
        }
    }

}
