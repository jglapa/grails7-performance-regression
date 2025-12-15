package grails7.performance

class Company {
    String name
    String industry
    String headquarters
    Date foundedDate
    BigDecimal annualRevenue
    Integer employeeCount
    Boolean isPublic
    Date dateCreated
    Date lastUpdated

    static hasMany = [departments: Department, employees: Employee]

    static constraints = {
        name blank: false, maxSize: 200
        industry nullable: true, maxSize: 100
        headquarters nullable: true, maxSize: 200
        foundedDate nullable: true
        annualRevenue nullable: true, scale: 2
        employeeCount nullable: true, min: 0
        isPublic nullable: true
    }

    static mapping = {
        departments lazy: false
    }
}
