import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

/**
 * @author yolo
 * @date 2020/2/26-15:51
 */
public class TestGson {
    public static void main(String[] args) {
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("name","曹操");
        hashMap.put("skill1","剑气");
        hashMap.put("skill2","三段跳");
        hashMap.put("skill3","加攻击并吸血");
        hashMap.put("skill4","加攻速");

        //通过map转成JSON结构字符串
        //1.创建gson对象
        Gson gson=new GsonBuilder().create();
        //2.使用toJson方法把键值对转成字符串
        String str=gson.toJson(hashMap);
        System.out.println(str);
        //{"skill3":"加攻击并吸血","skill4":"加攻速","skill1":"剑气","skill2":"三段跳","name":"曹操"}
        //键值对之间得顺序不保证
    }
}
