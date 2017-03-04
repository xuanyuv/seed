package com.jadyer.seed.open.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 申请单基本参数
 * Created by 玄玉<https://jadyer.github.io/> on 2016/5/10 18:58.
 */
public class LoanSubmit {
    @NotBlank
    @Size(min=1, max=32)
    private String partnerApplyNo;
    @Min(10000)
    @Max(20000000)
    private int loanMoney;
    @Min(3)
    @Max(36)
    private int loanTerm;
    @NotBlank
    @Pattern(regexp="^\\d{4}$", message="无效的产品编码(应为固定的4位数字)")
    private String productCode;
    @NotBlank
    @Size(min=2, max=16)
    private String userName;
    @NotBlank
    @Pattern(regexp="^1\\d{10}$", message="无效的手机号")
    private String userPhone;
    @NotBlank
    @Pattern(regexp="^[1-9]\\d{13,16}[xX0-9]$", message="无效的身份证号")
    private String userIdCard;
    @NotBlank
    @Pattern(regexp="^\\d{14,19}$", message="无效的银行卡号")
    private String bankCardNo;

    public String getPartnerApplyNo() {
        return partnerApplyNo;
    }

    public void setPartnerApplyNo(String partnerApplyNo) {
        this.partnerApplyNo = partnerApplyNo;
    }

    public int getLoanMoney() {
        return loanMoney;
    }

    public void setLoanMoney(int loanMoney) {
        this.loanMoney = loanMoney;
    }

    public int getLoanTerm() {
        return loanTerm;
    }

    public void setLoanTerm(int loanTerm) {
        this.loanTerm = loanTerm;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserIdCard() {
        return userIdCard;
    }

    public void setUserIdCard(String userIdCard) {
        this.userIdCard = userIdCard;
    }

    public String getBankCardNo() {
        return bankCardNo;
    }

    public void setBankCardNo(String bankCardNo) {
        this.bankCardNo = bankCardNo;
    }
}