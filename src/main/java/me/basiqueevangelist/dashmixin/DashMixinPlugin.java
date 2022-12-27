package me.basiqueevangelist.dashmixin;

import net.auoeke.reflect.Reflect;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;

public class DashMixinPlugin implements IMixinConfigPlugin {
    public static final Path DUMP_PATH = FabricLoader.getInstance().getGameDir().resolve("dashmixin.dump");

    @Override
    public void onLoad(String mixinPackage) {
        try {
            Instrumentation instrumentation = Reflect.instrument().value();

            tryAddClassToPlatformClassLoader(instrumentation, DashMixinPlugin.class);
            tryAddClassToPlatformClassLoader(instrumentation, Reflect.class);

            Class.forName(
                "me.basiqueevangelist.dashmixin.DashMixinInit",
                true,
                ClassLoader.getSystemClassLoader()
            )
                .getMethod("init", ClassLoader.class)
                .invoke(null, DashMixinPlugin.class.getClassLoader());
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void tryAddClassToPlatformClassLoader(Instrumentation instrumentation, Class<?> klass) {
        try {
            var path = new File(DashMixinPlugin.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                .toPath();

            if (Files.isRegularFile(path))
                instrumentation.appendToSystemClassLoaderSearch(new JarFile(path.toFile()));
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
