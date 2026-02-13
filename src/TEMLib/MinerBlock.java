package TEMLib;

import arc.util.Nullable;
import arc.util.Scaling;
import mindustry.Vars;
import mindustry.gen.BlockUnitUnit;
import mindustry.type.Item;
import mindustry.type.UnitType;
import mindustry.ui.Styles;
import mindustry.world.blocks.defense.turrets.BaseTurret;
import mindustry.world.blocks.environment.OreBlock;
import mindustry.world.blocks.environment.StaticWall;
import mindustry.world.meta.Stat;

public class MinerBlock extends BaseTurret {
    public float mineSpeed = 1f, mineRange = 100f;
    public int mineTier = 4;
    public boolean mineFloor = true, mineWalls = true;

    public @Nullable UnitType unitType;

    public MinerBlock(String name) {
        super(name);
    }

    @Override
    public void init() {
        super.init();

        unitType = new UnitType("miner-unit-" + name) {{
            hidden = true;
            internal = true;
            speed = 0f;
            hitSize = 0f;
            health = 1;
            itemCapacity = 0;
            mineSpeed = MinerBlock.this.mineSpeed;
            mineRange = MinerBlock.this.mineRange;
            mineTier = MinerBlock.this.mineTier;
            mineFloor = MinerBlock.this.mineFloor;
            mineWalls = MinerBlock.this.mineWalls;
            mineItems = Vars.content.blocks().select(b -> b instanceof OreBlock || (b instanceof StaticWall && b.itemDrop != null))
                    .map(b -> b.itemDrop).select(it -> it.hardness <= mineTier); //适配大部分模组的抽象可挖掘矿物筛选器
            rotateSpeed = MinerBlock.this.rotateSpeed;
            constructor = BlockUnitUnit::create;
        }};
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.addPercent(Stat.mineSpeed, mineSpeed);
        stats.addPercent(Stat.mineTier, mineTier);
        stats.add(TEStat.canMine, table -> {
            table.row();
            table.table(Styles.black5, t -> {
                for (Item it : unitType.mineItems) {
                    t.image(it.uiIcon).scaling(Scaling.fit).size(Vars.iconMed);
                }
            });
        });
    }

    public class MinerBuild extends BaseTurretBuild {
        @Override
        public void updateTile() {

        }
    }
}
