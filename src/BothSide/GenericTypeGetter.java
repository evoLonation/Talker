package BothSide;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 暂时用不上。。。。
 */
public class GenericTypeGetter {
    static public Type[] getFatherGenericTypes(Object object){
        Type[] type = object.getClass().getGenericInterfaces();
        ParameterizedType parameterizedType = (ParameterizedType) type[0];
        return parameterizedType.getActualTypeArguments();
    }
    static public Type[] getFatherGenericTypes(Request<?, ?> req){
        Type type = req.getClass().getGenericSuperclass();
        ParameterizedType parameterizedType = (ParameterizedType) type;
        return parameterizedType.getActualTypeArguments();
    }

    static public Type[] getFatherGenericTypes(Class<?> cls){
        Type[] type = cls.getGenericInterfaces();
        ParameterizedType parameterizedType = (ParameterizedType) type[0];
        return parameterizedType.getActualTypeArguments();
    }

    static private <T> Type[] abc(MsgHandlerRef<T> msgHandlerRef){
        return msgHandlerRef.getGenericType();
    }

}
abstract class MsgHandlerRef<T> {
    protected MsgHandlerRef(MsgHandler<T> factory) {}

    public Type[] getGenericType() {
        return ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
    }
}