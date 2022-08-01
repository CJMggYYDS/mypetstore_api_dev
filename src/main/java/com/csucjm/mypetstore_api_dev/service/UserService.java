package com.csucjm.mypetstore_api_dev.service;

import com.csucjm.mypetstore_api_dev.common.CommonResponse;
import com.csucjm.mypetstore_api_dev.entity.User;

public interface UserService {
    CommonResponse<User> login(String username, String password);

    CommonResponse<Object> checkField(String fieldName, String fieldValue);

    CommonResponse<Object> register(User user);

    CommonResponse<String> getForgetQuestion(String username);

    CommonResponse<String> checkForgetAnswer(String username, String question, String answer);

    CommonResponse<Object> resetForgetPassword(String username, String newPassword, String forgetToken);

    CommonResponse<User> getUserDetail(Integer userId);

    CommonResponse<Object> resetPassword(String oldPassword, String newPassword, User user);

    CommonResponse<Object> updateUserInfo(User user);
}
