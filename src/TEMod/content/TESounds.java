package TEMod.content;

import arc.audio.Sound;
import arc.files.Fi;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.gen.Sounds;

public class TESounds {
    private static final ObjectMap<String, Sound> sounds = new ObjectMap<>();
    public static void load() {
        Fi path = Vars.tree.get("sounds/");
        if (path.exists() && path.isDirectory()){
            Seq<Fi> files = path.findAll();
            for (Fi fi : files) {
                sounds.put(fi.name(), Vars.tree.loadSound(fi.name()));
            }
        }
    }

    public static Sound get(String soundName) {
        if (sounds.containsKey(soundName)) return sounds.get(soundName);
        else {
            Log.warn("[TEMod] Sound " + soundName + " not found.");
            return Sounds.none;
        }
    }
}