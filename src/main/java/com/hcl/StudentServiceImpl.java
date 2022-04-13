package com.hcl;


import com.hcl.entity.Student;

import java.util.HashMap;
import java.util.Scanner;

public class StudentServiceImpl {
    private static final HashMap<Integer, Student> students = new HashMap<>();

    public static void main(String[] args) {
    /*The requirement is, you need to build a maven driven program, that has ability
    to create, update, delete and read students based on sorted order.

    For update and delete you need to pass the roll number, and it should retrieve the
    student, and you should be able to change the name or age.
     */
        Scanner scanner = new Scanner(System.in);

        students.put(1, new Student(1, "Vince", 25));
        students.put(2,new Student(2, "Penny", 24));
        students.put(3,new Student(3, "Phil", 26));
        students.put(4,new Student(4, "David", 23));
        students.put(5,new Student(5, "Jayce", 22));
        printBannerSplash();

        boolean quit = false;
        while (!quit) {
            operations();
            int input = scanner.nextInt();
            scanner.nextLine();
            switch (input) {
                case 1:
                    addStudent(scanner);
                    break;
                case 2:
                    lookUpStudentByRollno(scanner);
                    break;
                case 3:
                    updateStudentByRollno(scanner);
                    break;
                case 4:
                    deleteStudentByRollno(scanner);
                    break;
                case 5:
                    printStudentList();
                    break;
                case 6:
                    quit = true;
                    footer();
                    break;
                default:
                    invalidInput();
                    break;
            }
        }

    }

    private static Boolean lookUpStudentByRollno(Scanner scanner) {
        // getting roll number input from user
        System.out.println("*******Look up Student*******");
        System.out.print("Please enter the Roll Number of the student you want to look up: ");
        int rollno = scanner.nextInt();

        Student student = checkIfStudentExist(rollno);
        // check if student exists or not
        if (student != null) {
            System.out.printf("Student with Roll number %d is %s with age %d",
                    student.getRollno(), student.getName(), student.getAge() );
            System.out.println();
            return true;
        }
        // student doesn't exist
        System.out.println("Student with " + rollno + " does not exist");
        return false;
    }

    private static Student checkIfStudentExist(int rollNo) {
        Student student = students.get(rollNo);
        if (students.containsKey(rollNo)) {
            return student;
        }
        return null;
    }

    private static void printBannerSplash() {
        System.out.println("==========Welcome==========");
    }

    public static void operations() {
        System.out.println("\n==========Main Menu==========");
        System.out.println("Please select from the following (1,2,3,4 or 5):");
        System.out.println("1. Add a Student");
        System.out.println("2. Look up an existing Student");
        System.out.println("3. Update an existing Student");
        System.out.println("4. Delete an existing Student");
        System.out.println("5. Print Student List");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }

    private static boolean addStudent(Scanner scanner) {
        // taking user inputs
        System.out.println("*******Add Student*******");
        System.out.print("Please enter the Roll ID: ");
        int rollId = scanner.nextInt();
        System.out.print("Please enter the name: ");
        String name = scanner.next();
        System.out.print("Please enter the age: ");
        int age = scanner.nextInt();
        // create new student object
        Student student = checkIfStudentExist(rollId);
        // check to see if it's already exist
        if (student != null) {
            System.out.printf("Student %s with Roll No. %d is already exist", student.getName(), student.getRollno());
            System.out.println();
            return false;
        }

        Student student1 = new Student(rollId, name, age);
        students.put(rollId, student1);
        System.out.printf("Successfully add student %s with Roll Number %d and age %d",
                student1.getName(), student1.getRollno(), student1.getAge());
        System.out.println();
        return true;
    }
    private static boolean deleteStudentByRollno(Scanner scanner) {
        System.out.println("*******Delete Student*******");
        System.out.print("Please enter the Roll Number of the student you want to delete: ");
        int rollno = scanner.nextInt();
        Student student = checkIfStudentExist(rollno);
        if (student != null) {
            System.out.printf("Are you sure that you want to delete student %s with roll number %d? (Y/N): ",
                    student.getName(), student.getRollno());
            char confirmation = scanner.next().charAt(0);
            // checking to see if input is equal to y/Y
            if (confirmation == 'Y' || confirmation == 'y') {
                boolean isRemoved = students.remove(rollno, student);
                // verify if student has been removed
                if (isRemoved) {
                    System.out.printf("Successfully remove student %s with roll number %d", student.getName(), student.getRollno());
                    System.out.println();
                    return true;
                }
            }  else {
                System.out.println("No student was deleted");
                return false;
            }
        }
        // failed to remove student
        System.out.printf("Student with roll number %d doesn't exist", rollno);
        System.out.println();
        return false;
    }

    private static Boolean updateStudentByRollno(Scanner scanner) {
        System.out.println("*******Update Student*******");
        System.out.print("Please enter the Roll Number of the student you want to update: ");
        int rollno = scanner.nextInt();
        Student student = checkIfStudentExist(rollno);
        if (student != null) {
            System.out.printf("Student with Roll Number %d is found, current name is %s" +
                    " and age is %d\n", student.getRollno(), student.getName(), student.getAge());
            // ask users for new inputs
            System.out.print("Please enter the new name: ");
            student.setName(scanner.next());
            System.out.print("Please enter the new age: ");
            student.setAge(scanner.nextInt());

            System.out.printf("Successfully updated student %s with age %d", student.getName(), student.getAge());
            System.out.println();
            return true;
        }
//
        // student could not be found
        System.out.printf("Student with roll number %d doesn't exist", rollno);
        System.out.println();
        return false;
    }

    private static void printStudentList() {
        System.out.println("*******Print Student*******");
        // checking if the set is empty
        if (students.size() == 0) {
            System.out.println("No student exists");
        } else {
            // print out student object with foreach loop
            for (Integer rollNo :
                    students.keySet()) {
                System.out.println(students.get(rollNo).toString());
            }
        }
    }

    private static void invalidInput() {
        System.out.println("Invalid choice, try again");
    }

    private static void footer() {
        System.out.println("\n==========Goodbye==========");
    }
}

