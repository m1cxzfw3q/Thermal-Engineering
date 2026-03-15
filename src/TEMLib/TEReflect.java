package TEMLib;

import arc.util.Log;
import arc.util.Reflect;
import mindustry.ctype.ContentType;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
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
    public static <T extends Enum<?>> void addEnum(Class<T> enumClass, String newEnumName, Object... fieldValues) throws Exception {
        // 1. 获取 $VALUES 字段（存储所有枚举实例的数组）
        Field valuesField = enumClass.getDeclaredField("$VALUES");
        if (valuesField == null) {
            // 备用名称，某些编译器可能使用 ENUM$VALUES
            valuesField = enumClass.getDeclaredField("ENUM$VALUES");
        }
        valuesField.setAccessible(true);

        // 2. 通过 Unsafe 获取静态字段的基地址和偏移量
        Object staticFieldBase = unsafe.staticFieldBase(valuesField);
        long valuesOffset = unsafe.staticFieldOffset(valuesField);

        // 3. 读取当前 $VALUES 数组
        T[] oldValues = (T[]) unsafe.getObject(staticFieldBase, valuesOffset);
        int oldLength = oldValues.length;
        int newOrdinal = oldLength; // 新枚举的序数

        // 4. 使用 Unsafe 分配一个未初始化的枚举实例
        T newInstance = (T) unsafe.allocateInstance(enumClass);

        // 5. 直接设置 Enum 基类的 name 和 ordinal 字段
        //    通过字段偏移量来定位内存位置
        Field nameField = Enum.class.getDeclaredField("name");
        long nameOffset = unsafe.objectFieldOffset(nameField);
        unsafe.putObject(newInstance, nameOffset, newEnumName);

        Field ordinalField = Enum.class.getDeclaredField("ordinal");
        long ordinalOffset = unsafe.objectFieldOffset(ordinalField);
        unsafe.putInt(newInstance, ordinalOffset, getNextOrdinal(enumClass));

        // 6. 设置自定义字段的值
        setCustomFields(enumClass, newInstance, fieldValues);

        // 7. 创建新的数组，包含旧元素和新元素
        T[] newValues = Arrays.copyOf(oldValues, oldLength + 1);
        newValues[oldLength] = newInstance;

        // 8. 直接将新数组的引用写入静态字段（绕过 final 检查）
        unsafe.putObject(staticFieldBase, valuesOffset, newValues);

        // 9. 清理枚举类内部的缓存，确保 Enum.valueOf() 能正常工作
        cleanEnumCache(enumClass);
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

    private static int getNextOrdinal(Class<?> enumClass) {
        return Reflect.<ContentType[]>get(enumClass, "$VALUES").length;
    }

    private static void makeNonFinalField(Field field) throws Exception {
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        // 获取 Field 类中 "modifiers" 字段的 VarHandle
        VarHandle modifiersHandle = MethodHandles.privateLookupIn(Field.class, MethodHandles.lookup())
                .findVarHandle(Field.class, "modifiers", int.class);

        // 移除 final 修饰符
        int mods = field.getModifiers();
        if (Modifier.isFinal(mods)) {
            modifiersHandle.set(field, mods & ~Modifier.FINAL);
        }
    }

    private static void cleanEnumCache(Class<?> enumClass) throws Exception {
        // 清空两个可能存在的缓存字段
        clearField(Class.class, enumClass, "enumConstantDirectory");
        clearField(Class.class, enumClass, "enumConstants");
    }

    private static void clearField(Class<?> targetClass, Object targetObject, String fieldName) throws Exception {
        Field field = targetClass.getDeclaredField(fieldName);
        makeNonFinalField(field);
        field.setAccessible(true);
        field.set(targetObject, null);
    }
}