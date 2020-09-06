package com.bankcard.trans.utils;

import android.content.ContentValues;

import com.bankcard.trans.model.TransData;
import com.pax.gl.db.DbException;
import com.pax.gl.db.IDb.Column;
import com.pax.gl.db.IDb.Id;

import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

import java.lang.reflect.Field;

public class DbUtils {
    public static void save(TransData entity, String tableName, SQLiteDatabase db) throws DbException {
        Field[] fields = getDeclaredField(TransData.class);
        ContentValues values = new ContentValues();
        try {
            values = putValue(fields, entity);
            if (values.size() <= 0)
                throw new DbException(DbException.ERR_SAVE_PARAM);
            long flag = db.insert(tableName, null, values);
            if (flag < 1) {
                throw new DbException(DbException.ERR_SAVE_PARAM);
            }
        } catch (Exception e) {
            throw new DbException(DbException.ERR_SAVE_PARAM, e.getCause());
        } finally {
            fileSystemSync();
        }
    }

    public static Field[] getDeclaredField(Class clazz) {
        Field[] superFields = clazz.getSuperclass().getDeclaredFields();
        Field[] fields = clazz.getDeclaredFields();

        Field[] fullFields = new Field[superFields.length + fields.length];

        System.arraycopy(superFields, 0, fullFields, 0, superFields.length);
        System.arraycopy(fields, 0, fullFields, superFields.length, fields.length);

        return fullFields;
    }

    private static ContentValues putValue(Field[] fields, TransData entity) throws DbException {
        ContentValues values = new ContentValues();

        for (Field field : fields) {
            if (!field.isAccessible())
                field.setAccessible(true);

            String strColName = getColumnName(field);
            if (strColName == null)
                continue;

            try {
                if (field.getGenericType().toString().equals("class java.lang.String")) {
                    values.put(strColName, (String) (field.get(entity)));
                } else if (field.getGenericType().toString().equals("class java.lang.Boolean")
                        || field.getGenericType().toString().equals("boolean")) {
                    values.put(strColName, (int) (((Boolean) (field.get(entity))) ? 1 : 0));
                } else if (field.getGenericType().toString().equals("class java.lang.Byte")
                        || field.getGenericType().toString().equals("byte")) {
                    values.put(strColName, (Byte) field.get(entity));
                } else if (field.getGenericType().toString().equals("class [B")) {
                    values.put(strColName, (byte[]) field.get(entity));
                } else if (field.getGenericType().toString().equals("class java.lang.Double")
                        || field.getGenericType().toString().equals("double")) {
                    values.put(strColName, (Double) field.get(entity));
                } else if (field.getGenericType().toString().equals("class java.lang.Float")
                        || field.getGenericType().toString().equals("float")) {
                    values.put(strColName, (Float) field.get(entity));
                } else if (field.getGenericType().toString().equals("class java.lang.Integer")
                        || field.getGenericType().toString().equals("int")) {
                    values.put(strColName, (Integer) field.get(entity));
                } else if (field.getGenericType().toString().equals("class java.lang.Long")
                        || field.getGenericType().toString().equals("long")) {
                    values.put(strColName, (Long) field.get(entity));
                } else if (field.getGenericType().toString().equals("class java.lang.Short")
                        || field.getGenericType().toString().equals("short")) {
                    values.put(strColName, (Short) field.get(entity));
                } else {
                    throw new DbException(DbException.ERR_UNSUPPORTED_COLUMN_DATA_TYPE);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new DbException(DbException.ERR_GET_FIELD_FROM_ENTITY);
            }
        }
        return values;
    }

    /**
     * 根据@Id或@Column的注解，字段名称使用属性名还是注解中的name
     *
     * @param field
     * @return 表字段名称
     */
    public static String getColumnName(Field field) {
        String strColName = null;

        if (!hasOrmAnnotation(field)) {
            return strColName;
        }

        return field.getName();
    }

    /**
     * 是否有自定义的ORM 注解
     *
     * @param field 当前字段
     * @return 有/无自定义注解
     */
    public static boolean hasOrmAnnotation(Field field) {
        boolean isNeedMap = false;

        if (field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(Column.class)) {
            isNeedMap = true;
        }
        return isNeedMap;
    }

    private static void fileSystemSync() {
        try {
            Runtime.getRuntime().exec("sync");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteAll(String tableName, SQLiteDatabase db) throws DbException {
        String strDelSql = "DELETE FROM " + tableName;
        String strRevSeqSql = "UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='" + tableName + "'";
        try {
            db.execSQL(strDelSql);
            db.execSQL(strRevSeqSql);
        } catch (SQLException se) {
            se.printStackTrace();
            throw new DbException(DbException.ERR_DEL_ALL_EXECSQL);
        }
    }
}
