package TEMLib.ui;

import mindustry.ui.dialogs.BaseDialog;

// TODO
public class MapAntiCheatConfigDialog extends BaseDialog {
    public MapAntiCheatConfigDialog() {
        super("@temod.rules.anticheat");

        addCloseButton();

        shown(this::setup);
    }

    public void setup() {
        cont.clear();
    }
}
