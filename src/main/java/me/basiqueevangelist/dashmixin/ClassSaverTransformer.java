package me.basiqueevangelist.dashmixin;

import net.auoeke.reflect.ClassTransformer;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.ProtectionDomain;
import java.util.Map;

public class ClassSaverTransformer implements ClassTransformer {
    private final DataOutputStream out;
    private final Map<String, byte[]> CLASSES;

    @SuppressWarnings("unchecked")
    public ClassSaverTransformer() throws IOException {
        out = new DataOutputStream(new BufferedOutputStream(
            Files.newOutputStream(DashMixinPlugin.DUMP_PATH, StandardOpenOption.APPEND, StandardOpenOption.CREATE)));

        try {
            var klass = Class.forName(
                "me.basiqueevangelist.dashmixin.ClassDumpLoader",
                true,
                ClassLoader.getSystemClassLoader()
            );

            CLASSES = (Map<String, byte[]>) klass.getField("CLASSES").get(null);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] transform(Module module, ClassLoader loader, String name, Class<?> type, ProtectionDomain domain, byte[] classFile) {
        if (!loader.getClass().getName().contains("Knot")) return null;
        if (CLASSES.containsKey(name)) return null;

        try {
            out.writeUTF(name);
            out.writeInt(classFile.length);
            out.write(classFile);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
