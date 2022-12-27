package me.basiqueevangelist.dashmixin;

import net.auoeke.reflect.ClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.ProtectionDomain;
import java.util.Objects;

public class KnotClassDelegateTransformer implements ClassTransformer {
    @Override
    public byte[] transform(Module module, ClassLoader loader, String name, Class<?> type, ProtectionDomain domain, byte[] classFile) {
        if (!Objects.equals(name, "net/fabricmc/loader/impl/launch/knot/KnotClassDelegate")) return null;

        try {
            ClassNode node = new ClassNode();
            new ClassReader(classFile).accept(node, 0);

            for (var m : node.methods) {
                if (!Objects.equals(m.name, "loadClass")) continue;

                InsnList liste = new InsnList();

                liste.add(new VarInsnNode(Opcodes.ALOAD, 0));
                liste.add(new FieldInsnNode(
                    Opcodes.GETFIELD,
                    "net/fabricmc/loader/impl/launch/knot/KnotClassDelegate",
                    "classLoader",
                    "Ljava/lang/ClassLoader;"
                ));
                liste.add(new VarInsnNode(Opcodes.ALOAD, 1));
                liste.add(new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "me/basiqueevangelist/dashmixin/LoaderHelper",
                    "tryLoadClass",
                    "(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;"));
                liste.add(new InsnNode(Opcodes.DUP));
                LabelNode label = new LabelNode();
                liste.add(new JumpInsnNode(Opcodes.IFNULL, label));
                liste.add(new VarInsnNode(Opcodes.ALOAD, 3));
                liste.add(new InsnNode(Opcodes.MONITOREXIT));
                liste.add(new InsnNode(Opcodes.ARETURN));
                liste.add(label);

                for (var insn : m.instructions) {
                    if (insn instanceof MethodInsnNode methodInsn && methodInsn.name.equals("findLoadedClassFwd"))
                        m.instructions.insert(insn, liste);
                }
            }

            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            node.accept(cw);
            byte[] bytes = cw.toByteArray();

            if (Boolean.getBoolean("dashmixin.dumpKnotClassDelegate")) {
                Files.write(Path.of("KnotClassDelegate.class"), bytes);
            }

            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
