package com.jadyer.seed.test.model;

import java.math.BigDecimal;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2019/1/22 16:50.
 */
public class Java8StreamInfo {
    private int loanTerm;
    private BigDecimal loanAmt;
    private Long loanCount;

    public Java8StreamInfo() {
    }

    public Java8StreamInfo(int loanTerm, BigDecimal loanAmt, Long loanCount) {
        this.loanTerm = loanTerm;
        this.loanAmt = loanAmt;
        this.loanCount = loanCount;
    }

    public int getLoanTerm() {
        return loanTerm;
    }

    public void setLoanTerm(int loanTerm) {
        this.loanTerm = loanTerm;
    }

    public BigDecimal getLoanAmt() {
        return loanAmt;
    }

    public void setLoanAmt(BigDecimal loanAmt) {
        this.loanAmt = loanAmt;
    }

    public Long getLoanCount() {
        return loanCount;
    }

    public void setLoanCount(Long loanCount) {
        this.loanCount = loanCount;
    }
}