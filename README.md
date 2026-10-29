- [CS 1632 - Software Quality Assurance](#cs-1632---software-quality-assurance)
  * [Overview](#overview)
  * [Background](#background)
  * [Compiling and Running](#compiling-and-running)
    + [Luck Mode](#luck-mode)
    + [Skill Mode](#skill-mode)
    + [Text UI Mode](#text-ui-mode)
  * [What to do](#what-to-do)
    + [Task 1: Write Unit Tests for BeanTest to drive Bean development](#task-1-write-unit-tests-for-beantest-to-drive-bean-development)
    + [Task 2: Write Integration Tests for BeanCounterLogicTest to drive BeanCounterLogic development](#task-2-write-integration-tests-for-beancounterlogictest-to-drive-beancounterlogic-development)
    + [Task 3: Generate Paths for Different Machine Configurations in JPFJUnitTest.java](#task-3-generate-paths-for-different-machine-configurations-in-jpfjunittestjava)
    + [Task 4: Write Property-based tests in JPFJUnitTest.java](#task-4-write-property-based-tests-in-jpfjunittestjava)
    + [Task 5: Add an Extra Property-based Test to JPFJUnitTest.java](#task-5-add-an-extra-property-based-test-to-jpfjunittestjava)
    + [Task 6: Linting and Auditing](#task-6-linting-and-auditing)
    + [Task 7: Manual System Testing](#task-7-manual-system-testing)
- [Grading](#grading)
- [Submission](#submission)
- [GradeScope Feedback](#gradescope-feedback)
- [Resources](#resources)
- [Extra Credit](#extra-credit)
  * [General Rules](#general-rules)
  * [Requirements Specification and Test Plan (Exercise 1)](#requirements-specification-and-test-plan-exercise-1)
  * [Code Coverage (Exercise 2)](#code-coverage-exercise-2)
  * [Behavior-Driven Development (Exercise 3)](#behavior-driven-development-exercise-3)
  * [CI Pipeline (Exercise 8 Part 1)](#ci-pipeline-exercise-8-part-1)

# CS 1632 - Software Quality Assurance
Fall Semester 2026 - Final Project

* DUE: December 4 (Friday), 2026 11:59 PM

Due to the grading deadline, there is no late submission.

Please use the link posted on the Teams Final Project channel to accept this
project and create your repository.

## Overview

For this final project, you will develop a full-fledged GUI program
(with the help of some skeleton code), along with various tests.  Don't
worry if you don't know GUI programming --- that part has already been done
for you.  In addition, there is a [Text UI mode](#text-ui-mode) that you can
use to manually test and debug your program.

This project brings together many of the techniques you have learned this
semester: test-driven development (TDD), unit and integration testing with
mocking and seeding to control randomness, linting and pattern-based bug
finding with CheckStyle and SpotBugs, model checking with Java PathFinder
(JPF), and manual system testing.  The GradeScope autograder will do extensive
testing to test various features of the software.  If any of the tests fail,
that means you have not thoroughly tested your software.  You can also earn up
to 10% [extra credit](#extra-credit) by applying techniques from earlier
exercises that this project does not already require.

Since JPF requires Java 11, please make sure you have Java 11 running on your
machine, just like for Exercise 7.  If you need a refresher on how to set it
up, or how to tell when you are running the wrong Java version, see the
README.md of sample_code/jpf_example/ in the course repository.

## Background

The bean counter is a device for statistics experiments devised by English
scientist Sir Francis Galton. It consists of an upright board with evenly
spaced pegs in a triangular form.  Beans are dropped from an opening at the top
of the board. Every time a bean hits a peg, it has a 50% chance of falling to
the left or to the right.  In this way, each bean takes a random path and
eventually falls into one of the slots at the bottom of the board.  After all
the beans fall through, the number of beans in each slot is counted.

See the following link for a more detailed description of the machine:
https://en.wikipedia.org/wiki/Bean_machine.

The bean counter had two contributions to statistics by demonstrating the following:
1. When the sample size is large enough, a [binomial distribution](https://en.wikipedia.org/wiki/Binomial_distribution) approaches a [bell curve](https://en.wikipedia.org/wiki/Normal_distribution).
2. It also demonstrated a phenomenon named [regression to the mean](https://en.wikipedia.org/wiki/Regression_toward_the_mean).

Regression to the mean had been (and still is) a source of numerous scientific
misconceptions.  People make conjectures all the time about all types of things
and provide reasons for it.

1. Why is my favorite sports team performing in a mediocre way when it won the championships last year?  Because my favorite player was traded.
2. Why did the crime rate in my city fall down to the national average?  Due to better policing.
3. Why did a student who did exceptionally well on the midterms perform just about average on the finals?  Because the student slacked off.

People always look for reasons for changes in data.  But often the reason
cannot be explained, because there was no reason for the change to begin with.
The change in data can just be due to a statistical anomaly called "regression
to the mean".  For example, an answer to question 3 can simply be that the
student was exceptionally lucky during the midterms (she guessed all multiple
choices and she got them all correct).  In the finals, her luck just wore off
and she got what she deserved.  This is called regression to the mean.
When a data point is on the extremes of the bell curve, it is often not because
there is anything special about that data point, it is because the laws of
probability worked in favor of it (or against it, depending on context) for
that particular trial.  If that's the case, chances are that the data point
will move to the mean in the next trial.

Now if the exceptional score was due to skill, then the regression would not
happen unless there was a regression in skill.  The problem is, it is very hard
to tell whether something was due to luck or skill just by looking at the
results, hence the numerous misconceptions.

## Compiling and Running

The program simulates a bean machine with a configurable number of slots at
the bottom.  The examples below use 10 slots (numbered 0-9).

Let's first compile the program by invoking Maven compile:

```
mvn compile
```

The program is executed with three or four commandline arguments:

```
$ java -cp target/classes edu.pitt.cs.BeanCounterGUI
Usage: java BeanCounterGUI <slot count> <number of beans> <luck | skill> [debug]
Example: java BeanCounterGUI 10 400 luck
```

The slot count and the number of beans must both be greater than 0.  The third
argument "luck" or "skill" decides whether individual beans will use luck or
skill in navigating the bean machine.  The optional fourth argument "debug"
prints the state of the machine to the console after every step, in the same
format as the [Text UI](#text-ui-mode).

Let's do some exploratory testing.  You could use the class files that you have
just compiled, but the app doesn't do much at this point because the internal
logic has not yet been implemented (by you).  Instead, let's use a reference
implementation (that I wrote) named BeanCounterSolution.jar.

### Luck Mode

In luck mode, the bean counter operates conventionally as originally built by
Galton: a bean has an equal chance of going left or right on a peg.  So where
the bean lands at the bottom is purely due to luck.  Hence, you would expect
the beans to be heavily susceptible to regression to the mean.  Try the following:

1. Run BeanCounterSolution.jar in luck mode:
```
java -jar BeanCounterSolution.jar 10 500 luck
```
2. Press the "Fast" button to fast-forward to the end.
3. Note the average (should be close to 4.5 = (0 + 9) / 2).
4. Press the "Upper Half" button to just take the upper half of the sample.
5. Note the average (now it should be much higher since it's the upper half).
6. Press the "Collect" button to scoop all the beans and bring them to the top.
7. Press the "Fast" button to restart the machine.
8. Note the average is again close to 4.5.

You have just observed regression to the mean.  You took the upper half of the
class, but when they were put through the exam again, they scored just about
average.  Did they slack off in the second exam?  No, they were just no better
than the other students to begin with.

To run your own (currently incomplete) code, you will be doing:

```
java -cp target/classes edu.pitt.cs.BeanCounterGUI 10 500 luck
```

### Skill Mode

In skill mode, the beans choose direction based on pure skill.  Each bean is
assigned a skill level from 0-9 on creation according to a bell curve with
average 4.5 and standard deviation of about 1.58 (the exact formula is in the
Javadoc comment of SkilledBeanImpl.java).  A skill level of 9 means the bean
always makes the "right" choices (pun intended).  That means the bean will
always go right when a peg is encountered, resulting in it falling into the
rightmost 9th slot. A skill level of 0 means that the bean will always go left,
resulting in it falling into the leftmost 0th slot. For the in-between skill
levels, the bean will first go right then left. For example, for a skill level
of 7, the bean will go right 7 times then go left twice.  So where the bean
lands at the bottom would be determined entirely by the skill level of the
bean.  Try the following:

1. Run BeanCounterSolution.jar in skill mode:
```
java -jar BeanCounterSolution.jar 10 500 skill
```
2. Press the "Fast" button to fast-forward to the end.
3. Note the average (should be close to 4.5 = (0 + 9) / 2).
4. Press the "Upper Half" button to just take the upper half of the sample.
5. Note the average (now it should be much higher since it's the upper half).
6. Press the "Collect" button to scoop all the beans and bring them to the top.
7. Press the "Fast" button to restart the machine.
8. Note the average is exactly the same as the average noted in Step 5.

You will observe that the average does not change at all when you repeat the
experiment with the upper half of the samples.  There is no regression to the
mean because the results are pre-determined by skill level.  In this case, the
student performed well on the first exam because they were actually skilled!

Also, you will notice the slots filling sequentially one by one in the second
run.  This is a side-effect of the slots at the bottom being collected one by
one when the Collect button is pressed.  All the beans in one slot have the same
skill level so the beans naturally get sorted out as a result of the
collection.

To run your own (currently incomplete) code, you will be doing:
```
java -cp target/classes edu.pitt.cs.BeanCounterGUI 10 500 skill
```

### Text UI Mode

There is also a text user interface (Text UI) that drives the same bean
counter logic from the console.  It takes the same commandline arguments as
the GUI.  You can invoke the solution's Text UI by doing:

```
java -cp BeanCounterSolution.jar edu.pitt.cs.BeanCounterTextUISolution 10 500 luck
```

This is the usage information that gets printed when you don't pass any arguments:

```
Usage: java BeanCounterTextUI slot_count bean_count <luck | skill> [debug]
Example: java BeanCounterTextUI 10 400 luck
Example: java BeanCounterTextUI 20 1000 skill debug
```

Once started, the Text UI shows a list of commands and prompts you for one.
After every command other than Q, it shows the slot bean counts, the number of
remaining beans, and the average.  For example, here is a session with 5 slots
and 10 beans in which the user typed S and then Q:

```
$ java -cp BeanCounterSolution.jar edu.pitt.cs.BeanCounterTextUISolution 5 10 luck
S - Start the bean simulation
L - Lower half
U - Upper half
C - Collect beans and prepare for the next run
R - Reset machine to initial state
Q - Quit the program
Enter command: S (start), L (lower half), U (upper half), C (collect), R (reset), Q (quit):
S

Slot bean counts:
   0   3   4   2   1
Remaining: 0
Average: 2.1
Enter command: S (start), L (lower half), U (upper half), C (collect), R (reset), Q (quit):
Q

```

The optional debug argument enables verbose output that prints the state of
the bean counter after each step.  This makes debugging easier as it lets you
test your bean counter logic in isolation from the GUI.

To run your own (currently incomplete) code, you will be doing:
```
java -cp target/classes edu.pitt.cs.BeanCounterTextUI 10 500 luck debug
```

## What to do

Here is a list of files and their contents:

* BeanCounterLogicImpl.java - The core logic of the bean machine.  Maintains a
  list of beans that fall one step in the machine whenever advanceStep() is
called.  Maps beans into a logical coordinate system. (**modify**)

* LuckyBeanImpl.java, SkilledBeanImpl.java - The Bean implementations for lucky
  and skilled beans respectively.  Maintains the x-coordinate and y-coordinate
of the bean as it advances through the machine.  The Random object that gives
randomness to the movement of the bean is injected in the constructor for
easier testing. (**modify**)

* JPFJUnitTest.java - An integration test for the BeanCounterLogicImpl class
  run on top of JPF.  You are asked to exhaustively test different machine
configurations (see [Task 3](#task-3-generate-paths-for-different-machine-configurations-in-jpfjunittestjava))
and all random outcomes of each machine. (**modify**)

* BeanCounterLogicTest.java - An integration test for the BeanCounterLogicImpl
  class run on the JUnit framework.  An integration test was chosen over a unit
test because, in order to do unit testing, you would have to mock the beans and
the beans are impossible to mock as their states need to change during machine
operation.  In order to do deterministic testing, either a seeded Random number
generator is used (for lucky beans) or a mock Random object is used (for
skilled beans). (**modify**)

* BeanCounterTextUITest.java - A system test for the Text UI.  It feeds
  commands to the Text UI through standard input and checks what it prints to
standard output. (**modify**)

* BeanTest.java - A unit test for the Bean class.  To keep the test
  deterministic, a mock Random object is used to control the randomness.
(**modify**)

* BeanCounterLogic.java - The public interface of BeanCounterLogic.  The
  createInstance method creates an instance of BeanCounterLogicImpl (your
code), BeanCounterLogicBuggy (a buggy implementation), or
BeanCounterLogicSolution (the solution implementation) depending on
InstanceType.  Do not modify.

* Bean.java - The public interface of Bean.  The createInstance method creates
  an instance of your bean, a buggy bean, or a solution bean depending on
InstanceType.  Do not modify.

* BeanCounterGUI.java - Contains the main method for the GUI of the program.
  Checks the commandline arguments and creates a MainFrame.

* BeanCounterTextUI.java - Contains the main method for the Text UI of the
  program.  Checks the commandline arguments, reads and runs commands, and
prints the state of the machine.  The GUI also uses it to print the debug
output and to format the average.

* MainFrame.java - Contains the MainPanel and the ButtonPanel.

* MainPanel.java - The main display of the program where all the bean machine
  animations happen.  Renders the state of BeanCounterLogic on to the window
coordinate system (the "physical" coordinates).  In the process the logical
coordinates of Beans are translated to physical coordinates.

* <Some>Button.java - GUI buttons at the bottom of the main frame, along with
  event handlers when the button is pressed.

* BeanCounterSolution.jar - The solution implementation of bean counter.

* BeanCounterBuggy.jar - A buggy implementation of bean counter.

* jpf-core/runTest.bat and jpf-core/runTest.sh - Scripts to run JPFJUnitTest on
  top of JPF.  

Take care that you limit your modifications to the files that are marked
**modify**, as all the other files will be ignored in your submission (the
only exception is [extra credit](#extra-credit) work, which is graded by
hand).  Also,
take care that you do not change the public interfaces of BeanCounterLogic and
Bean as GradeScope relies on them.

I expect you to employ test-driven development (TDD) for this project and fully
embrace it.  I can guarantee you that it will shorten development time.  You
are going to write the tests anyway.  Why not write them at the beginning when
they will be the most useful?  Besides, writing the tests will give you a much
better understanding of how test application is supposed to behave before
starting the implementation.  I will lay down the steps, roughly in the order
you should perform them.

### Task 1: Write Unit Tests for BeanTest to drive Bean development

Start by completing the tests in
[BeanTest.java](src/test/java/edu/pitt/cs/BeanTest.java).  Please start from
the @Before setUp() method to create the beans for testing.  Please use the
Bean.createInstance method to create the beans (the GradeScope autograder
relies on you to do that).  You have the option to pass in InstanceType.IMPL,
InstanceType.SOLUTION, or InstanceType.BUGGY to Bean.createInstance to create a
bean from your implementation classes (either LuckyBeanImpl or
SkilledBeanImpl), a solution bean, or a buggy bean.  As previously, you can
test against the solution and buggy beans to gain confidence that your test
is working properly before applying it to your implementation to drive your
development (TDD).  All tests should pass the solution and all tests should
fail the buggy Bean, with the exception of testConstructor and testReset.  On
your final submission, your test should be testing InstanceType.IMPL, since by
then your implementation would be complete.

Now, when testing Beans, there is a complication: Beans in their natural state
tend to behave wildly and randomly bound left and right, which is not amenable
to reliable testing.  This randomness comes from a Random object used to
produce that nondeterminism.  Fortunately, the software architect had the
foresight to make the code testable by injecting the Random object into the
Bean.createInstance method.  This allows you to pass in a Random object that
you can control to make it deterministic.  We learned two ways to do that: 1)
mock the Random object or 2) seed the Random object.  In this test, we are
going to choose the former.  Please stub the nextInt and nextGaussian methods
for the mock Random as specified in the // TODO comments.

When writing the tests, it helps to understand the bean machine coordinate
system to understand why the given postconditions are the postconditions.  The
Javadoc comment on top of BeanCounterLogicImpl provides you with a visual
representation of the coordinate system for a machine with 4 slots.

You can perform Maven testing as before using the commandline:

```
mvn test
```

Or using the VSCode Testing extension (the flask icon).

After you are satisfied with your tests, use them to drive the development of
LuckyBeanImpl.java and SkilledBeanImpl.java.
 
### Task 2: Write Integration Tests for BeanCounterLogicTest to drive BeanCounterLogic development

Next complete the tests in
[BeanCounterLogicTest.java](src/test/java/edu/pitt/cs/BeanCounterLogicTest.java).
Note that we are doing integration tests and not mocking the Bean objects,
which would be needed for unit testing BeanCounterLogic.  This is not out of
choice but necessity.  In essence, the Bean and BeanCounterLogic objects are
too tightly coupled with each other to allow unit testing.  Each Bean
experiences numerous state changes as its X and Y coordinates change while
streaming down the machine, and BeanCounterLogic relies on those state changes
to happen to display the Bean at its correct location.  This exposes the
limitations of Mockito as there is no way to emulate that kind of complex
behavior using stubbing (well, to be honest with you [there
is](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#13),
but the resulting testing infrastructure is often too complex and too fragile
to make it worth the effort, which is why we did not discuss it in this class).

If you ask Mockists (those who adhere to the central tenets of unit testing
through mocking), this type of integration testing is a code smell.  However,
if you ask Classicists (the opposing camp who do not like mocking), this type
of integration testing comes naturally.  The argument is that you already
verified Bean through BeanTest, so it should be safe to use real Beans for
purposes of testing BeanCounterLogic.  Of course, the counter argument is that
that is exactly where the problem lies: it prevents BeanCounterLogic to be
tested and developed ahead of Bean.  A thoughtful discussion about the merits
of the Mockist and Classicist perspectives is in [this Medium article by Adrian
Booth](
https://medium.com/@adrianbooth/test-driven-development-wars-detroit-vs-london-classicist-vs-mockist-9956c78ae95f).

Now, if we are going to use real Beans, then we have the same problem with
nondeterminism as in BeanTest.  This time, we are going to both mock and seed
the Random objects.  Please fill in the // TODO comments in the @Before setUp()
method to do one or the other as instructed in the comment.

Again, please use InstanceType.SOLUTION and InstanceType.BUGGY in
BeanCounterLogic.createInstance and Bean.createInstance to verify your testing
against the solution and buggy versions.  If you test against the buggy
version, all tests should fail with the exception of testReset().

Next, complete the tests in
[BeanCounterTextUITest.java](src/test/java/edu/pitt/cs/BeanCounterTextUITest.java).
These are system tests that run the Text UI end to end.  Each test needs to
feed commands to the Text UI by replacing standard input (System.setIn) with a
stream containing the commands, capture what the Text UI prints by replacing
standard output (System.setOut) with a stream you can read back, call
BeanCounterTextUI.main(String[]), and then restore the original standard input
and output.  Then check the captured output against the postconditions in the
test's Javadoc comment.  To test against the solution and buggy versions, call
BeanCounterTextUISolution.main(String[]) or
BeanCounterTextUIBuggy.main(String[]) instead of
BeanCounterTextUI.main(String[]).  If you test against the buggy version,
testStartCommandDisplaysFinalCounts() should fail, but
testInvalidInputRedisplaysCommandList() and
testUsageMessageNamesRunnableEntryPoint() pass, because handling commands and
printing the usage message are done by Text UI code that is the same in all
three versions.

After you are confident with your tests, please use them to drive the
development of BeanCounterLogicImpl using InstanceType.IMPL instances.  Again,
please pay close attention to the Javadoc comment on top of BeanCounterLogicImpl
before starting.

If you want to see the bugs in BeanCounterLogicBuggy for yourself, you can
run the buggy Text UI and enter the S command:

```
java -cp BeanCounterBuggy.jar edu.pitt.cs.BeanCounterTextUIBuggy 10 400 luck
```

Then you will get something like (with some random variations):

```
Slot bean counts:
   0   6  26  69 100  86  58  18   3   0
Remaining: 0
Average: 5.35
```

Note that now things look pretty normal, but if you look closely, you will
notice that if you add up all the beans, they don't sum up to 400, even though
there are no beans remaining.  Strange!  Now you might want to try this on a
smaller scale with 5 slots and 10 beans:

```
java -cp BeanCounterBuggy.jar edu.pitt.cs.BeanCounterTextUIBuggy 5 10 luck
```

This time, you may get something like:

```
Slot bean counts:
   1   2   1   4   2
```

Now, the beans add up to 10.  Where did the bug go?  As you can see, it is
going to be very hard to find these kinds of bugs with just plain JUnit testing
unless you know exactly what you are looking for and even then you need a
certain amount of luck.  To find these kinds of bugs reliably we need Java
PathFinder (JPF).

### Task 3: Generate Paths for Different Machine Configurations in JPFJUnitTest.java

By now we are somewhat confident that we have a mostly working application.
But as noted earlier with the buggy implementation, there still might be bugs
lurking because we did not test all different combinations of machine
configurations and also did not test all the random variations that a machine
goes through even for a single configuration.  For that we need model checking
using JPF.

Notice that I have intentionally separated out the logic part of the program
from the GUI.  This was done to make model checking easier.  Model checking a
GUI is tricky and so is a multi-threaded event-driven program like
BeanCounterGUI.  Yes, JPF can model check even multi-threaded programs by
exhaustively going through all the interleavings.  But it is complicated and it
takes a long time because it has to go through many more states.  So we will
just check the core logic (BeanCounterLogic), which is the important part
anyway.

Just like for Exercise 7, we are going to use the runTest.bat or runTest.sh
scripts to run our JUnit class on top of JPF.  JPF runs your compiled classes,
so make sure you have compiled both the application and the test classes
first, either with "mvn compile" and "mvn test-compile" or by letting VSCode
compile them automatically.  Then cd into jpf-core/ before running scripts:

```
cd jpf-core
```

Then, for Windows:

```
.\runTest.bat edu.pitt.cs.JPFJUnitTest
```

For Mac/Linux:

```
bash runTest.sh edu.pitt.cs.JPFJUnitTest
```

Initially, since all tests are empty, it should show:

```
...
(snipped for brevity)

......................................... execution of testsuite: edu.pitt.cs.JPFJUnitTest SUCCEEDED
.... [1] testAdvanceStepBeanCount: Ok
.... [2] testAdvanceStepCoordinates: Ok
.... [3] testAdvanceStepPostCondition: Ok
.... [4] testReset: Ok
.... [5] testLowerHalf: Ok
.... [6] testUpperHalf: Ok
.... [7] testCollect: Ok
......................................... tests: 7, failures: 0, errors: 0

tested classes: 1, passed: 1
```

The first thing you should do in JPFJUnitTest.java is to modify the setUp()
method to insert Verify calls to enumerate all possible user input values.  The
three input values relevant here are: slot count, bean count, and the boolean
value isLuck ("luck" or "skill" mode).  Once you insert the Verify calls, JPF
will explore each combination of input values.  As described in the "// TODO"
comment in the setUp() method, verify 1-5 slot count, 0-3 bean count, and both
"luck" and "skill" modes.  We will not test slot count 0 because it is not a
legal input (see FUN-USAGE in [requirements.md](requirements.md)): a machine
with no slots has nowhere for the beans to land.  Bean count 0 is not a legal
input for the GUI or the Text UI either, but BeanCounterLogic must still handle
an empty array of beans correctly, so we do test it.
Although the range of values is not exhaustive, these are enough values to give
us confidence that our machine works, while ensuring that JPF terminates within
a few seconds.

The testReset() method contains a println statement inserted in order to
demonstrate to you all the combinations of input values JPF explores.  Let's
see what it prints out initially without Verify API calls.  Run the tests again
and you should obtain output like the following:

```
...
(snipped for brevity)

......................................... testing testReset()
  running jpf with args:
JavaPathfinder core system v8.0 (rev 1a704e1d6c3d92178504f8cdfe57b068b4e22d9c) - (C) 2005-2014 United States Government. All rights reserved.


====================================================== system under test
edu.pitt.cs.JPFJUnitTest.runTestMethod()

====================================================== search started: 3/24/25, 6:26 AM
[WARNING] orphan NativePeer method: jdk.internal.misc.Unsafe.getUnsafe()Lsun/misc/Unsafe;
Failure in (slotCount=1, beanCount=0, isLucky=false):
Failure in (slotCount=1, beanCount=0, isLucky=true):
Failure in (slotCount=1, beanCount=1, isLucky=false):
Failure in (slotCount=1, beanCount=1, isLucky=true):
Failure in (slotCount=1, beanCount=2, isLucky=false):
Failure in (slotCount=1, beanCount=2, isLucky=true):
Failure in (slotCount=1, beanCount=3, isLucky=false):
Failure in (slotCount=1, beanCount=3, isLucky=true):
Failure in (slotCount=2, beanCount=0, isLucky=false):
Failure in (slotCount=2, beanCount=0, isLucky=true):
Failure in (slotCount=2, beanCount=1, isLucky=false):
Failure in (slotCount=2, beanCount=1, isLucky=true):
Failure in (slotCount=2, beanCount=2, isLucky=false):
Failure in (slotCount=2, beanCount=2, isLucky=true):
Failure in (slotCount=2, beanCount=3, isLucky=false):
Failure in (slotCount=2, beanCount=3, isLucky=true):
Failure in (slotCount=3, beanCount=0, isLucky=false):
Failure in (slotCount=3, beanCount=0, isLucky=true):
Failure in (slotCount=3, beanCount=1, isLucky=false):
Failure in (slotCount=3, beanCount=1, isLucky=true):
Failure in (slotCount=3, beanCount=2, isLucky=false):
Failure in (slotCount=3, beanCount=2, isLucky=true):
Failure in (slotCount=3, beanCount=3, isLucky=false):
Failure in (slotCount=3, beanCount=3, isLucky=true):
Failure in (slotCount=4, beanCount=0, isLucky=false):
Failure in (slotCount=4, beanCount=0, isLucky=true):
Failure in (slotCount=4, beanCount=1, isLucky=false):
Failure in (slotCount=4, beanCount=1, isLucky=true):
Failure in (slotCount=4, beanCount=2, isLucky=false):
Failure in (slotCount=4, beanCount=2, isLucky=true):
Failure in (slotCount=4, beanCount=3, isLucky=false):
Failure in (slotCount=4, beanCount=3, isLucky=true):
Failure in (slotCount=5, beanCount=0, isLucky=false):
Failure in (slotCount=5, beanCount=0, isLucky=true):
Failure in (slotCount=5, beanCount=1, isLucky=false):
Failure in (slotCount=5, beanCount=1, isLucky=true):
Failure in (slotCount=5, beanCount=2, isLucky=false):
Failure in (slotCount=5, beanCount=2, isLucky=true):
Failure in (slotCount=5, beanCount=3, isLucky=false):
Failure in (slotCount=5, beanCount=3, isLucky=true):

====================================================== results
no errors detected

====================================================== search finished: 3/24/25, 6:26 AM
......................................... testReset: Ok

(snipped for brevity)
...
```

You can see how JPF exhaustively tries out all possible combinations of machine
configurations.  Since the println was just for demonstration purposes, please
remove it in your final submission and replace it with the actual test.

### Task 4: Write Property-based tests in JPFJUnitTest.java

Complete [JPFJUnitTest.java](src/test/java/edu/pitt/cs/JPFJUnitTest.java) by
replacing the // TODO comments.  Pay close attention to the invariants you are
asked to test described in the Javadoc comment above each @Test method.

Just like in Exercise 7, use only the assertions that TestJPF provides, which
JPFJUnitTest inherits: assertEquals, assertTrue, assertFalse, assertNull,
assertNotNull, assertSame, and fail.  JUnit's org.junit.Assert class is not on
JPF's classpath, so calling one of its methods inside JPF throws a
ClassNotFoundException instead of checking anything.  I recommend that you
always insert the failString that I initialized for you as the first argument
of every assertion call so that you get that as part of your failure
message.  The failString describes the machine configuration that
is being currently tested and it will tell you which configuration led to the
failure.

Again, you can pass InstanceType.SOLUTION and InstanceType.BUGGY to
BeanCounterLogic.createInstance and Bean.createInstance to verify your tests
against the solution and buggy implementations.  The expectation is that all
solution tests pass and all buggy tests fail with the exception of testReset().
After you verify your tests, try running them on InstanceType.IMPL to verify
your own implementation.

### Task 5: Add an Extra Property-based Test to JPFJUnitTest.java

_Add one more test case of your own that helps you verify some invariant
property_.  Add that test at the very end.  Make sure you test a new invariant
that has not yet been tested (as in all the previous tests can pass and the new
test still fail).  There are many many invariants yet to be tested: be
creative!  Remember, it has to be an invariant.  If you write a test applicable
to a specific input combination using if statements, it doesn't count.  Make
sure that the test case is well documented with a Javadoc comment just like
other methods.

### Task 6: Linting and Auditing

Run the CheckStyle linter and the SpotBugs tool regularly while and after
coding.  Use the Maven site generation phase to generate the reports:

```
mvn site
```

Then open target/site/index.html and look under Project Reports.  Note that
pom.xml configures both tools to check your test classes as well as your
application classes, so warnings in your tests count too.

### Task 7: Manual System Testing

Even after doing model checking, you still need to verify that the program
"looks" right end-to-end in the GUI.  This is hard to do using automated
testing so you will do manual testing for this.  Refer to the
[requirements.md](requirements.md) file for the features that need testing.
You are not required to write a test plan for this (writing one is one of the
[extra credit](#extra-credit) options), but you will be graded on how closely
your GUI follows the requirements, as demonstrated by BeanCounterSolution.jar.

# Grading

* GradeScope autograder - 79%
* Verify API used properly - 5%
* Extra test checking new invariant - 5%
* Visual inspection of GUI application - 11%
* Extra credit (optional) - up to 10% (see [Extra Credit](#extra-credit))

The maximum project score is therefore 110%, and points above 100% carry over
to your course grade.  Please review the rubric in GradeScope for details.  I
reserve the right to deduct points for any attempt to try to game GradeScope
into giving you more points.  Also, plagiarism will get you a zero for the
project.

# Submission

When you are done, commit and push your changes to your repository and submit
it to GradeScope at the **Final Project** link.  Don't forget to add your
partner, if you have one.  Once you submit, GradeScope will run the autograder
to grade you and give feedback.  If you get deductions, fix your code based on
the feedback and resubmit.  Repeat until you don't get deductions.  If you did
any extra credit, it is graded by hand from the same submission, so there is no
separate link.

# GradeScope Feedback

It is encouraged that you submit to GradeScope early and often.  Please use the
feedback you get on each submission to improve your code!

The GradeScope autograder works in 6 phases:

1. BeanTest Grading (13 points)

   This phase runs your BeanTest against BeanSolution (the solution
implementation of Bean) and BeanBuggy (a buggy implementation of Bean).  All
your tests are expected to pass for BeanSolution and all your tests are
expected to fail for BeanBuggy, with the exception of testConstructor and
testReset.

1. BeanCounterLogicTest Grading (14 points)

   This phase runs your BeanCounterLogicTest against BeanCounterLogicSolution
and BeanCounterLogicBuggy.  Again, all your tests are expected to pass on the
solution and all your tests are expected to fail on the buggy version except
for testReset.

1. JPFJUnitTest Grading (21 points)

   This phase runs your JPFJUnitTest against BeanCounterLogicSolution and
BeanCounterLogicBuggy.  Again, all your tests are expected to pass on the
solution and all your tests are expected to fail on the buggy version except
for testReset.  The latter part of this section is composed of output from Java
PathFinder, to give you a hint on what the problem might be.  There are
three outputs respectively delimited by the header "[[[ JPFJUnitTest on
BeanCounterLogicSolution output ]]]", "[[[ JPFJUnitTest on
BeanCounterLogicBuggy output ]]]", and "[[[ JPFJUnitTestSolution on
BeanCounterLogicBuggy output ]]]".

1. CheckStyle (5 points)

   This phase runs the CheckStyle tool on your source code.  Each warning will
get you a 0.5 point deduction.

1. SpotBugs (5 points)

   This phase runs the SpotBugs tool on your class files.  Each warning will
get you a 0.5 point deduction.

1. BeanCounterLogic Grading (21 points)

   This phase runs the solution versions of BeanCounterLogicTest and
JPFJUnitTest on your BeanCounterLogicImpl implementation.  Both JUnit testing
and JPF model checking is used to thoroughly verify your code.

Please read the GradeScope feedback to get a hint on what the problem is.  For
each of the problems, you should be able to reproduce it by creating SOLUTION
or BUGGY objects using createInstance as explained in previous sections.

# Resources

* Java PathFinder manual:  
https://github.com/javapathfinder/jpf-core/wiki/How-to-use-JPF
http://javapathfinder.sourceforge.net/

* Java PathFinder Verify API:  
https://github.com/javapathfinder/jpf-core/wiki/Verify-API-of-JPF

* CheckStyle reference:  
https://checkstyle.org/checks.html

* SpotBugs reference:  
https://spotbugs.readthedocs.io/en/latest/bugDescriptions.html

# Extra Credit

You can earn up to two extra credits, each worth 5% of the project grade, by
applying a testing or specification technique from one of this semester's
exercises that the project does not already require.  With both extra credits,
the maximum project score is 110%, and points above 100% carry over to your
course grade.

The project already requires TDD, unit and integration testing with JUnit and
Mockito, linting and bug finding with CheckStyle and SpotBugs, and model
checking with JPF, so those do not count.  Playwright (Exercise 4) does not
apply because BeanCounter is not a web application, and ASAN and TSAN
(Exercise 6) apply only to C and C++ programs.  Performance testing
(Exercise 5) does not apply either, because BeanCounter has no performance
problems to find, and neither do Docker containers (Exercise 8 Part 2),
because BeanCounter is a Java program that Maven already builds and runs
anywhere Java is installed.  That leaves the four techniques in the
subsections below.  Each subsection lists what a complete submission
must include.  The requirements are there so that each 5% takes a comparable
amount of work regardless of which technique you choose, so a submission that
misses some of them will receive partial credit.

## General Rules

1. Choose at most two techniques.

1. Create an EXTRA_CREDIT.md file at the root of your repository.  For each
   technique, list: the technique, the exact commands to run it starting from a
   fresh clone of your repository, where its artifacts are (reports,
   screenshots, workflow runs), and a short paragraph on what the technique told
   you about BeanCounter: the bugs it found, the gaps it exposed, or why it
   found nothing.  If EXTRA_CREDIT.md lists more than two techniques, only the
   first two are graded.

1. Put extra credit tests in new test classes, not in BeanTest.java,
   BeanCounterLogicTest.java, or JPFJUnitTest.java.  The autograder expects
   every test in those three files to fail on the buggy implementation (with
   the exceptions noted in [GradeScope Feedback](#gradescope-feedback)), which
   most extra credit tests will not.

1. You may modify pom.xml to add the dependencies and plugins you need.  The
   autograder ignores pom.xml, so make sure the required tasks still work with
   the original pom.xml.

1. Extra credit code is held to the same CheckStyle and SpotBugs standards as
   the rest of your code: "mvn site" should report no warnings for it.

## Requirements Specification and Test Plan (Exercise 1)

Task 7 has you manually test the GUI without a test plan.  For this extra
credit, you write the specifications and the test plan using Sphinx-Needs,
just like for CatScale in Exercise 1.  Treat
[requirements.md](requirements.md) as the notes you jotted down after talking
to the stakeholders.

1. Create a docs/ folder modeled on project_cat_scale/ in Exercise 1, with
   conf.py, index.rst, requirements.rst, test_plan.rst, and traceability.rst,
   along with a requirements.txt listing the Python packages needed to build
   it.  Building with "sphinx-build -W --keep-going . _build/html" from inside
   docs/ must succeed with no warnings.

1. In requirements.rst, write one req for each of the 34 requirements in
   requirements.md, with the requirement's name (for example,
   FUN-MACHINE-LOWER-HALF) in its title.  Under each req, write specs that
   cover every equivalence class of the requirement.  For example,
   FUN-MACHINE-LOWER-HALF behaves differently for an even number of slotted
   beans, an odd number of slotted beans, and no slotted beans at all.

1. In test_plan.rst, write test cases with preconditions, execution steps,
   and postconditions that cover every spec of FUN-USAGE and of the FUN-GUI-*
   requirements.  You may leave the FUN-MACHINE-* and FUN-TEXTUI-*
   requirements without test cases, since your JUnit and JPF tests already
   verify much of them, but you must still fully specify them.

1. The forward and backward traceability matrices on the Traceability page
   must show no orphaned test cases and no uncovered specs for the
   requirements listed in the previous step.

1. Run every test case against both your implementation and
   BeanCounterSolution.jar, and record the results in EXTRA_CREDIT.md.  If a
   test case fails on your implementation, fix your code.  If it fails on
   BeanCounterSolution.jar, write a defect report for it as a GitHub issue in
   your repository, in the same format as Exercise 1, and list the issue's URL
   in EXTRA_CREDIT.md.

## Code Coverage (Exercise 2)

Your required tests already cover most of the logic and Bean classes, but they
check only two behaviors of the Text UI.  For this extra credit, you measure
and improve coverage the way you did for RentACat in Exercise 2.

1. Add the JaCoCo plugin to pom.xml, copying its configuration from
   Exercise 2's pom.xml, so that "mvn test" generates a report under
   target/site/jacoco/.

1. Create a new JUnit test class SystemsTest.java under src/test/java/edu/pitt/cs
   that tests BeanCounterTextUI.main end to end.  Cover every Text UI behavior
   described in FUN-USAGE, FUN-MACHINE-DEBUG, and the FUN-TEXTUI-*
   requirements, including each kind of invalid argument, each command, and
   the debug output.  Document each test with Preconditions,
   Execution steps, and Postconditions in its Javadoc comment, and assert on
   the system output, as in Exercise 2.

1. Reach at least 90% instruction coverage for each of LuckyBeanImpl.java,
   SkilledBeanImpl.java, BeanCounterLogicImpl.java, and BeanCounterTextUI.java.

1. Save screenshots of the JaCoCo report for those four classes under a
   jacoco_reports/ folder, named luckybeanimpl_jacoco.jpg,
   skilledbeanimpl_jacoco.jpg, beancounterlogicimpl_jacoco.jpg, and
   beancountertextui_jacoco.jpg.

1. In EXTRA_CREDIT.md, list every line that is still not covered in those
   four classes and explain why it is not covered, and why it either cannot
   be covered or is not worth covering.

## Behavior-Driven Development (Exercise 3)

For this extra credit, you specify BeanCounterLogic's behavior as Gherkin
scenarios and automate them with Cucumber, as you did for RentACat in
Exercise 3.

1. Add the Cucumber dependencies to pom.xml, copying them from Exercise 3's
   pom.xml, and add a Cucumber runner class and a new StepDefinitions.java under
   src/test/java/edu/pitt/cs.  Put your feature files under
   src/test/resources/edu/pitt/cs.

1. Write at least three feature files: one for advancing the machine in luck
   and skill modes, one for lowerHalf and upperHalf, and one for collect and
   reset.  Together, they must contain at least 10 scenarios, including at
   least one Scenario Outline with an Examples table of three or more rows and
   at least one Background section.

1. Write the scenarios in the language of the domain ("Given a machine with 5
   slots and 3 skilled beans"), not in terms of method calls.

1. Reuse steps across features with parameters, so that no two step
   definitions differ only in a constant.

1. Keep the scenarios deterministic by seeding or mocking the Random objects,
   as BeanCounterLogicTest does.

1. Declare the InstanceType used by your step definitions as a single
   constant, so it can be switched in one place.  Every scenario must pass on
   InstanceType.SOLUTION and InstanceType.IMPL, and each feature must have at
   least one scenario that fails on InstanceType.BUGGY.  Record which scenarios
   fail on the buggy implementation in EXTRA_CREDIT.md.

## CI Pipeline (Exercise 8 Part 1)

For this extra credit, you add a GitHub Actions workflow that runs all of the
project's checks on every push, like the Maven CI workflow in Exercise 8.

1. Add a workflow under .github/workflows/ that runs on every push and pull
   request to the main branch, using Java 11 since JPF requires it.

1. The workflow must run three kinds of checks: the JUnit tests with "mvn
   test", the JPF tests with the runTest.sh script in jpf-core/ (after "mvn
   test-compile"), and CheckStyle and SpotBugs with "mvn site".  Upload the
   generated site as a workflow artifact.

1. The workflow must fail if any JUnit test fails, any JPF test fails, or
   CheckStyle or SpotBugs reports any warning.  Do not assume that a command
   returns a non-zero exit code when its check fails.  Find out how each one
   reports failure, and wherever a failure does not fail the step on its own,
   add a step that inspects the command's output.  For example, "mvn site"
   does not fail on warnings, but it leaves XML reports under target/ that you
   can check.

1. Prove that each check works.  For each kind of check, create a branch with
   a commit that breaks only that check (for example, switching a JPFJUnitTest
   instance to InstanceType.BUGGY, or adding a CheckStyle warning), and open a
   pull request from it.  In EXTRA_CREDIT.md, link to a failed run for each
   kind of check and to a successful run on the main branch.

1. Enable Dependabot version updates for the Maven dependencies, as in
   Exercise 8.
