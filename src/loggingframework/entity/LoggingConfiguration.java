package loggingframework.entity;

import loggingframework.enums.Level;
import loggingframework.output.OutputDestination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class LoggingConfiguration {

    //effective level
    private Level level=Level.DEBUG;

    private List<OutputDestination> destinations = new LinkedList<>();


    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public void setDestinations(List<OutputDestination> destinations){
        //防御性拷贝：创建新列表，避免外部修改影响内部状态
        this.destinations = new ArrayList<>(destinations);
    }

   public void addDestination(OutputDestination destination){
        destinations.add(destination);
   }

    public List<OutputDestination> getDestinations() {
        return Collections.unmodifiableList(destinations);
    }
}
