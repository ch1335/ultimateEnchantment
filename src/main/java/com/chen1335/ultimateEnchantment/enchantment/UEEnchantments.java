package com.chen1335.ultimateEnchantment.enchantment;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.CutDown;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.LethalTempo;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.Ultimate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UEEnchantments {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final Map<ResourceKey<Enchantment>, EnchantmentBasic> MAP = new HashMap<>();
    public static final LethalTempo LETHAL_TEMPO = register(new LethalTempo());
    public static final Ultimate ULTIMATE = register(new Ultimate());
    public static final CutDown CUT_DOWN = register(new CutDown());

    public static Optional<EnchantmentBasic> getEnchantment(ResourceKey<Enchantment> key) {
        return Optional.ofNullable(MAP.get(key));
    }


    private static <T extends EnchantmentBasic> T register(T enchantment) {
        ResourceKey<Enchantment> key = enchantment.getKey();
        MAP.put(key, enchantment);
        Path directory = FMLPaths.CONFIGDIR.get()
                .resolve(UltimateEnchantment.MODID)
                .resolve("enchantments")
                .toAbsolutePath().normalize();
        Path file = directory.resolve(key.location().getPath() + ".json").normalize();
        if (!file.startsWith(directory)) {
            UltimateEnchantment.LOGGER.error("Enchant configuration path exceeds configuration directory：{}", file);
            return enchantment;
        }
        if (Files.exists(file, LinkOption.NOFOLLOW_LINKS)) {
            return enchantment;
        }

        try {
            Files.createDirectories(file.getParent());
            String json = GSON.toJson(enchantment.toJson()) + System.lineSeparator();
            Files.writeString(file, json, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
        } catch (FileAlreadyExistsException exception) {
            // 检查后文件可能已被其他调用创建，保留已有配置。
        } catch (IOException exception) {
            UltimateEnchantment.LOGGER.error("Unable to create enchantment profile {}", file, exception);
        }
        return enchantment;
    }

    public static void init() {

    }
}
