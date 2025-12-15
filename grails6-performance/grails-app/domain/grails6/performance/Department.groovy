package grails6.performance

class Department {
    String name
    String code
    BigDecimal budget
    Date dateCreated
    Date lastUpdated

    static belongsTo = [company: Company]
    static hasMany = [employees: Employee, projects: Project]

    static constraints = {
        name blank: false, maxSize: 100
        code blank: false, unique: true, maxSize: 20
        budget nullable: true, scale: 2
    }

    static mapping = {
        projects lazy: false
    }
}
