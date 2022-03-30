package com.jadyer.seed.comm.util;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

/**
 * JSR303验证工具
 * -------------------------------------------------------------------------------------------------
 * JSR303是JavaEE6中的一项子规范，叫做BeanValidation，它的官方参考实现是hibernate-validator
 * BeanValidation现在一共有两个规范：BeanValidation1.0（即JSR303）和BeanValidation1.1（即JSR349）
 * BeanValidation的官网是http://beanvalidation.org/
 * 关于JSR303的详细说明，请参考http://jcp.org/en/jsr/detail?id=303
 * 关于JSR349的详细说明，请参考http://jcp.org/en/jsr/detail?id=349
 * -------------------------------------------------------------------------------------------------
 * JSR303规范主要用于对JavaBean中的字段的值的验证，使得验证逻辑从业务代码中脱离出来
 * JSR303定义了基于注解方式的JavaBean验证元数据模型和API，也可通过XML进行元数据定义，但注解会覆盖XML的定义
 * JSR303主要是对JavaBean进行验证，而没有指定方法级别（参数or返回值）、依赖注入等验证，因此催生了JSR349规范
 * JSR349规范目前处于草案状态，它主要支持依赖注入的验证和方法级别的验证（方法的参数和返回值）
 * Spring3.1目前已经完全支持依赖注入验证和方法级别的验证了，只不过不是原生的（JSR349规范还是草案嘛）
 * 关于这个的详细说明，可以参考此文http://www.iteye.com/topic/1123007
 * -------------------------------------------------------------------------------------------------
 * 本工具需要借助以下三个jar
 * hibernate-validator-5.1.3.Final.jar
 * validation-api-1.1.0.Final.jar
 * jboss-logging-3.1.3.GA.jar
 * -------------------------------------------------------------------------------------------------
 * 以下为BeanValidation规范内嵌的约束注解定义（constraint）
 * @Null         限制只能为null
 * @NotNull      限制必须不为null
 * @AssertTrue   限制必须为true
 * @AssertFalse  限制必须为false
 * @Min           限制必须为一个不小于指定值的数字
 * @Max           限制必须为一个不大于指定值的数字
 * @DecimalMin   限制必须为一个不小于指定值的数字
 * @DecimalMax   限制必须为一个不大于指定值的数字
 * @Size         限制字符长度必须在min到max之间
 * @Digits      限制必须为一个小数,且整数部分的位数不能超过integer,小数部分的位数不能超过fraction
 * @Past        限制必须是一个过去的日期
 * @Future     限制必须是一个将来的日期
 * @Pattern   限制必须符合指定的正则表达式
 * 以下为HibernateValidator附加的constraint
 * @Email      被注释的元素必须是电子邮箱地址
 * @Length    被注释的字符串的长度必须在指定的范围
 * @NotEmpty 被注释的字符串必须非空
 * @Range    被注释的元素必须在合适的范围内
 * -------------------------------------------------------------------------------------------
 * 常用的pojo配置例子
 * @Min(value=3)
 * private int id;
 * @NotNull
 * @Min(3)
 * @Max(4)
 * private Integer id;
 * @NotEmpty
 * private List<OssInfo> ossInfoList = new ArrayList<>();
 * @NotBlank
 * @Size(max=10)
 * @Pattern(regexp="^[\\u4e00-\\u9fa5]+(·[\\u4e00-\\u9fa5]+)*$", message="不合法的中文姓名")
 * private String username;
 * @Size(min=6, max=16)
 * private String password;
 * @Pattern(regexp="(^$)|(^0|50|100$)", message="只能为0或50或100或不传值(建议传100)")
 * private String resize;
 * @Pattern(regexp="^\\b[1-9]\\d{0,1}\\b$", message="pageNo只能为1--99之间的数字")
 * private String pageNo;
 * @Pattern(regexp="^1\\d{1}|2\\d[0,1,2,3,4,5]{1}", message="pageSize只能为10--25之间的数字")
 * private String pageSize;
 * @Pattern(regexp="^(([1-9]\\d*)|0)(\\.\\d{1,2})?$", message="金额必须大于0且最多2位小数")
 * private String amount;
 * @NotBlank
 * @Pattern(regexp="^\\d{3,4}$", message="贷款金额超限(最少100,最多9999)")
 * private String loanAmount;
 * @NotBlank
 * @Pattern(regexp="^\\d{2}$", message="贷款期数无效(必须是固长2位整数,比如03或12)")
 * private String loanPeriod;
 * -------------------------------------------------------------------------------------------
 * 除了以上列出的JSR-303原生支持的限制类型之外，还可以定义自己的限制类型
 * 本工具类最下方的注释部分是一个例子（也可参考此文http://haohaoxuexi.iteye.com/blog/1812584）
 * -------------------------------------------------------------------------------------------
 * @version v1.7
 * @version v1.7-->优化校验不通过时的提示信息的格式
 * @version v1.6-->移除返回值为Map的验证方法，增加对java.util.List对象的验证支持
 * @history v1.5-->被验证对象若为空对象，由于没什么可验证的，故直接返回验证通过
 * @history v1.4-->简化例外属性的判断方式
 * @history v1.3-->增加一些常用的pojo配置例子
 * @history v1.2-->部分细节优化及增加描述：验证对象若其父类的属性也有验证注解则会一并验证
 * @history v1.1-->增加将验证的错误信息存入Map<String,String>后返回的<code>validateToMap()<code>方法
 * @history v1.0-->新建
 * -------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2015/06/09 23:25.
 */
public final class ValidatorUtil {
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private ValidatorUtil() {}


    /**
     * 验证对象中的属性的值是否符合注解定义
     * -------------------------------------------------------------------------------
     * 注意：被验证对象若为空对象，由于没什么可验证的，故直接返回验证通过
     * -------------------------------------------------------------------------------
     * @param obj 需要验证的对象，若其父类的属性也有验证注解则会一并验证
     * @return 返回空字符串""表示验证通过，否则返回错误信息
     */
    public static String validate(Object obj){
        StringBuilder sb = new StringBuilder();
        if(obj instanceof List){
            for (int i=0,len=((List<?>)obj).size(); i<len; i++) {
                sb.append(validate(((List<?>)obj).get(i), "List.object["+i+"]."));
            }
        }else{
            sb.append(validate(obj, null));
        }
        sb.delete(0, 2);
        return sb.toString();
    }


    /**
     * 验证对象中的属性的值是否符合注解定义
     * -------------------------------------------------------------------------------
     * 注意：被验证对象若为空对象，由于没什么可验证的，故直接返回验证通过
     * -------------------------------------------------------------------------------
     * @param obj             需要验证的对象，若其父类的属性也有验证注解则会一并验证
     * @param msgPrefix       验证结果消息前缀
     * @param exceptFieldName 不需要验证的属性
     * @return 返回空字符串""表示验证通过，否则返回错误信息
     */
    private static String validate(Object obj, String msgPrefix){
        if(null == obj){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Set<ConstraintViolation<Object>> validateSet = validator.validate(obj);
        for(ConstraintViolation<Object> constraintViolation : validateSet){
            String field = constraintViolation.getPropertyPath().toString();
            String message = constraintViolation.getMessage();
            sb.append(", ");
            // List.object[0].name不能为空, List.object[0].id最小不能小于1, List.object[1].name不能为空
            if(null != msgPrefix){
                sb.append(msgPrefix);
            }
            // name不能为空, id最小不能小于1
            sb.append(field).append(message);
        }
        return sb.toString();
    }
}
/*
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Retention(RetentionPolicy.RUNTIME)  
@Constraint(validatedBy=ByteLengthValidate.class)  
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD})  
public @interface ByteLength {
    int min() default 0;

    int max() default Integer.MAX_VALUE;

    String message() default "{org.hibernate.validator.constraints.Length.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
*/
/*
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ByteLengthValidate implements ConstraintValidator<ByteLength, String> {
    private int min;
    private int max;

    @Override
    public void initialize(ByteLength byteLength) {
        this.min = byteLength.min();
        this.max = byteLength.max();
    }

    @Override
    public boolean isValid(String input, ConstraintValidatorContext arg1) {
        String standard = input.replaceAll("[^\\x00-\\xff]", "**");
        int length = standard.length();
        if(min>0 && length<min){
            return false;
        }
        if(max>0 && length>max){
            return false;
        }
        return true;
    }
}
*/