package tests;

// (c) Larry Herman, 2022.  You are allowed to use this code yourself, but
// not to provide it to anyone else.

/* The last two public tests have the same results, meaning they should read
 * the same data into their Registrar objects.  They just have the data
 * divided up differently between a different number of data files, so they
 * create different numbers of threads.  To avoid duplicative code, a
 * private helper method declared at the end of this class checks the
 * expected contents of the Registrar object for both of these tests.
 */

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import registrar.Registrar;
import org.junit.*;
import static org.junit.Assert.*;

public class PublicTests {

  // Tests the basic functionality of several methods a modified in this
  // project, by calling dropCourse() on a course that is full (at
  // capacity), and checking that a new student is able to be added after
  // that.  Note that this test does not create or use any threads.
  @Test public void testPublic1() {
    Registrar registrar= new Registrar(5);
    int i, capacity= 10;

    registrar.addNewCourse("CMSC", 132, capacity);

    // add 10 students, so course will be full
    for (i= 0; i < capacity; i++)
      assertTrue(registrar.addToCourse("CMSC", 132, "Ellie" + i,
                                       "Elephant" + i));

    assertTrue(registrar.dropCourse("CMSC", 132, "Ellie0", "Elephant0"));
    assertTrue(registrar.addToCourse("CMSC", 132, "Sheila", "Sheep"));
  }

  // Tests the basic functionality of several methods a modified in this
  // project, by calling dropCourse() on a student who is not registered for
  // an existing course, verifying that it returns false, and ensuring that
  // the number of students is unchanged afterwards.  Note that this test
  // does not create or use any threads.
  @Test public void testPublic2() {
    Registrar registrar= new Registrar(5);

    registrar.addNewCourse("CMSC", 101, 40);
    registrar.addNewCourse("MATH", 510, 20);

    assertTrue(registrar.addToCourse("CMSC", 101, "Ellie", "Elephant"));
    assertTrue(registrar.addToCourse("MATH", 510, "Sheila", "Sheep"));
    assertTrue(registrar.addToCourse("CMSC", 101, "Horace", "Horse"));

    assertFalse(registrar.dropCourse("CMSC", 101, "Cammy", "Camel"));
    assertFalse(registrar.dropCourse("CMSC", 101, "Sheila", "Sheep"));
    assertFalse(registrar.dropCourse("MATH", 510, "Ellie", "Elephant"));

    assertEquals(2, registrar.numStudentsInCourse("CMSC", 101));
    assertEquals(1, registrar.numStudentsInCourse("MATH", 510));
  }

  // Creates one thread, which reads one list of data, which only contains
  // one course addition, just to ensure that one thread can be created and
  // manipulated correctly.
  @Test public void testPublic3() {
    Registrar registrar= new Registrar(5);

    registrar.doRegistrations(Arrays.asList("registrationdata-public03"));

    // check the number of courses is right
    assertEquals(1, registrar.numCourses());
    // should be zero studens, but not -1 if the course was added and exists
    assertEquals(0, registrar.numStudentsInCourse("CMSC", 132));
  }

  // Creates one thread, which reads one list of data, which contains
  // several course additions.
  @Test public void testPublic4() {
    Registrar registrar= new Registrar(5);
    List<String> departments= Arrays.asList("CMSC", "MATH", "BIOE", "ENGL",
                                            "PSYC", "COMM", "ENEE", "KNES");
    List<Integer> courseNumbers= Arrays.asList(131, 132, 250, 101,
                                               100, 184, 115, 217);
    int i;

    registrar.doRegistrations(Arrays.asList("registrationdata-public04"));

    // check the number of courses is right
    assertEquals(8, registrar.numCourses());
    for (i= 0; i < departments.size(); i++)
      // should be zero studens, but not -1 if the course was added and
      // exists
      assertEquals(0, registrar.numStudentsInCourse(departments.get(i),
                                                    courseNumbers.get(i)));
  }

  // Creates one thread, which reads one list of data, which only contains
  // one course addition and one student registration.
  @Test public void testPublic5() {
    Registrar registrar= new Registrar(5);

    registrar.doRegistrations(Arrays.asList("registrationdata-public05"));

    // check the number of courses is right
    assertEquals(1, registrar.numCourses());
    assertEquals(1, registrar.numStudentsInCourse("CMSC", 132));
    assertTrue(registrar.isInCourse("CMSC", 132, "Ellie", "Elephant"));
  }

  // Creates one thread, which reads one list of data, which contains
  // several course additions and several student registrations.
  @Test public void testPublic6() {
    Registrar registrar= new Registrar(5);
    List<String> departments= Arrays.asList("CMSC", "BIOE", "ENGL",
                                            "PSYC", "COMM");
    List<Integer> courseNumbers= Arrays.asList(132, 250, 101, 100, 184);
    List<String> firstNames= Arrays.asList("Ellie", "Sheena", "Amy",
                                           "Manny", "Leo");
    List<String> lastNames= Arrays.asList("Elephant", "Sheep", "Amoeba",
                                          "Manatee", "Leopard");
    int i;

    registrar.doRegistrations(Arrays.asList("registrationdata-public06"));

    // check the number of courses is right
    assertEquals(5, registrar.numCourses());
    // check that each course exists and has one student, and that the
    // correct student is registered for each one
    for (i= 0; i < departments.size(); i++) {
      assertEquals(1, registrar.numStudentsInCourse(departments.get(i),
                                                    courseNumbers.get(i)));
      assertTrue(registrar.isInCourse(departments.get(i),
                                      courseNumbers.get(i),
                                      firstNames.get(i), lastNames.get(i)));
    }
  }

  // Creates two threads, which each read a list of data, each of which
  // contains several course additions and several student registrations.
  @Test public void testPublic7() {
    Registrar registrar= new Registrar(5);
    List<String> departments= Arrays.asList("AMSC", "ASTR", "BIOE",
                                            "CMSC", "COMM", "ENGL",
                                            "IMDM", "ITAL", "PSYC",
                                            "STAT");
    List<Integer> courseNumbers= Arrays.asList(460, 315, 250, 132, 184,
                                               101, 150, 207, 100, 400);
    List<String> firstNames= Arrays.asList("Peggy", "Kortney", "Sheena",
                                           "Ellie", "Leo", "Amy",
                                           "Dolly", "Wally", "Manny",
                                           "Sally");
    List<String> lastNames= Arrays.asList("Penguin", "Koala", "Sheep",
                                          "Elephant", "Leopard", "Amoeba",
                                          "Dolphin", "Walrus", "Manatee",
                                          "Salamander");
    int i;

    registrar.doRegistrations(Arrays.asList("registrationdata-public07a",
                                            "registrationdata-public07b"));

    // check the number of courses is right
    assertEquals(10, registrar.numCourses());
    // check that each course exists and has one student, and that the
    // correct student is registered for each one
    for (i= 0; i < departments.size(); i++) {
      assertEquals(1, registrar.numStudentsInCourse(departments.get(i),
                                                    courseNumbers.get(i)));
      assertTrue(registrar.isInCourse(departments.get(i),
                                      courseNumbers.get(i),
                                      firstNames.get(i), lastNames.get(i)));
    }
  }

  // Creates two threads, which each read a list of data, each of which
  // contains several course additions and several student registrations,
  // with mutiple students taking the same courses.
  @Test public void testPublic8() {
    Registrar registrar= new Registrar(5);
    List<String> departments= Arrays.asList("AMSC", "ASTR", "BIOE",
                                            "CMSC", "COMM", "ENGL",
                                            "IMDM", "ITAL", "PSYC",
                                            "STAT");
    List<Integer> courseNumbers= Arrays.asList(460, 315, 250, 132, 184,
                                               101, 150, 207, 100, 400);
    List<String> firstNames= Arrays.asList("Peggy", "Sally", "Dolly",
                                           "Kortney", "Leo", "Sheena",
                                           "Ellie", "Manny", "Amy",
                                           "Leo", "Amy", "Ellie",
                                           "Dolly", "Peggy", "Kortney",
                                           "Wally", "Manny", "Sheena",
                                           "Sally", "Wally");
    List<String> lastNames= Arrays.asList("Penguin", "Salamander", "Dolphin",
                                          "Koala", "Leopard", "Sheep",
                                          "Elephant", "Manatee", "Amoeba",
                                          "Leopard", "Amoeba", "Elephant",
                                          "Dolphin", "Penguin", "Koala",
                                          "Walrus","Manatee", "Sheep",
                                          "Salamander", "Walrus");
    int i;

    registrar.doRegistrations(Arrays.asList("registrationdata-public08a",
                                            "registrationdata-public08b"));

    // check the number of courses is right
    assertEquals(10, registrar.numCourses());
    // check that each course exists and has two students, and that the
    // correct students are registered for each one
    for (i= 0; i < departments.size(); i++) {
      assertEquals(2, registrar.numStudentsInCourse(departments.get(i),
                                                    courseNumbers.get(i)));
      assertTrue(registrar.isInCourse(departments.get(i),
                                      courseNumbers.get(i),
                                      firstNames.get(i * 2),
                                      lastNames.get(i * 2)));
      assertTrue(registrar.isInCourse(departments.get(i),
                                      courseNumbers.get(i),
                                      firstNames.get(i * 2 + 1),
                                      lastNames.get(i * 2 + 1)));
    }
  }

  // Creates two threads, which each read a larger list of data containing
  // course additions and student registrations.
  @Test public void testPublic9() {
    Registrar registrar= new Registrar(5);

    registrar.doRegistrations(Arrays.asList("registrationdata-public09a",
                                            "registrationdata-public09b"));

    checkTests7and8(registrar);
  }

  // 
  @Test public void testPublic10() {
    Registrar registrar= new Registrar(5);
    List<String> filenames= new LinkedList<>();
    char ch;

    for (ch= 'a'; ch <= 'j'; ch++)
      filenames.add("registrationdata-public10" + ch);

    registrar.doRegistrations(filenames);

    checkTests7and8(registrar);
  }

  // private helper method //////////////////////////////////////////////

  private void checkTests7and8(Registrar registrar) {
    List<Object> courses= Arrays.asList(
                            "AASP", 154, "AMST", 548, "ANSC", 189,
                            "ANTH", 479, "ARCH", 269, "ASTR", 844,
                            "BIOE", 819, "BMGT", 646, "CCJS", 141,
                            "CHEM", 977, "CINE", 363, "CMSC", 531,
                            "DANC", 229, "ECON", 243, "EDHD", 804,
                            "ENGL", 567, "ENGM", 721, "ENME", 272,
                            "FIRE", 935, "GEMS", 589, "GEOG", 366,
                            "GEOL", 410, "HACS", 566, "HIST", 783,
                            "IMDM", 864, "ITAL", 494, "JAPN", 935,
                            "KNES", 887, "KORA", 254, "LING", 699,
                            "MATH", 564, "MUSC", 163, "NFSC", 398,
                            "PHIL", 619, "PHYS", 152, "PLSC", 648,
                            "PSYC", 982, "SOCY", 972, "STAT", 990,
                            "THET", 776);
    Map<List<String>, List<Object>> expectedResults= new HashMap<>();
    List<Object> studCourses;
    String firstName, lastName, department;
    int courseNumber, i;

    expectedResults.put(Arrays.asList("Allie", "Alligator"),
                        Arrays.asList("ANSC", 189, "CMSC", 531, "GEMS", 589,
                                      "GEOG", 366, "ITAL", 494));
    expectedResults.put(Arrays.asList("Amy", "Amoeba"),
                        Arrays.asList("ARCH", 269, "CHEM", 977, "CINE", 363,
                                      "ENGM", 721, "PHYS", 152));
    expectedResults.put(Arrays.asList("Armando", "Armadillo"),
                        Arrays.asList("ANTH", 479, "ARCH", 269, "CMSC", 531,
                                      "SOCY", 972, "THET", 776));
    expectedResults.put(Arrays.asList("Betty", "Butterfly"),
                        Arrays.asList("ANTH", 479, "CHEM", 977, "FIRE", 935,
                                      "PHYS", 152, "PLSC", 648));
    expectedResults.put(Arrays.asList("Bruce", "Moose"),
                        Arrays.asList("BIOE", 819, "IMDM", 864, "ITAL", 494,
                                      "MATH", 564, "SOCY", 972));
    expectedResults.put(Arrays.asList("Bryson", "Bison"),
                        Arrays.asList("AASP", 154, "ECON", 243, "LING", 699,
                                      "MUSC", 163, "PLSC", 648));
    expectedResults.put(Arrays.asList("Cammy", "Camel"),
                        Arrays.asList("ANTH", 479, "CMSC", 531, "ENGL", 567,
                                      "HACS", 566, "ITAL", 494));
    expectedResults.put(Arrays.asList("Cathy", "Cat"),
                        Arrays.asList("BIOE", 819, "ENGM", 721, "ENME", 272,
                                      "GEOL", 410, "MUSC", 163));
    expectedResults.put(Arrays.asList("Chippy", "Chipmunk"),
                        Arrays.asList("ANTH", 479, "ENGM", 721, "GEOG", 366,
                                      "KNES", 887, "SOCY", 972));
    expectedResults.put(Arrays.asList("Clem", "Clam"),
                        Arrays.asList("ASTR", 844, "DANC", 229, "GEMS", 589,
                                      "HACS", 566, "PHIL", 619));
    expectedResults.put(Arrays.asList("Dolly", "Dolphin"),
                        Arrays.asList("AASP", 154, "ASTR", 844, "BMGT", 646,
                                      "CINE", 363, "PHYS", 152));
    expectedResults.put(Arrays.asList("Earl", "Squirrel"),
                        Arrays.asList("ASTR", 844, "CCJS", 141, "ECON", 243,
                                      "GEOL", 410, "KNES", 887));
    expectedResults.put(Arrays.asList("Ellie", "Elephant"),
                        Arrays.asList("EDHD", 804, "GEMS", 589, "IMDM", 864,
                                      "LING", 699, "STAT", 990));
    expectedResults.put(Arrays.asList("Ferris", "Ferret"),
                        Arrays.asList("CHEM", 977, "ENGL", 567, "ENME", 272,
                                      "JAPN", 935, "PSYC", 982));
    expectedResults.put(Arrays.asList("Freddy", "Frog"),
                        Arrays.asList("ANSC", 189, "CCJS", 141, "ENGL", 567,
                                      "GEMS", 589, "NFSC", 398));
    expectedResults.put(Arrays.asList("Ginny", "Giraffe"),
                        Arrays.asList("EDHD", 804, "HACS", 566, "JAPN", 935,
                                      "PSYC", 982, "SOCY", 972));
    expectedResults.put(Arrays.asList("Horace", "Horse"),
                        Arrays.asList("ASTR", 844, "FIRE", 935, "ITAL", 494,
                                      "KORA", 254, "THET", 776));
    expectedResults.put(Arrays.asList("Iggy", "Iguana"),
                        Arrays.asList("CHEM", 977, "DANC", 229, "EDHD", 804,
                                      "GEOG", 366, "IMDM", 864));
    expectedResults.put(Arrays.asList("Jack", "Yak"),
                        Arrays.asList("CINE", 363, "FIRE", 935, "GEOL", 410,
                                      "MUSC", 163, "NFSC", 398));
    expectedResults.put(Arrays.asList("Jackie", "Jaguar"),
                        Arrays.asList("BMGT", 646, "GEOG", 366, "IMDM", 864,
                                      "PHYS", 152, "THET", 776));
    expectedResults.put(Arrays.asList("Kourtney", "Koala"),
                        Arrays.asList("ANSC", 189, "GEOL", 410, "HACS", 566,
                                      "KNES", 887, "MATH", 564));
    expectedResults.put(Arrays.asList("Lemuel", "Lemur"),
                        Arrays.asList("ECON", 243, "HIST", 783, "KORA", 254,
                                      "LING", 699, "PHIL", 619));
    expectedResults.put(Arrays.asList("Lizzie", "Lizard"),
                        Arrays.asList("ANSC", 189, "ENME", 272, "GEOG", 366,
                                      "JAPN", 935, "PSYC", 982));
    expectedResults.put(Arrays.asList("Manny", "Manatee"),
                        Arrays.asList("ASTR", 844, "CHEM", 977, "JAPN", 935,
                                      "KNES", 887, "MATH", 564));
    expectedResults.put(Arrays.asList("Neil", "Eel"),
                        Arrays.asList("AMST", 548, "DANC", 229, "EDHD", 804,
                                      "NFSC", 398, "STAT", 990));
    expectedResults.put(Arrays.asList("Oscar", "Ostrich"),
                        Arrays.asList("ARCH", 269, "EDHD", 804, "ENGM", 721,
                                      "NFSC", 398, "STAT", 990));
    expectedResults.put(Arrays.asList("Otto", "Otter"),
                        Arrays.asList("ANTH", 479, "CMSC", 531, "ENGL", 567,
                                      "NFSC", 398, "PLSC", 648));
    expectedResults.put(Arrays.asList("Pamela", "Camel"),
                        Arrays.asList("ARCH", 269, "BIOE", 819, "CINE", 363,
                                      "ENGL", 567, "GEMS", 589));
    expectedResults.put(Arrays.asList("Peggy", "Penguin"),
                        Arrays.asList("AMST", 548, "BMGT", 646, "ECON", 243,
                                      "LING", 699, "MATH", 564));
    expectedResults.put(Arrays.asList("Polly", "Possum"),
                        Arrays.asList("BIOE", 819, "ENME", 272, "IMDM", 864,
                                      "ITAL", 494, "THET", 776));
    expectedResults.put(Arrays.asList("Pooja", "Poodle"),
                        Arrays.asList("AMST", 548, "ENGM", 721, "LING", 699,
                                      "MATH", 564, "PHYS", 152));
    expectedResults.put(Arrays.asList("Rama", "Llama"),
                        Arrays.asList("AASP", 154, "CINE", 363, "ECON", 243,
                                      "PSYC", 982, "THET", 776));
    expectedResults.put(Arrays.asList("Rita", "Cheetah"),
                        Arrays.asList("CCJS", 141, "HACS", 566, "HIST", 783,
                                      "JAPN", 935, "KNES", 887));
    expectedResults.put(Arrays.asList("Robyn", "Robin"),
                        Arrays.asList("ARCH", 269, "BMGT", 646, "CMSC", 531,
                                      "PLSC", 648, "PSYC", 982));
    expectedResults.put(Arrays.asList("Roth", "Moth"),
                        Arrays.asList("AASP", 154, "BIOE", 819, "GEOL", 410,
                                      "HIST", 783, "PHIL", 619));
    expectedResults.put(Arrays.asList("Ryan", "Lion"),
                        Arrays.asList("AMST", 548, "CCJS", 141, "KORA", 254,
                                      "MUSC", 163, "PHIL", 619));
    expectedResults.put(Arrays.asList("Sally", "Salamander"),
                        Arrays.asList("CCJS", 141, "DANC", 229, "ENME", 272,
                                      "FIRE", 935, "HIST", 783));
    expectedResults.put(Arrays.asList("Sheena", "Sheep"),
                        Arrays.asList("AASP", 154, "FIRE", 935, "HIST", 783,
                                      "KORA", 254, "PHIL", 619));
    expectedResults.put(Arrays.asList("Timmy", "Termite"),
                        Arrays.asList("ANSC", 189, "DANC", 229, "KORA", 254,
                                      "MUSC", 163, "STAT", 990));
    expectedResults.put(Arrays.asList("Wally", "Walrus"),
                        Arrays.asList("AMST", 548, "BMGT", 646, "PLSC", 648,
                                      "SOCY", 972, "STAT", 990));

    // check the number of courses is right
    assertEquals(40, registrar.numCourses());

    // check that each course exists
    for (i= 0; i < courses.size(); i += 2) {
      // this casting is ugly but it allows the expected results to be
      // stored in a more readable form in the map above
      department= (String) courses.get(i);
      courseNumber= (Integer) courses.get(i + 1);
      assertEquals(5, registrar.numStudentsInCourse(department,
                                                    courseNumber));
    }

    for (List<String> studentName : expectedResults.keySet()) {
      firstName= studentName.get(0);
      lastName= studentName.get(1);

      studCourses= expectedResults.get(studentName);
      for (i= 0; i <= 8; i += 2)
        // this casting is ugly but it allows the expected results to be
        // stored in a more readable form in the map above
        assertTrue(registrar.isInCourse((String) studCourses.get(i),
                                        (Integer) studCourses.get(i + 1),
                                        firstName, lastName));
    }
  }

}
