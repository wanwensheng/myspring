package com.wan.filter;

import com.wan.annotation.GPAutowrrde;
import com.wan.annotation.GPContraller;
import com.wan.annotation.GPRequestMapping;
import com.wan.annotation.GPService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * @Author: WanWenSheng
 * @Description:
 * @Dete: Created in 10:10 2020/9/29
 * @Modified By:
 */
public class GPDispatcherServlet extends HttpServlet {

    private static Properties properties = new Properties();

    private Map<String,Object> beanMap = new HashMap<String,Object>();

    private Map<String,Method> handlerMapping = new HashMap<String,Method>();

    private List<String>  classNames = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();
        Method method = handlerMapping.get(requestURI);
        if(method==null){
            try {
                throw  new Exception("404，没有找到对Action");
            } catch (Exception e) {
               e.printStackTrace();
            }

        }
        try {
            String beanName = toLowerCaseFrist(method.getDeclaringClass().getSimpleName());
            method.invoke(beanMap.get(beanName),new Object[]{req,resp,req.getParameterValues("name")[0]});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //获取配置文件
        String gbServletConfig = config.getInitParameter("gbServletConfig");
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(gbServletConfig);
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //扫描路径下的包

        String packageScan = properties.getProperty("packageScan");
        try {
            loadPackageScan(packageScan);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //注入bean
        try {
            instenseBean();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //添加Bean依赖
        try {
            loadBeanDependent();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        //添加url映射
        try {
            loadHandlerMapping();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    private void instenseBean() throws Exception {
        if(classNames.isEmpty()){return;}
        for(String className:classNames){
            Class<?> aClass = Class.forName(className);
            if(aClass.isAnnotationPresent(GPContraller.class)){
                String beanName=toLowerCaseFrist(aClass.getSimpleName());
                beanMap.put(beanName,aClass.newInstance());
            }else if(aClass.isAnnotationPresent(GPService.class)){
                String value = aClass.getAnnotation(GPService.class).value();
                String beanName="";
                if("".equals(value)){
                    beanName =toLowerCaseFrist(aClass.getSimpleName());
                }else{
                    beanName=value;
                }
                Object intanse = aClass.newInstance();
                beanMap.put(beanName,intanse);
                Class<?>[] interfaces = aClass.getInterfaces();
                for (Class<?> interf : interfaces){
                    if(beanMap.containsKey(interf.getName())){
                        throw  new Exception("the"+interf.getName() + "is exists!!!");
                    }
                    beanMap.put(interf.getName(),intanse);
                }
            }else{
                continue;
            }
        }

    }

    private void loadHandlerMapping() throws ClassNotFoundException {
        if(classNames.isEmpty()){ return;}
        for(String className:classNames){
            Class<?> aClass = Class.forName(className);
            if(!aClass.isAnnotationPresent(GPContraller.class)){continue;}
            Method[] methods = aClass.getMethods();
            for (Method method : methods){
                if(!method.isAnnotationPresent(GPRequestMapping.class)){continue;}
                String value = method.getAnnotation(GPRequestMapping.class).value();
                handlerMapping.put((aClass.getAnnotation(GPRequestMapping.class).value()+"/"+ value).replaceAll("/+","/"),method);
            }
        }


    }

    private void loadBeanDependent() throws IllegalAccessException {
        if(beanMap.isEmpty()){return;}
        for (String key:beanMap.keySet()) {
            Field[] declaredFields = beanMap.get(key).getClass().getDeclaredFields();
            for (Field f: declaredFields) {
                if(!f.isAnnotationPresent(GPAutowrrde.class))continue;
                GPAutowrrde annotation = f.getAnnotation(GPAutowrrde.class);
                String beanName= annotation.value().trim();
                if("".equals(beanName)){
                    beanName=f.getType().getName();
                }
                f.setAccessible(true);
                f.set(beanMap.get(key),beanMap.get(beanName));
            }

        }
    }

    private void loadPackageScan(String packageScan) throws Exception {
        URL ur = this.getClass().getClassLoader().getResource("/" + packageScan.replaceAll("\\.", "/"));
        File[] files = new File(ur.toURI()).listFiles();
        for (File file : files){
            if(file.isDirectory()){
                loadPackageScan(packageScan+"."+file.getName());
            }else if(file.getName().endsWith(".class")){
                classNames.add(packageScan+"."+file.getName().replace(".class",""));
            }else{
                continue;
            }
        }

    }

    private String toLowerCaseFrist(String str) {
        char[] chars = str.toCharArray();
        chars[0]+=32;
        return String.valueOf(chars);
    }
}
