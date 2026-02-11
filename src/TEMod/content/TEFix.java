package TEMod.content;

import arc.graphics.Color;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.UnitTypes;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.FlakBulletType;
import mindustry.type.Category;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.meta.BuildVisibility;

import static mindustry.type.ItemStack.with;

public class TEFix {
    public static void load() {
        Items.graphite.hardness = 2;

        UnitTypes.alpha.buildSpeed = 1;
        UnitTypes.alpha.speed = 4;
        UnitTypes.alpha.weapons.get(0).reload = 10;

        UnitTypes.beta.buildSpeed = 1.5f;
        UnitTypes.beta.speed = 4.5f;
        UnitTypes.beta.weapons.get(0).reload = 15;

        UnitTypes.gamma.buildSpeed = 2;
        UnitTypes.gamma.speed = 4.66f;
        UnitTypes.gamma.weapons.get(0).reload = 10;

        Blocks.sporeWall.attributes.set(TEAttribute.sporeWalls, 1);
        Blocks.sporePine.attributes.set(TEAttribute.sporeWalls, 1);
        Blocks.whiteTree.attributes.set(TEAttribute.sporeWalls, 1);

        Blocks.oxidationChamber.canOverdrive = true;
    }
}
