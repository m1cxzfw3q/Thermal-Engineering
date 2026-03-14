package TEMLib;

import arc.util.Log;
import arc.util.Reflect;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

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
}
