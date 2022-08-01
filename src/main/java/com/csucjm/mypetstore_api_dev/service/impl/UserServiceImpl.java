package com.csucjm.mypetstore_api_dev.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.csucjm.mypetstore_api_dev.common.CONSTANT;
import com.csucjm.mypetstore_api_dev.common.CommonResponse;
import com.csucjm.mypetstore_api_dev.entity.User;
import com.csucjm.mypetstore_api_dev.persistence.UserMapper;
import com.csucjm.mypetstore_api_dev.service.UserService;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.UUID;

@Service("userService")
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    UserMapper userMapper;

    @Resource
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Resource
    Cache<String, String> localCache;


    @Override
    public CommonResponse<User> login(String username, String password) {
        User loginUser = userMapper.selectOne(Wrappers.<User>query().eq("username", username));
        if(loginUser == null) {
            return CommonResponse.createForError("用户名或者密码错误");
        }
        boolean checkPassword = bCryptPasswordEncoder.matches(password, loginUser.getPassword());
        loginUser.setPassword(StringUtils.EMPTY);

        return checkPassword ? CommonResponse.createForSuccess(loginUser) : CommonResponse.createForError("密码错误");
    }

    @Override
    public CommonResponse<Object> checkField(String fieldName, String fieldValue) {
        if(StringUtils.equals(fieldName, "username")) {
            long row = userMapper.selectCount(Wrappers.<User>query().eq("username", fieldValue));
            if(row > 0) {
                return CommonResponse.createForError("用户名已存在");
            }
        }
        else if(StringUtils.equals(fieldName, "phone")) {
            long rows = userMapper.selectCount(Wrappers.<User>query().eq("phone", fieldValue));
            if(rows > 0) {
                return CommonResponse.createForError("电话号码已存在");
            }
        }
        else if(StringUtils.equals(fieldName, "email")) {
            long rows = userMapper.selectCount(Wrappers.<User>query().eq("email", fieldValue));
            if(rows > 0) {
                return CommonResponse.createForError("邮箱已存在");
            }
        }
        else {
            return CommonResponse.createForError("参数错误");
        }
        return CommonResponse.createForSuccess();
    }

    @Override
    public CommonResponse<Object> register(User user) {
        CommonResponse<Object> checkResult = checkField("username", user.getUsername());
        if(!checkResult.isSuccess()) {
            return checkResult;
        }
        checkResult = checkField("email", user.getEmail());
        if(!checkResult.isSuccess()) {
            return checkResult;
        }
        checkResult = checkField("phone", user.getPhone());
        if(!checkResult.isSuccess()) {
            return checkResult;
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        user.setRole(CONSTANT.ROLE.CUSTOMER);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        int rows = userMapper.insert(user);
        if(rows == 0) {
            return CommonResponse.createForError("注册用户失败");
        }
        return CommonResponse.createForSuccess();
    }

    @Override
    public CommonResponse<String> getForgetQuestion(String username) {
        CommonResponse<Object> checkResult = checkField("username", username);
        if(checkResult.isSuccess()) {
            return CommonResponse.createForError("该用户不存在");
        }

        String question = userMapper.selectOne(Wrappers.<User>query().eq("username", username)).getQuestion();
        if(StringUtils.isNoneBlank(question)) {
            return CommonResponse.createForSuccess(question);
        }
        return CommonResponse.createForError("密保问题为空");
    }

    @Override
    public CommonResponse<String> checkForgetAnswer(String username, String question, String answer) {
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper
                .eq("username", username)
                .eq("question", question)
                .eq("answer", answer);
        long rows = userMapper.selectCount(queryWrapper);
        if(rows > 0) {
            String forgetToken = UUID.randomUUID().toString();
            localCache.put(username, forgetToken);

            log.info("Put into localCache: ({},{}), {}", username, forgetToken, LocalDateTime.now());

            return CommonResponse.createForSuccess(forgetToken);
        }
        return CommonResponse.createForError("找回密码的问题答案错误");
    }

    @Override
    public CommonResponse<Object> resetForgetPassword(String username, String newPassword, String forgetToken) {
        CommonResponse<Object> checkResult = checkField("username", username);
        if(checkResult.isSuccess()) {
            return CommonResponse.createForError("用户名不存在");
        }

        String token = localCache.getIfPresent(username);

        log.info("Get token from localCache: ({},{})", username, token);

        if(StringUtils.isBlank(token)) {
            return CommonResponse.createForError("token无效或者已过期");
        }
        if(StringUtils.equals(token, forgetToken)) {
            String md5_Password = bCryptPasswordEncoder.encode(newPassword);

            User user=new User();
            user.setUsername(username);
            user.setPassword(md5_Password);

            UpdateWrapper<User> userUpdateWrapper=new UpdateWrapper<>();
            userUpdateWrapper.eq("username", user.getUsername());
            userUpdateWrapper.set("password", user.getPassword());
            int rows = userMapper.update(user, userUpdateWrapper);

            if(rows > 0) {
                return CommonResponse.createForSuccess();
            }
            return CommonResponse.createForError("重置密码出错，请重试或重新获取token");
        }
        else {
            return CommonResponse.createForError("token错误，请重新获取token");
        }
    }

    @Override
    public CommonResponse<User> getUserDetail(Integer userId) {
        User user = userMapper.selectById(userId);
        if(user == null) {
            return CommonResponse.createForError("查询当前用户信息失败");
        }
        user.setPassword(StringUtils.EMPTY);
        return CommonResponse.createForSuccess(user);
    }

    @Override
    public CommonResponse<Object> resetPassword(String oldPassword, String newPassword, User user) {
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper
                .eq("id", user.getId())
                .eq("password", bCryptPasswordEncoder.encode(oldPassword));
        long rows = userMapper.selectCount(queryWrapper);
        if(rows==0) {
            return CommonResponse.createForError("旧密码错误");
        }

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", user.getId());
        updateWrapper.set("password", user.getPassword());

        rows = userMapper.update(user, updateWrapper);
        if(rows > 0) {
            return CommonResponse.createForSuccess();
        }
        return CommonResponse.createForError("密码更新失败");
    }

    @Override
    public CommonResponse<Object> updateUserInfo(User user) {

        CommonResponse<Object> checkResult = checkField("email", user.getEmail());
        if(!checkResult.isSuccess()){
            return checkResult;
        }

        checkResult = checkField("phone", user.getPhone());
        if(!checkResult.isSuccess()){
            return checkResult;
        }

        user.setUpdateTime(LocalDateTime.now());

        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id",user.getId());
        updateWrapper.set("email",user.getEmail());
        updateWrapper.set("phone", user.getPhone());
        updateWrapper.set("question", user.getQuestion());
        updateWrapper.set("answer", user.getAnswer());
        updateWrapper.set("update_time", user.getUpdateTime());
        long rows = userMapper.update(user,updateWrapper);

        if(rows > 0){
            return CommonResponse.createForSuccess();
        }
        return CommonResponse.createForError("更新用户信息失败");
    }
}
