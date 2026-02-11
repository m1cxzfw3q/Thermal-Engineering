package TEMLib;

import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Scaling;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.gen.BlockUnitUnit;
import mindustry.type.Item;
import mindustry.type.UnitType;
import mindustry.ui.Styles;
import mindustry.world.blocks.defense.turrets.BaseTurret;
import mindustry.world.meta.Stat;

public class MinerBlock extends BaseTurret {
    public float mineSpeed = 1f, mineRange = 100f;
    public int mineTier = 1;
    public boolean mineFloor = true, mineWalls = true;
    public Seq<Item> mineItems = Seq.with(Items.copper, Items.lead, Items.titanium, Items.thorium);

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
            mineItems = MinerBlock.this.mineItems;
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
                for (Item it : mineItems) {
                    t.image(it.uiIcon).scaling(Scaling.fit).size(Vars.iconMed);
                }
            });
        });
    }
}
