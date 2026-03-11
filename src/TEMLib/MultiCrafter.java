package TEMLib;

import arc.Core;
import arc.math.Mathf;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import arc.struct.*;
import arc.graphics.Color;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.mod.NoPatch;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.blocks.heat.HeatConsumer;
import mindustry.world.blocks.production.*;
import mindustry.world.consumers.Consume;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

public class MultiCrafter extends Block {
    public @Nullable Seq<Seq<Recipe>> recipes = new Seq<>();
    // HeatCrafter
    /** After heat meets this requirement, excess heat will be scaled by this number. */
    public float overheatScale = 1f;
    /** Maximum possible efficiency after overheat. */
    public float maxEfficiency = 1f;
    // HeatProducer
    public float warmupRate = 0.15f;

    // GenericCrafter
    /** Liquid output directions, specified in the same order as outputLiquids. Use -1 to dump in every direction. Rotations are relative to block. */
    public int[] liquidOutputDirections = {-1};
    /** if true, crafters with multiple liquid outputs will dump excess when there's still space for at least one liquid type */
    public boolean dumpExtraLiquid = true;
    public boolean ignoreLiquidFullness = false;

    public Effect craftEffect = Fx.none;
    public Effect updateEffect = Fx.none;
    public float updateEffectChance = 0.04f;
    public float updateEffectSpread = 4f;
    public float warmupSpeed = 0.019f;
    /** Only used for legacy cultivator blocks. */
    @NoPatch
    public boolean legacyReadWarmup = false;
    // AttributeCrafter
    public @Nullable Attribute attribute;
    public float baseEfficiency = 1f;
    public float boostScale = 1f;
    public float maxBoost = 1f;
    public float minEfficiency = -1f;
    public float displayEfficiencyScale = 1f;
    public boolean displayEfficiency = true;
    public boolean scaleLiquidConsumption = false;

    public DrawBlock drawer = new DrawDefault();

    public static float uniCraftTime = 60;

    final DrawMulti drawHeat = new DrawMulti();

    public MultiCrafter(String name) {
        super(name);
        update = true;
        solid = true;
        acceptsItems = true;
        ambientSound = Sounds.loopMachine;
        sync = true;
        ambientSoundVolume = 0.03f;
        flags = EnumSet.of(BlockFlag.factory);

        configurable = true;
        saveConfig = true;
        config(Integer.class, (MultiCrafterBuild build, Integer i) -> {
            if(!configurable || build.currentConfigurationId == i) return;
            build.currentConfigurationId = i < 0 || i >= recipes.size ? -1 : i;
            build.progress = 0;
        });
    }

    @Override
    public void init() {
        super.init();

        if (!recipes.isEmpty()) for (var recipe1 : recipes) if (!recipe1.isEmpty()) for (var recipe : recipe1) if (recipe != null) {
            if (recipe.input != null) {
                if (recipe.input.items != null) {
                    hasItems = true;
                    for (var item : recipe.input.items) itemFilter[item.item.id] = true;
                }

                if (recipe.input.liquids != null) {
                    hasLiquids = true;
                    for (var item : recipe.input.liquids) liquidFilter[item.liquid.id] = true;
                }

                if (recipe.heatRequirement > 0 && !Seq.with(drawHeat.drawers).contains(new DrawHeatInput("-heatInput")))
                    drawHeat.drawers = Seq.with(drawHeat.drawers).add(new DrawHeatInput("-heatInput")).toArray();

                if (recipe.heatOutput > 0 && !Seq.with(drawHeat.drawers).contains(new DrawHeatOutput())) {
                    drawHeat.drawers = Seq.with(drawHeat.drawers).add(new DrawHeatOutput() {
                        @Override
                        public void load(Block block) {
                            heat = Core.atlas.find(block.name + "-heatOutput");
                            glow = Core.atlas.find(block.name + "-heatGlow");
                            top1 = Core.atlas.find(block.name + "-heatTop1");
                            top2 = Core.atlas.find(block.name + "-heatTop2");
                            }
                    }).toArray();
                    rotate = true;
                } else drawArrow = false;
            }
            if (recipe.output != null) {
                if (recipe.output.items != null && recipe.output.items.length != 0) hasItems = true;
                if (recipe.output.liquids != null && recipe.output.liquids.length != 0) hasLiquids = true;
            }
        }
    }

    @Override
    public void load(){
        super.load();
        drawer.load(this);
        drawHeat.load(this);
    }

    @Override
    public boolean outputsItems() {
        boolean b = false;
        for (Seq<Recipe> recipes1 : recipes) for (Recipe recipe : recipes1) {
            b = b || recipe.output.items != ItemStack.empty;
        }
        return b;
    }

    boolean statsAddedEff = false;

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        if(!displayEfficiency || attribute == null) return;

        drawPlaceText(Core.bundle.format("bar.efficiency",
                (int)((baseEfficiency + Math.min(maxBoost, boostScale * sumAttribute(attribute, x, y))) * 100f)), x, y, valid);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(TEStat.recipe, table -> {
            table.row();

            final int[] i = {0}, i1 = {0};
            for (Seq<Recipe> configRecipe : recipes) {
                table.table(Styles.grayPanel, t -> {
                    if (configurable) t.add("[#ffd37f][" + i[0] + "][]").left().row();
                    i[0]++;
                    for (Recipe recipe : configRecipe) {
                        t.table(Styles.grayPanel, tl -> {
                            tl.add("[#ffd37f][" + i1[0] + "][]");
                            i1[0]++;
                            lib.itemsDisplay(recipe.input.items, t, recipe.craftTime < 0 ? uniCraftTime : recipe.craftTime);
                            lib.liquidsDisplay(recipe.input.liquids, t);
                            tl.image(Icon.right).color(Pal.darkishGray).size(40);
                            lib.itemsDisplay(recipe.output.items, t, recipe.craftTime < 0 ? uniCraftTime : recipe.craftTime);
                            lib.liquidsDisplay(recipe.output.liquids, t);

                            if (recipe.heatRequirement > 0) {
                                tl.add(recipe.heatRequirement + "[red]" + Iconc.waves + "[]" + Iconc.download).right().grow().pad(10f);
                                if (!statsAddedEff) stats.add(Stat.maxEfficiency, (int)(maxEfficiency * 100f), StatUnit.percent);
                                statsAddedEff = true;
                            }
                            if (recipe.heatOutput > 0) tl.add(recipe.heatOutput + "[red]" + Iconc.waves + "[]" + Iconc.upload).right().grow().pad(10f);
                        }).left();
                        t.row();
                    }
                }).width(650).pad(5);
                table.row().row();
            }
        });

        if (attribute != null) stats.add(baseEfficiency <= 0.0001f ? Stat.tiles : Stat.affinities, attribute, floating, boostScale * size * size, !displayEfficiency);
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("recipe", (MultiCrafterBuild e) -> new Bar(
                () -> Core.bundle.format("tebar.recipe", e.currentRecipe != null ? e.currentRecipe.localizedName() : "\uE815"),
                () -> Color.valueOf("4169e1"),
                () -> 1
        ));

        if (configurable) addBar("config", (MultiCrafterBuild e) -> new Bar(
                () -> Core.bundle.format("tebar.config", e.currentConfigurationId),
                () -> Pal.bar,
                () -> 1
        ));

        if(!displayEfficiency || attribute == null) return;

        addBar("efficiency", (AttributeCrafter.AttributeCrafterBuild entity) ->
                new Bar(
                        () -> Core.bundle.format("bar.efficiency", (int)(entity.efficiencyMultiplier() * 100 * displayEfficiencyScale)),
                        () -> Pal.lightOrange,
                        entity::efficiencyMultiplier));
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        //make sure there's enough efficiency at this location
        return attribute == null || baseEfficiency + tile.getLinkedTilesAs(this, tempTiles).sumf(other -> other.floor().attributes.get(attribute)) >= minEfficiency;
    }

    public class MultiCrafterBuild extends Building implements HeatConsumer, HeatBlock {
        public int currentRecipeId = -1;
        public int currentConfigurationId = 0;
        public @Nullable Seq<Recipe> currentRecipes;
        public @Nullable Recipe currentRecipe;
        public float heatRequirement, heatOutput;

        public float[] sideHeat = new float[4];
        public float heat = 0f, heatOut = 0f;

        public float progress, totalProgress, warmup;

        public float attrsum;

        @Override
        public void buildConfiguration(Table table) {
            table.table(Styles.black5, tab -> {
                tab.button(Icon.upOpen, Styles.emptyi, () -> {
                    currentConfigurationId++;
                    if (currentConfigurationId >= recipes.size) currentConfigurationId = recipes.size - 1;
                    rebuild(table);
                }).size(30).row();
                tab.add(String.valueOf(currentConfigurationId)).row();
                tab.button(Icon.downOpen, Styles.emptyi, () -> {
                    currentConfigurationId--;
                    if (currentConfigurationId < 0) currentConfigurationId = 0;
                    rebuild(table);
                }).size(30);
            }).width(50).height(200);
            table.table(Styles.black5, tab -> {
                Table cont = new Table().top();
                for (Recipe recipe : currentRecipes) {
                    cont.table(t -> recipe.printUI(t, 20)).width(450).height(25).left();
                    cont.row();
                }

                ScrollPane pane = new ScrollPane(cont, Styles.smallPane);
                pane.setScrollingDisabled(true, false);
                pane.exited(() -> {
                    if(pane.hasScroll()){
                        Core.scene.setScrollFocus(null);
                    }
                });

                if(block != null){
                    pane.setScrollYForce(block.selectScroll);
                    pane.update(() -> block.selectScroll = pane.getScrollY());
                }

                pane.setOverscroll(false, false);
                tab.top().add(pane).width(450).height(200);
            }).width(450).height(200);
        }

        public void rebuild(Table tab) {
            tab.clear();
            buildConfiguration(tab);
        }

        @Override
        public Integer config() {
            return currentConfigurationId;
        }

        @Override
        public void draw(){
            drawer.draw(this);
            drawHeat.draw(this);
        }

        @Override
        public void drawLight(){
            super.drawLight();
            drawer.drawLight(this);
            drawHeat.drawLight(this);
        }

        @Override
        public void updateTile() {
            heat = calculateHeat(sideHeat);
            currentConfigurationId = currentConfigurationId < 0 || currentConfigurationId >= recipes.size ? 0 : currentConfigurationId;

            currentRecipes = currentConfigurationId >= recipes.size || recipes.get(currentConfigurationId) == null ? null : recipes.get(currentConfigurationId);
            if (currentRecipes != null && !currentRecipes.isEmpty()) {
                // 首先检查当前配方是否仍可用
                if (currentRecipe != null && !((currentRecipe.input.items.length == 0 || items.has(currentRecipe.input.items))
                            && lib.hasLiquid(liquids, currentRecipe.input.liquids))) {
                    currentRecipe = null;// 当前配方失效，寻找下一个可用配方
                    currentRecipeId = -1;
                    progress = 0;
                }

                if (currentRecipe == null) {
                    // 查找第一个可用的配方
                    for (Recipe recipe : currentRecipes) {
                        if ((recipe.input.items.length == 0 || items.has(recipe.input.items)) && lib.hasLiquid(liquids, recipe.input.liquids)) {
                            currentRecipe = recipe;
                            currentRecipeId = currentRecipes.indexOf(recipe);
                            break;
                        }
                    }
                }
            }

            if (currentRecipe != null) {
                heatRequirement = currentRecipe.heatRequirement;
                heatOutput = currentRecipe.heatOutput;

                if(efficiency > 0){
                    progress += getProgressIncrease(currentRecipe.craftTime <= 0 ? uniCraftTime : currentRecipe.craftTime);
                    warmup = Mathf.approachDelta(warmup, warmupTarget(), warmupSpeed);

                    //continuously output based on efficiency
                    if(currentRecipe != null && currentRecipe.output != null && currentRecipe.output.liquids != null){
                        float inc = getProgressIncrease(1f);
                        for(var output : currentRecipe.output.liquids){
                            handleLiquid(this, output.liquid, Math.min(output.amount * inc, liquidCapacity - liquids.get(output.liquid)));
                        }
                    }

                    if(wasVisible && Mathf.chanceDelta(updateEffectChance)){
                        updateEffect.at(x + Mathf.range(size * updateEffectSpread), y + Mathf.range(size * updateEffectSpread));
                    }
                }else{
                    warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
                }

                //TODO may look bad, revert to edelta() if so
                totalProgress += warmup * Time.delta;

                if(progress >= 1f){
                    craft();
                }

                dumpOutputs();

                //heat approaches target at the same speed regardless of efficiency
                heatOut = Mathf.approachDelta(heatOut, heatOutput * efficiency, warmupRate * delta());
            }
        }

        public void dumpOutputs(){
            if (currentRecipe != null && currentRecipe.output != null){
                if (currentRecipe.output.items != null && timer(timerDump, dumpTime / timeScale)) {
                    for (ItemStack output : currentRecipe.output.items) {
                        while (items.has(output.item)){
                            dump(output.item);
                        }
                    }
                }

                if (currentRecipe.output.liquids != null) {
                    for (int i = 0; i < currentRecipe.output.liquids.length; i++) {
                        int dir = liquidOutputDirections.length > i ? liquidOutputDirections[i] : -1;

                        dumpLiquid(currentRecipe.output.liquids[i].liquid, 2f, dir);
                    }
                }
            }
        }

        @Override
        public void updateConsumption() {
            if (block.hasConsumers && !cheating()) {
                if (!enabled) {
                    potentialEfficiency = efficiency = optionalEfficiency = 0;
                    shouldConsumePower = false;
                } else {
                    boolean update = shouldConsume() && productionValid();
                    float minEfficiency = 1;
                    efficiency = optionalEfficiency = 1;
                    shouldConsumePower = true;

                    for(Consume cons : block.nonOptionalConsumers) {
                        float result = cons.efficiency(this);
                        if (cons != block.consPower && result <= 1.0E-7F) {
                            shouldConsumePower = false;
                        }

                        minEfficiency = Math.min(minEfficiency, result);
                    }

                    float ed = edelta();
                    if(ed <= 0.00000001f) minEfficiency = Math.min(minEfficiency, 0);
                    else {
                        float min = 1f;
                        if (currentRecipe != null && currentRecipe.input != null && currentRecipe.input.liquids != null)
                            for (LiquidStack stack : currentRecipe.input.liquids) {
                                min = Math.min(liquids.get(stack.liquid) / (stack.amount * ed), min);
                            }
                        minEfficiency = Math.min(minEfficiency, min);
                    }

                    for(Consume cons : block.optionalConsumers) {
                        optionalEfficiency = Math.min(optionalEfficiency, cons.efficiency(this));
                    }

                    efficiency = minEfficiency;
                    optionalEfficiency = Math.min(optionalEfficiency, minEfficiency);
                    potentialEfficiency = efficiency;
                    if (!update) {
                        efficiency = optionalEfficiency = 0;
                    }

                    updateEfficiencyMultiplier();
                    if (update && efficiency > 0) {
                        for(Consume cons : block.updateConsumers) {
                            cons.update(this);
                        }
                        if (currentRecipe != null && currentRecipe.input != null && currentRecipe.input.liquids != null)
                            for(LiquidStack stack : currentRecipe.input.liquids){
                                liquids.remove(stack.liquid, stack.amount * edelta());
                            }
                    }
                }
            } else {
                potentialEfficiency = enabled && productionValid() ? 1 : 0;
                efficiency = optionalEfficiency = shouldConsume() ? potentialEfficiency : 0;
                shouldConsumePower = true;
                updateEfficiencyMultiplier();
            }
        }

        @Override
        public void displayBars(Table bars) { //动态更新热量条及流体条
            super.displayBars(bars);

            if (currentRecipe != null) {
                if (currentRecipe.input != null && currentRecipe.input.liquids != null) for (var liquid : currentRecipe.input.liquids) bars.add(new Bar(
                        () -> liquid.liquid.localizedName,
                        liquid.liquid::barColor,
                        () -> liquids.get(liquid.liquid) / liquidCapacity
                )).growX().row();

                if (currentRecipe.output != null && currentRecipe.output.liquids != null) for (var liquid : currentRecipe.output.liquids) bars.add(new Bar(
                        () -> liquid.liquid.localizedName,
                        liquid.liquid::barColor,
                        () -> liquids.get(liquid.liquid) / liquidCapacity
                )).growX().row();
            }

            if (heatRequirement > 0) bars.add(new Bar(
                    () -> Core.bundle.format("tebar.heatrequire", (int)(heat + 0.01f), (int)(efficiencyScale() * 100 + 0.01f)),
                    () -> Pal.lightOrange,
                    () -> heat / heatRequirement
            )).growX().row();

            if (heatOutput > 0) bars.add(new Bar(
                    Core.bundle.format("tebar.heatoutput", (int)(heatOut + 0.01f)),
                    Pal.lightOrange,
                    () -> heatOut / heatOutput
            )).growX().row();
        }

        @Override
        public float getProgressIncrease(float baseTime){
            if(ignoreLiquidFullness){
                return super.getProgressIncrease(baseTime);
            }

            //limit progress increase by maximum amount of liquid it can produce
            float scaling = 1f, max = 1f;
            if(currentRecipe != null && currentRecipe.output != null && currentRecipe.output.liquids != null){
                max = 0f;
                for(var s : currentRecipe.output.liquids){
                    float value = (liquidCapacity - liquids.get(s.liquid)) / (s.amount * edelta());
                    scaling = Math.min(scaling, value);
                    max = Math.max(max, value);
                }
            }

            //when dumping excess take the maximum value instead of the minimum.
            return super.getProgressIncrease(baseTime) * (dumpExtraLiquid ? Math.min(max, 1f) : scaling) * (attribute != null ? efficiencyMultiplier() : 1f);
        }

        public float efficiencyMultiplier(){
            return baseEfficiency + Math.min(maxBoost, boostScale * attrsum) + attribute.env();
        }

        @Override
        public double sense(LAccess sensor){
            if(sensor == LAccess.progress) return progress();
            //attempt to prevent wild total liquid fluctuation, at least for crafters
            if(sensor == LAccess.totalLiquids && currentRecipe != null && currentRecipe.output != null && currentRecipe.output.liquids != null) return liquids.get(currentRecipe.output.liquids[0].liquid);
            if (sensor == LAccess.config) return currentConfigurationId;
            return super.sense(sensor);
        }

        @Override
        public float warmup(){
            return warmup;
        }

        @Override
        public float totalProgress(){
            return totalProgress;
        }

        public void craft(){
            consume();

            if(currentRecipe != null && currentRecipe.input != null && currentRecipe.input.items != null){
                for (var input : currentRecipe.input.items) {
                    items.remove(input);
                }
            }

            if(currentRecipe != null && currentRecipe.output != null && currentRecipe.output.items != null){
                for(var output : currentRecipe.output.items){
                    for(int i = 0; i < output.amount; i++){
                        offload(output.item);
                    }
                }
            }

            if(wasVisible){
                craftEffect.at(x, y);
            }
            progress %= 1f;
        }

        @Override
        public boolean shouldConsume(){
            if (currentRecipe != null && currentRecipe.output != null){
                if (currentRecipe.output.items != null && currentRecipe.output.items.length != 0) {
                    for (var output : currentRecipe.output.items) {
                        if (items.get(output.item) + output.amount > itemCapacity) {
                            return false;
                        }
                    }
                }

                if (currentRecipe.output.liquids != null && !ignoreLiquidFullness) {
                    boolean allFull = true;
                    if (currentRecipe.output.liquids.length != 0) {
                        for (var output : currentRecipe.output.liquids) {
                            if (liquids.get(output.liquid) >= liquidCapacity - 0.001f) {
                                if (!dumpExtraLiquid) return false;
                            } else {
                                allFull = false;
                            }
                        }

                        if (allFull) {
                            return false;
                        }
                    }
                }
            }

            return (heatRequirement <= 0f || heat > 0) && enabled;
        }

        @Override
        public float heatRequirement(){
            return heatRequirement > 0 ? heatRequirement : -1;
        }

        @Override
        public float[] sideHeat(){
            return sideHeat;
        }

        public float warmupTarget(){
            return heatRequirement > 0 ? Mathf.clamp(heat / heatRequirement) : 1;
        }

        @Override
        public float efficiencyScale(){
            float over = Math.max(heat - heatRequirement, 0f);
            return heatRequirement > 0 ? Math.min(Mathf.clamp(heat / heatRequirement) + over / heatRequirement * overheatScale, maxEfficiency) : attribute != null ? scaleLiquidConsumption ? efficiencyMultiplier() : 1f : 1f;
        }

        @Override
        public void pickedUp(){
            attrsum = 0f;
            warmup = 0f;
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();

            attrsum = sumAttribute(attribute, tile.x, tile.y);
        }

        @Override
        public float heat() {
            return heatOut;
        }

        @Override
        public float heatFrac() {
            return heatOut / heatOutput;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(progress);
            write.f(warmup);
            if(legacyReadWarmup) write.f(0f);

            write.f(heatOut);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            progress = read.f();
            warmup = read.f();
            if(legacyReadWarmup) read.f();

            heatOut = read.f();
        }

        @Override
        public float progress(){
            return Mathf.clamp(progress);
        }

        @Override
        public int getMaximumAccepted(Item item){
            return itemCapacity;
        }

        @Override
        public boolean shouldAmbientSound(){
            return efficiency > 0;
        }
    }

    public static class Recipe {
        public StackItemLiquid input = new StackItemLiquid(), output = new StackItemLiquid();
        public int craftTime = -1, heatRequirement = -1, heatOutput = -1;

        public Recipe() {}

        public Recipe(StackItemLiquid input, StackItemLiquid output) {
            this.input = input;
            this.output = output;
        }
        public Recipe(StackItemLiquid input, StackItemLiquid output, int craftTime) {
            this.input = input;
            this.output = output;
            this.craftTime = craftTime;
        }

        public String localizedName() {
            StringBuilder str = new StringBuilder();
            for (ItemStack it : input.items) {
                str.append(it.item.emoji());
            }
            for (LiquidStack it : input.liquids) {
                str.append(it.liquid.emoji());
            }
            if (heatRequirement > 0) str.append("+").append(heatRequirement).append("[red]").append(Iconc.waves).append("[]");
            str.append(" -> ");
            for (ItemStack it : output.items) {
                str.append(it.item.emoji());
            }
            for (LiquidStack it : output.liquids) {
                str.append(it.liquid.emoji());
            }
            if (heatOutput > 0) str.append("+").append(heatOutput).append("[red]").append(Iconc.waves).append("[]");
            return str.toString();
        }

        public void printUI(Table table, int iconSize) {
            for (ItemStack it : input.items) {
                table.image(it.item.uiIcon).size(iconSize);
            }
            for (LiquidStack it : input.liquids) {
                table.image(it.liquid.uiIcon).size(iconSize);
            }
            if (heatRequirement > 0) table.add("+" + heatRequirement + "[red]" + Iconc.waves + "[]");
            table.image(Icon.right).color(Pal.darkishGray).size(iconSize);
            for (ItemStack it : output.items) {
                table.image(it.item.uiIcon).size(iconSize);
            }
            for (LiquidStack it : output.liquids) {
                table.image(it.liquid.uiIcon).size(iconSize);
            }
            if (heatOutput > 0) table.add("+" + heatOutput + "[red]" + Iconc.waves + "[]");
        }
    }
}
