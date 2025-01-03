package com.translate.translatechat;

import java.util.HashMap;
import java.util.Map;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class Config {
        public static Map<String, CommonConfig> CONFIGS = new HashMap<>();
        public static ForgeConfigSpec COMMON_SPEC;

        static {
                ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
                CONFIGS.put("general", new CommonConfig(builder,"general"));
                COMMON_SPEC = builder.build();
        }

        public static void addConfigSection(String sectionName){
                ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
                CommonConfig newConfig = new CommonConfig(builder, sectionName);
                CONFIGS.put(sectionName, newConfig);
                COMMON_SPEC = builder.build();

                ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
        }

        public static class CommonConfig {
                public final ForgeConfigSpec.ConfigValue<String> fetchURL;
                public final ForgeConfigSpec.ConfigValue<String> fetchTextType;
                public final ForgeConfigSpec.ConfigValue<String> fetchTargetType;
                public final ForgeConfigSpec.ConfigValue<Boolean> debug;
                public final ForgeConfigSpec.ConfigValue<String> fetchKey;
                public final ForgeConfigSpec.ConfigValue<String> playerNameIndexOf;


                public CommonConfig(ForgeConfigSpec.Builder builder, String sectionName) {
                        builder.comment("Settings for section " + sectionName)
                                .push(sectionName);
                        fetchURL = builder
                                .comment("Set Translate Fetch URL\nDefault:https://script.google.com/macros/s/AKfycbxd0Z5iavmXxdxdtn71VYftLvIBzCjmE2NuxUSZw24z-JuYjuOf-FO3B922MBW3D_Y/exec?")
                                .define("fetchURL", "https://script.google.com/macros/s/AKfycbxd0Z5iavmXxdxdtn71VYftLvIBzCjmE2NuxUSZw24z-JuYjuOf-FO3B922MBW3D_Y/exec?");
                        fetchTextType = builder
                                .comment("Set Send Text Data Type\nDefault:text=")
                                .define("fetchTextType","text=");
                        fetchTargetType = builder
                                .comment("Set Send Target Language Type\nDefault:target=")
                                .define("fetchTargetType","target=");
                        debug = builder
                                .comment("Set Debug Mode\nDefault:false")
                                .define("debug",false);
                        fetchKey = builder
                                .comment("Set Fetch JSON Key\nDefault:text")
                                .define("fetchKey", "text");
                        playerNameIndexOf = builder
                                .comment("Set Player Name Start Index Of\nDefault:>")
                                .define("playerNameIndexOf", ">");
                        builder.pop();
                }
        }
}