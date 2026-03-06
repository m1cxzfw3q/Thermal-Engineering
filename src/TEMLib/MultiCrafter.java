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

public class MultiCrafter extends GenericCrafter {
    public @Nullable Seq<Seq<Recipe>> recipes = new Seq<>();

    public static float uniCraftTime;

    public MultiCrafter(String name) {
        super(name);
        configurable = true;
        saveConfig = true;
        config(Integer.class, (MultiCrafterBuild e, Integer i) -> {});
    }

    @Override
    public void init() {
        consume(new ConsumeItemDynamic(
                (MultiCrafterBuild e) -> e.currentRecipeId != -1 ?
                        e.getCurrentRecipes(e.currentConfigurationId).get(e.currentRecipeId).input.items : ItemStack.empty
        ));
        consume(new ConsumeLiquidsDynamic(
                (MultiCrafterBuild e) -> e.currentRecipeId != -1 ?
                        e.getCurrentRecipes(e.currentConfigurationId).get(e.currentRecipeId).input.liquids : LiquidStack.empty
        ));

        if (!recipes.isEmpty()) {
            for (var recipe1 : recipes) {
                if (!recipe1.isEmpty()) {
                    for (var recipe : recipe1) {
                        for (var item : recipe.input.items) {
                            itemFilter[item.item.id] = true;
                        }

                        for (var item : recipe.input.liquids) {
                            liquidFilter[item.liquid.id] = true;
                        }
                    }
                }
            }
        }
        
        super.init();
    }

    @Override
    public void load(){
        super.load();
    }

    @Override
    public boolean outputsItems() {
        boolean b = false;
        for (Seq<Recipe> recipes1 : recipes) for (Recipe recipe : recipes1) {
            b = b || recipe.input.items != ItemStack.empty;
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

            final int[] i = {0}, i1 = {0};
            for (Seq<Recipe> configRecipe : recipes) {
                table.table(Styles.black5, t -> {
                    t.add("[#ffd37f][" + i[0] + "][]");
                    for (Recipe recipe : configRecipe) {
                        table.table(Styles.black5, tl -> {
                            tl.left();
                            tl.add("[#ffd37f][" + i1[0] + "][]");
                            i[0]++;
                            tl.table(Styles.black5, t1 -> {
                                lib.itemsDisplay(recipe.input.items, table, recipe.craftTime);
                                lib.liquidsDisplay(recipe.input.liquids, table);
                            });
                            tl.image(Icon.right).color(Pal.darkishGray).size(40).pad(5f).fill();
                            tl.table(Styles.black5, t1 -> {
                                lib.itemsDisplay(recipe.output.items, table, recipe.craftTime);
                                lib.liquidsDisplay(recipe.output.liquids, table);
                            });
                        });
                        table.row();
                    }
                });
            }
        });
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("recipe", (MultiCrafterBuild e) -> new Bar(
                () -> Core.bundle.get("tebar.recipe") + ": " + e.currentRecipe.localizedName(),
                () -> Color.valueOf("4169e1"),
                () -> 1
        ));
        addBar("config", (MultiCrafterBuild e) -> new Bar(
                () -> Core.bundle.get("bar.config") + ": " + e.currentConfigurationId,
                () -> Pal.bar,
                () -> 1
        ));
    }

    public class MultiCrafterBuild extends GenericCrafterBuild {
        public int currentRecipeId = -1;
        public int currentConfigurationId = 0;
        public @Nullable Seq<Recipe> currentRecipes = null;
        public @Nullable Recipe currentRecipe = null;

        @Override
        public void buildConfiguration(Table table) {// TODO 重写交互UI

        }

        @Override
        public void updateTile() {
            currentRecipes = getCurrentRecipes(currentConfigurationId);
            if (currentRecipes != null) for (Recipe recipe : currentRecipes) {
                if (items.has(recipe.input.items) && lib.hasLiquid(liquids, recipe.input.liquids)) currentRecipe = recipe;
            }

            if (currentRecipe != null) {
                craftTime = currentRecipe.craftTime;
                outputItems = currentRecipe.output.items;
                outputLiquids = currentRecipe.output.liquids;
            }

            super.updateTile();
        }

        public Seq<Recipe> getCurrentRecipes(int configId) {
            if (configId == -1 && recipes.get(configId) == null) return null;
            return recipes.get(configId);
        }
    }

    public static class Recipe {
        public StackItemLiquid input = new StackItemLiquid(), output = new StackItemLiquid();
        public float craftTime = 60f;

        public Recipe() {}

        public Recipe(StackItemLiquid input, StackItemLiquid output) {
            this.input = input;
            this.output = output;
        }
        public Recipe(StackItemLiquid input, StackItemLiquid output, float craftTime) {
            this.input = input;
            this.output = output;
            this.craftTime = craftTime;
        }

        public String localizedName() {
            StringBuilder str = new StringBuilder();
            for (ItemStack it : input.items) {
                str.append(it.item.uiIcon);
            }
            for (LiquidStack it : input.liquids) {
                str.append(it.liquid.uiIcon);
            }
            str.append(" -> ");
            for (ItemStack it : output.items) {
                str.append(it.item.uiIcon);
            }
            for (LiquidStack it : output.liquids) {
                str.append(it.liquid.uiIcon);
            }
            return str.toString();
        }
    }
}
