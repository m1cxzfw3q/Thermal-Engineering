package TEMod.content;

import TEMLib.StarshipUnitType;
import arc.math.geom.Rect;
import mindustry.gen.MechUnit;
import mindustry.type.UnitType;

import static TEMod.content.TESounds.steelPipeSound1;

/** 开始画大饼了 */
public class TEUnitTypes {
    /** 传奇T6 */
    public static UnitType coupling;//致敬传奇肘击王耦合
    /** baimao投稿的单位 */
    public static UnitType siegeTank, liberator;
    /** 只要写成了我就是全mdt最强开发者写不成就是全mdt最fw开发者 之一的LTX(我正在开发的某独立游戏的代号)星舰单位 */
    public static StarshipUnitType cosmicClassStarship; //宇宙级星舰(别问为啥叫这个名)

    /** 特种单位 T3 */
    public static UnitType flame;
    //炽焰(陆军)

    /** 特种单位 T4 */
    public static UnitType incinerate;
    //焚世(陆军)

    /** 特种单位 T5 */
    public static UnitType destruction;
    //毁灭(陆军)

    //特种单位没有T6

    /** 特殊单位 */
    public static UnitType steelPipe;

    public static void load() {
        steelPipe = new UnitType("steel-pipe") {{
                health = 1145;
                armor = 30;
                constructor = MechUnit::create;
                hitSize = 17;
                drawCell = false;
                deathSound = steelPipeSound1;
                deathSoundVolume = 0.6f;
        }};
    }//TODO Unit
}
