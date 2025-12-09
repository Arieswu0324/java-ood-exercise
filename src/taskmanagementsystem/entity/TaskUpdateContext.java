package taskmanagementsystem.entity;

import taskmanagementsystem.enums.Priority;


public record TaskUpdateContext(String description, Priority priority) {

}
