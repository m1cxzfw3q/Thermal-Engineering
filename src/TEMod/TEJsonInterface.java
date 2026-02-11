package TEMod;

import TEMod.content.*;
import TEMLib.*;
import arc.struct.ObjectMap;
import mindustry.mod.ClassMap;

/// 为JSONMOD提供的接口
public class TEJsonInterface {
    static final ObjectMap<String, Class<?>> TEI = ObjectMap.of(
            "TEMOD_Content", TESpecialContent.TEContent.class,
            "TEMOD_MultiCrafter", MultiCrafter.class, //不建议使用这个多合成系统，容易崩溃且还在重置
            "TEMOD_Recipe", MultiCrafter.Recipe.class,
            "TEMOD_PortableCoreBlock", PortableCoreBlock.class,
            "TEMOD_StarshipUnitType", StarshipUnitType.class, //没做完
            "TEMOD_NullAI", NullAI.class,
            "TEMOD_CoverBlock", CoverBlock.class,
            "TEMOD_CoverExtract", CoverExtract.class, //没做完
            "TEMOD_CoverLiquidRequireFloor", CoverLiquidRequireFloor.class,
            "TEMOD_EnvironmentalPollution", EnvironmentalPollution.class, //没做完
            "TEMOD_ExpandOverdriveProjector", ExpandOverdriveProjector.class, //没做完
            "TEMOD_ExplosionLaserBulletType", ExplosionLaserBulletType.class, //这是史 不建议用
            "TEMOD_ExponentiationOverdriveProjector", ExponentiationOverdriveProjector.class, //没做完
            "TEMOD_HardDriveItem", HardDriveItem.class, //没做完 估计不会做完(这东西对于mdt还是太难了)
            "TEMOD_LightItemBridge", LightItemBridge.class, //这是史 但能用
            "TEMOD_MultiChargeTurret", MultiChargeTurret.class, //这是史 不建议用
            "TEMOD_PayloadLauncher", PayloadLauncher.class, //没做完
            "TEMOD_Plotline", Plotline.class, //没做完
            "TEMOD_Plot", Plotline.Plot.class, //没做完
            "TEMOD_StackItemLiquid", StackItemLiquid.class //这是史 不建议用
    );

    public TEJsonInterface() {
        load();
    }

    public static void load() {
        ClassMap.classes.putAll(TEI);
    }
}