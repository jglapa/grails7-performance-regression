package grails7.performance

class Skill {
    String name
    String category
    String proficiencyLevel
    Date dateCreated
    Date lastUpdated

    static belongsTo = Employee
    static hasMany = [employees: Employee]

    static constraints = {
        name blank: false, maxSize: 100
        category nullable: true, maxSize: 50
        proficiencyLevel nullable: true, inList: ['BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT']
    }

    static mapping = {
        employees lazy: false
    }
}
