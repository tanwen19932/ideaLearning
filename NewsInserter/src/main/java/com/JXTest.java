package com;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.lang.model.type.NullType;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.ArrayUtils;

public class JXTest {
    public static XMLGregorianCalendar convertToXMLGregorianCalendar(Date date) {

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        XMLGregorianCalendar gc = null;
        try {
            gc = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return gc;
    }

    @SuppressWarnings("deprecation")
    public static void main(String[] args) {
        // NewSearchAnalyseI searchAnalyseI =new
        // NewSearchAnalyseService().getNewSearchAnalysePort();
        //
        // String string=searchAnalyseI.getNewSearchAnalyse("上海", "", "", "",
        // "", 1,
        // 1000,"[\"keywords\",\"summary\",\"locationDistribution\",\"days\",\"opinionLeader\",\"transCount\"]");
        // System.out.println("客户端:"+string);
        //
        //// SearchI searchI=new SearchService().getSearchPort();
        //// Date beginDate=new Date(2016-1900,4-1,1);
        //// Date endDate=new Date(2016-1900,6-1,29);
        //// String string2=searchI.getSearch("地球",
        // convertToXMLGregorianCalendar(beginDate),
        // convertToXMLGregorianCalendar(endDate),false, "", "", 1, 10);
        //// System.out.println("客户端:"+string2);
        ////
        // NewRealTimeAnalyseI newRealTimeAnalyseI=new
        // NewRealTimeAnalyseService().getNewRealTimeAnalysePort();
        // String string2=newRealTimeAnalyseI.getRealTimeAnalyse("北京", "", "",
        // 1, 1000,
        // "[\"keywords\",\"locationDistribution\",\"opinionLeader\"]");
        // System.out.println("客户端:"+string2);

        // Object a[]=null;
        // Object b[]=null;
        // Object c[]=(Object[]) ArrayUtils.addAll(a,b);
        // System.out.println(c);

    }
}
