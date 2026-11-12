package org.example.campus_order.controller;


import org.example.campus_order.service.CategoryService;
import org.example.campus_order.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category/open")
public class UserCategoryController {

        @Autowired
        private CategoryService categoryService;

        /**
         * 用户端查看某店铺分类
         */
        @GetMapping("/list")
        public Result<?> list(@RequestParam Long shopId) {
            return Result.success(categoryService.getOpenShopCategoryList(shopId));
        }
    }

