package TEMod.content.Kepler;

import arc.func.Cons;
import arc.graphics.Color;
import mindustry.game.Rules;
import mindustry.game.Team;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MultiMesh;

import mindustry.type.Planet;
import org.jetbrains.annotations.NotNull;

import static TEMod.TECore.isComplete;
import static mindustry.content.Planets.*;

public class KeplerPlanet {
    public static Planet kepler;

    public static void setRule(@NotNull Planet planet, Cons<Rules> rules, Team enemyTeam, Cons<Rules.TeamRule> teamRules) {
        planet.ruleSetter = (Rules n) -> {
            rules.get(n);
            n.waveTeam = enemyTeam;

            if (n.teams != null) {
                Rules.TeamRule enemyTeamRule = n.teams.get(enemyTeam);
                if (enemyTeamRule != null) {
                    teamRules.get(enemyTeamRule);
                }
            }
        };
    }

    public static void load() {
        kepler = new Planet("kepler", sun, 2, 3) {{
            generator = new KeplerPlanetGenerator();
            meshLoader = () -> new HexMesh(this, 6);
            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 1, 0.3f, 0.28f, 6, new Color().set(Color.valueOf("d8ecff")).mul(Color.valueOf("d8ecff")).a(0.75f), 2, 0.45f, 0.9f, 0.38f),
                    new HexSkyMesh(this, 5, 0.8f, 0.32f, 6, Color.white.cpy().lerp(Color.valueOf("d8ecff"), 0.55f).a(0.75f), 2, 0.45f, 1f, 0.41f)
            );
            launchCapacityMultiplier = 0.5f;
            sectorSeed = 12;
            allowWaves = true;
            //allowLegacyLaunchPads = true;
            allowSectorInvasion = true;
            allowLaunchSchematics = true;
            enemyCoreSpawnReplace = true;
            allowLaunchLoadout = true;
            prebuildBase = false;
            ruleSetter = r -> setRule(this, rules -> {
                rules.placeRangeCheck = false;
                rules.showSpawns = true;
                rules.enemyCoreBuildRadius = 45f * 8f;
                rules.solarMultiplier = 1.5f;

                rules.hideBannedBlocks = true;
                rules.coreDestroyClear = true;
                rules.wavesSpawnAtCores = false;
            }, Team.blue, teamRule -> {
                teamRule.rtsAi = true;
                teamRule.rtsMaxSquad = 500;
                teamRule.rtsMinSquad = 3;
                teamRule.rtsMinWeight = 1f;
            });
            iconColor = Color.valueOf("87c7ff");
            atmosphereColor = Color.valueOf("87c7ff");
            atmosphereRadIn = 0.01f;
            atmosphereRadOut = 0.18f;
            startSector = 53;
            alwaysUnlocked = true;
            landCloudColor = Color.valueOf("89d2ff");
            showRtsAIRule = true;
            allowSelfSectorLaunch = true;
        }};
        isComplete(KeplerPlanet.class);
    }
}
