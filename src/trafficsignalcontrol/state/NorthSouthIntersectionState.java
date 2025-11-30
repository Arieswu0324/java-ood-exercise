package trafficsignalcontrol.state;

import trafficsignalcontrol.entity.SignalPhase;
import trafficsignalcontrol.enums.Direction;
import trafficsignalcontrol.enums.Signal;
import trafficsignalcontrol.service.Intersection;

import java.util.List;
import java.util.Map;


public class NorthSouthIntersectionState implements IntersectionState {

    @Override
    public List<SignalPhase> getSignalPhase(Intersection context) {
        // 阶段1：南北绿灯，东西红灯
        SignalPhase greenPhase = new SignalPhase(
                Map.of(
                        Direction.NORTH, Signal.GREEN,
                        Direction.SOUTH, Signal.GREEN,
                        Direction.EAST, Signal.RED,
                        Direction.WEST, Signal.RED
                ),
                context.getGreenDuration()
        );

        // 阶段2：南北黄灯，东西红灯
        SignalPhase yellowPhase = new SignalPhase(
                Map.of(
                        Direction.NORTH, Signal.YELLOW,
                        Direction.SOUTH, Signal.YELLOW,
                        Direction.EAST, Signal.RED,
                        Direction.WEST, Signal.RED
                ),
                context.getYellowDuration()
        );

        return List.of(greenPhase, yellowPhase);
    }

    @Override
    public IntersectionState getNext() {
        return new WestEastIntersectionState();
    }
}
