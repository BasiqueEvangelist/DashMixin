package me.basiqueevangelist.dashmixin;

import net.auoeke.reflect.ClassTransformer;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.ProtectionDomain;

public class ClassSaverTransformer implements ClassTransformer {
    private final DataOutputStream out;

    public ClassSaverTransformer() throws IOException {
        out = new DataOutputStream(new BufferedOutputStream(
            Files.newOutputStream(DashMixinPlugin.DUMP_PATH, StandardOpenOption.APPEND, StandardOpenOption.CREATE)));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                out.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    @Override
    public byte[] transform(Module module, ClassLoader loader, String name, Class<?> type, ProtectionDomain domain, byte[] classFile) {
        if (!loader.getClass().getName().contains("Knot")) return null;
        if (ClassDumpLoader.getLoadedClassNames().contains(name)) return null;

        try {
            byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
            out.writeShort(nameBytes.length);
            out.write(nameBytes);

            out.writeInt(classFile.length);
            out.write(classFile);

            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
