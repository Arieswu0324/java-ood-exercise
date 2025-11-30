package trafficsignalcontrol.state;

import trafficsignalcontrol.entity.SignalPhase;
import trafficsignalcontrol.enums.Direction;
import trafficsignalcontrol.enums.Signal;
import trafficsignalcontrol.service.Intersection;

import java.util.List;
import java.util.Map;

public class WestEastIntersectionState implements IntersectionState {

    @Override
    public List<SignalPhase> getSignalPhase(Intersection context) {
        // 阶段1：东西绿灯，南北红灯
        SignalPhase greenPhase = new SignalPhase(
                Map.of(
                        Direction.EAST, Signal.GREEN,
                        Direction.WEST, Signal.GREEN,
                        Direction.NORTH, Signal.RED,
                        Direction.SOUTH, Signal.RED
                ),
                context.getGreenDuration()
        );

        // 阶段2：东西黄灯，南北红灯
        SignalPhase yellowPhase = new SignalPhase(
                Map.of(
                        Direction.EAST, Signal.YELLOW,
                        Direction.WEST, Signal.YELLOW,
                        Direction.NORTH, Signal.RED,
                        Direction.SOUTH, Signal.RED
                ),
                context.getYellowDuration()
        );

        return List.of(greenPhase, yellowPhase);
    }

    @Override
    public IntersectionState getNext() {
        return new NorthSouthIntersectionState();
    }
}
