package com.wan.comtroller;

import com.wan.annotation.GPAutowrrde;
import com.wan.annotation.GPContraller;
import com.wan.annotation.GPRequestMapping;
import com.wan.service.HelloWorldService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: WanWenSheng
 * @Description:
 * @Dete: Created in 10:39 2020/9/29
 * @Modified By:
 */
@GPRequestMapping("/hello")
@GPContraller
public class HelloWordController {


    @GPAutowrrde
    private HelloWorldService helloWorldService;

    @GPRequestMapping("/add")
    public  void addHello(HttpServletRequest req , HttpServletResponse resp ,String name) throws IOException {
        System.out.println("执行了addHello方法，nane="+name);
        String hello = helloWorldService.hello(name);
        resp.getWriter().write("Hello,My name " +name+hello);
    }
}
