package tw.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.hadoop.hbase.util.Bytes;

import news.News;

public class ReflectUtil {

    public static Field[] getObjFields(Object o) {
        // 获取f对象对应类中的所有属性域
        Field[] fields = o.getClass().getDeclaredFields();
        for (int i = 0, len = fields.length; i < len; i++) {
            // 对于每个属性，获取属性名
            getObjFValue(o, fields[i]);
        }

        return fields;
    }

    public static Object getObjFValue(Object o, Field f) {
        Object o2 = null;

        try {
            // 获取原来的访问控制权限
            boolean accessFlag = f.isAccessible();
            // 修改访问控制权限
            f.setAccessible(true);
            o2 = f.get(o);
            // 恢复访问控制权限
            f.setAccessible(accessFlag);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }

        // String varName = f.getName();
        // String varType = f.getType().getSimpleName();
        // System.out.println("传入的对象中包含一个如下的变量："+varType+" " + varName + " = " +
        // o2);

        return o2;
    }

    public static Method[] getObjMethods(Object f) {
        // 获取f对象对应类中的所有属性域
        Method[] fields = f.getClass().getMethods();
        return fields;
    }

    public static Object invokeObjNoParaMethod(Object o, String methodName) {
        Object o2 = null;

        try {
            Method m = o.getClass().getMethod(methodName);
            o2 = m.invoke(o);
            // System.out.println("調用 "+ methodName + "————得到結果 " + o2);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return o2;
    }

    public static Object invokeObjGetMethod(Object o, String getMethod) {
        Object o2 = null;
        Method m = null;
        try {
            m = o.getClass().getMethod("get" + getMethod.substring(0, 1).toUpperCase() + getMethod.substring(1));
            boolean accessFlag = m.isAccessible();
            m.setAccessible(true);
            o2 = m.invoke(o);
            m.setAccessible(accessFlag);
            // System.out.println("調用 "+ m.getName() + "————得到結果 " + o2);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return o2;
    }

    public static void invokeObjSetMethod(Object o, String setMethod, String value) {
        Field f;
        try {
            f = o.getClass().getDeclaredField(setMethod);
            invokeObjSetMethod(o, f, value);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public static void invokeObjSetMethod(Object o, Field f, Object value) {
        Method m = null;
        try {
            m = o.getClass().getMethod("set" + f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1),
                    f.getType());
            boolean accessFlag = m.isAccessible();
            m.setAccessible(true);
            switch (m.getParameterTypes()[0].toString()) {
                case "class java.lang.String":
                    m.invoke(o, value);
                    break;
                case "int":
                    m.invoke(o, Integer.valueOf(String.valueOf(value)));
                    break;

                case "boolean":
                    m.invoke(o, Boolean.valueOf(String.valueOf(value)));
                    break;
                case "long":
                    m.invoke(o, Long.valueOf(String.valueOf(value)));
                    break;
                case "float":
                    m.invoke(o, Float.valueOf(String.valueOf(value)));
                    break;
                default:
                    m.invoke(o, value);
                    break;
            }
            m.setAccessible(accessFlag);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void invokeObjSetMethod(Object o, Field f, byte[] value) {
        Method m = null;
        try {
            m = o.getClass().getMethod("set" + f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1),
                    f.getType());
            switch (m.getParameterTypes()[0].toString()) {
                case "class java.lang.String":
                    m.invoke(o, Bytes.toString(value));
                    break;
                case "int":
                    m.invoke(o, Integer.valueOf(Bytes.toString(value)));
                    break;

                case "boolean":
                    m.invoke(o, Boolean.valueOf(Bytes.toString(value)));
                    break;
                case "long":
                    m.invoke(o, Long.valueOf(Bytes.toString(value)));
                    break;
                case "float":
                    m.invoke(o, Float.valueOf(Bytes.toString(value)));
                    break;
                default:
                    m.invoke(o);
                    break;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static Object constructor(Object obj) {
        try {
            Constructor<?> cons[] = obj.getClass().getConstructors();
            for (Constructor<?> con : cons) {
                if (con.getParameterCount() == 0) {
                    con.setAccessible(true);
                    return con.newInstance();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        News book = new News();
        getObjFields(book);
        invokeObjGetMethod(book, "productId");
        invokeObjGetMethod(book, "content");
        invokeObjSetMethod(book, "productId", "1");
        invokeObjGetMethod(book, "productId");
    }
}
