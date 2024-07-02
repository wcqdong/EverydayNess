package org.evd.game.ConnService;

import org.evd.game.annotation.Serializable;
import org.evd.game.annotation.SerializerField;

import java.util.*;

@Serializable
public class ConnInfo extends ConnInfoBase{
    @SerializerField
    public int a;
    @SerializerField
    public long longValue;
    @SerializerField
    public String stringValue;
    @SerializerField
    public int[] intArrayValue;
    @SerializerField
    public String[] stringArrayValue;

    @SerializerField
    public List<Integer> list = new ArrayList<>();
    @SerializerField
    public List<Integer> list1 = new LinkedList<>();
    @SerializerField
    public List<CoInfo> list2 = new LinkedList<>();

    @SerializerField
    public Map<Integer, String> map = new HashMap<>();
    @SerializerField
    public Map<Integer, String> map1 = new LinkedHashMap<>();
    @SerializerField
    public Map<Integer, CoInfo> map2 = new LinkedHashMap<>();

    @SerializerField
    public Set<Integer> set1 = new HashSet<>();
    @SerializerField
    public Set<String> set2 = new HashSet<>();
    @SerializerField
    public Set<CoInfo> set3 = new HashSet<>();

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public long getLongValue() {
        return longValue;
    }

    public void setLongValue(long l) {
        this.longValue = l;
    }

    public String getStringValue() {

        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }


    public int[] getIntArrayValue() {
        return intArrayValue;
    }

    public String[] getStringArrayValue() {
        return stringArrayValue;
    }

    public void setIntArrayValue(int[] ints) {
        intArrayValue = ints;
    }

    public void setStringArrayValue(String[] strings) {
        stringArrayValue = strings;
    }

    public List<Integer> getList() {
        return list;
    }

    public void setList(List<Integer> list) {
        this.list = list;
    }

    public List<Integer> getList1() {
        return list1;
    }

    public void setList1(List<Integer> list1) {
        this.list1 = list1;
    }

    public List<CoInfo> getList2() {
        return list2;
    }

    public void setList2(List<CoInfo> list2) {
        this.list2 = list2;
    }

    public Map<Integer, String> getMap() {
        return map;
    }

    public void setMap(Map<Integer, String> map) {
        this.map = map;
    }

    public Map<Integer, String> getMap1() {
        return map1;
    }

    public void setMap1(Map<Integer, String> map1) {
        this.map1 = map1;
    }

    public Map<Integer, CoInfo> getMap2() {
        return map2;
    }

    public void setMap2(Map<Integer, CoInfo> map2) {
        this.map2 = map2;
    }

    public Set<Integer> getSet1() {
        return set1;
    }

    public void setSet1(Set<Integer> set1) {
        this.set1 = set1;
    }

    public Set<String> getSet2() {
        return set2;
    }

    public void setSet2(Set<String> set2) {
        this.set2 = set2;
    }

    public Set<CoInfo> getSet3() {
        return set3;
    }

    public void setSet3(Set<CoInfo> set3) {
        this.set3 = set3;
    }
}
