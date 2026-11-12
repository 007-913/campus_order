package org.example.campus_order.controller;

import org.example.campus_order.dto.LoginDTO;
import org.example.campus_order.dto.UserDTO;
import org.example.campus_order.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.example.campus_order.entity.Admin;
import org.example.campus_order.entity.Shop;
import org.example.campus_order.entity.User;
import org.example.campus_order.service.AdminService;
import org.example.campus_order.service.ShopService;
import org.example.campus_order.service.UserService;
import org.example.campus_order.utils.Result;
import org.example.campus_order.vo.LoginVO;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 统一认证控制器
 * 角色统一: 0=管理员, 1=商家, 2=学生
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private UserService userService;

    /**
     * 统一登录接口
     * role: 0=管理员, 1=商家, 2=学生
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        String username = dto.getUsername();
        String password = dto.getPassword();
        Integer role = dto.getRole();

        Long userId;
        String name;
        Long shopId = null;

        switch (role) {
            case JwtUtil.ROLE_ADMIN: {
                Admin admin = adminService.lambdaQuery()
                        .eq(Admin::getUsername, username)
                        .eq(Admin::getPassword, password)
                        .one();
                if (admin == null) return Result.error("管理员账号或密码错误");
                userId = admin.getId();
                name = admin.getName();
                break;
            }
            case JwtUtil.ROLE_SHOP: {
                Shop shop = shopService.lambdaQuery()
                        .eq(Shop::getUsername, username)
                        .eq(Shop::getPassword, password)
                        .one();

                if (shop == null) {
                    return Result.error("商家账号或密码错误");
                }

                if (shop.getStatus() == 2) {
                    return Result.error("该商家已被管理员禁用");
                }

                userId = shop.getId();
                name = shop.getShopName();
                shopId = shop.getId();
                break;
            }
            case JwtUtil.ROLE_USER: {
                User user = userService.lambdaQuery()
                        .eq(User::getUsername, username)
                        .eq(User::getPassword, password)
                        .one();
                if (user == null) return Result.error("学生账号或密码错误");

                if (user.getStatus() != null && user.getStatus() == 0) {
                    return Result.error("该账号已被管理员禁用，请联系管理员解封");
                }

                userId = user.getId();
                name = user.getUsername();
                break;
            }
            default:
                return Result.error("角色非法");
        }

        String token = JwtUtil.createToken(userId, role, shopId);

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserId(userId);
        loginVO.setRole(role);
        loginVO.setUsername(name);
        loginVO.setShopId(shopId);

        return Result.success(loginVO, "登录成功");
    }
    /**
     * 获取当前登录用户信息（需要 token，不排除拦截）
     */
    @GetMapping("/me")
    public Result<LoginVO> me(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("loginId");
        Integer role = (Integer) request.getAttribute("loginRole");
        Long shopId = (Long) request.getAttribute("loginShopId");

        if (userId == null) {
            return Result.error("未登录");
        }

        LoginVO loginVO = new LoginVO();
        loginVO.setUserId(userId);
        loginVO.setRole(role);
        loginVO.setShopId(shopId);

        // 补充用户名等详情
        switch (role) {
            case JwtUtil.ROLE_ADMIN: {
                Admin admin = adminService.getById(userId);
                if (admin != null) {
                    loginVO.setUsername(admin.getName());
                }
                break;
            }
            case JwtUtil.ROLE_SHOP: {
                Shop shop = shopService.getById(userId);
                if (shop != null) {
                    loginVO.setUsername(shop.getShopName());
                    loginVO.setShopId(shop.getId());
                }
                break;
            }
            case JwtUtil.ROLE_USER: {
                User user = userService.getById(userId);
                if (user != null) {
                    loginVO.setUsername(user.getUsername());
                }
                break;
            }
        }

        return Result.success(loginVO);
    }

    //注册
    @PostMapping("/register")
    public Result<String> register(@RequestBody UserDTO userDTO){

        if(userDTO.getUsername() == null || userDTO.getUsername().trim().isEmpty()){
            return Result.error("用户名不能为空");
        }

        if(userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()){
            return Result.error("密码不能为空");
        }

        long count = userService.lambdaQuery()
                .eq(User::getUsername, userDTO.getUsername())
                .count();

        if(count > 0){
            return Result.error("用户名已存在");
        }

        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setRole(2);

        userService.save(user);

        return Result.success("注册成功");
    }
}