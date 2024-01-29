package edu.vt.ranhuo.codewaveserver.utils;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class BeanCopyUtils {
    private BeanCopyUtils(){

    }

    public static <V> V copyBean(Object source,Class<V> targetClass){
        V target = null;
        try{
            target = targetClass.newInstance();
            BeanUtils.copyProperties(source,target);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return target;
    }

    public static <O,V> List<V> copyList(List<O> sourceList,Class<V> targetClass){
        List<V> targetList = new ArrayList<>();
        for(O source:sourceList){
            V target = copyBean(source,targetClass);
            targetList.add(target);
        }
        return targetList;
    }


}
