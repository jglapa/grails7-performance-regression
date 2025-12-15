# Grails 7 Performance Regression - ~4x Slower Than Grails 6

## Summary

Grails 7.0.3 exhibits significant performance regression compared to Grails 6.2.3, running approximately **4 times slower** in GORM-based operations. Testing indicates this is likely related to Groovy 4's "indy-only" implementation rather than Spring or Hibernate changes.

## Versions

- **Grails 6**: 6.2.3 (Groovy 3.0.23)
- **Grails 7**: 7.0.3 (Groovy 4.0.29)
- **Java**: 17.0.15 (Amazon Corretto)
- **Platform**: M1 Max MacBook Pro

## Reproduction

This project contains freshly bootstrapped Grails 6 and Grails 7 web applications with identical functionality. (Note: The JDBC pooling library in Grails 6 was changed to HikariCP to match Grails 7 for fair comparison.)

To highlight the performance regression, an example domain graph was created (Company, Department, Employee, Milestone, Project, Skill, Task) together with testing business logic.

The applications are using default grails settings. GORM is used for persistence; however, for simplicity H2 database is used.

### Repository

Link to this repository: https://github.com/[your-username]/grails7-performance-regression

### Steps to Reproduce

A testing benchmark endpoint is available at `/performanceTest/benchmark` that can be parametrized with:
* `iterations` - number of test iterations
* `warmup` - number of warmup iterations
* `pretty` - output in tabular format instead of json

#### 1. Run Grails 6 Application

```shell
cd grails6-performance/
./gradlew :bootWar
java -Dgrails.env=development -jar build/libs/grails6-performance-0.1.war
# Wait for: Grails application running at http://localhost:8080
```

#### 2. Run Grails 7 Application

```shell
cd grails7-performance/
./gradlew :bootWar
java -Dgrails.env=development -jar build/libs/grails7-performance-0.1.war
# Wait for: Grails application running at http://localhost:8080
```

#### 3. Run Benchmark

```shell
curl 'localhost:8080/performanceTest/benchmark?pretty=true'
```

## Expected vs Actual Behavior

**Expected**: Similar performance between Grails 6 and Grails 7, with at most minor differences due to framework improvements.

**Actual**: Grails 7 is approximately **4 times slower** than Grails 6 in GORM-based operations.

## Benchmark Results

### Default Configuration (bootWar)
```
============================================================
BENCHMARK RESULTS
============================================================
Configuration:
------------------------------------------------------------
  Warmup Iterations         : 5          : 5               
  Warmup Time               : 812 ms     : 1762 ms         
  Test Iterations           : 50         : 50              
Performance Metrics:                                       
------------------------------------------------------------
  Minimum Time              : 54 ms      : 269 ms          
  Maximum Time              : 75 ms      : 417 ms          
  Average Time              : 61.18 ms   : 323.48 ms   
      
  Total Time                : 3059 ms    : 16174 ms        

Environment:                                               
------------------------------------------------------------
  Groovy Version            : 3.0.23     : 4.0.29          
  Java Version              : 17.0.15    : 17.0.15         
============================================================
```



## Observations
I suspect the regression is caused by groovy 4 and its "indy" only version and not spring or hibernate changes.

### indy
I tried tweaking the indy thresholds to see if it would help. It's not entirely clear to me how they work but with setting them to `0` I got some improvement.
Other combinations didn't necessarily cause any better results.
```
-Dgroovy.indy.optimize.threshold=0
-Dgroovy.indy.fallback.threshold=0
```

```
============================================================
BENCHMARK RESULTS :bootWar -Dgroovy.indy.optimize.threshold=0 -Dgroovy.indy.fallback.threshold=0
============================================================

Configuration:
------------------------------------------------------------
  Warmup Iterations         : 5
  Warmup Time               : 860 ms
  Test Iterations           : 50

Performance Metrics:
------------------------------------------------------------
  Minimum Time              : 128 ms
  Maximum Time              : 198 ms
  Average Time              : 157.46 ms
  Total Time                : 7873 ms

Environment:
------------------------------------------------------------
  Groovy Version            : 4.0.29
  Java Version              : 17.0.15

============================================================
```

### Flame Graphs
I compared flame graphs of both versions (grails6/grails7) and they are quite different.
Unsurprisingly grails7 is full of `org.codehaus.groovy.vmplugin.v8.*` where grails6 is using `org.codehaus.groovy.runtime.*`


### @CompileStatic

I haven't explored annotating the code with @CompileStatic. It might be worthwhile, however I would really like to avoid doing this right at the beginning to all of my production code.
Doing this on a smaller scale, however, identifying proper places is challenging. 
Especially if GORM is involved.


### bootRun Performance
Interesting observation: when running the app using `./gradlew bootRun`, the Grails 7 version is ~8 times slower compared to Grails 6, and ~3 times slower when comparing to `:bootWar` with `-Dgrails.env=development`.

**This means that running it in development mode in an IDE will be significantly slower as well.**
```
============================================================
BENCHMARK RESULTS :bootRun
============================================================
Configuration:
------------------------------------------------------------
  Warmup Iterations         : 5           5                 
  Warmup Time               : 1141 ms     4498 ms           
  Test Iterations           : 50          50                
Performance Metrics:                                        
------------------------------------------------------------
  Minimum Time              : 100 ms      685 ms            
  Maximum Time              : 116 ms      1119 ms           
  Average Time              : 103.96 ms   858.00 ms         

  Total Time                : 5198 ms     42900 ms          
                                                         
Environment:                                                
------------------------------------------------------------
  Groovy Version            : 3.0.23      4.0.29            
  Java Version              : 17.0.15     17.0.15           
============================================================
```