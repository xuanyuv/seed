package com.jadyer.seed.controller;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 按照定制的格式输出日期时间的TagHandler
 * <ul>
 *     <li>通过两个实例变量接收JSP页面传来的属性，并格式化后输出相应的时间</li>
 *     <li>若没有传这俩属性参数，则默认按照yyyy-MM-dd hh:mm:ss输出当前时间</li>
 * </ul>
 * Created by 玄玉<http://jadyer.cn/> on 2016/7/9 19:15.
 */
public class DateTimeTag extends TagSupport {
    private static final long serialVersionUID = 6861117798037409372L;
    private String pattern = "yyyy-MM-dd HH:mm:ss";
    private Date date;

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * 如果标记不包含内容或者不需要显示标记所包含的内容，则不用重写doStartTag()
     */
    @Override
    public int doEndTag() throws JspException {
        //不能全局初始化该date，因为在JSP没有传data情况下，我们要每次都取最新的时间输出
        if(null == this.date){
            this.date = new Date();
        }
        try {
            this.pageContext.getOut().write(new SimpleDateFormat(this.pattern).format(this.date));
            //输出完要置空，否则页面无论怎么刷新，都会一直显示最初访问时的时间
            this.date = null;
        } catch (IOException e) {
            throw new JspTagException(e);
        }
        return super.doEndTag();
    }
}