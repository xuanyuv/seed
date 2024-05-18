package com.jadyer.seed.comm.exception;

import com.alibaba.fastjson.JSON;
import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommResult;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全局异常控制器
 * ----------------------------------------------------------------------------------------------------------------------
 * ControllerAdvice是Spring3.2提供的新注解，该注解使用了@Component注解，所以使用<context:component-scan>就能扫描到
 * ControllerAdvice可以把使用了@ExceptionHandler/@InitBinder/@ModelAttribute注解的方法应用到所有@RequestMapping注解的方法
 * 最常用的就是通过@ExceptionHandler进行全局异常的统一捕获和控制
 * ----------------------------------------------------------------------------------------------------------------------
 * ControllerAdvice注解的作用域是全局Controller
 * ----------------------------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2015/6/6 12:31.
 */
// @DisableLog
@ControllerAdvice
public class GlobalExceptionHandler {
    /*
     * 1、这里会捕获Throwable及其所有子异常
     * 2、欲返回JSON则需使用@ResponseBody，否则会去找JSP页面（即它不会受到被捕获的方法是否使用了@ResponseBody的影响）
     */
    // @ResponseBody
    // @ExceptionHandler(Throwable.class)
    // public CommResult<Void> process(Throwable cause, HttpServletRequest request){
    //     LogUtil.getLogger().info("Exception Occured URL=" + request.getRequestURL() + "，堆栈轨迹如下", cause);
    //     int code;
    //     String msg = cause.getMessage();
    //     if(cause instanceof SeedException){
    //         code = ((SeedException)cause).getCode();
    //     }else{
    //         code = CodeEnum.SYSTEM_ERROR.getCode();
    //         msg = JadyerUtil.extractStackTraceCausedBy(cause);
    //     }
    //     return CommResult.fail(code, msg);
    // }


    /**
     * -----------------------------------------------------------------------------------------------------------
     * 异常发生场景：
     * 1、Controller方法接收参数是一个实体对象，该方法对应接口入参是一个json
     * 2、当传入json不是合格的json时，比如传这样：{"name":""zhangsan""}的值时，会报告下面的异常：
     *    [AbstractHandlerExceptionResolver.logException]Resolved [org.springframework.http.converter.HttpMessageNotReadableException: JSON parse error: error parse false; nested exception is com.alibaba.fastjson.JSONException: error parse false]
     * 即：此时LogAspect拦截不到，故写此方法，拦截处理，以便提示友好信息给调用方
     * -----------------------------------------------------------------------------------------------------------
     * 注意事项：
     * 1、此方法不能使用@ResponseBody
     * 2、此方法直接输出到HttpServletResponse
     * -----------------------------------------------------------------------------------------------------------
     * Comment by 玄玉<https://jadyer.cn/> on 2021/8/25 14:52.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public void process(HttpMessageNotReadableException cause, HttpServletRequest request, HttpServletResponse response) {
        LogUtil.getLogger().info("Exception Occured URL={}，入参格式有误", request.getRequestURL(), cause);
        RequestUtil.writeToResponse(JSON.toJSONString(CommResult.fail(CodeEnum.SYSTEM_BUSY.getCode(), "入参格式有误")), response);
    }
}