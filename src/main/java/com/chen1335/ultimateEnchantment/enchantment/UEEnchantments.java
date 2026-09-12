package com.chen1335.ultimateEnchantment.enchantment;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.CutDown;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.DoubleHook;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.EtherealArrow;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.HardenedMana;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.HurtDeepens;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.KineticEnergy;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.LethalTempo;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.LifeSteal;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.ManaSteal;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.QuickBait;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.Tear;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.ThunderBolt;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.Vanquisher;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.Ultimate;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.LastStand;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.Legend;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.OverGrow;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.QuickShooting;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.Scabbing;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.QuickLatch;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.Smelting;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.Eternal;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.UltimateSlayer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonParseException;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.script.SimpleBindings;

public class UEEnchantments {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final Map<ResourceKey<Enchantment>, EnchantmentBasic> MAP = new HashMap<>();
    public static final LethalTempo LETHAL_TEMPO = register(new LethalTempo());
    public static final Ultimate ULTIMATE = register(new Ultimate());
    public static final CutDown CUT_DOWN = register(new CutDown());
    public static final DoubleHook DOUBLE_HOOK = register(new DoubleHook());
    public static final HardenedMana HARDENED_MANA = register(new HardenedMana());
    public static final KineticEnergy KINETIC_ENERGY = register(new KineticEnergy());
    public static final LifeSteal LIFE_STEAL = register(new LifeSteal());
    public static final ManaSteal MANA_STEAL = register(new ManaSteal());
    public static final QuickBait QUICK_BAIT = register(new QuickBait());
    public static final Tear TEAR = register(new Tear());
    public static final ThunderBolt THUNDER_BOLT = register(new ThunderBolt());
    public static final Vanquisher VANQUISHER = register(new Vanquisher());
    public static final LastStand LAST_STAND = register(new LastStand());
    public static final Legend LEGEND = register(new Legend());
    public static final OverGrow OVER_GROW = register(new OverGrow());
    public static final QuickShooting QUICK_SHOOTING = register(new QuickShooting());
    public static final Scabbing SCABBING = register(new Scabbing());
    public static final QuickLatch QUICK_LATCH = register(new QuickLatch());
    public static final Smelting SMELTING = register(new Smelting());
    public static final Eternal ETERNAL = register(new Eternal());
    public static final UltimateSlayer ULTIMATE_SLAYER = register(new UltimateSlayer());
    public static final EtherealArrow ETHEREAL_ARROW = register(new EtherealArrow());
    public static final HurtDeepens HURT_DEEPENS = register(new HurtDeepens());

    public static SimpleBindings buildBindings(int level) {
        return EnchantmentBasic.buildBindings(level);
    }

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
            UltimateEnchantment.LOGGER.error("Enchant configuration path exceeds configuration directory: {}", file);
            return enchantment;
        }

        String version = ModList.get()
                .getModFileById(UltimateEnchantment.MODID)
                .versionString();
        if (Files.exists(file, LinkOption.NOFOLLOW_LINKS)) {
            try {
                JsonObject existing = JsonParser.parseString(
                        Files.readString(file, StandardCharsets.UTF_8)
                ).getAsJsonObject();
                if (existing.has("version")
                        && version.equals(existing.get("version").getAsString())) {
                    return enchantment;
                }
            } catch (JsonParseException | IllegalStateException | IOException exception) {
                UltimateEnchantment.LOGGER.error(
                        "Unable to read enchantment profile version {}, keeping existing file {}",
                        version,
                        file,
                        exception
                );
                return enchantment;
            }
        }

        try {
            Files.createDirectories(file.getParent());
            JsonObject json = enchantment.toJson();
            json.addProperty("version", version);
            Files.writeString(
                    file,
                    GSON.toJson(json) + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException exception) {
            UltimateEnchantment.LOGGER.error("Unable to create enchantment profile {}", file, exception);
        }
        return enchantment;
    }

    public static void init() {

    }
}
