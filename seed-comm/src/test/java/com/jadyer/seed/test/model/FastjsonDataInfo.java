package com.jadyer.seed.test.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2018/6/19 17:48.
 */
public class FastjsonDataInfo {
    private String loginname;
    private String blackList;  //----------
    private String redList;  //----------
    private String param;
    private String unitedType;
    private String departmentname;
    private String personname;
    private RedblackBackList redblackBackList;

    public static class RedblackBackList {
        private String requestId;
        private String redblackRequestId;
        private List<RedblackBack> redblackBack = new ArrayList<>();

        public String getRequestId() {
            return requestId;
        }

        public void setRequestId(String requestId) {
            this.requestId = requestId;
        }

        public String getRedblackRequestId() {
            return redblackRequestId;
        }

        public void setRedblackRequestId(String redblackRequestId) {
            this.redblackRequestId = redblackRequestId;
        }

        public List<RedblackBack> getRedblackBack() {
            return redblackBack;
        }

        public void setRedblackBack(List<RedblackBack> redblackBack) {
            this.redblackBack = redblackBack;
        }

        public static class RedblackBack {
            private String name;
            private String code;
            private List<RedblackType> redblackType = new ArrayList<>();

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public List<RedblackType> getRedblackType() {
                return redblackType;
            }

            public void setRedblackType(List<RedblackType> redblackType) {
                this.redblackType = redblackType;
            }

            public static class RedblackType {
                private String unitedName;
                private Date clTime;
                private String originDept;
                private String remark;
                private String handleType;
                private String redblackDetailList; //-----
                private String unitedId;
                private String action;
                private String unitedType;
                private String feedbackResult;
                private List<UnionPair> unionPairList = new ArrayList<>();

                public String getUnitedName() {
                    return unitedName;
                }

                public void setUnitedName(String unitedName) {
                    this.unitedName = unitedName;
                }

                public Date getClTime() {
                    return clTime;
                }

                public void setClTime(Date clTime) {
                    this.clTime = clTime;
                }

                public String getOriginDept() {
                    return originDept;
                }

                public void setOriginDept(String originDept) {
                    this.originDept = originDept;
                }

                public String getRemark() {
                    return remark;
                }

                public void setRemark(String remark) {
                    this.remark = remark;
                }

                public String getHandleType() {
                    return handleType;
                }

                public void setHandleType(String handleType) {
                    this.handleType = handleType;
                }

                public String getRedblackDetailList() {
                    return redblackDetailList;
                }

                public void setRedblackDetailList(String redblackDetailList) {
                    this.redblackDetailList = redblackDetailList;
                }

                public String getUnitedId() {
                    return unitedId;
                }

                public void setUnitedId(String unitedId) {
                    this.unitedId = unitedId;
                }

                public String getAction() {
                    return action;
                }

                public void setAction(String action) {
                    this.action = action;
                }

                public String getUnitedType() {
                    return unitedType;
                }

                public void setUnitedType(String unitedType) {
                    this.unitedType = unitedType;
                }

                public String getFeedbackResult() {
                    return feedbackResult;
                }

                public void setFeedbackResult(String feedbackResult) {
                    this.feedbackResult = feedbackResult;
                }

                public List<UnionPair> getUnionPairList() {
                    return unionPairList;
                }

                public void setUnionPairList(List<UnionPair> unionPairList) {
                    this.unionPairList = unionPairList;
                }

                public static class UnionPair {
                    private List<UnionPairContent> unionPairContent = new ArrayList<>();

                    public List<UnionPairContent> getUnionPairContent() {
                        return unionPairContent;
                    }

                    public void setUnionPairContent(List<UnionPairContent> unionPairContent) {
                        this.unionPairContent = unionPairContent;
                    }

                    public static class UnionPairContent {
                        private String title;
                        private String content;

                        public String getTitle() {
                            return title;
                        }

                        public void setTitle(String title) {
                            this.title = title;
                        }

                        public String getContent() {
                            return content;
                        }

                        public void setContent(String content) {
                            this.content = content;
                        }
                    }
                }
            }
        }
    }

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    public String getBlackList() {
        return blackList;
    }

    public void setBlackList(String blackList) {
        this.blackList = blackList;
    }

    public String getRedList() {
        return redList;
    }

    public void setRedList(String redList) {
        this.redList = redList;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getUnitedType() {
        return unitedType;
    }

    public void setUnitedType(String unitedType) {
        this.unitedType = unitedType;
    }

    public String getDepartmentname() {
        return departmentname;
    }

    public void setDepartmentname(String departmentname) {
        this.departmentname = departmentname;
    }

    public String getPersonname() {
        return personname;
    }

    public void setPersonname(String personname) {
        this.personname = personname;
    }

    public RedblackBackList getRedblackBackList() {
        return redblackBackList;
    }

    public void setRedblackBackList(RedblackBackList redblackBackList) {
        this.redblackBackList = redblackBackList;
    }
}