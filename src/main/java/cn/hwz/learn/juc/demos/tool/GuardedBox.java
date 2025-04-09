package cn.hwz.learn.juc.demos.tool;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author needleHuo
 * @version jdk11
 * @description 盒子类，
 * @since 2025/3/14
 */
@Slf4j(topic = "c.GuardedBox")
public class GuardedBox {

    private static int beginGuardId;

    private static Map<Integer,GuardedObj> grardedMap = new HashMap<>();

    private static synchronized Integer getGuardId(){
        beginGuardId++;
        return beginGuardId;
    }

    /**
     * 生成guardObj
     * @return
     */
    public static int createGuardedObj(){
        Integer guardId = getGuardId();
        GuardedObj guardedObj = new GuardedObj();
        grardedMap.put(guardId,guardedObj);
        return guardId;
    }

    /**
     * 获取结果
     * @param mills
     * @return
     * @throws InterruptedException
     */
    public static Object getObject(int guardId,long mills) throws InterruptedException {
        GuardedObj guardedObj = grardedMap.get(guardId);
        return guardedObj.get(mills);
    }

    /**
     * 完成结果
     * @param id
     * @param object
     */
    public static void completeGuard(Integer id,Object object){
        GuardedObj o = grardedMap.get(id);
        o.complete(object);
        grardedMap.remove(id);
    }


    /**
     * 用的hashTable，没有线程安全问题
     * @return
     */
    public static List<Integer> getAllGuardId(){
        List<Integer> ids = new ArrayList<>();
        for (Map.Entry<Integer,GuardedObj> entry:grardedMap.entrySet()){
            ids.add(entry.getKey());
        }
        return ids;
    }

}
