package trafficsignalcontrol.service;

import trafficsignalcontrol.entity.SignalPhase;
import trafficsignalcontrol.entity.TrafficLight;
import trafficsignalcontrol.enums.Direction;
import trafficsignalcontrol.enums.Signal;
import trafficsignalcontrol.state.IntersectionState;
import trafficsignalcontrol.state.NorthSouthIntersectionState;
import trafficsignalcontrol.state.WestEastIntersectionState;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Intersection implements Runnable {
    private final int id;
    private final Map<Direction, TrafficLight> lights;
    private IntersectionState intersectionState;
    private volatile long greenDuration;
    private volatile long yellowDuration;
    private volatile boolean shutdown = false;


    private Intersection(int id, long greenDuration, long yellowDuration, IntersectionState state) {
        this.id = id;
        this.lights = new HashMap<>();
        for (Direction d : Direction.values()) {
            TrafficLight light = new TrafficLight(id, d);
            lights.put(d, light);
        }

        this.greenDuration = greenDuration;
        this.yellowDuration = yellowDuration;
        this.intersectionState = state;
    }


    public Map<Direction, TrafficLight> getLights() {
        return Collections.unmodifiableMap(lights);
    }

    public int getId() {
        return id;
    }

    @Override
    public void run() {
        while (!shutdown) {
            try {
                List<SignalPhase> signalPhase = intersectionState.getSignalPhase(this);

                // 依次执行每个阶段
                for (int i = 0; i < signalPhase.size(); i++) {
                    if (shutdown) break;  // 检查是否需要停止

                    SignalPhase phase = signalPhase.get(i);

                    // 应用信号配置
                    applySignalConfiguration(phase);

                    // 打印日志
                    String phaseName = getPhaseName(i);
                    String message = MessageFormat.format("INTERSECTION {0}: {1} Phase (Duration: {2}ms)", id, phaseName, phase.duration());
                    System.out.println(message);

                    // 等待该阶段的持续时间
                    Thread.sleep(phase.duration());
                }

                intersectionState = intersectionState.getNext();

            } catch (InterruptedException e) {
                System.err.printf("Intersection %d interrupted", id);
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private String getPhaseName(int phaseIndex) {
        String direction = getCurrentDirection();
        String signal = phaseIndex == 0 ? "GREEN" : "YELLOW";
        return direction + " " + signal;
    }

    private String getCurrentDirection() {
        if (intersectionState instanceof NorthSouthIntersectionState) {
            return "North-South";
        } else if (intersectionState instanceof WestEastIntersectionState) {
            return "West-East";
        }
        return "Unknown";
    }


    private String getCurrentPhaseName() {
        if (intersectionState instanceof NorthSouthIntersectionState) {
            return "North-South GREEN";
        } else if (intersectionState instanceof WestEastIntersectionState) {
            return "West-East GREEN";
        }
        return "Unknown";
    }

    private void applySignalConfiguration(SignalPhase phase) {
        for (Map.Entry<Direction, Signal> entry : phase.signals().entrySet()) {
            lights.get(entry.getKey()).setSignal(entry.getValue());
        }
    }

    public void stop() {
        shutdown = true;
    }

    public long getGreenDuration() {
        return greenDuration;
    }

    public void setGreenDuration(long greenDuration) {
        this.greenDuration = greenDuration;
    }

    public long getYellowDuration() {
        return yellowDuration;
    }

    public void setYellowDuration(long yellowDuration) {
        this.yellowDuration = yellowDuration;
    }

    //场用于创建不可变对象，
    public static class Builder {
        //默认参数防止用户没有调用withDurations方法
        private long greenDuration = 500;
        private long yellowDuration = 100;
        private IntersectionState state = new NorthSouthIntersectionState();
        private final int id;

        public Builder(int id) {
            this.id = id;
        }

        public Builder withYellowDuration(long yellowDuration) {
            if (yellowDuration <= 0) {
                throw new IllegalArgumentException("yellow duration should be greater than 0!");
            }
            this.yellowDuration = yellowDuration;
            return this;
        }

        public Builder withGreenDuration(long greenDuration) {
            if (greenDuration <= 0) {
                throw new IllegalArgumentException("green duration should be greater than 0!");
            }
            this.greenDuration = greenDuration;
            return this;
        }

        public Builder initializeState(IntersectionState state) {
            this.state = state;
            return this;
        }

        public Intersection build() {
            return new Intersection(id, greenDuration, yellowDuration, state);
        }
    }
}
