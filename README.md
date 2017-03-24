grails integration tests appear to be leaking a copy of the entire spring application context) when the `@DirtiesContext` annotation is used.

To reproduce, 

```
git clone https://github.com/jonroler/grails-dirties-context-mem-leak-2.git
cd grails-dirties-context-mem-leak-2
./grailsw test-app -integration
```

Then, open a browser to build/reports/tests/index.html and look at the output for `memleak.Leak1Spec` and `memleak.Leak2Spec`. You should see something like:

```
Grails application running at http://localhost:36649 in environment: test
2017-03-23 18:41:54.170 ERROR --- [    Test worker] memleak.Leak1Spec                        : Size of ConstrainedProperty.constraints.unique: 3
Grails application running at http://localhost:37441 in environment: test
2017-03-23 18:41:56.600 ERROR --- [    Test worker] memleak.Leak1Spec                        : Size of ConstrainedProperty.constraints.unique: 4
Grails application running at http://localhost:34903 in environment: test
2017-03-23 18:41:59.024 ERROR --- [    Test worker] memleak.Leak1Spec                        : Size of ConstrainedProperty.constraints.unique: 5
```

and

```
Grails application running at http://localhost:38457 in environment: test
2017-03-23 18:41:49.118 ERROR --- [    Test worker] memleak.Leak1Spec                        : Size of ConstrainedProperty.constraints.unique: 1
2017-03-23 18:41:49.135 ERROR --- [    Test worker] memleak.Leak1Spec                        : Size of ConstrainedProperty.constraints.unique: 1
Grails application running at http://localhost:34933 in environment: test
2017-03-23 18:41:51.761 ERROR --- [    Test worker] memleak.Leak1Spec                        : Size of ConstrainedProperty.constraints.unique: 2
```

What this is output is showing is that the list `grails.validation.ConstrainedProperty.constraints.unique` is growing when the `@DirtiesContext` annotation is used on a test method, and it is not growing when this annotation is not used. If you look at the two test classes, you will see that all that it is doing is logging the size of this list (with some of the methods annotated with `@DirtiesContext` and others not annotated). 

This is a very serious memory leak for projects that have a large Spring application context since it appears that a copy of the spring application context is reachable for each entry in this list. For example, in our application, one entry in this map appears to retain about 15 MB of memory, and since we are using the `@DirtiesContext` in some of our tests, after running through a bunch of our tests, we have over 2 GB of memory held in this map due to this leak. As a result, our tests are failing with an OutOfMemory error.  Note that it also appears that a copy of the Hibernate session factory object is leaked whenever the `@DirtiesContext` annotation is used as well.
