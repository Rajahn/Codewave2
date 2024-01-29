package edu.vt.ranhuo.codewaveserver.multiThreadTest;

import java.util.*;

public class Test2 {
    public static  int minimumPushes(String word) {
        HashMap<Character,Integer> map = new HashMap<>();
        for(char c : word.toCharArray()){
            map.put(c,map.getOrDefault(c,0)+1);
        }
        PriorityQueue<Map.Entry<Character,Integer>> maxq = new PriorityQueue<>((a,b)-> b.getValue()-a.getValue());
        for(Map.Entry<Character,Integer> entry: map.entrySet()){
            maxq.add(entry);
        }
        HashSet<Character> set1 = new HashSet<>();
        HashSet<Character> set2 = new HashSet<>();
        HashSet<Character> set3 = new HashSet<>();
        HashSet<Character> set4 = new HashSet<>();
        int count=1;

        while(maxq.size()>0){
            char c = maxq.poll().getKey();

            if(count<=8){
                set1.add(c);
            } else if(count>8 && count<=16){
                set2.add(c);
            }else if(count>16 && count<=24){
                set3.add(c);
            }else {
                set4.add(c);
            }
            count++;
        }

        int res = 0;

        for(Character c : set1){
            res+= map.get(c);
        }

        for(Character c: set2){
            res+= map.get(c)*2;
        }

        for(Character c: set3){
            res+= map.get(c)*3;
        }


        for(Character c : set4){
            res+= map.get(c) *4;
        }

        return res;
    }

    public static void main(String[] args) {
        System.out.println(minimumPushes("aabbccddeeffgghhiiiiii"));
    }
}
