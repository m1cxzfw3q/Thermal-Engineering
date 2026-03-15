package TEMLib;

import arc.util.Log;
import arc.util.Reflect;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Arrays;

public class TEReflect {
    static final Unsafe unsafe;

    static {
        Log.info("[TEReflect] Initialization Unsafe");
        try {
            unsafe = Reflect.get(Unsafe.class, "theUnsafe");
        } catch (Exception e) {
            throw new RuntimeException("[TEReflect] Failed to initialization Unsafe : " + e);
        }
    }

    public static void setConstant(Class<?> type, String fieldName, Object newValue) throws NoSuchFieldException {
        Field field = type.getDeclaredField(fieldName);
        field.setAccessible(true);
        Object staticFieldBase = unsafe.staticFieldBase(field);
        long offset = unsafe.staticFieldOffset(field);
        unsafe.putObject(staticFieldBase, offset, newValue);
    }

    /**
     * 为枚举类动态添加一个新常量（支持自定义字段）
     *
     * @param enumClass   目标枚举类
     * @param newName     新常量名称
     * @param fieldValues 自定义字段的值（按声明顺序）
     * @param <T>         枚举类型
     * @author DeepSeek
     */
    @SuppressWarnings("unchecked")
    public static <T extends Enum<?>> void addEnum(Class<T> enumClass, String newName, Object... fieldValues) throws Exception {
        // 1. 获取存储所有枚举常量的数组 $VALUES
        Field valuesField = getValuesField(enumClass);
        Object staticBase = unsafe.staticFieldBase(valuesField);
        long valuesOffset = unsafe.staticFieldOffset(valuesField);

        T[] oldValues = (T[]) unsafe.getObject(staticBase, valuesOffset);
        int oldLen = oldValues.length;

        // 2. 创建新枚举实例（不调用构造器）
        T newInstance = (T) unsafe.allocateInstance(enumClass);

        // 3. 设置 Enum 基类的 name 和 ordinal 字段
        Field nameField = Enum.class.getDeclaredField("name");
        long nameOffset = unsafe.objectFieldOffset(nameField);
        unsafe.putObject(newInstance, nameOffset, newName);

        Field ordinalField = Enum.class.getDeclaredField("ordinal");
        long ordinalOffset = unsafe.objectFieldOffset(ordinalField);
        unsafe.putInt(newInstance, ordinalOffset, oldLen);

        // 4. 设置自定义字段
        setCustomFields(enumClass, newInstance, fieldValues);

        // 5. 创建新数组并替换 $VALUES
        T[] newValues = Arrays.copyOf(oldValues, oldLen + 1);
        newValues[oldLen] = newInstance;
        unsafe.putObject(staticBase, valuesOffset, newValues);

        // 6. 清理 Class 内部缓存（使用 Unsafe 直接写字段，无需 setAccessible）
        clearEnumCache(enumClass);
    }

    private static Field getValuesField(Class<?> enumClass) throws NoSuchFieldException {
        try {
            return enumClass.getDeclaredField("$VALUES");
        } catch (NoSuchFieldException e) {
            // 某些编译器可能使用 ENUM$VALUES
            return enumClass.getDeclaredField("ENUM$VALUES");
        }
    }

    private static <T> void setCustomFields(Class<T> enumClass, T instance, Object[] values) throws Exception {
        Field[] fields = enumClass.getDeclaredFields();
        int idx = 0;
        for (Field f : fields) {
            // 跳过静态字段和编译器生成的字段
            if (f.isSynthetic() || java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
                continue;
            }
            long offset = unsafe.objectFieldOffset(f);
            Object val = values[idx++];
            Class<?> type = f.getType();
            if (type == int.class) {
                unsafe.putInt(instance, offset, (Integer) val);
            } else if (type == long.class) {
                unsafe.putLong(instance, offset, (Long) val);
            } else if (type == boolean.class) {
                unsafe.putBoolean(instance, offset, (Boolean) val);
            } else if (type == byte.class) {
                unsafe.putByte(instance, offset, (Byte) val);
            } else if (type == char.class) {
                unsafe.putChar(instance, offset, (Character) val);
            } else if (type == short.class) {
                unsafe.putShort(instance, offset, (Short) val);
            } else if (type == float.class) {
                unsafe.putFloat(instance, offset, (Float) val);
            } else if (type == double.class) {
                unsafe.putDouble(instance, offset, (Double) val);
            } else {
                unsafe.putObject(instance, offset, val);
            }
        }
        if (idx != values.length) {
            throw new IllegalArgumentException("提供的字段值数量与自定义字段数量不匹配");
        }
    }

    /**
     * 使用 Unsafe 直接清理 Class 内部的枚举缓存，无需调用 field.set()
     */
    private static void clearEnumCache(Class<?> enumClass) throws Exception {
        // 清理 enumConstantDirectory (用于 valueOf 的缓存)
        Field dirField = Class.class.getDeclaredField("enumConstantDirectory");
        long dirOffset = unsafe.objectFieldOffset(dirField);
        unsafe.putObject(enumClass, dirOffset, null);

        // 清理 enumConstants (用于 values() 的缓存)
        Field constField = Class.class.getDeclaredField("enumConstants");
        long constOffset = unsafe.objectFieldOffset(constField);
        unsafe.putObject(enumClass, constOffset, null);
    }
}