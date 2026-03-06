package org.example.devroadmapskilltracker.skill;

public enum SkillStatus {
    BACKLOG("Like to learn"),
    IN_PROGRESS("Working on"),
    MASTERED("Mastered knowledge");

    private final String displayName;

    SkillStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
