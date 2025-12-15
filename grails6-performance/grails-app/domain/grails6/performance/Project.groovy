package grails6.performance

class Project {
    String name
    String description
    String status
    Date startDate
    Date endDate
    BigDecimal budget
    Integer priority
    Date dateCreated
    Date lastUpdated

    static belongsTo = [department: Department, manager: Employee]
    static hasMany = [tasks: Task, milestones: Milestone]

    static constraints = {
        name blank: false, maxSize: 200
        description nullable: true, maxSize: 2000
        status inList: ['PLANNING', 'IN_PROGRESS', 'ON_HOLD', 'COMPLETED', 'CANCELLED']
        startDate nullable: true
        endDate nullable: true
        budget nullable: true, scale: 2
        priority nullable: true, min: 1, max: 10
        manager nullable: true
    }

    static mapping = {
        tasks lazy: false
        milestones lazy: false
    }
}
