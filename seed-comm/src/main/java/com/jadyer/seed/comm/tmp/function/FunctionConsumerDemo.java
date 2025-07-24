package com.jadyer.seed.comm.tmp.function;

import java.util.function.Consumer;

/**
 * --------------------------------------------------------------------------------------------------------------------
 * java.util.function.Consumer 正好与 Supplier 相反
 * 它不生产数据，而是消费数据，其数据类型由泛型决定
 * --------------------------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://www.xuanyuv.com> on 2025/7/24 17:51.
 */
public class FunctionConsumerDemo {
    public static <T> void work01(T data, Consumer<T> consumer){
        consumer.accept(data);
    }


    public static <T> void work02(T data, Consumer<T> consumer01, Consumer<T> consumer02){
        // 对两个Consumer接口进行消费，谁写在前面就先消费谁
        // 这里通过consumer01连接consumer02，先执行consumer01再执行consumer02
        consumer01.andThen(consumer02).accept(data);
    }


    public static void work03(String[] datas, Consumer<String> consumer01, Consumer<String> consumer02){
        for (String data : datas) {
            consumer01.andThen(consumer02).accept(data);
        }
    }


    public static void main(String[] args) {
        work01("秦仲海", (String name) -> {
            // 对传递的字符串进行消费，消费方式：先原样输出，再反转后输出
            System.out.print(name + "\t");
            System.out.println(new StringBuilder().append(name).reverse());
        });

        work02("VeryWell",
                (name) -> {
                    System.out.print(name.toUpperCase() + "，");
                    System.out.print(new StringBuilder().append(name).reverse() + "\t");
                },
                (name) -> {
                    System.out.print(name.toLowerCase() + "，");
                    System.out.println(new StringBuilder().append(name).reverse());
                }
        );

        String[] datas = {"卢云，男", "顾倩兮，女", "琼芳，女"};
        work03(datas,
                (data) -> {
                    // 消费方式：打印姓名
                    String name = data.split("，")[0];
                    System.out.print("姓名：" + name + "\t");
                },
                (data) -> {
                    // 消费方式：打印性别
                    String sex = data.split("，")[1];
                    System.out.println("性别：" + sex);
                }
        );
    }
}
