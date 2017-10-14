package yunas.api;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import yunas.Context;

/**
 * Interceptor interface for Java.
 */
public interface Interceptor extends Function1<Context, Unit> {

    public void action(Context context);

    @Override
    default Unit invoke(Context context) {
        action(context);
        return null;
    }
}
