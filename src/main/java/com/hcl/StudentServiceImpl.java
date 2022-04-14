package com.hcl;

import com.hcl.entity.Student;
import com.hcl.exception.AgeLimitExceptiopn;

import java.io.*;
import java.util.*;

public class StudentServiceImpl {
    private static final Set<Student> studentSet = new TreeSet<>(new Comparator<Student>() {
        @Override
        public int compare(Student o1, Student o2) {
            // TODO Auto-generated method stub
            return o1.getName().compareTo(o2.getName());
        }
    });

    public static void main(String[] args) {
    /*The requirement is, you need to build a maven driven program, that has ability
    to create, update, delete and read students based on sorted order.
    For update and delete you need to pass the roll number, and it should retrieve the
    student, and you should be able to change the name or age.
     */

//        studentSet.add(new Student(1, "Vince", 25));
//        studentSet.add(new Student(2, "Penny", 24));
//        studentSet.add(new Student(3, "Phil", 26));
//        studentSet.add(new Student(4, "David", 23));
//        studentSet.add(new Student(5, "Jayce", 22));
        Scanner scanner = new Scanner(System.in);

        readFromSavedFile();

        printBannerSplash();

        boolean quit = false;
        while (!quit) {
            operations();
            int input = 0;
            // checking to see if choice is valid
            try {
                input = scanner.nextInt();
            } catch (InputMismatchException e) {
            }
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
                    break;
                default:
                    invalidInput();
                    break;
            }
        }
        saveExistingStudentList();
        footer();
    }

    private static void readFromSavedFile() {
        System.out.println("*******Importing Student From File*******");
        FileReader fr = null;
        Scanner scanner = null;
        // try catch if the students.txt exists
        try {
            scanner = new Scanner(new FileReader("students.txt"));
            scanner.useDelimiter(",");
            while (scanner.hasNextLine()) {
                // reading data field one by one
                int rollNo = scanner.nextInt();
                scanner.skip(scanner.delimiter());
                String name = scanner.next();
                scanner.skip(scanner.delimiter());
                String age = scanner.nextLine();
                System.out.printf("Import Student %s with Roll Number %d and age %d",
                        name, rollNo, Integer.parseInt(age));
                System.out.println();
                // adding new student into set
                studentSet.add(new Student(rollNo, name, Integer.parseInt(age)));
            }
        } catch (FileNotFoundException e) {
            System.out.println("No file found, will create one later");
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    private static int validateRollnoInput(Scanner scanner) {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.print("Not a valid input, please enter a number: ");
            }
        }
    }

    private static String validateNameInput(Scanner scanner) {
        while (true) {
            try {
                String name = scanner.nextLine();
                String regexName = "\\p{Upper}(\\p{Lower}+\\s?)";
                String patternName = "(" + regexName + ")";
                if (!name.matches(patternName)) {
                    throw new InputMismatchException();
                }
                return name;
            } catch (InputMismatchException e) {
                System.out.print("Please enter a valid name starts with Capital: ");
            }
        }
    }

    private static int validateAgeInput(Scanner scanner) {
        while (true) {
            try {
                int age = scanner.nextInt();
                if (age <= 60 && age >= 7) {
                    return age;
                } else {
                    throw new AgeLimitExceptiopn();
                }
            } catch (InputMismatchException e) {
                scanner.nextLine();

                System.out.print("Please enter a valid age (7-60): ");
            } catch (AgeLimitExceptiopn e) {
                scanner.nextLine();
                System.out.print("Please enter a valid age (7-60): ");
            }
        }
    }

    private static void saveExistingStudentList() {
        System.out.println();
        System.out.println("*******Save Student Records*******");
        FileWriter locFile = null;
        // create/write to students.txt with existing studetns
        try {
            locFile = new FileWriter("students.txt");
            for (Student student : studentSet) {
                locFile.write(String.format("%d,%s,%d\n", student.getRollno(),
                        student.getName(), student.getAge()));
            }
            locFile.close();
        } catch (IOException e) {
            System.out.println(e);
        }
        System.out.println("File saved successfully");
    }

    private static Boolean lookUpStudentByRollno(Scanner scanner) {
        // getting roll number input from user
        System.out.println("*******Look up Student*******");
        System.out.print("Please enter the Roll Number of the student you want to look up: ");
        int rollno = validateRollnoInput(scanner);
        // validating input
        Student student = checkIfStudentExist(rollno);
        // check if student exists or not
        if (student != null) {
            System.out.printf("Student with Roll number %d is %s with age %d",
                    student.getRollno(), student.getName(), student.getAge());
            System.out.println();
            return true;
        }
        // student doesn't exist
        System.out.println("Student with " + rollno + " does not exist");
        return false;
    }

    private static Student checkIfStudentExist(int rollNo) {
        //This is O(n) since it is sorted can be do O(log n)????
        for (Student student : studentSet) {
            if (student.getRollno() == rollNo) {
                return student;
            }
        }
        return null;
    }

    private static void printBannerSplash() {
        System.out.println();
        System.out.println("==========Welcome==========");
        System.out.println("Student Tree Set Implementation");
    }

    public static void operations() {
        System.out.println("\n==========Main Menu==========");
        System.out.println("Please select from the following (1,2,3,4,5 or 6):");
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
        int rollId = validateRollnoInput(scanner);
        scanner.nextLine();
        System.out.print("Please enter the name: ");
        String name = validateNameInput(scanner);
        System.out.print("Please enter the age: ");
        int age = validateAgeInput(scanner);
        scanner.nextLine();
        // create new student object
        Student student = checkIfStudentExist(rollId);
        // check to see if it's already exist
        if (student != null) {
            System.out.printf("Student %s with Roll No. %d is already exist", student.getName(), student.getRollno());
            System.out.println();
            return false;
        }

        Student student1 = new Student(rollId, name, age);
        studentSet.add(student1);
        System.out.printf("Successfully add student %s with Roll Number %d and age %d",
                student1.getName(), student1.getRollno(), student1.getAge());
        System.out.println();
        return true;
    }

    private static boolean deleteStudentByRollno(Scanner scanner) {
        System.out.println("*******Delete Student*******");
        System.out.print("Please enter the Roll Number of the student you want to delete: ");
        int rollno = validateRollnoInput(scanner);
        Student student = checkIfStudentExist(rollno);
        if (student != null) {
            System.out.printf("Are you sure that you want to delete student %s with roll number %d? (Y/N): ",
                    student.getName(), student.getRollno());
            char confirmation = scanner.next().charAt(0);
            // checking to see if input is equal to y/Y
            if (confirmation == 'Y' || confirmation == 'y') {
                boolean isRemoved = studentSet.remove(student);
                // verify if student has been removed
                if (isRemoved) {
                    System.out.printf("Successfully remove student %s with roll number %d", student.getName(), student.getRollno());
                    System.out.println();
                    return true;
                }
            } else {
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
        int rollno = validateRollnoInput(scanner);
        scanner.nextLine();
        Student student = checkIfStudentExist(rollno);
        if (student != null) {
            System.out.printf("Student with Roll Number %d is found, current name is %s" +
                    " and age is %d\n", student.getRollno(), student.getName(), student.getAge());
            // ask users for new inputs
            System.out.print("Please enter the new name: ");
            student.setName(validateNameInput(scanner));
            System.out.print("Please enter the new age: ");
            student.setAge(validateAgeInput(scanner));

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
        if (studentSet.isEmpty()) {
            System.out.println("There are no Students in the Set");
        }
        for (Student s : studentSet) {
            System.out.println(s.toString());
        }
    }

    private static void invalidInput() {
        System.out.println("Invalid choice, try again");
    }

    private static void footer() {
        System.out.println("\n==========Goodbye==========");
    }
}