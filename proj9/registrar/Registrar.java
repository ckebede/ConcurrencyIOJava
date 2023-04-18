package registrar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

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
 *          The registrar class imitates small scale university registrar. It
 *          keeps track of students enrolled in classes, how many classes they
 *          are taking, courses, the number of seats available in the course, as
 *          well as the name of students enrolled in these classes. More than
 *          one registrar object can be instantiated; meaning multiple
 *          universities can have their own registrar and it will not interfere
 *          with each other. For most of the methods, any invalid arguments
 *          passed in (eg. null, empty strings, or negative/0 numbers, will
 *          result in a illegal argument exception being thrown. There is two
 *          inner classes in this class. The first is a course class that
 *          defines a course. The second is a class that defines an class
 *          utilizing threads.
 */
public class Registrar {

  // Student firstname + lastname, number of courses taking
  private HashMap<String, Integer> studentSchedules;
  // Course object, student names
  private HashMap<Course, HashSet<String>> courseRoster;
  private int numCourses;
  private final int maxCourses;
  private Object lock = new Object();

  /**
   * This class defines a course object which has a department name, a course
   * number, and number of seats.
   *
   */
  private class Course {

    private String department;
    private int courseID;
    private int numSeats;

    private Course(String dept, int id, int seats) {
      department = dept;
      courseID = id;
      numSeats = seats;
    }

    private void removeSeat() {
      numSeats--;
    }

    private void addSeat() {
      numSeats++;
    }

    private boolean hasSpace() {
      return numSeats > 0;
    }

    @Override
    public int hashCode() {
      return 0;
    }

    /**
     * This equals method only checks that the department and the course id are
     * equal. Number of seats are not checked for equality.
     */
    @Override
    public boolean equals(Object obj) {
      boolean equal = false;
      if (obj == this)
        equal = true;
      else if (obj instanceof Course) {
        Course courseObj = (Course) obj;
        equal = courseObj.department.equals(department)
            && courseID == courseObj.courseID;
      }
      return equal;
    }

  }

  /**
   * This inner class defines an object that implements Runnable to allow for
   * threading.
   */
  public class RegistrarOfficer implements Runnable {
    private String fileName;

    /**
     * This constructor initializes a new RegistrarOfficer object with the
     * filename passed in.
     * 
     * @param fileName
     */
    private RegistrarOfficer(String fileName) {
      this.fileName = fileName;
    }

    /**
     * This reads the file associated with its fileName field, and adds courses
     * and students into the current Registrar object using the methods from the
     * Registrar class.
     */
    @Override
    public void run() {
      BufferedReader bf;
      String line;
      String[] words;

      try {
        bf = new BufferedReader(new FileReader(fileName));

        // This reads the first line in the file, checks it is not null and
        // tokenizes the line.
        while ((line = bf.readLine()) != null) {
          words = line.split("\\s+");

          // Given that I can assume that the files will always be valid, there
          // is no reason to add other conditions. I.e. no need to worry about
          // null exceptions or parsing an String incorrectly.
          if (words[0].equals("addcourse")) {
            addNewCourse(words[1], Integer.parseInt(words[2]),
                Integer.parseInt(words[3]));
          } else if (words[0].equals("addregistration")) {
            addToCourse(words[1], Integer.parseInt(words[2]), words[3],
                words[4]);
          }
        }

        // This is just necessary. I will not do anything else to handle that
        // exception.
      } catch (IOException e) {
        e.printStackTrace();
      }

    }

  }

  /**
   * This constructor initializes a Registrar object with the max number of
   * courses that a student can take passed in.
   * 
   * @param maxCoursesPerStudent
   */
  public Registrar(int maxCoursesPerStudent) {
    if (maxCoursesPerStudent <= 0)
      throw new IllegalArgumentException();

    maxCourses = maxCoursesPerStudent;
    studentSchedules = new HashMap<>();
    courseRoster = new HashMap<>();
  }

  /**
   * This method adds a new course to the current object if the course does not
   * already exist and the values inputted are valid. Regardless, it will return
   * the current object to allow for chained calls.
   * 
   * @param department
   * @param number
   * @param numSeats
   * @return Registrar
   */
  public Registrar addNewCourse(String department, int number, int numSeats) {
    if (department == null || department.equals("") || number <= 0
        || numSeats <= 0)
      throw new IllegalArgumentException();
    synchronized (lock) {
      Course newCourse = new Course(department, number, numSeats);
      if (!courseRoster.containsKey(newCourse)) {
        courseRoster.put(newCourse, new HashSet<>());
        numCourses++;
      }
    }

    return this;
  }

  /**
   * This method removes a course with the department, and number passed in. It
   * makes sure that if a class is cancelled, all the students who were in that
   * class have another space to sign up for another class. If there is a no
   * class in that department with that number, or the department or number
   * passed in is invalid, no updates will be made to the current object.
   * 
   * @param department
   * @param number
   * @return boolean
   */
  public boolean cancelCourse(String department, int number) {
    if (department == null || number <= 0)
      throw new IllegalArgumentException();

    Course course = getCourse(department, number);
    boolean cancelled = false;

    if (courseRoster.containsKey(course)) {
      // Removes course from roster and returns the HashSet of students taking
      // that course
      HashSet<String> studentsInCancelledClass = courseRoster.remove(course);
      // Decreases number of courses that students taking that course have
      for (String student : studentsInCancelledClass) {
        // if (studentSchedules.keySet().contains(student)) {
        int num = studentSchedules.get(student) - 1;
        studentSchedules.put(student, num);
        // }
      }
      cancelled = true;
    }
    return cancelled;
  }

  public int numCourses() {
    return numCourses;
  }

  /**
   * This method adds a student to an existing course and returns true. If the
   * arguments passed in are invalid, an exception will be thrown. The student's
   * number of class they are taking will increase by one. The number of seats
   * available in the course will decrease by one, and the name of student will
   * be added to the course roster. Otherwise, if the student is already in the
   * course, if the course does not exist, the course is full, or the student
   * does not have anymore space to take the class in their schedule, none of
   * the current object's fields are updated and false is returned. I.e. the
   * student will not be added to any course.
   * 
   * @param department
   * @param number
   * @param firstName
   * @param lastName
   * @return boolean
   */
  public boolean addToCourse(String department, int number, String firstName,
      String lastName) {

    if (department == null || department.equals("") || firstName == null
        || lastName == null || number <= 0 || firstName.equals("")
        || lastName.equals(""))
      throw new IllegalArgumentException();

    boolean added = false;
    String student = firstName + " " + lastName;

    synchronized (lock) {

      Course course = getCourse(department, number);

      /*
       * Only add student to course if: (1)course exists in roster (2) student
       * not in course (3) student has space for another course
       */

      if (courseRoster.containsKey(course)
          && !courseRoster.get(course).contains(student)) {
        // When a new student is added, 0 should be their starting num of
        // courses
        if (!studentSchedules.containsKey(student))
          studentSchedules.put(student, 0);

        // Regardless, I need to check I do not add too many courses to a
        // student
        if (studentSchedules.get(student) < maxCourses && course.hasSpace()) {
          courseRoster.get(course).add(student);
          // Number of courses needs to increase by one.
          int num = studentSchedules.get(student) + 1;
          studentSchedules.put(student, num);

          added = true;
          course.removeSeat();
        }

      }
      return added;

    }

  }

  /**
   * This method simply returns the number of students within the course passed
   * in. If the course does not exist, -1 will be returned. If the course exists
   * but has no students enrolled, 0 will be returned. Else, it will return the
   * number of students in the course.
   * 
   * @param department
   * @param number
   * @return
   */
  public int numStudentsInCourse(String department, int number) {
    if (department == null || department.equals("") || number <= 0)
      throw new IllegalArgumentException();

    int numStudents = -1;
    Course course = getCourse(department, number);

    if (courseRoster.containsKey(course)) {
      numStudents = 0;
      // Increment numStudents for every student in the course.
      for (String student : courseRoster.get(course)) {
        numStudents++;
      }
    }
    return numStudents;
  }

  /**
   * This method returns the number of students in the course passed in with the
   * lastname passed in. Like the method above, -1 will be returned if there is
   * not course with
   * 
   * @param department
   * @param number
   * @param lastName
   * @return
   */
  public int numStudentsInCourseWithLastName(String department, int number,
      String lastName) {
    if (department == null || department.equals("") || lastName == null
        || lastName.equals("") || number <= 0)
      throw new IllegalArgumentException();

    int numStudents = -1;
    Course course = new Course(department, number, 0);
    if (courseRoster.containsKey(course)) {
      numStudents = 0;
      for (String student : courseRoster.get(course)) {
        // The split function returns an array of strings separated by the
        // string passed in. Given that last name is the second part of the
        // name, it will be the second element of the array.
        if (lastName.equals(student.split(" ")[1])) {
          numStudents++;
        }
      }
    }
    return numStudents;
  }

  /**
   * This method checks if a student is in the course passed in. If the student
   * is in the course, true will be returned. If the course does not exist or
   * the student is not in the course, false will be returned.
   * 
   * @param department
   * @param number
   * @param firstName
   * @param lastName
   * @return boolean
   */

  public boolean isInCourse(String department, int number, String firstName,
      String lastName) {
    if (department == null || department.equals("") || lastName == null
        || firstName == null || number <= 0 || firstName.equals("")
        || lastName.equals(""))
      throw new IllegalArgumentException();

    Course course = new Course(department, number, 0);
    boolean inCourse = false;
    String name = firstName + " " + lastName;

    if (courseRoster.containsKey(course)) {
      for (String student : courseRoster.get(course)) {
        if (student.equals(name)) {
          inCourse = true;
        }
      }
    }
    return inCourse;
  }

  /**
   * This method returns the number of courses that the student passed in is
   * taking. If the student is not taking any courses or if the student does not
   * exist, 0, is returned.
   * 
   * @param firstName
   * @param lastName
   * @return int
   */
  public int howManyCoursesTaking(String firstName, String lastName) {
    if (lastName == null || firstName == null || firstName.equals("")
        || lastName.equals(""))
      throw new IllegalArgumentException();

    int num = 0;
    String name = firstName + " " + lastName;
    if (studentSchedules.containsKey(name)) {
      num = studentSchedules.get(name);
    }

    return num;
  }

  /**
   * This method returns true if the student passed in is in the course passed
   * in and is subsequently dropped from the course. The student will have an
   * extra space to take another class and the course will have another seat
   * available. If the student with the names passed in does not exist, the
   * student is not taking the course, or the course itself does not exist, then
   * the method will return false.
   * 
   * @param department
   * @param number
   * @param firstName
   * @param lastName
   * @return boolean
   */
  public boolean dropCourse(String department, int number, String firstName,
      String lastName) {
    if (department == null || department.equals("") || lastName == null
        || firstName == null || number <= 0 || firstName.equals("")
        || lastName.equals(""))
      throw new IllegalArgumentException();

    boolean dropped = false;
    String name = firstName + " " + lastName;
    Course course = getCourse(department, number);

    if (isInCourse(department, number, firstName, lastName)) {
      // Update number of courses student currently taking.
      int num = studentSchedules.get(name) + 1;
      studentSchedules.put(name, num);

      // Update the number of seats in the course
      course.addSeat();

      // Remove student from class roster
      dropped = courseRoster.get(course).remove(name);
    }
    return dropped;
  }

  /**
   * This method will remove a student from the school's registrar and return
   * true. If they took any courses, they will be removed from them and the
   * number of seats available in those courses will increase by one. If the
   * student does not exist with the names passed in, then nothing will be
   * updated and it will return false.
   * 
   * @param firstName
   * @param lastName
   * @return
   */

  public boolean cancelRegistration(String firstName, String lastName) {
    if (firstName == null || lastName == null || firstName.equals("")
        || lastName.equals(""))
      throw new IllegalArgumentException();

    boolean cancelled = false;
    String name = firstName + " " + lastName;

    if (studentSchedules.containsKey(name)) {
      for (Course course : courseRoster.keySet()) {
        if (courseRoster.get(course).contains(name)) {
          courseRoster.get(course).remove(name);
          course.addSeat();
          cancelled = true;
        }
      }

    }

    return cancelled;
  }

  /**
   * This method will take in a collection of String filenames. It will create
   * threads that will process the files associated with those names. A thread
   * will be created for each file and they will run concurrently to either add
   * courses and/or add students into courses using the addNewCourse() and
   * addToCourse() as seen in their run methods.
   * 
   * @param filenames
   */
  public void doRegistrations(Collection<String> filenames) {
    if (filenames == null)
      throw new IllegalArgumentException();

    Iterator<String> fileIter = filenames.iterator();
    Thread[] threads = new Thread[filenames.size()];
    int i = 0;
    while (fileIter.hasNext()) {
      String filename = fileIter.next();
      threads[i] = new Thread(new RegistrarOfficer(filename));
      threads[i].start();
      i++;
    }
    i = 0;
    while (i < threads.length) {
      try {
        threads[i].join();
        i++;
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * This method returns a course object that already exists in the current
   * Registrar object. If the course does not exist, null will be returned.
   * 
   * @param dept
   * @param num
   * @return
   */

  private Course getCourse(String dept, int num) {
    // The equals method does not check number of seats. Therefore, 0 is fine.
    Course course = new Course(dept, num, 0);
    for (Course crs : courseRoster.keySet()) {
      if (crs.equals(course)) {
        course = crs;
        return course;
      }
    }
    return null;
  }

}
