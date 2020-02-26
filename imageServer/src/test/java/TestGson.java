import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

/**
 * @author yolo
 * @date 2020/2/26-15:51
 */
class Hero {
    public String name;
    public String skill1;
    public String skill2;
    public String skill3;
    public String skill4;
}
public class TestGson {
    public static void main(String[] args) {
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("name","曹操");
        hashMap.put("skill1","剑气");
        hashMap.put("skill2","三段跳");
        hashMap.put("skill3","加攻击并吸血");
        hashMap.put("skill4","加攻速");

        Hero hero = new Hero();
        hero.name = "曹操";
        hero.skill1 = "剑气";
        hero.skill2 = "三段跳";
        hero.skill3 = "加攻击并吸血";

        //通过map转成JSON结构字符串
        //1.创建gson对象
        Gson gson=new GsonBuilder().create();
        //2.使用toJson方法把键值对转成字符串
        String str1=gson.toJson(hashMap);
        String str2=gson.toJson(hero);
        System.out.println(str2);
        //{"skill3":"加攻击并吸血","skill4":"加攻速","skill1":"剑气","skill2":"三段跳","name":"曹操"}
    }
}
