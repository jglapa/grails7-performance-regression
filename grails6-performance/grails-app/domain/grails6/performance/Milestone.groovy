package grails6.performance

class Milestone {
    String name
    String description
    Date targetDate
    Date completedDate
    Boolean isCompleted
    Date dateCreated
    Date lastUpdated

    static belongsTo = [project: Project]

    static constraints = {
        name blank: false, maxSize: 200
        description nullable: true, maxSize: 1000
        targetDate nullable: true
        completedDate nullable: true
        isCompleted nullable: true
    }
}
