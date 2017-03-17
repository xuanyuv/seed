package com.jadyer.seed.mpp.sdk.weixin.model.template;

/**
 * 微信模板对象
 * <p>用于接收模板列表获取接口返回值</p>
 * Created by 玄玉<https://jadyer.github.io/> on 2016/5/20 13:07.
 */
public class WeixinTemplate {
    /**
     * 模板ID
     */
    private String template_id;

    /**
     * 模板标题
     */
    private String title;

    /**
     * 模板所属行业的一级行业
     */
    private String primary_industry;

    /**
     * 模板所属行业的二级行业
     */
    private String deputy_industry;

    /**
     * 模板内容
     */
    private String content;

    /**
     * 模板示例
     */
    private String example;

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrimary_industry() {
        return primary_industry;
    }

    public void setPrimary_industry(String primary_industry) {
        this.primary_industry = primary_industry;
    }

    public String getDeputy_industry() {
        return deputy_industry;
    }

    public void setDeputy_industry(String deputy_industry) {
        this.deputy_industry = deputy_industry;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }
}