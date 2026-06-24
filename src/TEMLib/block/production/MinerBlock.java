package TEMLib.block.production;

import arc.Core;
import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.Vars;
import mindustry.gen.BlockUnitUnit;
import mindustry.gen.BlockUnitc;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.world.blocks.RotBlock;
import mindustry.world.blocks.defense.turrets.BaseTurret;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.OreBlock;
import mindustry.world.blocks.environment.StaticWall;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

// TODO
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
        stats.add(Stat.mineTier, StatValues.drillables(mineSpeed, 1f, 1, null, b ->
                b.itemDrop != null &&
                        (b instanceof Floor f && (((f.wallOre && mineWalls) || (!f.wallOre && mineFloor))) ||
                                (!(b instanceof Floor) && mineWalls)) &&
                        b.itemDrop.hardness <= mineTier && (!b.playerUnmineable || Core.settings.getBool("doubletapmine"))));
    }

    public class MinerBuild extends BaseTurretBuild implements RotBlock {
        public BlockUnitc unit = (BlockUnitc)unitType.create(team);
        public @Nullable Unit following;
        public float warmup;

        {
            unit.rotation(90f);
        }

        @Override
        public float buildRotation(){
            return unit.rotation();
        }

        @Override
        public void updateTile() {
            unit.tile(this);
            unit.team(team);

            rotation = unit.rotation();

            if(unit.activelyBuilding()){
                unit.lookAt(angleTo(unit.buildPlan()));
            }

            if(checkSuppression()){
                efficiency = potentialEfficiency = 0f;
            }

            unit.mineTimer(potentialEfficiency * timeScale);

            warmup = Mathf.lerpDelta(warmup, unit.activelyBuilding() ? efficiency : 0f, 0.1f);


        }
    }
}
