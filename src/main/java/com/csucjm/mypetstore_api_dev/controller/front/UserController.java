package com.csucjm.mypetstore_api_dev.controller.front;

import com.csucjm.mypetstore_api_dev.common.CONSTANT;
import com.csucjm.mypetstore_api_dev.common.CommonResponse;
import com.csucjm.mypetstore_api_dev.entity.User;
import com.csucjm.mypetstore_api_dev.dto.UpdateUserDTO;
import com.csucjm.mypetstore_api_dev.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public CommonResponse<User> login(@RequestBody Map<String, String> params, HttpSession session) {
        String username = params.get("username");
        String password = params.get("password");
        CommonResponse<User> result = userService.login(username, password);
        if(result.isSuccess()) {
            session.setAttribute(CONSTANT.LOGIN_USER, result.getData());
        }
        return result;
    }

    @PostMapping("/checkField")
    public CommonResponse<Object> checkField(
            @RequestParam @NotBlank(message = "字段名不能为空") String fieldName,
            @RequestParam @NotBlank(message = "字段值不能为空") String fieldValue)
    {
        return userService.checkField(fieldName, fieldValue);
    }

    @PostMapping("/register")
    public CommonResponse<Object> register(@RequestBody @Valid User user) {
        return userService.register(user);
    }

    @PostMapping("/getForgetQuestion")
    public CommonResponse<String> getForgetQuestion(
            @RequestParam @NotBlank(message = "用户名不能为空") String username)
    {
        return userService.getForgetQuestion(username);
    }

    @PostMapping("/checkForgetAnswer")
    public CommonResponse<String> checkForgetAnswer(
            @RequestParam @NotBlank(message = "用户名不能为空") String username,
            @RequestParam @NotBlank(message = "忘记密码问题不能为空") String question,
            @RequestParam @NotBlank(message = "忘记密码问题答案不能为空") String answer
    ) {
        return userService.checkForgetAnswer(username, question, answer);
    }

    @PostMapping("/resetForgetPassword")
    public CommonResponse<Object> resetForgetPassword(
            @RequestParam @NotBlank(message = "用户名不能为空") String username,
            @RequestParam @NotBlank(message = "新密码不能为空") String newPassword,
            @RequestParam @NotBlank(message = "重置密码token不能为空") String forgetToken
    ) {
        return userService.resetForgetPassword(username, newPassword, forgetToken);
    }

    @PostMapping("/getUserDetail")
    public CommonResponse<User> getUserDetail(HttpSession session) {
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null) {
            return CommonResponse.createForError("用户未登录");
        }
        return userService.getUserDetail(loginUser.getId());
    }

    @PostMapping("/resetPassword")
    public CommonResponse<Object> resetPassword(@RequestBody Map<String, String> params, HttpSession session) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");

        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null) {
            return CommonResponse.createForError("用户未登录");
        }
        return userService.resetPassword(oldPassword, newPassword, loginUser);
    }

    @PostMapping("/updateUserInfo")
    public CommonResponse<Object> updateUserInfo(
            @RequestBody @Valid UpdateUserDTO updateUser,
            HttpSession session) {
        User loginUser = (User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null) {
            return CommonResponse.createForError("用户未登录");
        }
        loginUser.setEmail(updateUser.getEmail());
        loginUser.setPhone(updateUser.getPhone());
        loginUser.setQuestion(updateUser.getQuestion());
        loginUser.setAnswer(updateUser.getAnswer());

        CommonResponse<Object> result = userService.updateUserInfo(loginUser);

        if(result.isSuccess()) {
            loginUser = userService.getUserDetail(loginUser.getId()).getData();
            session.setAttribute(CONSTANT.LOGIN_USER, loginUser);
            return CommonResponse.createForSuccess();
        }
        return CommonResponse.createForError(result.getMsg());

    }

    @GetMapping("/logout")
    public CommonResponse<Object> logout(HttpSession session) {
        session.removeAttribute(CONSTANT.LOGIN_USER);
        return CommonResponse.createForError("推出登录成功");
    }


}
