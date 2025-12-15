package grails7.performance

import grails.converters.JSON

class PerformanceTestController {

    PerformanceTestService performanceTestService

     /**
     * GET /performanceTest/summary
     * Quick summary endpoint for basic health check
     */
    def summary() {
        def startTime = System.currentTimeMillis()

        def summary = [
            companies: Company.count(),
            departments: Department.count(),
            employees: Employee.count(),
            projects: Project.count(),
            tasks: Task.count(),
            milestones: Milestone.count(),
            skills: Skill.count()
        ]

        def endTime = System.currentTimeMillis()
        summary.queryTimeMs = endTime - startTime

        render summary as JSON
    }

    def benchmark() {
        def iterations = params.int('iterations', 50)
        def warmupIterations = params.int('warmup', 5)
        def pretty = params.boolean('pretty', false)

        def warmupStartTime = System.currentTimeMillis()
        warmupIterations.times { i ->
            performanceTestService.runComplexAnalysis()
        }
        def warmupTime = System.currentTimeMillis() - warmupStartTime


        def timings = []
        iterations.times { i ->
            def startTime = System.currentTimeMillis()
            performanceTestService.runComplexAnalysis()
            def endTime = System.currentTimeMillis()
            timings << (endTime - startTime)
        }

        def result = [
                warmupIterations: warmupIterations,
                warmupTime      : warmupTime,
                iterations      : iterations,
                timings         : timings,
                minTimeMs       : timings.min(),
                maxTimeMs       : timings.max(),
                avgTimeMs       : (timings.sum() / timings.size()).round(2),
                totalTimeMs     : timings.sum(),
                groovyVersion  : GroovySystem.version,
                javaVersion    : System.getProperty('java.version')
        ]

        if (pretty) {
            def output = new StringBuilder()
            output.append("=" * 60).append("\n")
            output.append("BENCHMARK RESULTS\n")
            output.append("=" * 60).append("\n\n")

            output.append("Configuration:\n")
            output.append("-" * 60).append("\n")
            output.append(sprintf("  %-25s : %s\n", "Warmup Iterations", result.warmupIterations))
            output.append(sprintf("  %-25s : %s ms\n", "Warmup Time", result.warmupTime))
            output.append(sprintf("  %-25s : %s\n", "Test Iterations", result.iterations))
            output.append("\n")

            output.append("Performance Metrics:\n")
            output.append("-" * 60).append("\n")
            output.append(sprintf("  %-25s : %s ms\n", "Minimum Time", result.minTimeMs))
            output.append(sprintf("  %-25s : %s ms\n", "Maximum Time", result.maxTimeMs))
            output.append(sprintf("  %-25s : %s ms\n", "Average Time", result.avgTimeMs))
            output.append(sprintf("  %-25s : %s ms\n", "Total Time", result.totalTimeMs))
            output.append("\n")

            output.append("Environment:\n")
            output.append("-" * 60).append("\n")
            output.append(sprintf("  %-25s : %s\n", "Groovy Version", result.groovyVersion))
            output.append(sprintf("  %-25s : %s\n", "Java Version", result.javaVersion))
            output.append("\n")
            output.append("=" * 60).append("\n")

            render(text: output.toString(), contentType: "text/plain")
        } else {
            render result as JSON
        }
    }

}
