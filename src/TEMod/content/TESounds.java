package TEMod.content;

import arc.Core;
import arc.assets.AssetDescriptor;
import arc.assets.loaders.SoundLoader;
import arc.audio.Sound;
import arc.files.Fi;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.Vars;

public class TESounds {
    private static final ObjectMap<String, Sound> sounds = new ObjectMap<>();
    public static void load() {
        Seq<Fi> file = Vars.tree.get("sounds/").findAll(s -> s.name().contains(".ogg") || s.name().contains(".mp3"));
        for (Fi fi : file) {
            Sound sound = new Sound();
            AssetDescriptor<?> desc = Core.assets.load(fi.path(), Sound.class, new SoundLoader.SoundParameter(sound));
            desc.errored = Throwable::printStackTrace;
            sounds.put(fi.name(), sound);
        }
    }

    public static Sound get(String soundName) {
        return sounds.get(soundName);
    }
}