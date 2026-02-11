package TEMLib;

import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import mindustry.game.Team;
import mindustry.game.Teams;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.state;

public class lib {//没什么用的lib
    public static void noop() {}

    public static ItemStack[] sizeWith(ItemStack[] stacks, Block source) {
        return ItemStack.mult(stacks, (float) Math.pow(source.size, 2));
    }

    public static void itemsDisplay(ItemStack[] stacks, Table table) {
        if (stacks == null) return;
        for (ItemStack it : stacks) {
            table.add(StatValues.displayItem(it.item, it.amount, true)).pad(5f);
        }
    }

    public static void itemsDisplay(ItemStack[] stacks, Table table, float craftTime) {
        if (stacks == null) return;
        for (ItemStack it : stacks) {
            table.add(StatValues.displayItem(it.item, it.amount, craftTime, true)).pad(5f);
        }
    }

    public static void liquidsDisplay(LiquidStack[] stacks, Table table) {
        if (stacks == null) return;
        for (LiquidStack it : stacks) {
            table.add(StatValues.displayLiquid(it.liquid, it.amount, true)).pad(5f);
        }
    }
}
