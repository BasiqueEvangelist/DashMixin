package me.basiqueevangelist.dashmixin;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class ClassDumpLoader {
    private static Map<String, byte[]> CLASSES;
    private static Set<String> CLASS_NAMES;
    public static BiConsumer<Object, Object> LOGGER_ADAPTER;

    private static void init() {
        if (Files.exists(DashMixinPlugin.DUMP_PATH)) {
            CLASSES = new HashMap<>();
            try (DataInputStream dis
                     = new DataInputStream(new BufferedInputStream(Files.newInputStream(DashMixinPlugin.DUMP_PATH)))) {
                while (true) {
                    int nameLength = dis.readUnsignedShort();
                    byte[] nameBytes = new byte[nameLength];
                    dis.readFully(nameBytes);
                    String name = new String(nameBytes, StandardCharsets.UTF_8);

                    int length = dis.readInt();
                    byte[] bytes = new byte[length];
                    dis.readFully(bytes);

                    CLASSES.put(name, bytes);
                }
            } catch (EOFException e) {
                // ...
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            CLASSES = Map.of();
        }

        CLASS_NAMES = new HashSet<>(CLASSES.keySet());
    }

    public static Map<String, byte[]> getClasses() {
        if (CLASSES == null) init();
        return CLASSES;
    }

    public static Set<String> getLoadedClassNames() {
        if (CLASS_NAMES == null) init();
        return CLASS_NAMES;
    }
}
