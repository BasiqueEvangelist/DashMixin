package me.basiqueevangelist.dashmixin;

import net.auoeke.reflect.Reflect;

import java.io.IOException;
import java.lang.instrument.UnmodifiableClassException;

public class DashMixinInit {
    public static void init(ClassLoader knotClassLoader) {
        try {
            var instrumentation = Reflect.instrument().value();
            var transformer = new KnotClassDelegateTransformer();
            instrumentation.addTransformer(transformer, true);
            instrumentation.retransformClasses(Class.forName("net.fabricmc.loader.impl.launch.knot.KnotClassDelegate", true, knotClassLoader));
            instrumentation.removeTransformer(transformer);

            instrumentation.addTransformer(new ClassSaverTransformer());
        } catch (ClassNotFoundException | UnmodifiableClassException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
