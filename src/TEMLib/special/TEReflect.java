package TEMLib.special;

import arc.util.Log;
import arc.util.Reflect;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Arrays;

// TODO
public class TEReflect {
    static final Unsafe unsafe, UNSAFE;

    static {
        Log.info("[TEReflect] Initialization Unsafe");
        try {
            unsafe = UNSAFE = Reflect.get(Unsafe.class, "theUnsafe");
        } catch (Exception e) {
            throw new RuntimeException("[TEReflect] Failed to initialization Unsafe : " + e);
        }
    }

    /**
     * 修改任意 static final 字段的值（包括数组、对象、基本类型）
     *
     * @param clazz     包含该字段的类
     * @param fieldName 字段名
     * @param newValue  新值（必须与字段类型兼容）
     * @throws Exception 如果字段不存在或类型不兼容
     */
    public static void setStaticFinalField(Class<?> clazz, String fieldName, Object newValue) throws Exception {
        // 获取 Field 对象 —— 不需要 setAccessible
        Field field = clazz.getDeclaredField(fieldName);
        // 获取静态字段的内存基地址和偏移量
        Object staticBase = UNSAFE.staticFieldBase(field);
        long offset = UNSAFE.staticFieldOffset(field);

        // 根据字段类型调用对应的 put 方法
        Class<?> fieldType = field.getType();
        if (fieldType == int.class) {
            UNSAFE.putInt(staticBase, offset, (Integer) newValue);
        } else if (fieldType == long.class) {
            UNSAFE.putLong(staticBase, offset, (Long) newValue);
        } else if (fieldType == boolean.class) {
            UNSAFE.putBoolean(staticBase, offset, (Boolean) newValue);
        } else if (fieldType == byte.class) {
            UNSAFE.putByte(staticBase, offset, (Byte) newValue);
        } else if (fieldType == char.class) {
            UNSAFE.putChar(staticBase, offset, (Character) newValue);
        } else if (fieldType == short.class) {
            UNSAFE.putShort(staticBase, offset, (Short) newValue);
        } else if (fieldType == float.class) {
            UNSAFE.putFloat(staticBase, offset, (Float) newValue);
        } else if (fieldType == double.class) {
            UNSAFE.putDouble(staticBase, offset, (Double) newValue);
        } else {
            // 引用类型（包括数组、字符串、自定义对象等）
            UNSAFE.putObject(staticBase, offset, newValue);
        }
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

    private static <T> void setCustomFields(Class<T> enumClass, T instance, Object[] values) {
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

    /**
     * 扩展一个 static final 数组字段（替换为新的大数组）
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T> void extendStaticFinalArray(Class<?> clazz, String fieldName, T... newElements) throws Exception {
        // 1. 获取原数组 —— 这里仍然需要通过反射读取原值，但不需要 setAccessible
        //    因为我们可以直接用 Unsafe 读取，无需调用 field.get()
        Field field = clazz.getDeclaredField(fieldName);
        Object staticBase = UNSAFE.staticFieldBase(field);
        long offset = UNSAFE.staticFieldOffset(field);
        T[] original = (T[]) UNSAFE.getObject(staticBase, offset);

        // 2. 创建新数组（纯 Java 方式）
        T[] newArray = Arrays.copyOf(original, original.length + newElements.length);
        System.arraycopy(newElements, 0, newArray, original.length, newElements.length);

        // 3. 替换数组引用（再次使用 Unsafe 写入）
        UNSAFE.putObject(staticBase, offset, newArray);
    }
}