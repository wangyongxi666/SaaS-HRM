package com.ihrm.common.util;

import org.springframework.cglib.beans.BeanMap;

import java.util.HashMap;
import java.util.Map;

public class BeanMapUtils {

    /**
     * 将对象属性转化为map结合
     */
    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = new HashMap<>();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                map.put(key+"", beanMap.get(key));
            }
        }
        return map;
    }

    /**
     * 将map集合中的数据转化为指定对象的同名属性中
     */
    public static <T> T mapToBean(Map<String, Object> map,Class<T> clazz) throws Exception {
        System.out.println( map.get("enVisible"));
        Object enVisible = map.get("enVisible");
        Integer enVisible1 = Integer.parseInt(enVisible.toString());
        map.put("enVisible", enVisible1);

        //反射生成对象
        T bean = clazz.newInstance();
        //通过工具绑定对象
        BeanMap beanMap = BeanMap.create(bean);
        //将map数据通过工具设置入对象中
        beanMap.putAll(map);
        return bean;
    }
}
