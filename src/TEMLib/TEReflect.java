package TEMLib;

import arc.util.Log;
import arc.util.Reflect;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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

    @SuppressWarnings("unchecked")
    public static <T extends Enum<?>> void addEnum(Class<T> enumClass, String newName, Object... fieldValues) throws Exception {
        // 1. 获取 $VALUES 字段
        Field valuesField = enumClass.getDeclaredField("$VALUES");
        valuesField.setAccessible(true);

        // 2. 获取静态字段的内存基地址和偏移量
        Object staticBase = unsafe.staticFieldBase(valuesField);
        long offset = unsafe.staticFieldOffset(valuesField);

        // 3. 读取当前数组
        T[] oldValues = (T[]) unsafe.getObject(staticBase, offset);
        int oldLen = oldValues.length;

        // 4. 创建新实例
        T newInstance = (T) unsafe.allocateInstance(enumClass);

        // 5. 设置 name 和 ordinal
        Field nameField = Enum.class.getDeclaredField("name");
        long nameOff = unsafe.objectFieldOffset(nameField);
        unsafe.putObject(newInstance, nameOff, newName);

        Field ordinalField = Enum.class.getDeclaredField("ordinal");
        long ordOff = unsafe.objectFieldOffset(ordinalField);
        unsafe.putInt(newInstance, ordOff, oldLen); // 新序数

        // 6. 设置自定义字段（如果有）
        setCustomFields(enumClass, newInstance, fieldValues);

        // 7. 创建新数组并写入
        T[] newValues = Arrays.copyOf(oldValues, oldLen + 1);
        newValues[oldLen] = newInstance;
        unsafe.putObject(staticBase, offset, newValues); // 直接替换数组引用

        // 8. 清理缓存（如果需要）
        clearEnumCache(enumClass);
    }

    private static void clearEnumCache(Class<?> clazz) throws Exception {
        Field cache = Class.class.getDeclaredField("enumConstantDirectory");
        makeAccessible(cache);
        cache.set(clazz, null);
    }

    private static void makeAccessible(Field field) throws Exception {
        field.setAccessible(true);
        // 不需要修改 modifiers，因为不需要修改 final
    }

    /**
     * 根据字段声明顺序，使用 Unsafe 设置自定义字段的值。
     */
    private static <T> void setCustomFields(Class<T> enumClass, T instance, Object[] fieldValues) throws Exception {
        // 获取枚举类中声明的所有字段（不包括从 Enum 继承的）
        Field[] declaredFields = enumClass.getDeclaredFields();
        int fieldIndex = 0;
        for (Field field : declaredFields) {
            // 跳过静态字段和 Enum 自带的 name/ordinal（它们已经在 Enum 类中）
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            // 如果是编译器生成的字段（如 $VALUES 等），也跳过
            if (field.isSynthetic()) {
                continue;
            }
            // 确保我们有足够的值
            if (fieldIndex >= fieldValues.length) {
                throw new IllegalArgumentException(
                        "[TEReflect] The number of field values provided is insufficient; expected " + fieldIndex + " but only " + fieldValues.length
                );
            }

            Object value = fieldValues[fieldIndex];
            Class<?> fieldType = field.getType();
            long offset = unsafe.objectFieldOffset(field);

            // 根据字段类型使用 Unsafe 的适当方法设置值
            if (fieldType == int.class) {
                unsafe.putInt(instance, offset, (Integer) value);
            } else if (fieldType == long.class) {
                unsafe.putLong(instance, offset, (Long) value);
            } else if (fieldType == boolean.class) {
                unsafe.putBoolean(instance, offset, (Boolean) value);
            } else if (fieldType == byte.class) {
                unsafe.putByte(instance, offset, (Byte) value);
            } else if (fieldType == char.class) {
                unsafe.putChar(instance, offset, (Character) value);
            } else if (fieldType == short.class) {
                unsafe.putShort(instance, offset, (Short) value);
            } else if (fieldType == float.class) {
                unsafe.putFloat(instance, offset, (Float) value);
            } else if (fieldType == double.class) {
                unsafe.putDouble(instance, offset, (Double) value);
            } else {
                // 引用类型
                unsafe.putObject(instance, offset, value);
            }
            fieldIndex++;
        }

        if (fieldIndex != fieldValues.length) {
            throw new IllegalArgumentException(
                    "[TEReflect] The number of field values provided exceeds the actual number of fields, expected " + fieldIndex + " but received "
                            + fieldValues.length
            );
        }
    }
}