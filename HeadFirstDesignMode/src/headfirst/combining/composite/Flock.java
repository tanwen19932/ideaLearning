package headfirst.combining.composite;

import java.util.Iterator;
import java.util.ArrayList;

public class Flock implements Quackable {
	ArrayList quackers = new ArrayList();
 
	public void add(Quackable quacker) {
		quackers.add(quacker);
	}
 
	public void quack() {
		Iterator iterator = quackers.iterator();
		while (iterator.hasNext()) {
			Quackable quacker = (Quackable)iterator.next();
			quacker.quack();
		}
		String a = "<!--type:txt--><!--type:str--> <div class=\"cpage\">#page: 1 #</div> ##br####br####br####br####br##　　9资料图。陈超 摄##br####br##　　中新网昆明10月1日电 (钱克勤)记者1日从昆明铁路局了解到，当日为国庆黄金周客流最高峰日，预计发送旅客达19.8万人次，同比增长将超过20%、突破昆明铁路局历史最高纪录。##br####br##　　9月30日，昆明铁路局迎来国庆黄金周客流出行高峰，全局发送旅客14.9万人，同比增加17.4%，其中大理丽江方向发送旅客4.3万人，同比增加39.6%。##br####br##　　从车票发售情况看，假日期间云南铁路客流主要以旅游、探亲为主，长途客流集中在北京、广州、成都等方向，短途客流以大理、丽江、河口方向为主。##br####br##　　为满足热门方向旅客出行需求，昆明铁路局在丽江、河口、宣威等方向增开列车60多趟，同时通过增设售票、改签及退票窗口，减少旅客排队等候时间；合理安排旅客购票、候车、乘降、进出站流线，为老弱病残孕等重点旅客提供帮扶服务，让旅客安全、方便、温馨出行。(完)##br##";
	}
 
	public String toString() {
		return "Flock of Quackers";
	}
}
