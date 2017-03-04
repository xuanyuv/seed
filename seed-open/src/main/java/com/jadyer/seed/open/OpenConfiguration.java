package com.jadyer.seed.open;

import com.jadyer.seed.open.constant.OpenConstant;
import com.jadyer.seed.open.filter.OpenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 玄玉<https://jadyer.github.io/> on 2016/5/8 19:32.
 */
@Configuration
public class OpenConfiguration {
    //@Resource
    //private JedisCluster jedisCluster;
    //
    //@Bean
    //@Order(5)
    //public Filter redisFilter(){
    //    List<String> filterMethodList = new ArrayList<>();
    //    filterMethodList.add(OpenConstant.METHOD_boot_loan_submit);
    //    filterMethodList.add(OpenConstant.METHOD_boot_loan_agree);
    //    return new RedisFilter(jedisCluster, filterMethodList);
    //}


    @Bean
    @Order(4)
    public Filter openFilter(){
        Map<String, String> appsecretMap = new HashMap<>();
        appsecretMap.put("670", "cPs6euPuvtsru2I3vmYb2Q");
        appsecretMap.put("770", "PDlTxZ8Vql5Y8owPSU6hzw");
        appsecretMap.put("870", "Xy0KEVSWmfl97J4wLKZJwg");
        appsecretMap.put("970", "{\"publicKey\":\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkKna17e0o5kR9Sjd1OsUfWPADhqbnUU8VScqCc0D3fsPCIMKgk6dZrL7O1Z7WGdowM1VHHftjpJKbyk33s4DnuqrgiUAeq1sBFB3YIQw307LmH79bP7mWd5iNk9NDAJXKV1spqhBpaBONhi63CcQ7OWpo_puWW8xbHjCfaF1nR7NeKSR3mbTn71AhAmRw243oIrmUVe9XPhU2syLxvJ9z2b_hDPDMY1X1hyoObrIU_zjP24bi6XuXQhGHMCH8OSrv7MbnXWbx72wjaL4jHzNpzxJQ9DMQEVvQTXkywHisSUf0FND3l7_Y1kGEjT9IujQlRUAcV3lviFqGUXrkYOhSwIDAQAB\",\"openPublicKey\":\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl0Oc-htdurUaW537t7lDSKdRoHLrzFESz_0P8UL4oImLH_W4JKw90bi1yhZNIK1-FLFk-xosEgjxm7sV_KYytS6BIxx1efuy6yMErlP06nnQrUz4aZcKSuQK-jddJZ7_RQUhgqB9h6bjI3koRUP2YY4GUNX3y4t45J-jqDHLtiGdcv8wYDqAertBZXKIanJ78dXC0ip29SGiqZ_qlMolma5AK0w5M_2r7cczTT7QCYqhycvF27wArOYXzCEp-3hcSac8meWpoo9aIJFhETfmcaqyiuR9KRVMpUCQqC0bihl1IuOSOKft9VCKVoleECUFoXTsfb1mEeh5p6lfDKyL1wIDAQAB\",\"openPrivateKey\":\"MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCXQ5z6G126tRpbnfu3uUNIp1GgcuvMURLP_Q_xQvigiYsf9bgkrD3RuLXKFk0grX4UsWT7GiwSCPGbuxX8pjK1LoEjHHV5-7LrIwSuU_TqedCtTPhplwpK5Ar6N10lnv9FBSGCoH2HpuMjeShFQ_ZhjgZQ1ffLi3jkn6OoMcu2IZ1y_zBgOoB6u0Flcohqcnvx1cLSKnb1IaKpn-qUyiWZrkArTDkz_avtxzNNPtAJiqHJy8XbvACs5hfMISn7eFxJpzyZ5amij1ogkWERN-ZxqrKK5H0pFUylQJCoLRuKGXUi45I4p-31UIpWiV4QJQWhdOx9vWYR6HmnqV8MrIvXAgMBAAECggEAe3vx4WeHy8zemJ5PCLwQBna5N0-52VDyF6MSabVvfLbsQYn56s5FpTiyByjPi_a177SlqCvEgHCWtLg1CcyvpqMxPvWTnaJxfG1Y--uEDAqn5WHphWGNQHgL0hpySy0SXUkEAzx7XctKsAHPc0Q5FYi49TLHmH3iklkUZFFKaQKasrEtSV95KQU8J6A8JNEKgdNexLITGxu7Br7h0u8UrFwrR2DZrqQne25sOaGZNQo2Ge5Kb59Uk780V9igCrApgGeo2jH7wU6zt4rM2aHriSFAaMACiDjBkYpTPBsl_h0YRX6Be4kUF683C_Nwuxu3eUKM1Q8DO-N4M_rSaQKdqQKBgQD82PvI5gffhH38iIHJmXTI0blwTox_KHrtD1ws5KOTtCxzS0Aj3Cf9d7pZQ62RJx6-KM_geBZQ1sG1umqNiEj7Loz-Q0gR6XpcrnYLJTGQLgB7sdGweLJ9kvAeEc0JIizfRGUg6vNsAzRUr-RQEl2h_qan81XPjsOZ6S57PuYU9QKBgQCZJmeQIyQzFlqknFd0oVFOQ5KR8-lD11FSOLbRA_H-oyjZg7Eu6VxCAKMrAhbdyPkY7YR54tKrVphohhuJNoGM7H3RNpridm4dUTGAoWPc6auh_1sajHk2ij_tHR4rD8QER3jffcgbyvaGSA7jzfSzMNZm11Gb9nto1fICbuE-GwKBgBijRBWVZJsBHA2pd4wfaePPm9Q3szUIysGix4aQWOghnFs3r-5Om471RbFBCP6l4zcAXxEuYm5KHuIIYi2cZBAlvxNflvw31faGAXagjy3Pbbif4SDjAfF-ietELMiBDgJLddc5TbLutQCzivnKcFlCqRagfXIWJTUwJZlYza6pAoGAQdwBFzv1ej63tihBd7dTtptacYd5naH_p6MaAyJ2M7HIhFmcnXbfJMcPZBRe7vtZG74whRXk0KfJnNFnEAeviIG-zXR1AMQ7MEJVTmKZBkw6cczVScJ08d5cBUTLT2tVOR3fPgTiRAlxIGfmd1J_U3vCB7G6t540je3LR_6UwFcCgYEAhrQypdoINiujxn-mXavBtA69Z3uK0XneQfRbsE9kQbmsTbdpJw-1-Ddsg28pAf-s_eTOYa-RoQ5-Y_rqGH508ijn1APIC4NKlSIp_a6Q9NoDpsEU2v6sdpKr9XQgysDpcosy4WoGrFgMg4qSYt6tlr-o8R106skWtWNEdSrSqJM\"}");
        Map<String, List<String>> apiGrantMap = new HashMap<>();
        List<String> list670 = new ArrayList<>();
        list670.add(OpenConstant.METHOD_boot_file_upload);
        list670.add(OpenConstant.METHOD_boot_loan_submit);
        List<String> list770 = new ArrayList<>();
        list770.add(OpenConstant.METHOD_boot_file_upload);
        list770.add(OpenConstant.METHOD_boot_loan_submit);
        list770.add(OpenConstant.METHOD_boot_loan_get);
        List<String> list870 = new ArrayList<>();
        list870.add(OpenConstant.METHOD_boot_file_upload);
        list870.add(OpenConstant.METHOD_boot_loan_submit);
        list870.add(OpenConstant.METHOD_boot_loan_get);
        list870.add(OpenConstant.METHOD_boot_loan_agree);
        list870.add(OpenConstant.METHOD_boot_apidoc_h5);
        List<String> list970 = new ArrayList<>();
        list970.add(OpenConstant.METHOD_boot_loan_get);
        apiGrantMap.put("670", list670);
        apiGrantMap.put("770", list770);
        apiGrantMap.put("870", list870);
        apiGrantMap.put("970", list970);
        return new OpenFilter("/router/rest", apiGrantMap, appsecretMap);
    }
}