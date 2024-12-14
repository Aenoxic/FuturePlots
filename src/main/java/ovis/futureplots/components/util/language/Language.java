/*
 * Copyright 2024 tim03we, Ovis Development
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package ovis.futureplots.components.util.language;

import cn.nukkit.utils.Config;
import ovis.futureplots.FuturePlots;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tim tim03we, Ovis Development (2024)
 */
public class Language {

    private static String version = "2.0.0";
    public static HashMap<String, HashMap<String, String>> messages = new HashMap<>();

    private static String[] official_langs = new String[]{"en_US", "de_DE"};

    public static void init() {
        messages.clear();
        new File(FuturePlots.getInstance().getDataFolder() + "/lang/").mkdirs();
        for (String languageKey : official_langs) {
            FuturePlots.getInstance().saveResource("lang/" + languageKey + ".yml");
            File file = new File(FuturePlots.getInstance().getDataFolder() + "/lang/" + languageKey + ".yml");
            Config messageConfig;
            if(file.exists()) {
                messageConfig = new Config(file, Config.YAML);
                if(!messageConfig.getString("version").equals(version)) {
                    File oldFile = new File(FuturePlots.getInstance().getDataFolder() + "/lang/" + languageKey + "_old.yml");
                    if(oldFile.exists()) oldFile.delete();
                    file.renameTo(oldFile);
                    FuturePlots.getInstance().saveResource("lang/" + languageKey + ".yml");
                    messageConfig = new Config(FuturePlots.getInstance().getDataFolder() + "/lang/" + languageKey + ".yml", Config.YAML);
                    System.out.println("New File");
                }
            } else {
                System.out.println("File " + languageKey + ".yml" + " not found...");
                break;
            }
            HashMap<String, String> messageMap = new HashMap<>();

            if (messageConfig.getAll() != null) {
                FuturePlots.getInstance().getLogger().info("Cache language " + languageKey + "...");
                for (Map.Entry<String, Object> map : messageConfig.getAll().entrySet()) {
                    String key = map.getKey();
                    if (map.getValue() instanceof String) {
                        String val = (String)map.getValue();
                        messageMap.put(key, val);
                    }
                }
                messages.put(languageKey, messageMap);
            }
        }
        FuturePlots.getInstance().getLogger().info("All languages cached...");
    }

    private final String locale;

    public Language(String locale) {
        this.locale = locale;
    }


    /*
    public static String translate(boolean prefix, String key, Object... replacements) {
        String message;
        if(prefix) {
            message = get(key);
        } else {
            message = getNoPrefix(key);
        }
        if(replacements == null) {
            return message;
        }
        int i = 1;
        for (Object replacement : replacements) {
            message = message.replace("%" + i, String.valueOf(replacement));
            i++;
        }
        return message;
    }
     */

    public String message(String key, Object... replacements) {
        String message = get(key);
        if (replacements == null)
            return message;
        int i = 1;
        for (Object replacement : replacements) {
            message = message.replace("%" + i, String.valueOf(replacement));
            i++;
        }
        return message;
    }

    public String message(TranslationKey key, Object... replacements) {
        return message(key.getKey(), replacements);
    }

    private String getValidLocale() {
        return Language.messages.get(this.locale) != null ? this.locale : "en_US";
    }

    private String get(String key) {
        return (String)((HashMap) Language.messages.get(getValidLocale())).getOrDefault(key, "null");
    }
}