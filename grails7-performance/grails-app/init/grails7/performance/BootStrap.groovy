package grails7.performance

import grails.gorm.transactions.Transactional
import groovy.time.TimeCategory

class BootStrap {

    def init = {
        createDummyData()
    }

    def destroy = {
    }

    private Date daysAgo(int days) {
        use(TimeCategory) {
            return new Date() - days.days
        }
    }

    private Date daysFromNow(int days) {
        use(TimeCategory) {
            return new Date() + days.days
        }
    }

    private Date addDays(Date date, int days) {
        use(TimeCategory) {
            return date + days.days
        }
    }

    @Transactional
    void createDummyData() {
        if (Company.count() > 0) {
            return // Data already exists
        }

        def random = new Random(42) // Fixed seed for reproducibility

        // Create skills first (to be shared across employees)
        def skillNames = [
            ['Java', 'Programming'],
            ['Groovy', 'Programming'],
            ['Python', 'Programming'],
            ['JavaScript', 'Programming'],
            ['SQL', 'Database'],
            ['MongoDB', 'Database'],
            ['AWS', 'Cloud'],
            ['Docker', 'DevOps'],
            ['Kubernetes', 'DevOps'],
            ['Agile', 'Methodology'],
            ['Scrum', 'Methodology'],
            ['React', 'Frontend'],
            ['Angular', 'Frontend'],
            ['Spring Boot', 'Framework'],
            ['Grails', 'Framework'],
            ['Machine Learning', 'AI'],
            ['Data Analysis', 'Analytics'],
            ['Project Management', 'Management'],
            ['Leadership', 'Soft Skills'],
            ['Communication', 'Soft Skills']
        ]

        def proficiencyLevels = ['BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT']
        def skills = skillNames.collect { skillData ->
            new Skill(
                name: skillData[0],
                category: skillData[1],
                proficiencyLevel: proficiencyLevels[random.nextInt(proficiencyLevels.size())]
            ).save(flush: true, failOnError: true)
        }

        // Company data
        def companyData = [
            ['TechCorp Industries', 'Technology', 'San Francisco, CA'],
            ['Global Finance Ltd', 'Finance', 'New York, NY'],
            ['HealthFirst Solutions', 'Healthcare', 'Boston, MA'],
            ['EcoGreen Energy', 'Energy', 'Austin, TX'],
            ['RetailMax Corp', 'Retail', 'Seattle, WA']
        ]

        def departmentNames = ['Engineering', 'Sales', 'Marketing', 'Human Resources', 'Finance', 'Operations', 'Research', 'Customer Support']
        def jobTitles = ['Software Engineer', 'Senior Developer', 'Tech Lead', 'Manager', 'Analyst', 'Consultant', 'Director', 'Specialist', 'Coordinator', 'Administrator']
        def projectStatuses = ['PLANNING', 'IN_PROGRESS', 'ON_HOLD', 'COMPLETED']
        def taskStatuses = ['TODO', 'IN_PROGRESS', 'REVIEW', 'DONE', 'BLOCKED']

        def firstNames = ['John', 'Jane', 'Michael', 'Emily', 'David', 'Sarah', 'Robert', 'Lisa', 'William', 'Jennifer', 'James', 'Maria', 'Daniel', 'Susan', 'Christopher', 'Karen', 'Matthew', 'Nancy', 'Anthony', 'Betty']
        def lastNames = ['Smith', 'Johnson', 'Williams', 'Brown', 'Jones', 'Garcia', 'Miller', 'Davis', 'Rodriguez', 'Martinez', 'Hernandez', 'Lopez', 'Gonzalez', 'Wilson', 'Anderson', 'Thomas', 'Taylor', 'Moore', 'Jackson', 'Martin']

        def employeeCounter = 0

        companyData.each { companyInfo ->
            def company = new Company(
                name: companyInfo[0],
                industry: companyInfo[1],
                headquarters: companyInfo[2],
                foundedDate: daysAgo(random.nextInt(10000)),
                annualRevenue: (random.nextInt(900) + 100) * 1000000.0,
                employeeCount: random.nextInt(5000) + 500,
                isPublic: random.nextBoolean()
            ).save(flush: true, failOnError: true)

            // Create departments for each company
            def numDepartments = random.nextInt(4) + 4 // 4-7 departments
            def companyDepartments = []

            (0..<numDepartments).each { deptIdx ->
                def deptName = departmentNames[deptIdx % departmentNames.size()]
                def department = new Department(
                    name: deptName,
                    code: "${company.name.take(3).toUpperCase()}-${deptName.take(3).toUpperCase()}-${deptIdx}",
                    budget: (random.nextInt(500) + 50) * 10000.0,
                    company: company
                ).save(flush: true, failOnError: true)
                companyDepartments << department
            }

            // Create employees for each department
            def companyEmployees = []
            companyDepartments.each { department ->
                def numEmployees = random.nextInt(15) + 5 // 5-19 employees per department

                (0..<numEmployees).each { empIdx ->
                    def firstName = firstNames[random.nextInt(firstNames.size())]
                    def lastName = lastNames[random.nextInt(lastNames.size())]
                    employeeCounter++

                    def employee = new Employee(
                        firstName: firstName,
                        lastName: lastName,
                        email: "${firstName.toLowerCase()}.${lastName.toLowerCase()}.${employeeCounter}@${company.name.replaceAll(/\s+/, '').toLowerCase()}.com",
                        jobTitle: jobTitles[random.nextInt(jobTitles.size())],
                        salary: (random.nextInt(150) + 50) * 1000.0,
                        hireDate: daysAgo(random.nextInt(3650)),
                        isActive: random.nextInt(10) > 1, // 90% active
                        performanceRating: random.nextInt(5) + 1,
                        company: company,
                        department: department
                    ).save(flush: true, failOnError: true)

                    // Assign 2-5 random skills to each employee
                    def numSkills = random.nextInt(4) + 2
                    def employeeSkills = skills.shuffled().take(numSkills)
                    employeeSkills.each { skill ->
                        employee.addToSkills(skill)
                    }
                    employee.save(flush: true, failOnError: true)

                    companyEmployees << employee
                }
            }

            // Create projects for each department
            companyDepartments.each { department ->
                def numProjects = random.nextInt(4) + 2 // 2-5 projects per department
                def deptEmployees = companyEmployees.findAll { it.department == department }

                (0..<numProjects).each { projIdx ->
                    def projectManager = deptEmployees ? deptEmployees[random.nextInt(deptEmployees.size())] : null
                    def projectStartDate = daysAgo(random.nextInt(365))

                    def project = new Project(
                        name: "Project ${department.name} ${projIdx + 1}",
                        description: "This is a ${['strategic', 'innovative', 'critical', 'transformational'][random.nextInt(4)]} project for ${department.name} department focusing on ${['optimization', 'growth', 'efficiency', 'modernization'][random.nextInt(4)]}.",
                        status: projectStatuses[random.nextInt(projectStatuses.size())],
                        startDate: projectStartDate,
                        endDate: daysFromNow(random.nextInt(365)),
                        budget: (random.nextInt(100) + 10) * 10000.0,
                        priority: random.nextInt(10) + 1,
                        department: department,
                        manager: projectManager
                    ).save(flush: true, failOnError: true)

                    if (projectManager) {
                        projectManager.addToManagedProjects(project)
                        projectManager.save(flush: true, failOnError: true)
                    }

                    // Create milestones for each project
                    def numMilestones = random.nextInt(4) + 2 // 2-5 milestones
                    (0..<numMilestones).each { msIdx ->
                        def isCompleted = random.nextBoolean()
                        def targetDate = addDays(projectStartDate, msIdx * 30)
                        new Milestone(
                            name: "Milestone ${msIdx + 1}: ${['Planning', 'Development', 'Testing', 'Deployment', 'Review'][msIdx % 5]}",
                            description: "Milestone ${msIdx + 1} for ${project.name}",
                            targetDate: targetDate,
                            completedDate: isCompleted ? addDays(targetDate, random.nextInt(10)) : null,
                            isCompleted: isCompleted,
                            project: project
                        ).save(flush: true, failOnError: true)
                    }

                    // Create tasks for each project
                    def numTasks = random.nextInt(10) + 5 // 5-14 tasks per project
                    (0..<numTasks).each { taskIdx ->
                        def taskStatus = taskStatuses[random.nextInt(taskStatuses.size())]
                        def assignee = deptEmployees ? deptEmployees[random.nextInt(deptEmployees.size())] : null

                        def task = new Task(
                            title: "Task ${taskIdx + 1}: ${['Implement', 'Design', 'Test', 'Review', 'Document', 'Analyze', 'Configure', 'Deploy'][random.nextInt(8)]} ${['feature', 'module', 'component', 'service', 'API'][random.nextInt(5)]}",
                            description: "Detailed description for task ${taskIdx + 1} in ${project.name}",
                            status: taskStatus,
                            estimatedHours: random.nextInt(40) + 4,
                            actualHours: taskStatus == 'DONE' ? random.nextInt(50) + 4 : null,
                            dueDate: addDays(projectStartDate, random.nextInt(180)),
                            completedDate: taskStatus == 'DONE' ? daysAgo(random.nextInt(30)) : null,
                            priority: random.nextInt(10) + 1,
                            project: project,
                            assignee: assignee
                        ).save(flush: true, failOnError: true)

                        if (assignee) {
                            assignee.addToAssignedTasks(task)
                            assignee.save(flush: true, failOnError: true)
                        }
                    }
                }
            }
        }

        println "=== Data Population Complete ==="
        println "Companies: ${Company.count()}"
        println "Departments: ${Department.count()}"
        println "Employees: ${Employee.count()}"
        println "Projects: ${Project.count()}"
        println "Tasks: ${Task.count()}"
        println "Milestones: ${Milestone.count()}"
        println "Skills: ${Skill.count()}"
    }
}
