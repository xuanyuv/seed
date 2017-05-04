package com.jadyer.seed.comm.tag.bak.paging;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

/**
 * 自定义的分页标签类
 * Created by 玄玉<http://jadyer.cn/> on 2011/04/06 21:00.
 */
public class PageTag extends SimpleTagSupport {
    //private int pageStyle = 1;  //分页风格
    private String path;          //后台处理分页显示的Servlet的路径
    private String param;         //传递的参数
    private int currPage;         //当前页数
    private int totalPage;        //总页数
    private boolean hasSelect;    //是否有下拉列表
    private boolean hasTextField; //是否有提交文本框

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public int getCurrPage() {
        return currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public boolean isHasSelect() {
        return hasSelect;
    }

    public void setHasSelect(boolean hasSelect) {
        this.hasSelect = hasSelect;
    }

    public boolean isHasTextField() {
        return hasTextField;
    }

    public void setHasTextField(boolean hasTextField) {
        this.hasTextField = hasTextField;
    }

    @Override
    public void doTag() throws JspException, IOException {
        JspWriter out = this.getJspContext().getOut();
        StringBuffer sb = new StringBuffer();
        if(currPage == 1){
            //在第一页中，显示【首页】和【上一页】的时候，不需要显示链接
            sb.append("首页");
            sb.append("&nbsp;&nbsp;");
            sb.append("/n");    //在页面的源代码中换行，但不是在页面中换行
            sb.append("上一页"); //换行之后，在查看源代码时，看得会更方便些
            sb.append("&nbsp;&nbsp;");
            sb.append("/n");
        }else{
            //构造：<a href="<%=path%>/servlet/Mp3?currentPage=1">首页</a>
            sb.append("<a href=\"");
            sb.append(path);
            sb.append("?");
            sb.append(param);
            sb.append("=");
            sb.append(1);
            sb.append("\"");
            sb.append(">");
            sb.append("首页");
            sb.append("</a>");
            sb.append("&nbsp;&nbsp;");
            sb.append("/n");
            //构造：<a href="<%=path%>/servlet/Mp3?currentPage=<%=currentPage-1%>">上一页</a>
            sb.append("<a href=\"");
            sb.append(path);
            sb.append("?");
            sb.append(param);
            sb.append("=");
            sb.append(currPage-1);
            sb.append("\"");
            sb.append(">");
            sb.append("上一页");
            sb.append("</a>");
            sb.append("&nbsp;&nbsp;");
            sb.append("/n");
        }
        if(currPage == totalPage){
            //在最后一页中，显示【下一页】和【末页】的时候，不需要显示链接
            sb.append("下一页");
            sb.append("&nbsp;&nbsp;");
            sb.append("/n");
            sb.append("末页");
            sb.append("&nbsp;&nbsp;");
            sb.append("/n");
        }else{
            //构造：<a href="<%=path%>/servlet/Mp3?currentPage=<%=currentPage+1%>">下一页</a>
            sb.append("<a href=\"");
            sb.append(path);
            sb.append("?");
            sb.append(param);
            sb.append("=");
            sb.append(currPage+1);
            sb.append("\"");
            sb.append(">");
            sb.append("下一页");
            sb.append("</a>");
            sb.append("&nbsp;&nbsp;");
            sb.append("/n");
            //构造：<a href="<%=path%>/servlet/Mp3?currentPage=<%=totalPage%>">末页</a>
            sb.append("<a href=\"");
            sb.append(path);
            sb.append("?");
            sb.append(param);
            sb.append("=");
            sb.append(totalPage);
            sb.append("\"");
            sb.append(">");
            sb.append("末页");
            sb.append("</a>");
            sb.append("&nbsp;&nbsp;");
            sb.append("/n");
        }
        if(this.hasSelect){
            //构造：<form action="<%=path%>/servlet/Mp3">
            sb.append("<form action=\"");
            sb.append(path);
            sb.append("\">");
            sb.append("/n");
            sb.append("跳转至");
            sb.append("/n");
            //构造：<select name="page" onchange="javascript:document.forms[0].submit()">
            sb.append("<select name=\"");
            sb.append(param);
            sb.append("\" ");
            sb.append("onchange=\"javascript:document.forms[0].submit()\">");
            sb.append("/n");
            //构造：<option value="${i}" selected>${i}</option>
            for(int i=1; i<=totalPage; i++){
                sb.append("<option value=\"");
                sb.append(i);
                sb.append("\" ");
                if(currPage == i){
                    sb.append("selected");
                }
                sb.append(">");
                sb.append(i);
                sb.append("</option>");
                sb.append("/n");
            }
            //构造：</select>
            sb.append("</select>");
            sb.append("/n");
            sb.append("页");
            sb.append("/n");
            //构造：</form>
            sb.append("</form>");
            sb.append("/n");
        }
        if(this.hasTextField){
            //构造：<form name="ff" action="<%=path%>/servlet/Mp3">
            sb.append("<form name=\"ff\" action=\"");
            sb.append(path);
            sb.append("\">");
            sb.append("/n");
            //构造：跳转至<input type="text" size="2" name="currentPage">页&nbsp;&nbsp;
            sb.append("跳转至");
            sb.append("<input type=\"text\" size=\"2\" name=\"");
            sb.append(param);
            sb.append("\">页&nbsp;&nbsp;");
            //它的缺点是：只有在鼠标滑过的时候才会聚焦文本框，功能不如下面构造的JavaScript代码
            //sb.append("\" onMouseOver=\"javascript:focus()\">页&nbsp;&nbsp;");
            sb.append("/n");
            //构造：<input type="submit" value="跳转">
            sb.append("<input type=\"submit\" value=\"跳转\">");
            sb.append("/n");
            //构造：</form>
            sb.append("</form>");
            sb.append("/n");
            //构造聚焦文本框的JavaScript代码
            sb.append("<script language=\"javascript\">");
            sb.append("document.ff.elements[0].focus()");
            sb.append("</script>");
        }
        //打印到页面上
        out.println(sb);
    }
}