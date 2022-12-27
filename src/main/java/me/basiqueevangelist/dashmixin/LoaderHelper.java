package me.basiqueevangelist.dashmixin;

import net.auoeke.reflect.ClassDefiner;
import net.auoeke.reflect.Classes;

public class LoaderHelper {
    private static double TOTAL_DEFINE_TIME = 0;

    public static Class<?> tryLoadClass(ClassLoader loader, String name) {
        long start = System.nanoTime();

        Class<?> klass = Classes.findLoadedClass(loader, name);
        if (klass != null)
            return klass;

        byte[] bytes = ClassDumpLoader.getClasses().remove(name.replace('.', '/'));

        if (bytes != null) {
             klass = ClassDefiner.make()
                .loader(loader)
                .classFile(bytes)
                .name(name)
                .unsafe()
                .define();

            TOTAL_DEFINE_TIME += (System.nanoTime() - start) / 1000000000.0;

            return klass;
        }

        return null;
    }
}
