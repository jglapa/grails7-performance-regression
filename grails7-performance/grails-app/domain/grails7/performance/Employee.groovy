package grails7.performance

class Employee {
    String firstName
    String lastName
    String email
    String jobTitle
    BigDecimal salary
    Date hireDate
    Boolean isActive
    Integer performanceRating
    Date dateCreated
    Date lastUpdated

    static belongsTo = [company: Company, department: Department]
    static hasMany = [skills: Skill, assignedTasks: Task, managedProjects: Project]

    static constraints = {
        firstName blank: false, maxSize: 50
        lastName blank: false, maxSize: 50
        email email: true, unique: true, maxSize: 100
        jobTitle nullable: true, maxSize: 100
        salary nullable: true, scale: 2
        hireDate nullable: true
        isActive nullable: true
        performanceRating nullable: true, min: 1, max: 5
    }

    String getFullName() {
        "${firstName} ${lastName}"
    }
}
