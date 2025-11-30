package trafficsignalcontrol.state;


import trafficsignalcontrol.entity.SignalPhase;
import trafficsignalcontrol.service.Intersection;

import java.util.List;

//状态类只负责状态转换，时序控制移到 Intersection
public interface IntersectionState {
    //分开返回黄灯和绿灯阶段
    List<SignalPhase> getSignalPhase(Intersection context);
    IntersectionState getNext();
}
