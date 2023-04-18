package tests;

import org.junit.*;

import registrar.Registrar;

import static org.junit.Assert.*;

/**
 * @author Caleb Kebede
 * @UID 119063194
 * @Login ckebede
 * @Section 0203
 * @version 12/08/2022
 * 
 *          I pledge on my honor that I have not given or received any
 *          unauthorized assistance on this assignment.
 * 
 *          These are some student tests from project 1 with some minor
 *          additions. I do not check if threading works here given the public
 *          tests covers that.
 * 
 */

public class StudentTests {

  @Test
  public void testStudent1() {
    Registrar registrar = new Registrar(5);
    assertEquals(0, registrar.numCourses());
  }

  @Test // This test makes sure that the cancelcourse() method does not cancel
  // with the same department name but different number, or a different
  // department name and same number.
  public void testStudent2() {
    Registrar registrar = new Registrar(5);
    registrar.addNewCourse("CMSC", 121, 22);

    registrar.cancelCourse("CMSC", 120); // Same department name, different
    // number
    registrar.cancelCourse("SOCI", 121); // Different department name, same
    // number

    assertEquals(1, registrar.numCourses());

  }

  @Test // This tests that students are not added to the wrong course if either
  // number or department are equal to an existing course.
  public void testStudent3() {
    Registrar registrar = new Registrar(5);
    registrar.addNewCourse("CMSC", 121, 22);
    registrar.addNewCourse("BIOL", 100, 22);
    registrar.addNewCourse("BIOL", 121, 22);

    registrar.addToCourse("BIOL", 100, "My", "Self");

    assertTrue(registrar.isInCourse("BIOL", 100, "My", "Self"));
    assertFalse(registrar.isInCourse("CMSC", 121, "My", "Self"));
    assertFalse(registrar.isInCourse("BIOL", 101, "My", "Self"));
  }

  @Test // This tests the numStudentsInCourse method to see if it returns -1
  // when an invalid course is passed in.
  public void testStudent4() {
    Registrar registrar = new Registrar(5);

    registrar.addNewCourse("CMSC", 121, 22);
    registrar.addNewCourse("BIOL", 100, 22);
    registrar.addNewCourse("BIOL", 121, 22);

    registrar.addToCourse("BIOL", 100, "My", "Self");

    assertEquals(-1, registrar.numStudentsInCourse("BIOL", 123));
    assertEquals(0, registrar.numStudentsInCourse("BIOL", 121));
  }

  @Test // This test ensures that cancel course returns false and true
  // accurately.
  public void testStudent5() {
    Registrar registrar = new Registrar(5);

    registrar.addNewCourse("CMSC", 121, 22);
    registrar.addNewCourse("BIOL", 100, 22);
    registrar.addNewCourse("BIOL", 121, 22);

    assertTrue(registrar.cancelCourse("BIOL", 121));
    assertFalse(registrar.cancelCourse("BIOL", 121));
    assertFalse(registrar.cancelCourse("CMSC", 122));
    assertFalse(registrar.cancelCourse("LMTE", 100));

  }

  @Test // This test makes sure that cancelRegistration works; that the student
  // is not enrolled in any course any longer.
  public void testStudent6() {
    Registrar registrar = new Registrar(5);

    registrar.addNewCourse("CMSC", 121, 22);
    registrar.addNewCourse("BIOL", 100, 22);
    registrar.addNewCourse("BIOL", 121, 22);

    registrar.addToCourse("CMSC", 121, "My", "Self");
    registrar.addToCourse("BIOL", 100, "My", "Self");
    registrar.addToCourse("BIOL", 121, "My", "Self");

    assertTrue(registrar.isInCourse("CMSC", 121, "My", "Self"));
    assertTrue(registrar.isInCourse("BIOL", 100, "My", "Self"));
    assertTrue(registrar.isInCourse("BIOL", 121, "My", "Self"));

    assertTrue(registrar.cancelRegistration("My", "Self"));

    assertFalse(registrar.isInCourse("CMSC", 121, "My", "Self"));
    assertFalse(registrar.isInCourse("BIOL", 100, "My", "Self"));
    assertFalse(registrar.isInCourse("BIOL", 121, "My", "Self"));

    assertFalse(registrar.cancelRegistration("My", "Self"));

// Student should be able to register again to the course.
    assertTrue(registrar.addToCourse("CMSC", 121, "My", "Self"));
    assertTrue(registrar.isInCourse("CMSC", 121, "My", "Self"));
  }

  @Test // This test makes sure that the class max works.
  public void testStudent7() {
    Registrar registrar = new Registrar(3);

    registrar.addNewCourse("CMSC", 121, 22);
    registrar.addNewCourse("BIOL", 100, 22);
    registrar.addNewCourse("BIOL", 121, 22);
    registrar.addNewCourse("PHYS", 411, 21);

    registrar.addToCourse("CMSC", 121, "My", "Self");
    registrar.addToCourse("BIOL", 100, "My", "Self");
    registrar.addToCourse("BIOL", 121, "My", "Self");
    assertFalse(registrar.addToCourse("PHYS", 411, "My", "Self"));

    assertEquals(3, registrar.howManyCoursesTaking("My", "Self"));

    registrar.cancelCourse("CMSC", 121);
    assertFalse(registrar.isInCourse("CMSC", 121, "My", "Self"));
    assertEquals(2, registrar.howManyCoursesTaking("My", "Self"));

  }

  @Test
  public void testStudent8() {

    Registrar registrar = new Registrar(3);

    registrar.addNewCourse("CMSC", 121, 22);

    registrar.addToCourse("CMSC", 121, "My", "Self");
    registrar.addToCourse("CMSC", 121, "Your", "Self");
    registrar.addToCourse("CMSC", 121, "My", "Self");

    assertEquals(2,
        registrar.numStudentsInCourseWithLastName("CMSC", 121, "Self"));
  }

  @Test
  public void testStudent9() {
    Registrar registrar = new Registrar(3);

    registrar.addNewCourse("CMSC", 121, 22);
    registrar.addNewCourse("BIOL", 100, 22);
    registrar.addNewCourse("BIOL", 121, 22);

    registrar.addToCourse("CMSC", 121, "My", "Self");
    registrar.addToCourse("BIOL", 100, "My", "Self");
    registrar.addToCourse("BIOL", 121, "My", "Self");

    assertTrue(registrar.cancelRegistration("My", "Self"));

    assertFalse(registrar.isInCourse("CMSC", 121, "My", "Self"));
    assertFalse(registrar.isInCourse("BIOL", 100, "My", "Self"));
    assertFalse(registrar.isInCourse("BIOL", 121, "My", "Self"));

  }
}
