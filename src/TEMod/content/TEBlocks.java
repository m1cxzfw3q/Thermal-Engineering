package TEMod.content;

import TEMLib.*;
import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.pattern.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.unit.*;
import mindustry.world.Block;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.heat.*;
import mindustry.world.blocks.liquid.*;
import mindustry.world.blocks.logic.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static TEMod.TECore.isComplete;
import static mindustry.content.Fx.none;
import static mindustry.content.StatusEffects.shocked;
import static mindustry.content.StatusEffects.unmoving;
import static mindustry.type.ItemStack.mult;
import static mindustry.type.ItemStack.with;

public class TEBlocks {
    public static Block

    oreUranium, oreZinc, oreIron, //矿石
    wallOreCopper, wallOreLead, wallOreTitanium, wallOreCoal, wallOreScrap, //S墙矿
    oreGraphitic, //地石墨
    liquidCoverCryo, liquidCoverOil, liquidCoverWater, liquidCoverSlag, liquidCoverArkycite, //一些盖板地板

    //基础方块(S)
    highEfficiencyDisassembler, //高效解离机
    missileLauncher, //导弹发射井
    nuclearFuelRodManufacturingMachine, //核燃料棒制造机
    oreSmeltingFurnace, arcFurnace, //冶炼炉子
    oreCrusher, //矿石粉碎机
    cryofluidMixerLarge, //大型冷冻液混合机
    advancedOverdriveDome, //高级超速穹顶
    componentAssemblyPlant, //组装厂

    preliminaryLaboratory, intermediateLaboratory, advancedLaboratory, ultimateLaboratory, //实验室
    chipManufacturingMachine, chipPrinter, //芯片制造机 (造芯片的)

    nuclearReactor, thoriumNuclearExplosiveReactor, //核反应堆
    advancedPowerNode, //高级电力节点

    simpleStorage, //简易储存器
    highSpeedUnloader, //高速装卸器
    itemQuantumTransmissionLightBridge, liquidQuantumTransmissionLightBridge, //量子传输光桥

    coreExplore, //核心

    machineCannon, //机炮
    prism, //棱镜 重名了

    unitLauncher, advancedUnitLauncher, //废稿之单位发射台
    unitStorageVault, unitStorageVaultLarge, //单位储存仓

    terminalProcessor, //终端处理器
    hugeLogicDisplay, //巨型逻辑显示屏
    memoryBankLarge, //大型内存库

    IllustratedReconstructor, //虚数级单位重构厂 //T6鸽

    payloadConveyorLarge, payloadConveyorHuge, payloadConveyorGigantic, //载荷传送带
    payloadRouterLarge, payloadRouterHuge, payloadRouterGigantic, //载荷路由器
    payloadLauncher, payloadLauncherLarge, payloadLauncherHuge,//载荷发射器

    shieldGenerator, shieldGeneratorLarge, shieldGeneratorHuge, sectorShieldGenerator, //护盾发生器

    advancedWaterExtractor, //fw抽水机
    mechanicalCliffCrusher, pneumaticCliffCrusher, laserBore, //墙钻
    sporeWallCliffCrusher, //孢子墙粉碎机
    pyratiteHeater, //S热机

    plasticAlloyWall, plasticAlloyWallLarge,//塑质合金墙
    plasticAlloyConveyor, plasticAlloyPacketConveyor,//塑制合金带

    //基础方块(E)
    reinforcedPowerNode, //E电力节点

    //基础方块(TEMod)
    liquidCover, //盖板

    stoneWall, stoneWallLarge, stoneConveyor, stoneDrill //石头

    ;

    public static void load() {//别问为什么前段写那么屎(让以后的我能看懂的)
        machineCannon = new ItemTurret("machine-cannon") {{
            //这个mod从Json版本开始的第一个建筑，也是梦开始的地方
            //Json版本早没了，你想玩也玩不到
            //更别说Json版本更是一坨屎，比现在的这个mod还要屎
            requirements(Category.turret,
                    with(Items.copper, 120, Items.lead, 100, Items.graphite, 30)
            );
            ammo(Items.copper, new BasicBulletType(8.0F, 27.0F) {{
                pierceCap = 1;
                knockback = 0.2F;
                width = 2.0F;
                height = 5.0F;
                lifetime = 30.0F;
                ammoMultiplier = 10.0F;
            }}, Items.lead, new BasicBulletType(8.0F, 27.0F) {{
                pierceCap = 2;
                width = 2.0F;
                height = 5.0F;
                lifetime = 30.0F;
                ammoMultiplier = 10.0F;
                knockback = 0.2F;
            }}, Items.graphite, new BasicBulletType(8.0F, 35.0F) {{
                pierceCap = 4;
                width = 2.0F;
                height = 5.0F;
                reloadMultiplier = 0.7F;
                ammoMultiplier = 10.0F;
                lifetime = 30.0F;
                status = unmoving;
                statusDuration = 2F;
                knockback = 0.2F;
            }}, Items.metaglass, new BasicBulletType(8.0F, 35.0F) {{
                pierceCap = 3;
                width = 2.0F;
                height = 5.0F;
                reloadMultiplier = 0.8F;
                ammoMultiplier = 7.0F;
                lifetime = 30.0F;
                status = unmoving;
                statusDuration = 2F;
                fragBullets = 2;
                fragLifeMin = 0.1F;
                fragRandomSpread = 30F;
                fragSpread = 45F;
                fragVelocityMin = 0.5F;
                fragVelocityMax = 1F;
                knockback = 0.4F;
                fragBullet = new BasicBulletType(2F, 6.0F) {{
                    pierce = true;
                    width = 1.0F;
                    height = 1.0F;
                    pierceCap = 2;
                }};
            }}, Items.surgeAlloy, new BasicBulletType(8.0F, 150.0F) {{
                pierceCap = 1;
                pierceArmor = true;
                reloadMultiplier = 0.25F;
                width = 2.0F;
                height = 5.0F;
                lifetime = 30.0F;
                ammoMultiplier = 25.0F;
                knockback = 0.5F;
                lightningDamage = 25F;
                lightning = 4;
                lightningLength = 8;
                lightningLengthRand = 8;
                lightningColor = Color.valueOf("ab99d3ff");
                status = shocked;
                fragBullets = 2;
                fragLifeMin = 0.1F;
                fragRandomSpread = 30F;
                fragSpread = 45F;
                fragVelocityMin = 0.5F;
                fragVelocityMax = 1F;
                fragBullet = new BasicBulletType(2F, 40.0F) {{
                    pierce = true;
                    width = 2.0F;
                    height = 2.0F;
                    pierceCap = 1;
                    lightningLength = 4;
                    lightning = 2;
                }};
            }},Items.silicon, new BasicBulletType(8.0F, 27.0F) {{
                pierceCap = 2;
                width = 2.0F;
                height = 5.0F;
                lifetime = 30.0F;
                ammoMultiplier = 10.0F;
                knockback = 0.2F;
                homingPower = 0.1f;
                homingRange = 26f;
            }},TEItems.preliminaryChip, new BasicBulletType(8.0F, 30.0F) {{
                width = 2.0F;
                height = 5.0F;
                lifetime = 30.0F;
                ammoMultiplier = 15.0F;
                homingPower = 0.2f;
                homingRange = 30f;
            }});
            maxAmmo = 300;
            recoil = 0.7F;
            recoilTime = 2F;
            shootY = 5F;
            reload = 1.5F;
            range = 240.0F;
            ammoUseEffect = TEFx.casingMini;
            shootEffect = TEFx.shootMini;
            health = 600;
            inaccuracy = 4F;
            rotateSpeed = 40F;
            coolantMultiplier = 1.7f;
            heatColor = Color.valueOf("ff0000");
            coolant = consume(new ConsumeLiquid(Liquids.water, 6f / 60f));
        }};

        highEfficiencyDisassembler = new Separator("high-efficiency-disassembler"){{
            requirements(Category.crafting,
                    with(Items.copper, 450, Items.titanium, 200, Items.lead, 300, Items.graphite, 200, Items.thorium, 150, Items.silicon, 200,
                            Items.plastanium, 200, Items.phaseFabric, 80, TEItems.ultimateChip, 4, TEItems.ultimateAgreement, 1)
            );
            results = with(
                    Items.copper, 4,
                    Items.lead, 4,
                    Items.graphite, 2,
                    Items.titanium, 2,
                    Items.thorium, 1,
                    Items.silicon, 2,
                    Items.metaglass, 2,
                    Items.surgeAlloy, 1,
                    Items.phaseFabric, 1,
                    Items.plastanium, 1
            );
            craftTime = 5f;
            size = 4;
            health = 2500;
            itemCapacity = 40;
            liquidCapacity = 40;
            consumePower(8f);
            consumeLiquid(Liquids.slag, 30f / 60f);
            consumeItem(Items.scrap, 1);

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile(),
                    new DrawRegion("-spinner", 7, true),
                    new DrawDefault()
            );
        }};

        oreUranium = new OreBlock("ore-uranium", TEItems.uranium) {{
            oreDefault = true;
            oreThreshold = 0.81f;
            oreScale = 23.47619f;
        }};

        chipManufacturingMachine = new MultiCrafter("chip-manufacturing-machine") {{
            requirements(Category.crafting, with(Items.copper, 700, Items.titanium, 700, Items.lead, 800, Items.graphite, 800, Items.silicon, 8000));
            size = 2;
            health = 800;
            itemCapacity = 30;
            consumePower(5f);

            recipes.addAll(new Recipe(
                    new StackItemLiquid(with(Items.silicon, 3, Items.copper, 2, Items.lead, 1)),
                    new StackItemLiquid(with(TEItems.preliminaryChip, 1)),
                    40f
            ), new Recipe(
                            new StackItemLiquid(with(Items.silicon, 4, Items.copper, 2, Items.lead, 1)),
                    new StackItemLiquid(with(TEItems.intermediateChip, 1)),
                    50f
            ));
        }};


        chipPrinter = new MultiCrafter("chip-printer") {{
            requirements(Category.crafting,
                    with(Items.copper, 2000, Items.titanium, 1000, Items.lead, 2400, Items.graphite, 1500, Items.silicon, 10000,
                    Items.plastanium, 2000, Items.phaseFabric, 800, TEItems.intermediateChip, 70, TEItems.intermediateChip, 1)
            );

            size = 3;
            health = 1000;
            itemCapacity = 40;
            consumePower(6f);
            alwaysUnlocked = false;
            //requiresLiquid(Liquids.cryofluid, 0.2f);

            recipes.addAll(new Recipe(
                    new StackItemLiquid(with(Items.silicon, 3)),
                    new StackItemLiquid(with(TEItems.preliminaryChip, 1)),
                    10f
            ), new Recipe(
                    new StackItemLiquid(with(Items.silicon, 5)),
                    new StackItemLiquid(with(TEItems.intermediateChip, 1)),
                    20f
            ), new Recipe(
                    new StackItemLiquid(with(Items.silicon, 6)),
                    new StackItemLiquid(with(TEItems.advancedChip, 1)),
                    30f
            ), new Recipe(
                    new StackItemLiquid(with(Items.silicon, 8)),
                    new StackItemLiquid(with(TEItems.ultimateChip, 1)),
                    40f
            ));

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidRegion() {{
                        drawLiquid = Liquids.cryofluid;
                        suffix = "-cryo";
                    }},
                    new DrawDefault(),
                    new DrawBlock() {
                        @Override
                        public void getRegionsToOutline(Block block, Seq<TextureRegion> out) {
                            super.getRegionsToOutline(block, out);
                        }
                        @Override
                        public void draw(Building build) {
                            MultiCrafterBuild build1 = (MultiCrafterBuild) build;
                            float speedScl = 0, time = 0;
                            if (build1.currentRecipeId != -1) {
                                if (build1.progress > 0) {
                                    time += build.edelta() * speedScl * build.timeScale();
                                    speedScl = Mathf.lerpDelta(speedScl, 1f, 0.05f);
                                } else {
                                    speedScl = Mathf.lerpDelta(speedScl, 0f, 0.05f);
                                }
                                float finalTime = time, finalSpeedScl = speedScl;
                                Draw.draw(
                                        Layer.blockOver,
                                        () -> Drawf.construct(
                                                build, build1.currentRecipe.outputItems[0].item, build.rotdeg() - 90f,
                                                build1.progress / build1.currentRecipe.craftTime, finalSpeedScl, finalTime
                                        )
                                );
                            }
                        }
                    }
            );
            liquidCapacity = 20f;
            hasLiquids = true;
        }};


        missileLauncher = new ItemTurret("missile-launcher") {{
            health = 3500;
            size = 5;

            rotate = false;

            requirements(Category.turret,
                    with(Items.copper, 900, Items.lead, 1100, Items.graphite, 700, Items.metaglass, 300, Items.silicon, 250,
                            TEItems.preliminaryChip, 20)
            );

            rotateSpeed = 0F;
            range = 145f * 8;
            reload = 90f;
            liquidCapacity = 60f;
            coolantMultiplier = 2f;
            coolant = consume(consumeLiquid(Liquids.water, 20f / 60f));
            maxAmmo = 80;
            ammoPerShot = 20;
            consumePower(6f);
            shootCone = 360;
            minWarmup = 0.8f;
            shootWarmupSpeed = 0.055f;
            warmupMaintainTime = 120;

            shoot = new ShootBarrel() {{
                shots = 4;
                shotDelay = 10f;
                barrels = new float[]{
                        -14, 0, 0,
                        -11, 11, 0,
                        0, 14, 0,
                        11 ,11, 0,
                        14, 0, 0,
                        11, -11, 0,
                        0, -14, 0,
                        -11, -11, 0
                };
            }};
            shootSound = Sounds.shootScathe;
            recoil = 0f;
            shootY = 0;

            ammo(Items.pyratite, new BasicBulletType(0f, 1f) {{
                spawnUnit = new MissileUnitType("missile-launcher-missile-pyratite") {{
                    speed = 8f;
                    lifetime = 4f * 60f;
                    trailLength = 11;
                    homingPower = 0.1f;
                    homingDelay = 80f;
                    missileAccelTime = 120f;
                    health = 400f;
                    rotateSpeed = 15f;
                    deathSound = Sounds.explosionMissile;
                    lowAltitude = true;
                    loopSound = Sounds.loopMissileTrail;
                    loopSoundVolume = 0.6f;
                    weapons.add(new Weapon() {{
                        shootCone = 360f;
                        mirror = false;
                        reload = 1f;
                        x = y = 0;
                        deathExplosionEffect = Fx.massiveExplosion;
                        shootOnDeath = true;
                        shake = 10f;
                        bullet = new ExplosionBulletType(659f, 86f) {{
                            hitColor = Pal.redLight;
                            shootEffect = new MultiEffect(Fx.massiveExplosion, new WaveEffect(){{
                                lifetime = 6f;
                                strokeFrom = 4f;
                                sizeTo = 109f;
                            }});
                            buildingDamageMultiplier = 0.8f;
                            ammoMultiplier = 1f;
                            status = StatusEffects.burning;
                        }};
                    }});
                }};
            }}, Items.blastCompound, new BasicBulletType(0f, 1f) {{
                spawnUnit = new MissileUnitType("missile-launcher-missile-blast-compound") {{
                    speed = 8f;
                    lifetime = 4f * 60f;
                    trailLength = 11;
                    homingPower = 0.1f;
                    homingDelay = 80f;
                    missileAccelTime = 120f;
                    health = 400f;
                    rotateSpeed = 15f;
                    deathSound = Sounds.explosionMissile;
                    lowAltitude = true;
                    loopSound = Sounds.loopMissileTrail;
                    loopSoundVolume = 0.6f;
                    weapons.add(new Weapon() {{
                        shootCone = 360f;
                        mirror = false;
                        reload = 1f;
                        x = y = 0;
                        deathExplosionEffect = Fx.massiveExplosion;
                        shootOnDeath = true;
                        shake = 10f;
                        bullet = new ExplosionBulletType(1020f, 102f) {{
                            hitColor = Pal.redLight;
                            shootEffect = new MultiEffect(Fx.massiveExplosion, new WaveEffect(){{
                                lifetime = 6f;
                                strokeFrom = 4f;
                                sizeTo = 168f;
                            }});
                            buildingDamageMultiplier = 0.8f;
                            ammoMultiplier = 1f;
                        }};
                    }});
                }};
            }});
        }};

        nuclearReactor = new NuclearReactor("nuclear-reactor") {{ //等待重置
            health = 5000;
            size = 5;
            liquidCapacity = 30;
            itemCapacity = 20;
            hasItems = hasLiquids = outputsPower = true;
            powerProduction = 13800f / 60f;
            itemDuration = 30f;
            lightColor = TEItems.nuclearFuelRod.color;
            explosionShake = 9;
            explosionShakeDuration = 120;
            explosionRadius = 80;
            explosionDamage = 10000;
            explodeSound = Sounds.explosionReactor;
            fuelItem = TEItems.nuclearFuelRod;
            heating = 0.06f;
            coolantPower = 2;
            ambientSound = Sounds.loopThoriumReactor;
            ambientSoundVolume = 0.24f;
            consumeItem(TEItems.nuclearFuelRod, 1);
            consumeLiquid(Liquids.cryofluid, heating / coolantPower).update(false);
            buildCostMultiplier = 0.7f;
            requirements(Category.power,
                    with(Items.lead, 1400, Items.graphite, 890, Items.metaglass, 320, Items.thorium, 750, Items.titanium, 800,
                    Items.surgeAlloy, 260, TEItems.advancedChip, 10, TEItems.zinc, 600)
            );
        }};

        simpleStorage = new StorageBlock("simple-storage") {{
            requirements(Category.effect, with(Items.copper, 180, Items.lead, 220, Items.graphite, 40));
            size = 2;
            itemCapacity = 120;
            coreMerge = false;
            health = 120;
        }};

        highSpeedUnloader = new Unloader("high-speed-unloader") {{
            speed = 0.75f;
            group = BlockGroup.transportation;
            requirements(Category.effect,
                    with(Items.thorium, 15, Items.silicon, 20, Items.titanium, 26, Items.plastanium, 20)
            );
            health = 60;
        }};

        nuclearFuelRodManufacturingMachine = new GenericCrafter("nuclear-fuel-rod-manufacturing-machine") {{
            size = 3;
            health = 1600;
            itemCapacity = 20;
            liquidCapacity = 20;
            hasItems = hasLiquids = hasPower = true;
            craftTime = 30f;
            updateEffect = none;
            consumePower(35f / 60f);
            consumeItem(TEItems.uranium, 5);
            consumeLiquid(Liquids.water, 10f / 60f);
            outputItem = new ItemStack(TEItems.nuclearFuelRod, 1);
            requirements(Category.crafting,
                    with(Items.thorium, 530, Items.titanium, 680, Items.silicon, 580, Items.lead, 680, Items.surgeAlloy, 200,
                    Items.graphite, 790, TEItems.advancedChip, 20)
            );
        }};

        coreExplore = new PortableCoreBlock("core-explore") {{
            coreCap = 20;
            health = 3000;
            itemCapacity = 1000;
            unitCapModifier = 3;
            thrusterLength = 20 / 4f;
            isFirstTier = alwaysUnlocked = true;
            armor = 15;
            size = 3;
            unitType = UnitTypes.alpha;
            requirements(Category.effect,
                    with(Items.copper, 1000, Items.lead, 1000, Items.graphite, 400, Items.silicon, 200));
        }};

        unitStorageVault = new StorageBlock("unit-storage-vault") {{
            health = 24000;
            unitCapModifier = 24;
            size = 5;
            armor = 25;
            itemCapacity = 0;
            requirements(Category.effect, with(Items.silicon, 4000, Items.thorium, 4000, Items.surgeAlloy, 2000, Items.graphite, 6000,
                    TEItems.advancedChip, 40, Items.titanium, 5000, Items.metaglass, 6000, Items.lead, 8000)
            );
            buildCostMultiplier = 0.8f;
        }};

        unitStorageVaultLarge = new StorageBlock("large-unit-storage-vault") {{
            health = 70000;
            unitCapModifier = 64;
            itemCapacity = 0;
            size = 9;
            armor = 80;
            requirements(Category.effect, with(Items.silicon, 30000, Items.thorium, 40000, Items.surgeAlloy, 20000, Items.graphite, 60000,
                    TEItems.advancedChip, 300, TEItems.ultimateChip, 40, Items.titanium, 50000, Items.metaglass, 60000, Items.lead, 80000,
                    Items.phaseFabric, 100000)
            );
            buildCostMultiplier = 0.8f;
        }};

        prism = new MultiChargeTurret("prism") {{
            health = 1500;
            size = 1;
            shoot.firstShotDelay = 40f;

            MultiCharge( //我就说自己写才是最好的吧
                    new ChargeTier(60, new LightningBulletType() {{
                        lightningLength = 16;
                    }}),
                    new ChargeTier(180, new LaserBulletType(546){{
                        colors = new Color[]{Pal.lancerLaser.cpy().a(0.4f), Pal.lancerLaser, Color.white};
                        chargeEffect = new MultiEffect(Fx.lancerLaserCharge, Fx.lancerLaserChargeBegin);

                        buildingDamageMultiplier = 0.5f;
                        hitEffect = Fx.hitLancer;
                        hitSize = 6;
                        lifetime = 16f;
                        drawSize = 660f;
                        length = 475f;
                        ammoMultiplier = 1f;
                        pierceCap = 25;
                        width = 26f;
                    }}),
                    new ChargeTier(10, new LaserBulletType(724){{
                        colors = new Color[]{Pal.lancerLaser.cpy().a(0.4f), Pal.lancerLaser, Color.white};
                        chargeEffect = new MultiEffect(Fx.lancerLaserCharge, Fx.lancerLaserChargeBegin);

                        buildingDamageMultiplier = 0.5f;
                        hitEffect = Fx.hitLancer;
                        hitSize = 7;
                        lifetime = 16f;
                        drawSize = 660f;
                        length = 600f;
                        ammoMultiplier = 1f;
                        width = 30f;
                    }}),
                    new ChargeTier[]{
                            new ChargeTier(40, ((PowerTurret) Blocks.lancer).shootType),
                            new ChargeTier(40, new LaserBulletType(250){{
                                colors = new Color[]{Pal.lancerLaser.cpy().a(0.4f), Pal.lancerLaser, Color.white};
                                chargeEffect = new MultiEffect(Fx.lancerLaserCharge, Fx.lancerLaserChargeBegin);

                                buildingDamageMultiplier = 0.25f;
                                hitEffect = Fx.hitLancer;
                                hitSize = 5;
                                lifetime = 16f;
                                drawSize = 460f;
                                length = 246f;
                                ammoMultiplier = 1f;
                                pierceCap = 7;
                                width = 20f;
                            }}),
                            new ChargeTier(40, new LaserBulletType(427){{
                                colors = new Color[]{Pal.lancerLaser.cpy().a(0.4f), Pal.lancerLaser, Color.white};
                                chargeEffect = new MultiEffect(Fx.lancerLaserCharge, Fx.lancerLaserChargeBegin);

                                buildingDamageMultiplier = 0.25f;
                                hitEffect = Fx.hitLancer;
                                hitSize = 6;
                                lifetime = 16f;
                                drawSize = 540f;
                                length = 400f;
                                ammoMultiplier = 1f;
                                pierceCap = 10;
                                width = 23f;
                            }})
                    }
            );//好个毛线

            requirements(Category.turret, with(Items.copper, 500, Items.lead, 650, Items.titanium, 350, Items.silicon, 200));

            rotateSpeed = 10f;
            range = 51.625f * 8f;
            reload = 1.5f * 60f;
            consumePower(10f);

            tiers[0].bullet.collidesAir = true;
        }};

        oreSmeltingFurnace = new MultiCrafter("ore-smelting-furnace") {{
            health = 1280;
            consumePower(3.5f);
            size = 3;

            recipes.addAll(
                    new Recipe(
                            new StackItemLiquid(with(TEItems.copperPowder, 1)),
                            new StackItemLiquid(with(Items.copper, 1))
                    ), new Recipe(
                            new StackItemLiquid(with(TEItems.leadPowder, 1)),
                            new StackItemLiquid(with(Items.lead, 1))
                    ), new Recipe(
                            new StackItemLiquid(with(TEItems.thoriumPowder, 1)),
                            new StackItemLiquid(with(Items.thorium, 1))
                    ), new Recipe(
                            new StackItemLiquid(with(TEItems.titaniumPowder, 1)),
                            new StackItemLiquid(with(Items.titanium, 1))
                    ), new Recipe(
                            new StackItemLiquid(with(TEItems.zincPowder, 1)),
                            new StackItemLiquid(with(TEItems.zinc, 1))
                    )
            );

            uniCraftTime = 40f;

            drawer = new DrawMulti(new DrawDefault(), new DrawFlame(Color.valueOf("ffc099")));

            requirements(Category.crafting, with(Items.copper, 500, Items.lead, 700, Items.silicon, 700, Items.titanium, 500));
        }};

        arcFurnace = new MultiCrafter("arc-furnace") {{
            size = 4;
            health = 400;
            recipes.addAll(
                    new Recipe(
                            new StackItemLiquid(with(Items.coal, 1, TEItems.iron, 19)),
                            new StackItemLiquid(with(TEItems.steel, 20)),
                            30f
                    ), new Recipe(
                            new StackItemLiquid(with(Items.graphite, 1, Items.sand, 4)),
                            new StackItemLiquid(with(Items.silicon, 4)),
                            7f
                    ), new Recipe(
                            new StackItemLiquid(with(Items.copper, 3, Items.lead, 4, Items.titanium, 2, Items.silicon, 3)),
                            new StackItemLiquid(with(Items.surgeAlloy, 1)),
                            12f
                    ), new Recipe(
                            new StackItemLiquid(with(Items.silicon, 2, Items.plastanium, 2, Items.surgeAlloy, 1)),
                            new StackItemLiquid(with(TEItems.plasticAlloy, 1)),
                            20f
                    )
            );

            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawArcSmelt(), new DrawDefault());

            consumePower(450f / 60f);
            itemCapacity = 40;
            requirements(Category.crafting, with(Items.copper, 600, TEItems.iron, 200, Items.silicon, 500, Items.titanium, 300, Items.lead, 400, Items.surgeAlloy, 40));
        }};

        oreZinc = new OreBlock("ore-zinc", TEItems.zinc) {{
            oreDefault = true;
            oreThreshold = 0.81f;
            oreScale = 23.47619f;
        }};

        oreCrusher = new MultiCrafter("ore-crusher") {{
            health = 1000;
            size = 2;

            recipes.addAll(new Recipe(
                    new StackItemLiquid(with(Items.copper, 1)),
                    new StackItemLiquid(with(TEItems.copperPowder, 1))
            ), new Recipe(
                    new StackItemLiquid(with(Items.lead, 1)),
                    new StackItemLiquid(with(TEItems.leadPowder, 1))
            ), new Recipe(
                    new StackItemLiquid(with(Items.thorium, 1)),
                    new StackItemLiquid(with(TEItems.thoriumPowder, 1))
            ), new Recipe(
                    new StackItemLiquid(with(Items.titanium, 1)),
                    new StackItemLiquid(with(TEItems.titaniumPowder, 1))
            ), new Recipe(
                    new StackItemLiquid(with(TEItems.zinc, 1)),
                    new StackItemLiquid(with(TEItems.zincPowder, 1))
            ), new Recipe(
                    new StackItemLiquid(with(Items.scrap, 1)),
                    new StackItemLiquid(with(Items.sand, 1))
            ), new Recipe(
                    new StackItemLiquid(with(TEItems.stone, 1)),
                    new StackItemLiquid(with(Items.sand, 1))
            ));

            uniCraftTime = 40f;

            requirements(Category.crafting, with(Items.copper, 400, Items.lead, 650, Items.silicon, 300, Items.titanium, 250));

            consumePower(1f);

            drawer = new DrawMulti(new DrawDefault(), new DrawRegion("-rotator"){{
                spinSprite = true;
                rotateSpeed = 2f;
            }}, new DrawRegion("-top"));

            craftEffect = Fx.pulverize;
            updateEffect = Fx.pulverizeSmall;
        }};

        preliminaryLaboratory = new GenericCrafter("preliminary-laboratory") {{
            health = 300;
            size = 2;
            outputItem = with(TEItems.preliminaryAgreement, 2)[0];
            consumePower(10);
        }};

        intermediateLaboratory = new GenericCrafter("intermediate-laboratory") {{
            health = 300;
            size = 3;
            outputItem = with(TEItems.intermediateAgreement, 2)[0];
            consumePower(15);
        }};

        advancedLaboratory = new GenericCrafter("advanced-laboratory") {{
            health = 300;
            size = 4;
            outputItem = with(TEItems.advancedAgreement, 2)[0];
            consumePower(20);
            consumeLiquid(Liquids.cryofluid, 0.5f);
        }};

        ultimateLaboratory = new GenericCrafter("ultimate-laboratory") {{
            health = 300;
            size = 6;
            outputItem = with(TEItems.ultimateAgreement, 2)[0];
            consumePower(35);
            consumeLiquid(Liquids.cryofluid, 1f);
        }};


        cryofluidMixerLarge = new GenericCrafter("large-cryofluid-mixer") {{
            requirements(Category.crafting, with(Items.lead, 650, Items.silicon, 300, Items.titanium, 600, Items.thorium, 350));
            outputLiquid = new LiquidStack(Liquids.cryofluid, 35f / 60f);
            size = 3;
            hasPower = hasLiquids = hasItems = outputsLiquid = solid = true;
            rotate = false;
            envEnabled = Env.any;
            drawer = new DrawMulti(
                    new DrawRegion("-bottom"), new DrawLiquidTile(Liquids.water),
                    new DrawLiquidTile(Liquids.cryofluid) {{
                        drawLiquidLight = true;
                    }}, new DrawDefault()
            );
            liquidCapacity = 50f;
            craftTime = 50;
            lightLiquid = Liquids.cryofluid;
            itemCapacity = 30;

            consumePower(7f);
            consumeItem(TEItems.titaniumPowder);
            consumeLiquid(Liquids.water, 35f / 60f);
        }};

        advancedOverdriveDome = new OverdriveProjector("advanced-overdrive-dome") {{
            requirements(Category.effect,
                    with(Items.lead, 1250, Items.titanium, 1450, Items.silicon, 1200, Items.plastanium, 1200, Items.surgeAlloy, 850,
                    TEItems.zinc, 450, TEItems.advancedChip, 450)
            );
            consumePower(14f);
            size = 4;
            range = 400f;
            speedBoost = 5.5f;
            useTime = 200f;
            hasBoost = false;
            itemCapacity = 20;

            consumeItems(with(Items.phaseFabric, 3, Items.silicon, 3, TEItems.zinc, 2));
        }};

        advancedPowerNode = new PowerNode("advanced-power-node") {{
            requirements(Category.power,
                    with(Items.copper, 5, Items.lead, 12, Items.titanium, 15, Items.silicon, 6));
            maxNodes = 20;
            laserRange = 12;
        }};

        /* TODO cnm
        terminalProcessor = new LogicBlock("terminal-processor") {{
            requirements(Category.logic,
                    with(Items.lead, 4500, Items.silicon, 3000, Items.thorium, 1000, Items.surgeAlloy, 500, Items.titanium, 2000,
                    TEItems.zinc, 800, Items.phaseFabric, 300, Items.plastanium, 1200, TEItems.specialChip, 400)
            );

            consumeLiquid(Liquids.cryofluid, 0.2f);
            hasLiquids = true;

            instructionsPerTick = 80;
            range = 8 * 100;
            size = 5;
        }};

        hugeLogicDisplay = new LogicDisplay("huge-logic-display") {{
            requirements(Category.logic,
                    with(Items.lead, 2000, Items.silicon, 1600, Items.metaglass, 1000, Items.phaseFabric, 400, Items.surgeAlloy, 300,
                    TEItems.zinc, 500, TEItems.advancedChip, 100, Items.plastanium, 600)
            );

            displaySize = 300;

            size = 10;
        }};
        */

        memoryBankLarge = new MemoryBlock("large-memory-bank") {{
            requirements(Category.logic,
                    with(Items.graphite, 800, Items.silicon, 100, Items.phaseFabric, 400, Items.copper, 800, TEItems.advancedChip, 400,
                    TEItems.zinc, 500, Items.plastanium, 700, Items.surgeAlloy, 200)
            );

            memoryCapacity = 1024;
            size = 3;
        }};

//        IllustratedReconstructor = new Reconstructor("illustrated-reconstructor") {{
//            requirements(Category.units,
//                  with(Items.silicon, 55000, Items.plastanium, 35000, Items.surgeAlloy, 12450, Items.phaseFabric, 5000,
//                      TEItems.zinc, 35000, TEItems.advancedChip, 5000, TEItems.uranium, 12000, TEItems.ultimateChip, 2000)
//            );
//
//            size = 11;
//            consumePower(75f);
//            consumeItems(
//                  with(Items.silicon, 16384, Items.plastanium, 12356, Items.surgeAlloy, 5420, Items.phaseFabric, 3500,
//                      TEItems.zinc, 14000, TEItems.advancedChip, 600, TEItems.uranium, 4000, TEItems.ultimateChip, 100)
//            );
//            consumeLiquid(Liquids.cryofluid, 280f / 60f);
//            liquidCapacity = 440f;
//
//            constructTime = 60f * 60f * 12.5f;
//
//            upgrades.addAll(
//                    new UnitType[]{UnitTypes.eclipse, TEUnitTypes.},
//                    new UnitType[]{UnitTypes.toxopid, TEUnitTypes.},
//                    new UnitType[]{UnitTypes.reign, TEUnitTypes.coupling},//传奇肘击王耦合
//                    new UnitType[]{UnitTypes.omura, TEUnitTypes.},
//                    new UnitType[]{UnitTypes.oct, TEUnitTypes.},
//                    new UnitType[]{UnitTypes.corvus, TEUnitTypes.},
//                    new UnitType[]{UnitTypes.navanax, TEUnitTypes.}
//            );
//        }};      TODO T6Unit 赶进度中，别急

        payloadConveyorLarge = new PayloadConveyor("large-payload-conveyor") {{
            requirements(Category.units, with(Items.graphite, 50, Items.copper, 100, Items.silicon, 25));
            size = (int) (payloadLimit = 5);
            canOverdrive = false;
        }};

        payloadConveyorHuge = new PayloadConveyor("huge-payload-conveyor") {{
            requirements(Category.units, with(Items.graphite, 200, Items.copper, 200, Items.silicon, 70));
            size = (int) (payloadLimit = 7);
            canOverdrive = false;
        }};

        payloadConveyorGigantic = new PayloadConveyor("gigantic-payload-conveyor") {{
            requirements(Category.units,
                    with(Items.graphite, 500, Items.copper, 500, Items.silicon, 250, Items.titanium, 150));
            size = (int) (payloadLimit = 9);
            canOverdrive = false;
        }};

        payloadRouterLarge = new PayloadRouter("large-payload-router") {{
            requirements(Category.units, payloadConveyorLarge.requirements);
            canOverdrive = false;
            size = (int) (payloadLimit = payloadConveyorLarge.size);
        }};

        payloadRouterHuge = new PayloadRouter("huge-payload-router") {{
            requirements(Category.units, payloadConveyorHuge.requirements);
            size = (int) (payloadLimit = payloadConveyorHuge.size);
            canOverdrive = false;
        }};

        payloadRouterGigantic = new PayloadRouter("gigantic-payload-router") {{
            requirements(Category.units, payloadConveyorGigantic.requirements);
            size = (int) (payloadLimit = payloadConveyorGigantic.size);
            canOverdrive = false;
        }};

        shieldGenerator = new ForceProjector("shield-generator") {{
            requirements(Category.effect,
                    with(Items.lead, 50, Items.titanium, 45, Items.silicon, 75, Items.copper, 80, TEItems.preliminaryChip, 5));
            phaseRadiusBoost = 6f * 8f;
            radius = 8f * 8f;
            shieldHealth = 450f;
            cooldownNormal = 1.0f;
            cooldownLiquid = 1.5f;
            cooldownBrokenBase = 5f / 60f;
            phaseShieldBoost = 250f;

            itemConsumer = consumeItem(TEItems.preliminaryChip, 1).boost();
            consumePower(1f);
        }};

        shieldGeneratorLarge = new ForceProjector("large-shield-generator") {{
            requirements(Category.effect,
                    with(Items.lead, 450, Items.titanium, 230, Items.silicon, 150, Items.copper, 550, TEItems.advancedChip, 10));
            size = 2;
            phaseRadiusBoost = 16f * 8f;
            radius = 25f * 8f;
            shieldHealth = 1200f;
            cooldownNormal = 0.9f;
            cooldownLiquid = 1.5f;
            cooldownBrokenBase = 1.0f;
            phaseShieldBoost = 900f;

            itemConsumer = consumeItem(Items.phaseFabric, 2).boost();
            consumePower(8f);
        }};

        shieldGeneratorHuge = new ForceProjector("huge-shield-generator") {{
            requirements(Category.effect,
                    with(Items.lead, 2400, Items.titanium, 2000, Items.silicon, 2500, Items.copper, 5400, TEItems.ultimateChip, 50,
                    Items.phaseFabric, 400)
            );
            size = 4;
            phaseRadiusBoost = 45f * 8f;
            radius = 70f * 8f;
            shieldHealth = 3500f;
            cooldownNormal = 0.8f;
            cooldownLiquid = 1.5f;
            cooldownBrokenBase = 120f / 60f;
            phaseShieldBoost = 3500f;

            itemConsumer = consumeItems(with(Items.phaseFabric, 5, TEItems.zinc, 3, TEItems.intermediateChip, 2)).boost();
            consumePower(8f);
        }};

        sectorShieldGenerator = new ForceProjector("sector-shield-generator") {{
            requirements(Category.effect,
                    with(Items.lead, 24000, Items.titanium, 16000, Items.silicon, 30000, Items.copper, 25000, TEItems.advancedChip, 500,
                            Items.phaseFabric, 2000)
            );
            size = 6;
            radius = 900f * 8f;
            shieldHealth = 50000f;
            cooldownNormal = 3000f / 60f;
            cooldownLiquid = 2f;
            cooldownBrokenBase = 4000f / 60f;
            phaseShieldBoost = 30000f;
            itemCapacity = 20;

            itemConsumer = consumeItems(with(Items.phaseFabric, 10, TEItems.zinc, 6, TEItems.intermediateChip, 8, TEItems.advancedChip, 5)).boost();
            consumePower(8f);
        }};

        itemQuantumTransmissionLightBridge = new LightItemBridge("item-quantum-transmission-light-bridge") {{
            requirements(Category.distribution,
                    with(Items.phaseFabric, 30, Items.silicon, 50, Items.lead, 200, Items.graphite, 100));
            range = 60;
            arrowPeriod = 0.9f;
            arrowTimeScl = 2.75f;
            hasPower = true;
            pulse = true;
            transportTime = 0.2f;
            consumePower(0.9f);
            itemCapacity = 40;
        }};

        liquidQuantumTransmissionLightBridge = new LiquidBridge("liquid-quantum-transmission-light-bridge") {{
            requirements(Category.liquid,
                    with(Items.phaseFabric, 30, Items.silicon, 35, Items.metaglass, 100, Items.titanium, 60));
            range = 60;
            arrowPeriod = 0.9f;
            arrowTimeScl = 2.75f;
            hasPower = true;
            pulse = true;
            consumePower(0.9f);
            liquidCapacity = 300;
        }};

        advancedWaterExtractor = new SolidPump("advanced-water-extractor") {{
            requirements(Category.production,
                    with(Items.metaglass, 200, Items.graphite, 300, Items.lead, 450, Items.copper, 400, TEItems.zinc, 150,
                    TEItems.preliminaryChip, 20)
            );
            result = Liquids.water;
            pumpAmount = 15f / 60f;
            size = 3;
            liquidCapacity = 60f;
            rotateSpeed = 2.4f;
            attribute = Attribute.water;
            envRequired |= Env.groundWater;

            consumePower(3f);
        }};

        mechanicalCliffCrusher = new WallCrafter("mechanical-cliff-crusher") {{
            health = 120;
            drillTime = 300f;
            consumeLiquid(Liquids.water, 4f / 60f).boost();
            attribute = Attribute.sand;
            output = Items.sand;
            ambientSound = Sounds.loopDrill;
            ambientSoundVolume = 0.04f;
            fogRadius = 2;
            liquidBoostIntensity = 2.56f;
            requirements(Category.production, with(Items.copper, 35, Items.lead, 20));
        }};

        pneumaticCliffCrusher = new WallCrafter("pneumatic-cliff-crusher") {{
            health = 120;
            drillTime = 200f;
            consumeLiquid(Liquids.water, 4f / 60f).boost();
            attribute = Attribute.sand;
            output = Items.sand;
            ambientSound = Sounds.loopDrill;
            ambientSoundVolume = 0.04f;
            fogRadius = 2;
            liquidBoostIntensity = 2.56f;
            requirements(Category.production, with(Items.copper, 50, Items.lead, 30, Items.graphite, 15));
        }};

        sporeWallCliffCrusher = new WallCrafter("spore-wall-cliff-crusher") {{
            health = 120;
            drillTime = 175f;
            consumeLiquid(Liquids.water, 4f / 60f).boost();
            attribute = TEAttribute.sporeWalls;
            output = Items.sporePod;
            ambientSound = Sounds.loopDrill;
            ambientSoundVolume = 0.04f;
            fogRadius = 2;
            requirements(Category.production, with(Items.copper, 75, Items.lead, 40, Items.graphite, 25));
        }};

        pyratiteHeater = new HeatProducer("pyratite-heater") {{
            requirements(Category.crafting,
                    with(Items.copper, 120, Items.silicon, 200, Items.lead, 150, Items.graphite, 225,
                    Items.titanium, 100)
            );
            drawer = new DrawMulti(new DrawDefault(), new DrawHeatOutput());
            size = 2;
            heatOutput = 6.0F;
            craftTime = 200.0F;
            ambientSound = Sounds.loopHum;
            consumeItem(Items.pyratite);
        }};

        wallOreCopper = new OreBlock("wall-ore-copper", Items.copper) {{
            wallOre = true;
        }};

        wallOreLead = new OreBlock("wall-ore-lead", Items.lead) {{
            wallOre = true;
        }};

        wallOreTitanium = new OreBlock("wall-ore-titanium", Items.titanium) {{
            wallOre = true;
        }};

        wallOreCoal = new OreBlock("wall-ore-coal", Items.coal) {{
            wallOre = true;
        }};

        wallOreScrap = new OreBlock("wall-ore-scrap", Items.scrap) {{
            wallOre = true;
        }};

        oreGraphitic = new OreBlock("ore-graphitic", Items.graphite);

        oreIron = new OreBlock("ore-iron", TEItems.iron);

        laserBore = new BeamDrill("laser-bore"){{
            requirements(Category.production, with(Items.copper, 70, Items.lead, 45, Items.titanium, 35, Items.silicon, 50));
            consumePower(1.1f);

            drillTime = 100;
            tier = 4;
            size = 2;
            range = 2;
            fogRadius = 3;

            consumeLiquid(Liquids.water, 0.09f).boost();
        }};

        plasticAlloyWall = new Wall("plastic-alloy-wall") {{
            requirements(Category.defense, with(TEItems.plasticAlloy, 6));
            health = 200 * 4;
            absorbLasers = true;
            schematicPriority = 10;
        }};

        plasticAlloyWallLarge = new Wall("large-plastic-alloy-wall") {{
            requirements(Category.defense, with(TEItems.plasticAlloy, 24));
            health = 200 * 16;
            absorbLasers = true;
            schematicPriority = 10;
            size = 2;
        }};

        plasticAlloyConveyor = new Conveyor("plastic-alloy-conveyor") {{
            requirements(Category.distribution, with(TEItems.plasticAlloy, 1, TEItems.steel, 1, Items.titanium, 1, Items.surgeAlloy, 1));
            speed = 37.00905f / 148;
            displayedSpeed = 30f;
            health = 140;
            absorbLasers = true;
        }};

        plasticAlloyPacketConveyor = new StackConveyor("plastic-alloy-packet-conveyor") {{
            requirements(Category.distribution, mult(plasticAlloyConveyor.requirements, 5f));
            speed = 75f / 600f;
            health = 300;
            itemCapacity = 20;
            absorbLasers = true;
        }};

        componentAssemblyPlant = new MultiCrafter("component-assembly-plant") {{//万能组装厂
            health = 1200;
            itemCapacity = 20;
            hasPower = hasItems = true;
            consumePower(400 / 60f);

            recipes.addAll(
                    new Recipe(
                            new StackItemLiquid(with()),
                            new StackItemLiquid(with()),
                            120f
                    )
            );
            requirements(Category.crafting, with());
        }};

        /*
        thoriumNuclearExplosiveReactor = new NuclearReactor("thorium-nuclear-explosive-reactor") {{
            health = 2400;
            size = 9;

        }}; TODO 贴图没画
         */


        //基础方块(E)

        reinforcedPowerNode = new PowerNode("reinforced-power-node") {{
            requirements(Category.power, with(Items.beryllium, 8, Items.tungsten, 4, Items.graphite, 6));
            maxNodes = 5;
            laserRange = 8;
            health = 200;
            laserColor2 = Color.valueOf("cbfd81");
            laserScale = 0.4f;
        }};

        //基础方块(TEMod)
        liquidCoverOil = new CoverLiquidRequireFloor("liquid-cover-oil", Liquids.oil);//GUIDE
        liquidCoverWater = new CoverLiquidRequireFloor("liquid-cover-water", Liquids.water);
        liquidCoverSlag = new CoverLiquidRequireFloor("liquid-cover-slag", Liquids.slag);
        liquidCoverArkycite = new CoverLiquidRequireFloor("liquid-cover-arkycite", Liquids.arkycite);
        liquidCoverCryo = new CoverLiquidRequireFloor("liquid-cover-cryo", Liquids.cryofluid);

        liquidCover = new CoverBlock("liquid-cover") {{
            requirements(Category.effect, with(Items.titanium, 50, Items.silicon, 30, Items.metaglass, 40));
            convertFloor = new Floor[][]{
                    new Floor[]{Blocks.cryofluid.asFloor(), TEBlocks.liquidCoverCryo.asFloor()},
                    new Floor[]{Blocks.slag.asFloor(), TEBlocks.liquidCoverSlag.asFloor()},
                    new Floor[]{Blocks.water.asFloor(), TEBlocks.liquidCoverWater.asFloor()},
                    new Floor[]{Blocks.arkyciteFloor.asFloor(), TEBlocks.liquidCoverArkycite.asFloor()},
                    new Floor[]{Blocks.tar.asFloor(), TEBlocks.liquidCoverOil.asFloor()}
            };
            health = 120;
        }};

        //石头！！！
        float stoneWallHealth = 200;

        stoneWall = new Wall("stone-wall") {{
            health = (int) (Math.pow(size, 2) * stoneWallHealth);
            requirements(Category.defense, lib.sizeWith(with(TEItems.stone, 3), this));
        }};

        stoneWallLarge = new Wall("large-stone-wall") {{
            size = 2;
            health = (int) (Math.pow(size, 2) * stoneWallHealth);
            requirements(Category.defense, lib.sizeWith(with(TEItems.stone, 3), this));
        }};

        stoneConveyor = new Conveyor("stone-conveyor"){{
            requirements(Category.distribution, with(TEItems.stone, 1));
            health = 20;
            speed = 1f / 60f;
            displayedSpeed = 2.5f;
            buildCostMultiplier = 2f;
            researchCost = with(TEItems.stone, 20);
        }};

        stoneDrill = new Drill("stone-drill") {{
            health = 100;
            requirements(Category.production, with(TEItems.stone, 15, Items.copper, 2));
            tier = 2;
            drillTime = 500;
            consumeLiquid(Liquids.water, 3f / 60f).boost();
        }};


        isComplete(TEBlocks.class);
    }
}
