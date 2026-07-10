package TEMLib.type;

import arc.util.Nullable;
import mindustry.type.UnitType;

public class TEUnitType extends UnitType {
    public @Nullable SpecialMain main;

    public TEUnitType(String name) {
        super(name);
    }
}
