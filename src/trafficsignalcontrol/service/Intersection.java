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


    public Intersection(int id, long greenDuration, long yellowDuration) {
        this.id = id;
        this.lights = new HashMap<>();
        for (Direction d : Direction.values()) {
            TrafficLight light = new TrafficLight(id, d);
            lights.put(d, light);
        }

        this.greenDuration = greenDuration;
        this.yellowDuration = yellowDuration;

        intersectionState = new NorthSouthIntersectionState();
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
}
