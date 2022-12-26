package me.basiqueevangelist.dashmixin;

import net.auoeke.reflect.ClassDefiner;

public class LoaderHelper {
    public static Class<?> tryLoadClass(ClassLoader loader, String name) {
        byte[] bytes = ClassDumpLoader.CLASSES.get(name.replace('.', '/'));

        if (bytes != null) {
            return ClassDefiner.make()
                .loader(loader)
                .classFile(bytes)
                .name(name)
                .define();
        }

        return null;
    }
}
