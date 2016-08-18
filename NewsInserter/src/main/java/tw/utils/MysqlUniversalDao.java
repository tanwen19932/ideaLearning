package tw.utils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class MysqlUniversalDao {
    String table;
    Object object = null;
    Connection connection;

    public MysqlUniversalDao(Connection connection, String table, Object obj) {
        this.connection = connection;
        this.table = table;
        this.object = obj;
    }

    void setPreparement(PreparedStatement preparedStatement, int i, Field field, Object obj) throws SQLException {
        String name = field.getType().getSimpleName();
        i++;
        try {
            switch (name) {
                case "String":
                    preparedStatement.setString(i, (String) ReflectUtil.invokeObjGetMethod(obj, field.getName()));
                    break;
                case "int":
                    preparedStatement.setInt(i, (int) ReflectUtil.invokeObjGetMethod(obj, field.getName()));
                    break;
                case "boolean":
                    preparedStatement.setBoolean(i, (boolean) (ReflectUtil.invokeObjGetMethod(obj, field.getName())));
                    break;
                case "long":
                    preparedStatement.setLong(i, (long) (ReflectUtil.invokeObjGetMethod(obj, field.getName())));
                    break;
                case "float":
                    preparedStatement.setFloat(i, (float) (ReflectUtil.invokeObjGetMethod(obj, field.getName())));
                    break;
                case "Date":
                    preparedStatement.setDate(i,
                            new java.sql.Date(((Date) ReflectUtil.invokeObjGetMethod(obj, field.getName())).getTime()));
                    break;
                default:
                    preparedStatement.setString(i, String.valueOf(ReflectUtil.invokeObjGetMethod(obj, field.getName())));
                    break;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public ResultSet select(String sql) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            return rs;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public Object getObjFromR(ResultSet r) {
        return getObjFromR(r, 0);
    }

    public Object getObjFromR(ResultSet r, int offset) {
        // TODO Auto-generated method stub
        offset++;
        Object obj = null;
        Field[] fields = ReflectUtil.getObjFields(object);

        obj = ReflectUtil.constructor(object);
        for (int i = 0; i < fields.length; i++) {

            String name = fields[i].getType().getSimpleName();
            try {
                switch (name) {

                    case "String":
                        ReflectUtil.invokeObjSetMethod(obj, fields[i], r.getString(i + offset));
                        break;
                    case "int":
                        ReflectUtil.invokeObjSetMethod(obj, fields[i], r.getInt(i + offset));
                        break;
                    case "boolean":
                        ReflectUtil.invokeObjSetMethod(obj, fields[i], r.getBoolean(i + offset));
                        break;
                    case "long":
                        ReflectUtil.invokeObjSetMethod(obj, fields[i], r.getLong(i + offset));
                        break;
                    case "float":
                        ReflectUtil.invokeObjSetMethod(obj, fields[i], r.getFloat(i + offset));
                        break;
                    case "Date":
                        ReflectUtil.invokeObjSetMethod(obj, fields[i], r.getDate(i + offset));
                        break;
                    default:
                        ReflectUtil.invokeObjSetMethod(obj, fields[i], r.getString(i + offset));
                        break;
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // System.out.println(r.getString(i+1));
        }

        // System.out.println(news.getId() + " " + news.getMediaNameZh() + " " +
        // news.getTitleSrc() + " " + news.getCreated() + news.getTextSrc());

        return obj;
    }

    public boolean select(Object obj) {
        Field[] fields = ReflectUtil.getObjFields(obj);
        PreparedStatement preparedStatement = null;
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT * FROM " + table + " where ");
            for (int i = 0; i < fields.length - 1; i++) {
                sb.append(fields[i].getName()).append(" = ? and ");
            }
            sb.replace(sb.length() - 5, sb.length(), "");

            preparedStatement = connection.prepareStatement(sb.toString());
            for (int i = 0; i < fields.length - 1; i++) {
                setPreparement(preparedStatement, i, fields[i], obj);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return true;
            else
                return false;
        } catch (SQLException e) {
            System.err.println(preparedStatement.toString());
            return false;
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public boolean delete(Object obj) {
        Field[] fields = ReflectUtil.getObjFields(obj);
        PreparedStatement preparedStatement = null;
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("DELETE FROM " + table + " where ");
            for (int i = 0; i < fields.length - 1; i++) {
                sb.append(fields[i].getName()).append(" = ? and ");
            }
            sb.replace(sb.length() - 5, sb.length(), "");

            preparedStatement = connection.prepareStatement(sb.toString());
            for (int i = 0; i < fields.length - 1; i++) {
                setPreparement(preparedStatement, i, fields[i], obj);
            }
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            System.err.println(preparedStatement.toString());
            return false;
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public boolean update(Object obj) {
        return update(obj, 0);
    }

    public boolean update(Object obj, int offset) {
        Field[] fields = ReflectUtil.getObjFields(obj);
        PreparedStatement preparedStatement = null;
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("UPDATE  " + table + " SET ");
            for (int i = 0; i < fields.length; i++) {
                sb.append(fields[i].getName()).append(" = ?,");
            }
            sb.replace(sb.length() - 1, sb.length(), " ").append("  where ");

            for (int i = 0; i < offset; i++) {
                sb.append(fields[i].getName()).append(" = ? AND ");
            }
            sb.replace(sb.length() - 5, sb.length(), "");
            preparedStatement = connection.prepareStatement(sb.toString());
            for (int i = 0; i < fields.length; i++) {
                setPreparement(preparedStatement, i, fields[i], obj);
            }
            for (int i = 0; i < offset; i++) {
                setPreparement(preparedStatement, i + fields.length, fields[i], obj);
            }

            int i = preparedStatement.executeUpdate();
            if (i == 1)
                return true;
            else
                return false;
        } catch (SQLException e) {
            System.err.println(preparedStatement.toString());
            return false;
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public boolean insert(Object obj) {
        boolean update = false;
        return insert(obj, false);
    }

    public boolean insert(Object obj, boolean update) {
        return insert(obj, 0, update);
    }

    public boolean insert(Object obj, int offset, boolean update) {
        // TODO Auto-generated method stub
        Field[] fields = ReflectUtil.getObjFields(obj);
        PreparedStatement preparedStatement = null;
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("INSERT INTO " + table + "(");
            for (int i = 0; i < fields.length; i++) {
                sb.append(fields[i].getName()).append(",");
            }
            sb.replace(sb.length() - 1, sb.length(), ")").append("  VALUES (");
            for (int i = 0; i < fields.length; i++) {
                sb.append("?").append(",");
            }
            sb.replace(sb.length() - 1, sb.length(), ")");

            preparedStatement = connection.prepareStatement(sb.toString());
            for (int i = 0; i < fields.length; i++) {
                setPreparement(preparedStatement, i, fields[i], obj);
            }
            preparedStatement.execute();
            return true;
        } catch (MySQLIntegrityConstraintViolationException ex) {
            if (update)
                return update(obj, offset);
            else {
                return false;
            }
        } catch (SQLException e) {
            System.err.println(preparedStatement.toString());
            System.err.println(e);
            return false;
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
