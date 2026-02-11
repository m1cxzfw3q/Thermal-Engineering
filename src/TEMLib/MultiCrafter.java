package TEMLib;

import arc.Core;
import arc.scene.ui.layout.Table;
import arc.struct.*;
import arc.graphics.Color;
import arc.util.Nullable;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.production.*;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.consumers.ConsumeLiquidsDynamic;
import mindustry.world.meta.*;

import java.util.Arrays;

public class MultiCrafter extends GenericCrafter {
    public @Nullable Seq<Recipe> recipes = new Seq<>();

    public static float uniCraftTime;

    public MultiCrafter(String name) {
        super(name);
        configurable = true;
        saveConfig = true;
        config(Integer.class, (MultiCrafterBuild e, Integer i) -> {});

        boolean b = false;
        for (Recipe recipe : recipes) {
            b = b || !Arrays.equals(recipe.inputItems, new ItemStack[]{});
        }
        hasItems = b;
        b = false;
        for (Recipe recipe : recipes) {
            b = b || !Arrays.equals(recipe.inputLiquids, new LiquidStack[]{});
        }
        hasLiquids = b;
    }

    @Override
    public void init() {
        consume(new ConsumeItemDynamic(
                (MultiCrafterBuild e) -> e.currentRecipeId != -1 ? recipes.get(Math.min(e.currentRecipeId, recipes.size - 1)).inputItems : new ItemStack[]{}
        ));
        consume(new ConsumeLiquidsDynamic(
                (MultiCrafterBuild e) -> e.currentRecipeId != -1 ? recipes.get(Math.min(e.currentRecipeId, recipes.size - 1)).inputLiquids : new LiquidStack[]{}
        ));
        
        super.init();
    }

    @Override
    public void load(){
        super.load();
    }

    @Override
    public boolean outputsItems() {
        boolean b = false;
        for (Recipe recipe : recipes) {
            b = b || !Arrays.equals(recipe.outputItems, new ItemStack[]{});
        }
        return b;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.output);
        stats.remove(Stat.productionTime);
        stats.add(Stat.output, table -> {
            table.row();

            final int[] i = {0};
            for (Recipe recipe : recipes) {
                table.table(Styles.grayPanel, t -> {
                    t.left();
                    t.add("[#ffd37f][" + i[0] + "][]");
                    i[0]++;
                    t.table(Styles.black5, t1 -> {
                        lib.itemsDisplay(recipe.inputItems, table, recipe.craftTime);
                        lib.liquidsDisplay(recipe.inputLiquids, table);
                    });
                    t.image(Icon.right).color(Pal.darkishGray).size(40).pad(5f).fill();
                    t.table(Styles.black5, t1 -> {
                        lib.itemsDisplay(recipe.outputItems, table, recipe.craftTime);
                        lib.liquidsDisplay(recipe.outputLiquids, table);
                    });

                });
                table.row();
            }
        });
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("recipe", e -> new Bar(
                () -> Core.bundle.get("bar.recipe"),
                () -> Color.valueOf("4169e1"),
                () -> 1
        ));
    }

    /// 正在重写
    public class MultiCrafterBuild extends Building {
        public int currentRecipeId = -1;
        public @Nullable Recipe currentRecipe = getCurrentRecipe(currentRecipeId);
        public float progress;

        @Override
        public void buildConfiguration(Table table) {

        }

        @Override
        public void updateTile() {
            currentRecipe = getCurrentRecipe(currentRecipeId);
        }

        public Recipe getCurrentRecipe(int id) {
            if (id == -1) return null;
            return ((MultiCrafter) block).recipes.get(id);
        }

        @Override
        public void draw(){
            drawer.draw(this);
        }

        @Override
        public void drawLight(){
            super.drawLight();
            drawer.drawLight(this);
        }
    }

    public static class Recipe {
        public ItemStack[] inputItems = {}, outputItems = {};
        public float craftTime = 60f;
        public LiquidStack[] inputLiquids = {}, outputLiquids = {};

        public Recipe() {}

        public Recipe(StackItemLiquid input, StackItemLiquid output) {
            inputItems = input.items;
            outputItems = output.items;
            inputLiquids = input.liquids;
            outputLiquids = output.liquids;
        }
        public Recipe(StackItemLiquid input, StackItemLiquid output, float craftTime) {
            inputItems = input.items;
            outputItems = output.items;
            inputLiquids = input.liquids;
            outputLiquids = output.liquids;
            this.craftTime = craftTime;
        }

        public String localizedName() {
            StringBuilder str = new StringBuilder("{");
            for (ItemStack it : inputItems) {
                str.append(it.item.uiIcon);
            }
            for (LiquidStack it : inputLiquids) {
                str.append(it.liquid.uiIcon);
            }
            str.append("}->{");
            for (ItemStack it : outputItems) {
                str.append(it.item.uiIcon);
            }
            for (LiquidStack it : outputLiquids) {
                str.append(it.liquid.uiIcon);
            }
            str.append("}#").append(craftTime);
            return str.toString();
        }
    }
}
