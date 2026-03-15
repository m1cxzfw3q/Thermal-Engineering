package TEMLib;

import arc.util.Log;
import arc.util.Reflect;
import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class TEReflect {
    static Unsafe unsafe;

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
    public static <T extends Enum<?>> void addEnum(Class<T> enumClass, String newEnumName, Class<?>[] additionalTypes, Object[] additionalValues) throws Exception {

        // 1. 获取枚举的内部 $VALUES 字段
        Field valuesField = getStaticFinalField(enumClass, "$VALUES");
        if (valuesField == null) {
            // 不同编译器可能名称不同，例如 ENUM$VALUES
            valuesField = getStaticFinalField(enumClass, "ENUM$VALUES");
        }
        if (valuesField == null) {
            throw new RuntimeException("[TEReflect] The field $VALUES cannot be found.");
        }
        valuesField.setAccessible(true);

        // 2. 获取当前的枚举实例数组
        T[] previousValues = (T[]) valuesField.get(null);
        int newOrdinal = previousValues.length;

        // 3. 构建新枚举实例的参数列表 (name, ordinal, ...)
        Object[] params = new Object[2 + additionalValues.length];
        params[0] = newEnumName; // name
        params[1] = newOrdinal;  // ordinal
        System.arraycopy(additionalValues, 0, params, 2, additionalValues.length);

        // 4. 获取枚举的构造函数并创建新实例
        Class<?>[] paramTypes = new Class[2 + additionalTypes.length];
        paramTypes[0] = String.class;
        paramTypes[1] = int.class;
        System.arraycopy(additionalTypes, 0, paramTypes, 2, additionalTypes.length);

        Constructor<T> constructor = enumClass.getDeclaredConstructor(paramTypes);
        constructor.setAccessible(true);
        T newInstance = constructor.newInstance(params);

        // 5. 创建新的 $VALUES 数组，包含新旧所有值
        T[] newValues = Arrays.copyOf(previousValues, newOrdinal + 1);
        newValues[newOrdinal] = newInstance;

        // 6. 移除 $VALUES 字段的 final 修饰符
        makeNonFinalField(valuesField);

        // 7. 将新的数组赋值给 $VALUES 字段
        valuesField.set(null, newValues);

        // 8. 清理类缓存，让 valueOf() 能识别新值
        cleanEnumCache(enumClass);
    }

    private static Field getStaticFinalField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private static void makeNonFinalField(Field field) throws Exception {
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        int modifiers = modifiersField.getInt(field);
        modifiers &= ~Modifier.FINAL; // 去除 FINAL 标志位
        modifiersField.setInt(field, modifiers);
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