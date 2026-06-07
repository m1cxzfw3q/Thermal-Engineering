package TEMod;

import TEMLib.ai.NullAI;
import TEMLib.block.defense.ExpandOverdriveProjector;
import TEMLib.block.defense.ExponentiationOverdriveProjector;
import TEMLib.block.defense.turrets.MultiChargeTurret;
import TEMLib.block.defense.turrets.PayloadLauncher;
import TEMLib.block.distribution.LightItemBridge;
import TEMLib.block.env.CoverBlock;
import TEMLib.block.env.CoverLiquidSupplyFloor;
import TEMLib.block.meta.StackItemLiquid;
import TEMLib.block.production.CoverExtract;
import TEMLib.block.production.MultiCrafter;
import TEMLib.block.storage.PortableCoreBlock;
import TEMLib.entities.bullets.ExplosionLaserBulletType;
import TEMLib.entities.unit.StarshipUnitType;
import TEMLib.special.EnvironmentalPollution;
import TEMLib.special.Plotline;
import TEMod.content.*;
import arc.struct.ObjectMap;
import mindustry.mod.ClassMap;

/// 为JSONMOD提供的接口
public class TEJsonInterface {
    static final ObjectMap<String, Class<?>> TEI = ObjectMap.of(
            "TEMOD_Content", TESpecialContent.TEContent.class,
            "TEMOD_MultiCrafter", MultiCrafter.class,
            "TEMOD_Recipe", MultiCrafter.Recipe.class,
            "TEMOD_PortableCoreBlock", PortableCoreBlock.class,
            "TEMOD_StarshipUnitType", StarshipUnitType.class, //没做完
            "TEMOD_NullAI", NullAI.class,
            "TEMOD_CoverBlock", CoverBlock.class,
            "TEMOD_CoverExtract", CoverExtract.class, //没做完
            "TEMOD_CoverLiquidRequireFloor", CoverLiquidSupplyFloor.class,
            "TEMOD_EnvironmentalPollution", EnvironmentalPollution.class, //没做完
            "TEMOD_ExpandOverdriveProjector", ExpandOverdriveProjector.class, //没做完
            "TEMOD_ExplosionLaserBulletType", ExplosionLaserBulletType.class, //这是史 不建议用
            "TEMOD_ExponentiationOverdriveProjector", ExponentiationOverdriveProjector.class, //没做完
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