package TEMLib;

import arc.util.Log;
import arc.util.Reflect;
import sun.misc.Unsafe;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class TEReflect {
    static final Unsafe unsafe;
    static final ReflectionFactory REFLECTION_FACTORY = ReflectionFactory.getReflectionFactory();

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

        // 1. 获取 $VALUES 字段（代码同方法1）
        Field valuesField = enumClass.getDeclaredField("$VALUES");
        valuesField.setAccessible(true);
        T[] previousValues = (T[]) valuesField.get(null);
        int newOrdinal = previousValues.length;

        // 2. 准备构造函数参数类型 (String, int, ...)
        Class<?>[] fullParamTypes = new Class[2 + additionalTypes.length];
        fullParamTypes[0] = String.class;
        fullParamTypes[1] = int.class;
        System.arraycopy(additionalTypes, 0, fullParamTypes, 2, additionalTypes.length);
        Constructor<T> declaredConstructor = enumClass.getDeclaredConstructor(fullParamTypes);
        declaredConstructor.setAccessible(true);

        // 3. 使用 ReflectionFactory 创建一个不调用构造函数的构造函数访问器
        //    这是绕过枚举反射限制的关键 [citation:2][citation:6]
        Constructor<T> silentConstructor = (Constructor<T>) REFLECTION_FACTORY.newConstructorForSerialization(enumClass, declaredConstructor);

        // 4. 通过这个特殊的构造函数创建实例
        Object[] params = new Object[2 + additionalValues.length];
        params[0] = newEnumName;
        params[1] = newOrdinal;
        System.arraycopy(additionalValues, 0, params, 2, additionalValues.length);
        T newInstance = silentConstructor.newInstance(params);

        // 5. 更新 $VALUES 数组（代码同方法1）
        T[] newValues = Arrays.copyOf(previousValues, newOrdinal + 1);
        newValues[newOrdinal] = newInstance;

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        int modifiers = modifiersField.getInt(valuesField);
        modifiers &= ~Modifier.FINAL;
        modifiersField.setInt(valuesField, modifiers);

        valuesField.set(null, newValues);

        // 6. 清理缓存（代码同方法1）
        cleanEnumCache(enumClass);
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