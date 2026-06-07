package TEMLib.ui;

import TEMod.TEVars;
import arc.Core;
import arc.func.Boolc;
import arc.func.Boolp;
import arc.func.Cons;
import arc.scene.style.Drawable;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

import static arc.Core.graphics;
import static mindustry.Vars.mobile;
import static mindustry.Vars.ui;

public class TEMapInfoDialog extends BaseDialog {
    private static final Seq<RulesCategory> builder = new Seq<>();
    private Table main;
    private MapAntiCheatConfigDialog antiCheatConfig;

    public String ruleSearch = "";

    public TEMapInfoDialog() {
        super("@temod.mapinfo");

        addCloseButton();

        shown(this::setup);
    }

    private void setup() {
        cont.clear();
        cont.table(t -> {
            t.add("@search").padRight(10);
            var field = t.field(ruleSearch, text -> {
                ruleSearch = text.trim().replaceAll(" +", " ").toLowerCase();
                build();
            }).grow().pad(8).get();
            field.setCursorPosition(ruleSearch.length());
            Core.scene.setKeyboardFocus(field);
            t.button(Icon.cancel, Styles.emptyi, () -> {
                ruleSearch = "";
                build();
            }).padLeft(10f).size(35f);
        }).row();

        Cell<ScrollPane> paneCell = cont.pane(m -> main = m);

        builder.clear();
        main.clear();
        main.left().defaults().fillX().left();
        main.row();

        build();

        for(var i = 0; i < builder.size; i++){
            addToMain(builder.get(i).current, Core.bundle.get("temod.rules.title." + builder.get(i).name));
        }

        paneCell.scrollX(main.getPrefWidth() + 40f > graphics.getWidth());
    }

    private void build() {
        category("misc", rules -> {
            rules.check("@temod.rules.enableanticheat", b -> TEVars.rules.enableAntiCheat = b, () -> TEVars.rules.enableAntiCheat);
            rules.button("temod.rules.mapanticheatconfig", Icon.wrenchSmall, () -> {});
        });
    }

    void addToMain(Table category, String title){
        if(category.hasChildren()){
            main.add(title).color(Pal.accent).padTop(20).padRight(100f).padBottom(-3).fillX().left().pad(5).row();
            main.image().color(Pal.accent).height(3f).padRight(100f).padBottom(20).fillX().left().pad(5).row();
            main.add(category).row();
        }
    }

    public void category(String name, Cons<RulesCategory> cons) {
        RulesCategory add = new RulesCategory(name);
        cons.get(add);
        builder.add(add);
    }

    public class RulesCategory {
        private final String name;
        private final Table current = new Table();

        public RulesCategory(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void check(String text, Boolc cons, Boolp prov){
            check(text, cons, prov, () -> true);
        }

        public void check(String text, Boolc cons, Boolp prov, Boolp condition){
            if(!Core.bundle.get(text.substring(1)).toLowerCase().contains(ruleSearch)) return;
            var cell = current.check(text, cons).checked(prov.get()).update(a -> a.setDisabled(!condition.get()));
            cell.get().left();
            ruleInfo(cell, text);
            current.row();
        }

        public void button(String text, Drawable icon, Runnable run, Boolp condition) {
            if(!Core.bundle.get(text.substring(1)).toLowerCase().contains(ruleSearch)) return;
            var cell = current.button(text, icon, Styles.flatt, run).growX().update(a -> a.setDisabled(!condition.get()));
            cell.get().left();
            ruleInfo(cell, text);
            current.row();
        }

        public void button(String text, Runnable run, Boolp condition) {
            if(!Core.bundle.get(text.substring(1)).toLowerCase().contains(ruleSearch)) return;
            var cell = current.button(text, Styles.flatt, run).update(a -> a.setDisabled(!condition.get()));
            cell.get().left();
            ruleInfo(cell, text);
            current.row();
        }

        public void button(String text, Runnable run) {
            button(text, run, () -> true);
        }

        public void button(String text, Drawable icon, Runnable run) {
            button(text, icon, run, () -> true);
        }

        public void ruleInfo(Cell<?> cell, String text){
            if(Core.bundle.has(text.substring(1) + ".info")){
                if(mobile && !graphics.isPortrait()){ //disabled in portrait - broken and goes offscreen
                    Table table = new Table();
                    table.add(cell.get()).left().expandX().fillX();
                    cell.clearElement();
                    table.button(Icon.infoSmall, () -> ui.showInfo(text + ".info")).size(32f).right();
                    cell.setElement(table).left().expandX().fillX();
                }else{
                    cell.tooltip(text + ".info");
                }
            }
        }

        public Table current() {
            return current;
        }
    }
}
