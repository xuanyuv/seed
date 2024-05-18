package com.jadyer.seed.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jadyer.seed.test.model.FastjsonDataInfo;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FastjsonTest {
    /**
     * 测试Fastjson针对常见Java数据类型的序列化与反序列化
     */
    @Test
    public void dataTypeTest(){
        //List<Person> personList = JSON.parseArray(jsonString, Person.class);
        Test11 test11 = new Test11();
        test11.setName("铁面生");
        test11.setIsBuy("true");
        test11.setCurrentTime("2015-05-28 01:47:30");
        test11.setMoney("8000.58");
        String test11msg = JSON.toJSONString(test11);
        System.out.println("全部String类型的数据生成JSON为-->" + test11msg);
        System.out.println("----------------------------------------------------------------------------------------------------------------------");
        Test22 test22 = JSON.parseObject(test11msg, Test22.class);
        System.out.println("全部String类型生成的JSON解析为标准Java数据类型对象后的属性为" + ReflectionToStringBuilder.toString(test22, ToStringStyle.MULTI_LINE_STYLE));
        System.out.println("----------------------------------------------------------------------------------------------------------------------");
        Test33 test33 = new Test33();
        test33.setName("铁面生");
        test33.setIsBuy(true);
        test33.setCurrentTime(new Date());
        test33.setMoney(new BigDecimal("8000.58"));
        String test33msg = JSON.toJSONString(test33);
        System.out.println("标准Java数据类型的数据生成JSON为-->" + test33msg);
        System.out.println("----------------------------------------------------------------------------------------------------------------------");
        Test22 test22again = JSON.parseObject(test33msg, Test22.class);
        System.out.println("标准Java数据类型生成的JSON解析为标准Java数据类型对象后的属性为" + ReflectionToStringBuilder.toString(test22again, ToStringStyle.MULTI_LINE_STYLE));
    }
    private static class Test11{
        private String name;
        private String isBuy;
        private String money;
        private String currentTime;
        public String getName() {
            return name;
        }
        void setName(String name) {
            this.name = name;
        }
        public String getIsBuy() {
            return isBuy;
        }
        void setIsBuy(String isBuy) {
            this.isBuy = isBuy;
        }
        public String getMoney() {
            return money;
        }
        void setMoney(String money) {
            this.money = money;
        }
        public String getCurrentTime() {
            return currentTime;
        }
        void setCurrentTime(String currentTime) {
            this.currentTime = currentTime;
        }
    }
    private static class Test22{
        private String name;
        private boolean isBuy;
        private BigDecimal money;
        private Date currentTime;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public boolean isBuy() {
            return isBuy;
        }
        public void setBuy(boolean isBuy) {
            this.isBuy = isBuy;
        }
        public BigDecimal getMoney() {
            return money;
        }
        public void setMoney(BigDecimal money) {
            this.money = money;
        }
        public Date getCurrentTime() {
            return currentTime;
        }
        public void setCurrentTime(Date currentTime) {
            this.currentTime = currentTime;
        }
    }
    private static class Test33{
        private String name;
        private Boolean isBuy;
        private BigDecimal money;
        private Date currentTime;
        public String getName() {
            return name;
        }
        void setName(String name) {
            this.name = name;
        }
        public Boolean getIsBuy() {
            return isBuy;
        }
        void setIsBuy(Boolean isBuy) {
            this.isBuy = isBuy;
        }
        public BigDecimal getMoney() {
            return money;
        }
        void setMoney(BigDecimal money) {
            this.money = money;
        }
        public Date getCurrentTime() {
            return currentTime;
        }
        void setCurrentTime(Date currentTime) {
            this.currentTime = currentTime;
        }
    }


    /**
     * java.util.List<String>和List<Map<String,Object>>对象的生成与解析JSON
     */
    @Test
    public void listStringTest(){
        List<String> dataList = new ArrayList<>();
        dataList.add("铁面生");
        dataList.add("汪藏海");
        dataList.add("解连环");
        String jsonData = JSON.toJSONString(dataList);
        System.out.println("生成JSON-->" + jsonData);
        List<String> list = JSON.parseObject(jsonData, new TypeReference<List<String>>(){});
        for(int i=0,len=list.size(); i<len; i++){
            System.out.println("解析到["+len+"]个字符串,第["+(i+1)+"]个字符串为-->" + list.get(i));
        }
        System.out.println("-------------------------------------------------------------------");
        Map<String, Object> map11 = new HashMap<>();
        map11.put("鬼吹灯", "张佛爷");
        map11.put("藏海花", "张起灵");
        Map<String, Object> map22 = new HashMap<>();
        map22.put("青蚨门人", 1);
        map22.put("国术通神", 2);
        Map<String, Object> map33 = new HashMap<>();
        map33.put("盗墓笔记", "铁面生");
        map33.put("三国演义", "曹孟德");
        List<Map<String,Object>> datalist = new ArrayList<>();
        datalist.add(map11);
        datalist.add(map22);
        datalist.add(map33);
        jsonData = JSON.toJSONString(datalist);
        System.out.println("生成JSON-->" + jsonData);
        List<Map<String,Object>> list22 = JSON.parseObject(jsonData, new TypeReference<List<Map<String,Object>>>(){});
        for(int i=0,len=list22.size(); i<len; i++){
            System.out.println("解析到["+len+"]个对象,第["+(i+1)+"]个对象属性如下" + ReflectionToStringBuilder.toString(list22.get(i), ToStringStyle.MULTI_LINE_STYLE));
        }
    }


    /**
     * java.util.List<JavaBean>对象的生成与解析JSON
     */
    @Test
    public void listJavaBeanTest(){
        User user11 = new User();
        user11.setId(11);
        user11.setUsername("铁面生");
        user11.setPassword("02200059");
        User user22 = new User();
        user22.setId(22);
        user22.setUsername("汪藏海");
        user22.setPassword("02200060");
        User user33 = new User();
        user33.setId(33);
        user33.setUsername("解连环");
        user33.setPassword("02200061");
        List<User> dataList = new ArrayList<>();
        dataList.add(user11);
        dataList.add(user22);
        dataList.add(user33);
        String jsonData = JSON.toJSONString(dataList);
        System.out.println("生成JSON-->" + jsonData);
        //也可采用下面的方式解析List<User> list = JSON.parseArray(jsonData, User.class);
        List<User> list = JSON.parseObject(jsonData, new TypeReference<List<User>>(){});
        for(int i=0,len=list.size(); i<len; i++){
            System.out.println("解析到["+len+"]个对象,第["+(i+1)+"]个对象属性如下" + ReflectionToStringBuilder.toString(list.get(i), ToStringStyle.MULTI_LINE_STYLE));
        }
    }
    private static class User{
        private int id;
        private String username;
        private String password;
        public int getId() {
            return id;
        }
        void setId(int id) {
            this.id = id;
        }
        public String getUsername() {
            return username;
        }
        void setUsername(String username) {
            this.username = username;
        }
        public String getPassword() {
            return password;
        }
        void setPassword(String password) {
            this.password = password;
        }
    }
    /**
     * java.util.Map<String,List<Object>>对象的生成与解析JSON
     */
    @Test
    public void mapTest(){
        User user11 = new User();
        user11.setId(11);
        user11.setUsername("铁面生");
        user11.setPassword("02200059");
        User user22 = new User();
        user22.setId(22);
        user22.setUsername("汪藏海");
        user22.setPassword("02200060");
        User user33 = new User();
        user33.setId(33);
        user33.setUsername("解连环");
        user33.setPassword("02200061");
        List<User> dataList = new ArrayList<>();
        dataList.add(user11);
        dataList.add(user22);
        dataList.add(user33);
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("comment", "这是测试java.util.Map<String,List<Object>>对象的生成与解析JSON");
        dataMap.put("data", dataList);
        String jsonData = JSON.toJSONString(dataMap);
        System.out.println("生成JSON-->" + jsonData);
        Map<String, String> map = JSON.parseObject(jsonData, new TypeReference<Map<String, String>>(){});
        System.out.println("解析到comment-->" + map.get("comment"));
        System.out.println("解析到data-->" + map.get("data"));
        List<User> list = JSON.parseObject(map.get("data"), new TypeReference<List<User>>(){});
        for(int i=0,len=list.size(); i<len; i++){
            System.out.println("解析到["+len+"]个对象,第["+(i+1)+"]个对象属性如下" + ReflectionToStringBuilder.toString(list.get(i), ToStringStyle.MULTI_LINE_STYLE));
        }
    }


    @Test
    public void keyTest(){
        String data = "{\"SERVICE\":{\"SERVICE_HEADER\":{\"SERVICE_ID\":\"obtainAppNo\",\"ORG\":\"000000000001\",\"CHANNEL_ID\":\"BANK\",\"ACQ_ID\":\"10000000\",\"SUB_TERMINAL_TYPE\":\"Web\",\"SERVICESN\":\"BAIDU20151216224602780\",\"REQUEST_TIME\":\"20151216224602\",\"VERSION_ID\":\"01\",\"SERV_RESPONSE\":{\"STATUS\":\"S\",\"CODE\":\"0000\",\"DESC\":\"????\"}},\"SERVICE_BODY\":{\"RESPONSE\":{\"APP_NO\":\"20151216220000006553\"}}}}";
        Map<String, Map<String, Map<String, String>>> dataMap = JSON.parseObject(data, new TypeReference<Map<String, Map<String, Map<String, String>>>>(){});
        System.out.println(dataMap.get("SERVICE").get("SERVICE_HEADER").get("SERV_RESPONSE"));
        System.out.println(dataMap.get("SERVICE").get("SERVICE_BODY").get("RESPONSE"));
        System.out.println(JSONObject.parseObject(dataMap.get("SERVICE").get("SERVICE_HEADER").get("SERV_RESPONSE")).get("CODE"));
        System.out.println(JSONObject.parseObject(dataMap.get("SERVICE").get("SERVICE_BODY").get("RESPONSE")).get("APP_NO"));
        System.out.println("----------------------------------------------------------------------------------------------");
        String  mydata = "{\"loginname\":\"lisi\",\"blackList\":\"\",\"param\":\"\",\"redblackBackList\":{\"requestId\":\"be7fa8d1-52616687680c\",\"redblackRequestId\":\"ed59adf3-9037dbe4a384\",\"redblackBack\":[{\"redblackType\":[{\"unitedName\":\"A级纳税人\",\"clTime\":\"2017-7-20 09:10:00\",\"originDept\":\"税务总局\",\"unionPairList\":{\"unionPair\":[{\"unionPairContent\":[{\"content\":\"建立绿色通道\",\"title\":\"措施\"}]}]},\"remark\":\"remark-tongdao\",\"handleType\":\"1\",\"redblackDetailList\":null,\"unitedId\":\"8\",\"action\":\"action-tongdao\",\"unitedType\":\"red\",\"feedbackResult\":\"feedbackResult-tongdao\"}],\"name\":\"远古公司\",\"code\":\"91500250\"}]},\"unitedType\":\"\",\"departmentname\":\"运管局\",\"personname\":\"小李飞刀\",\"redList\":\"\"}";
        //Map<String, Map<String, List<Map<String, List<Map<String, Map<String, List<Map<String, List<Map<String, String>>>>>>>>>>> myMap = JSON.parseObject(mydata, new TypeReference<Map<String, Map<String, List<Map<String, List<Map<String, Map<String, List<Map<String, List<Map<String, String>>>>>>>>>>>>(){});
        FastjsonDataInfo di = JSON.parseObject(mydata, FastjsonDataInfo.class);
        System.out.println(ReflectionToStringBuilder.toString(di, ToStringStyle.MULTI_LINE_STYLE));
        System.out.println(ReflectionToStringBuilder.toString(di.getRedblackBackList(), ToStringStyle.MULTI_LINE_STYLE));
    }


    /**
     * 测试json解析为子类对象时，是否会解析包含的父类属性
     */
    @Test
    public void javabeanParse(){
        UserChild uc = new UserChild();
        uc.setId(2);
        uc.setName("玄玉");
        uc.setSex("M");
        String jsonStr = JSON.toJSONString(uc);
        System.out.println("生成了-->" + jsonStr);
        UserChild userChild = JSON.parseObject(jsonStr, UserChild.class);
        System.out.println("解析到-->" + ReflectionToStringBuilder.toString(userChild, ToStringStyle.MULTI_LINE_STYLE));
    }
    static class UserParent{
        private int id;
        private String name;
        public int getId() {
            return id;
        }
        void setId(int id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        void setName(String name) {
            this.name = name;
        }
    }
    private static class UserChild extends UserParent {
        private String sex;
        public String getSex() {
            return sex;
        }
        void setSex(String sex) {
            this.sex = sex;
        }
    }
//    /**
//     * 一个关于Jackson序列化与反序列化Map里面包含普通对象和List的JSON
//     */
//    @Test
//    @SuppressWarnings("unchecked")
//    public void jscksonMapTest() throws JsonParseException, JsonMappingException, IOException{
//        String loanInfo = "{\"attachments\":{\"ident_front\":\"实际的Base64的byte[]对应的字符串很长\",\"ident_back\":\"实际的Base64的byte[]对应的字符串很长\"},\"loan\":{\"productId\":\"1000001\"},\"bankCard\":{\"bankCardNo\":\"622600000000\",\"bankBranchProvinceCode\":\"230000\",\"bankBranchCityCode\":\"230100\"}}";
//        String customerInfo = "{\"customer\":{\"name\":\"汪藏海\",\"mobile\":\"13612345678\",\"ident\":\"232126000000000000\",\"identCardExpire\":\"2020-09-20 00:00:00\",\"currentAddrProvince\":\"黑龙江省\",\"currentAddrProvinceCode\":\"230000\",\"currentAddrCity\":\"哈尔滨市\",\"currentAddrCityCode\":\"230100\",\"currentAddrDistrict\":\"巴彦县\",\"currentAddrDetail\":\"黑龙江省哈尔滨市巴彦县兴隆镇\",\"schoolName\":null,\"monthlyIncomeWork\":null,\"unitName\":null,\"unitPosition\":null,\"unitPhoneArea\":null,\"unitPhone\":null,\"unitPhoneExt\":null},\"contacts\":[{\"name\":\"铁面生\",\"relationShip\":\"其他\",\"phone\":\"13602200059\"},{\"name\":\"沈浪\",\"relationShip\":\"朋友\",\"phone\":\"13602200060\"}]}";
//        Map<String, Map<String, String>> loanInfoMap = new ObjectMapper().readValue(loanInfo, Map.class);
//        System.out.println(loanInfoMap.get("loan").get("productId"));
//        System.out.println(loanInfoMap.get("bankCard").get("bankCardNo"));
//        System.out.println(loanInfoMap.get("attachments").get("ident_front"));
//        System.out.println("------------------------------------------------------------------------");
//        Map<String, Map<String, String>> customerInfoMap11 = new ObjectMapper().readValue(customerInfo, Map.class);
//        System.out.println(customerInfoMap11.get("customer").get("name"));
//        System.out.println("------------------------------------------------------------------------");
//        Map<String, List<Map<String,String>>> customerInfoMap22 = new ObjectMapper().readValue(customerInfo, Map.class);
//        System.out.println(customerInfoMap22.get("contacts").get(0).get("name"));
//        System.out.println(customerInfoMap22.get("contacts").get(1).get("name"));
//    }


    @Test
    public void jsonArrayTest(){
        // {"dataList":[{"id":99,"name":"秦仲海"},{"id":88,"name":"杨肃观"},{"id":77,"name":"伍定远"}],"msg":"成功","code":"0000"}
        String jsonStr = "{\"dataList\":[{\"id\":99,\"name\":\"秦仲海\"},{\"id\":88,\"name\":\"杨肃观\"},{\"id\":77,\"name\":\"伍定远\"}],\"msg\":\"成功\",\"code\":\"0000\"}";
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        String code = jsonObject.get("code").toString();
        String dataList = jsonObject.get("dataList").toString();
        // String jsonStr = "[{\"dataList\":[{\"id\":99,\"name\":\"秦仲海\"},{\"id\":88,\"name\":\"杨肃观\"},{\"id\":77,\"name\":\"伍定远\"}],\"msg\":\"成功\",\"code\":\"0000\"}]";
        // JSONArray jsonArray = JSONArray.parseArray(jsonStr);
        // String code = ((JSONObject)jsonArray.get(0)).get("code").toString();
        // String dataList = ((JSONObject)jsonArray.get(0)).get("dataList").toString();
        List<MyJSONArryObjectData> MyDataList = JSON.parseObject(dataList, new TypeReference<List<MyJSONArryObjectData>>(){});
        System.out.println("code = " + code);
        System.out.println("dataList = " + dataList);
        System.out.println("MyJSONArryDataList = " + JSON.toJSONString(MyDataList));
    }
    static class MyJSONArryObjectData {
        private int id;
        private String name;
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }
}