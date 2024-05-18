package com.jadyer.seed.open.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * 针对1101产品的申请单参数
 * Created by 玄玉<https://jadyer.cn/> on 2016/5/11 13:15.
 */
public class LoanSubmit1101 extends LoanSubmit {
    @Size(min=4, max=4)
    private String loanPurpose;
    @Pattern(regexp="^Y|N$", message="是否加入寿险计划只能传Y或N")
    private String useLifeInsurance;
    @NotBlank
    @Pattern(regexp="^\\d{4}$", message="无效的银行行号")
    private String bankCode;
    @NotBlank
    @Pattern(regexp="^\\d{6}$", message="无效的银行分行省份编码")
    private String bankProvinceCode;
    @NotBlank
    @Pattern(regexp="^\\d{6}$", message="无效的银行分行城市编码")
    private String bankCityCode;
    @NotBlank
    @Pattern(regexp="^\\d{6}$", message="无效的居住地省份编码")
    private String liveProvinceCode;
    @NotBlank
    @Pattern(regexp="^\\d{6}$", message="无效的居住地城市编码")
    private String liveCityCode;
    @NotBlank
    @Pattern(regexp="^\\d{6}$", message="无效的居住地区县编码")
    private String liveTownCode;
    @NotBlank
    @Size(max=128)
    private String liveDetailAddress;
    @Size(max=14)
    private String livePhone;
    @NotBlank
    @Size(min=2, max=2)
    private String maritalStatus;
    @Email
    @Size(max=64)
    private String email;
    @Size(min=4, max=4)
    private String education;
    @Size(max=64)
    private String schoolAddress;
    @Size(max=64)
    private String schoolName;
    @Pattern(regexp="^1|2|3|4|5|6|7|8$", message="学制只能为1--8的整数")
    private String schoolLength;
    @Size(min=7, max=7, message="入学时间格式应为yyyy-MM")
    private String schoolEnterDate;
    @Size(min=7, max=7, message="入职时间格式应为yyyy-MM")
    private String unitEnterDate;
    @Size(min=7, max=7, message="参加工作时间格式应为yyyy-MM")
    private String unitStartDate;
    @Size(max=64)
    private String unitName;
    @Size(min=2, max=16)
    private String unitDepartment;
    @Size(min=1, max=1)
    private String unitPosition;
    @Size(min=11, max=14)
    private String unitPhone;
    @Pattern(regexp="^\\d{0,5}$", message="无效的工作单位电话分机号")
    private String unitPhoneExt;
    @Pattern(regexp="^\\d{6}$", message="无效的工作单位地址省份编码")
    private String unitProvinceCode;
    @Pattern(regexp="^\\d{6}$", message="无效的工作单位地址城市编码")
    private String unitCityCode;
    @Pattern(regexp="^\\d{6}$", message="无效的工作单位地址区县编码")
    private String unitTownCode;
    @Size(max=128)
    private String unitDetailAddress;
    @Size(min=1, max=1)
    private String unitBusinessType;
    @Size(min=1, max=1)
    private String unitIndustry;
    @Size(min=1, max=2)
    private String unitOccupation;
    @Pattern(regexp="^\\d{1,9}$", message="夸张的月收入")
    private String unitMonthIncome;
    @Pattern(regexp="^\\d{1,9}$", message="夸张的其它收入")
    private String otherIncome;
    @Pattern(regexp="^\\d{1,9}$", message="夸张的其它贷款金额")
    private String otherLoan;
    @NotBlank
    @Size(min=4, max=4)
    private String userIdentity;
    @Size(min=1, max=1)
    private String userCredit;
    @Size(min=1, max=1)
    private String userHousing;
    @Size(min=1, max=1)
    private String userCarInfo;
    @Pattern(regexp="^0|1$", message="担保方式只能传0或1")
    private String warrantMode;
    @Pattern(regexp="^0|1$", message="金融机构只能传0或1")
    private String financeOrganize;
    private List<LoanFile> fileList = new ArrayList<>();
    private List<LoanContact> contactList = new ArrayList<>();
    private List<LoanAddons> addonsList = new ArrayList<>();
    @NotBlank
    @Pattern(regexp="(^IOS|Android|WeChat|PCBrowser|MobileBrowser$)", message="客户端类型只能为IOS|Android|WeChat|PCBrowser|MobileBrowser")
    private String clientType;
    @Size(max=64)
    private String clientVersion;
    @Size(max=64)
    private String clientChannel;
    @Size(max=64)
    private String clientInVersion;
    @Size(max=64)
    private String clientCompany;
    @Size(max=64)
    private String clientTradeMark;
    @Size(max=64)
    private String clientModels;
    @Size(max=64)
    private String clientCompileId;
    @Size(max=64)
    private String clientIMEI;
    @Size(max=32)
    private String clientGPS;
    @Size(min=1, max=1)
    private String clientNetwork;
    @Size(max=512)
    private String notifyUrl;
    @Size(max=512)
    private String state;

    /**
     * 申请单文件
     */
    public static class LoanFile{
        private String fileId;
        private String fileCategory;
        public String getFileId() {
            return fileId;
        }
        public void setFileId(String fileId) {
            this.fileId = fileId;
        }
        public String getFileCategory() {
            return fileCategory;
        }
        public void setFileCategory(String fileCategory) {
            this.fileCategory = fileCategory;
        }
    }

    /**
     * 申请单联系人
     */
    public static class LoanContact{
        private String name;
        private String relation;
        private String phoneNo;
        private String address;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getRelation() {
            return relation;
        }
        public void setRelation(String relation) {
            this.relation = relation;
        }
        public String getPhoneNo() {
            return phoneNo;
        }
        public void setPhoneNo(String phoneNo) {
            this.phoneNo = phoneNo;
        }
        public String getAddress() {
            return address;
        }
        public void setAddress(String address) {
            this.address = address;
        }
    }

    /**
     * 申请单附加信息
     */
    public static class LoanAddons{
        private String type;
        private String value;
        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
    }

    public String getLoanPurpose() {
        return loanPurpose;
    }

    public void setLoanPurpose(String loanPurpose) {
        this.loanPurpose = loanPurpose;
    }

    public String getUseLifeInsurance() {
        return useLifeInsurance;
    }

    public void setUseLifeInsurance(String useLifeInsurance) {
        this.useLifeInsurance = useLifeInsurance;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankProvinceCode() {
        return bankProvinceCode;
    }

    public void setBankProvinceCode(String bankProvinceCode) {
        this.bankProvinceCode = bankProvinceCode;
    }

    public String getBankCityCode() {
        return bankCityCode;
    }

    public void setBankCityCode(String bankCityCode) {
        this.bankCityCode = bankCityCode;
    }

    public String getLiveProvinceCode() {
        return liveProvinceCode;
    }

    public void setLiveProvinceCode(String liveProvinceCode) {
        this.liveProvinceCode = liveProvinceCode;
    }

    public String getLiveCityCode() {
        return liveCityCode;
    }

    public void setLiveCityCode(String liveCityCode) {
        this.liveCityCode = liveCityCode;
    }

    public String getLiveTownCode() {
        return liveTownCode;
    }

    public void setLiveTownCode(String liveTownCode) {
        this.liveTownCode = liveTownCode;
    }

    public String getLiveDetailAddress() {
        return liveDetailAddress;
    }

    public void setLiveDetailAddress(String liveDetailAddress) {
        this.liveDetailAddress = liveDetailAddress;
    }

    public String getLivePhone() {
        return livePhone;
    }

    public void setLivePhone(String livePhone) {
        this.livePhone = livePhone;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getSchoolAddress() {
        return schoolAddress;
    }

    public void setSchoolAddress(String schoolAddress) {
        this.schoolAddress = schoolAddress;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolLength() {
        return schoolLength;
    }

    public void setSchoolLength(String schoolLength) {
        this.schoolLength = schoolLength;
    }

    public String getSchoolEnterDate() {
        return schoolEnterDate;
    }

    public void setSchoolEnterDate(String schoolEnterDate) {
        this.schoolEnterDate = schoolEnterDate;
    }

    public String getUnitEnterDate() {
        return unitEnterDate;
    }

    public void setUnitEnterDate(String unitEnterDate) {
        this.unitEnterDate = unitEnterDate;
    }

    public String getUnitStartDate() {
        return unitStartDate;
    }

    public void setUnitStartDate(String unitStartDate) {
        this.unitStartDate = unitStartDate;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getUnitDepartment() {
        return unitDepartment;
    }

    public void setUnitDepartment(String unitDepartment) {
        this.unitDepartment = unitDepartment;
    }

    public String getUnitPosition() {
        return unitPosition;
    }

    public void setUnitPosition(String unitPosition) {
        this.unitPosition = unitPosition;
    }

    public String getUnitPhone() {
        return unitPhone;
    }

    public void setUnitPhone(String unitPhone) {
        this.unitPhone = unitPhone;
    }

    public String getUnitPhoneExt() {
        return unitPhoneExt;
    }

    public void setUnitPhoneExt(String unitPhoneExt) {
        this.unitPhoneExt = unitPhoneExt;
    }

    public String getUnitProvinceCode() {
        return unitProvinceCode;
    }

    public void setUnitProvinceCode(String unitProvinceCode) {
        this.unitProvinceCode = unitProvinceCode;
    }

    public String getUnitCityCode() {
        return unitCityCode;
    }

    public void setUnitCityCode(String unitCityCode) {
        this.unitCityCode = unitCityCode;
    }

    public String getUnitTownCode() {
        return unitTownCode;
    }

    public void setUnitTownCode(String unitTownCode) {
        this.unitTownCode = unitTownCode;
    }

    public String getUnitDetailAddress() {
        return unitDetailAddress;
    }

    public void setUnitDetailAddress(String unitDetailAddress) {
        this.unitDetailAddress = unitDetailAddress;
    }

    public String getUnitBusinessType() {
        return unitBusinessType;
    }

    public void setUnitBusinessType(String unitBusinessType) {
        this.unitBusinessType = unitBusinessType;
    }

    public String getUnitIndustry() {
        return unitIndustry;
    }

    public void setUnitIndustry(String unitIndustry) {
        this.unitIndustry = unitIndustry;
    }

    public String getUnitOccupation() {
        return unitOccupation;
    }

    public void setUnitOccupation(String unitOccupation) {
        this.unitOccupation = unitOccupation;
    }

    public String getUnitMonthIncome() {
        return unitMonthIncome;
    }

    public void setUnitMonthIncome(String unitMonthIncome) {
        this.unitMonthIncome = unitMonthIncome;
    }

    public String getOtherIncome() {
        return otherIncome;
    }

    public void setOtherIncome(String otherIncome) {
        this.otherIncome = otherIncome;
    }

    public String getOtherLoan() {
        return otherLoan;
    }

    public void setOtherLoan(String otherLoan) {
        this.otherLoan = otherLoan;
    }

    public String getUserIdentity() {
        return userIdentity;
    }

    public void setUserIdentity(String userIdentity) {
        this.userIdentity = userIdentity;
    }

    public String getUserCredit() {
        return userCredit;
    }

    public void setUserCredit(String userCredit) {
        this.userCredit = userCredit;
    }

    public String getUserHousing() {
        return userHousing;
    }

    public void setUserHousing(String userHousing) {
        this.userHousing = userHousing;
    }

    public String getUserCarInfo() {
        return userCarInfo;
    }

    public void setUserCarInfo(String userCarInfo) {
        this.userCarInfo = userCarInfo;
    }

    public String getWarrantMode() {
        return warrantMode;
    }

    public void setWarrantMode(String warrantMode) {
        this.warrantMode = warrantMode;
    }

    public String getFinanceOrganize() {
        return financeOrganize;
    }

    public void setFinanceOrganize(String financeOrganize) {
        this.financeOrganize = financeOrganize;
    }

    public List<LoanFile> getFileList() {
        return fileList;
    }

    public void setFileList(List<LoanFile> fileList) {
        this.fileList = fileList;
    }

    public List<LoanContact> getContactList() {
        return contactList;
    }

    public void setContactList(List<LoanContact> contactList) {
        this.contactList = contactList;
    }

    public List<LoanAddons> getAddonsList() {
        return addonsList;
    }

    public void setAddonsList(List<LoanAddons> addonsList) {
        this.addonsList = addonsList;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public String getClientChannel() {
        return clientChannel;
    }

    public void setClientChannel(String clientChannel) {
        this.clientChannel = clientChannel;
    }

    public String getClientInVersion() {
        return clientInVersion;
    }

    public void setClientInVersion(String clientInVersion) {
        this.clientInVersion = clientInVersion;
    }

    public String getClientCompany() {
        return clientCompany;
    }

    public void setClientCompany(String clientCompany) {
        this.clientCompany = clientCompany;
    }

    public String getClientTradeMark() {
        return clientTradeMark;
    }

    public void setClientTradeMark(String clientTradeMark) {
        this.clientTradeMark = clientTradeMark;
    }

    public String getClientModels() {
        return clientModels;
    }

    public void setClientModels(String clientModels) {
        this.clientModels = clientModels;
    }

    public String getClientCompileId() {
        return clientCompileId;
    }

    public void setClientCompileId(String clientCompileId) {
        this.clientCompileId = clientCompileId;
    }

    public String getClientIMEI() {
        return clientIMEI;
    }

    public void setClientIMEI(String clientIMEI) {
        this.clientIMEI = clientIMEI;
    }

    public String getClientGPS() {
        return clientGPS;
    }

    public void setClientGPS(String clientGPS) {
        this.clientGPS = clientGPS;
    }

    public String getClientNetwork() {
        return clientNetwork;
    }

    public void setClientNetwork(String clientNetwork) {
        this.clientNetwork = clientNetwork;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
