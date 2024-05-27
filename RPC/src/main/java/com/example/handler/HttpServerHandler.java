package com.example.handler;

import com.example.model.RpcRequest;
import com.example.model.RpcResponse;
import com.example.registry.LocalRegistry;
import com.example.serializer.JdkSerializer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

@Component
public class HttpServerHandler implements HandlerInterceptor {
    // 指定序列化器
    private final JdkSerializer serializer = new JdkSerializer();

    /**
     * 请求处理前操作
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{


        System.out.println("Received request: " + request.getMethod() + " " + request.getRequestURL());
        // 返回请求体长度
        if (request.getContentLength() < 0)
            return false;

        InputStream inputStream = request.getInputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        byte[] bodyBytes = byteArrayOutputStream.toByteArray();

        // 反序列化
        RpcRequest rpcRequest = serializer.deserialize(bodyBytes, RpcRequest.class);

        // 构造响应结果对象
        RpcResponse rpcResponse = new RpcResponse();
        // 如果请求为 null，直接返回
        if (null == rpcRequest) {
            rpcResponse.setMessage("rpcRequest is null");
            doResponse(request, rpcResponse, response);
            return false;
        }

        try {
            // 获取要调用的服务实现类，通过反射调用
            Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
            Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
            Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
            // 封装返回结果
            rpcResponse.setData(result);
            rpcResponse.setDataType(method.getReturnType());
            rpcResponse.setMessage("ok");
        } catch (Exception e) {
            e.printStackTrace();
            rpcResponse.setMessage(e.getMessage());
            rpcResponse.setException(e);
        }

        // 响应
        doResponse(request, rpcResponse, response);
        return false;
    }

    private void doResponse(HttpServletRequest request, RpcResponse rpcResponse, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        byte[] serialized = serializer.serialize(rpcResponse);
        response.getOutputStream().write(serialized);
    }



    /**
     * 请求处理后但在视图渲染前的操作
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println(response);

    }

    /**
     * 请求完成后的操作
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求完成后的操作
    }

}
