package grails6.performance

class Task {
    String title
    String description
    String status
    Integer estimatedHours
    Integer actualHours
    Date dueDate
    Date completedDate
    Integer priority
    Date dateCreated
    Date lastUpdated

    static belongsTo = [project: Project, assignee: Employee]

    static constraints = {
        title blank: false, maxSize: 200
        description nullable: true, maxSize: 2000
        status inList: ['TODO', 'IN_PROGRESS', 'REVIEW', 'DONE', 'BLOCKED']
        estimatedHours nullable: true, min: 0
        actualHours nullable: true, min: 0
        dueDate nullable: true
        completedDate nullable: true
        priority nullable: true, min: 1, max: 10
        assignee nullable: true
    }
}
