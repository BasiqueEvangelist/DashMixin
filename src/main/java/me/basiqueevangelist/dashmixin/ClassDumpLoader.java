package me.basiqueevangelist.dashmixin;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class ClassDumpLoader {
    public static final Map<String, byte[]> CLASSES;

    static {
        if (Files.exists(DashMixinPlugin.DUMP_PATH)) {
            CLASSES = new HashMap<>();
            try (DataInputStream dis
                     = new DataInputStream(new BufferedInputStream(Files.newInputStream(DashMixinPlugin.DUMP_PATH)))) {
                while (true) {
                    String name = dis.readUTF();
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

    }
}
