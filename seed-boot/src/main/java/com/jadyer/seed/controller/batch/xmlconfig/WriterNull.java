package com.jadyer.seed.controller.batch.xmlconfig;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * 空的Writer：啥都不做
 * Created by 玄玉<https://jadyer.cn/> on 2019/7/22 20:11.
 */
public class WriterNull implements ItemWriter<T> {
    @Override
    public void write(List<? extends T> items) {
        //nothing to do
    }
}