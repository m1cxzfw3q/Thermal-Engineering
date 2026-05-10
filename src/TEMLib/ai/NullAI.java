package TEMLib.ai;

import mindustry.entities.units.UnitController;
import mindustry.gen.Unit;

public class NullAI implements UnitController{
    Unit u;

    @Override
    public void unit(Unit unit){
        u = unit;
    }

    @Override
    public Unit unit(){
        return u;
    }
}