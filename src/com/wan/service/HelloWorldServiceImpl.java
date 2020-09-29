package com.wan.service;

import com.wan.annotation.GPService;

/**
 * @Author: WanWenSheng
 * @Description:
 * @Dete: Created in 11:01 2020/9/29
 * @Modified By:
 */
@GPService("helloWorldServiceImpl")
public class HelloWorldServiceImpl implements HelloWorldService {
    @Override
    public String hello(String name) {
        return "for service"+name;
    }
}
