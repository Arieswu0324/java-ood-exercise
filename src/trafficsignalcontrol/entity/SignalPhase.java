package trafficsignalcontrol.entity;

import trafficsignalcontrol.enums.Direction;
import trafficsignalcontrol.enums.Signal;

import java.util.Collections;
import java.util.Map;

//一个阶段的信号配置，包含各个方向的信号状态和该阶段的持续时间
public record SignalPhase(Map<Direction, Signal> signals, long duration) {

    @Override
    public Map<Direction, Signal> signals() {
        return Collections.unmodifiableMap(signals);
    }
}
