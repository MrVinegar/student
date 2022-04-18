package com.hcl;

import com.hcl.entity.Student;
import com.hcl.exception.AgeLimitExceptiopn;

import java.sql.*;
import java.util.*;

public class StudentServiceImpl {
    // mysql info
    private String url = "jdbc:mysql://localhost:3306/analysis";
    private String user = "root";
    private String password = "56805680";

    private static final String CREATE_TABLE_SQL = "create table students (" + "  id  int primary key,"
            + "  name varchar(20)," + "  age int) ";
    private static final String INSERT_STUDENT_SQL = "INSERT INTO students" + "  (id, name, age) VALUES "
            + " (?, ?, ?);";
    private static final String SELECT_STUDENT_BY_ROLLNO = "select id,name,age from students where id =?";
    private static final String SELECT_ALL_STUDENTS = "select * from students";
    private static final String DELETE_STUDENT_SQL = "delete from students where id = ?;";
    private static final String UPDATE_STUDENT_SQL = "update students set name = ?,age = ? where id = ?;";

    // connection
    public Connection connect() {
        Connection conn = null;
        try {
//            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public StudentServiceImpl() {
    }

    private static Set<Student> studentSet = new TreeSet<>((o1, o2) -> (o1.getName().compareTo(o2.getName())));
    static StudentServiceImpl studentService = new StudentServiceImpl();

    public static void main(String[] args) throws SQLException {
        try (Connection connection = studentService.connect()) {
            studentService.createTable(connection);
            studentService.insertUser(connection, new Student(1, "Vince", 25));
            studentService.insertUser(connection, new Student(2, "Penny", 24));
            studentService.insertUser(connection, new Student(3, "Phil", 26));
            studentService.insertUser(connection, new Student(4, "David", 23));
            studentService.insertUser(connection, new Student(5, "Jayce", 22));
            studentSet = studentService.selectAllStudents(connection);

            Scanner scanner = new Scanner(System.in);

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
                        addStudent(scanner, connection);
                        break;
                    case 2:
                        lookUpStudentByRollno(scanner, connection);
                        break;
                    case 3:
                        updateStudentByRollno(scanner, connection);
                        break;
                    case 4:
                        deleteStudentByRollno(scanner, connection);
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
            footer();
        }
    }

    // creating schema in mysql database
    public void createTable(Connection connection) throws SQLException {
        // Step 1: Establishing a Connection
        try (Statement statement = connection.createStatement()) {
            // Step 2: Execute the query or update query
            statement.execute(CREATE_TABLE_SQL);
        } catch (SQLException e) {
            // print SQL exception information
            printSQLException(e);
        }
    }

    // Adding students into tree set from the database
    public Set<Student> selectAllStudents(Connection connection) {

        // using try-with-resources to avoid closing resources (boiler plate code)
        final Set<Student> studentSet = new TreeSet<>((o1, o2) -> (o1.getName().compareTo(o2.getName())));

        // Step 1: Establishing a Connection
        try (Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_UPDATABLE);) {
            System.out.println(statement);
            // Step 3: Execute the query or update query
            ResultSet rs = statement.executeQuery(SELECT_ALL_STUDENTS);

            ResultSetMetaData dm = rs.getMetaData();
            System.out.println("dm=" + dm);

            // Step 4: Process the ResultSet object.
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                studentSet.add(new Student(id, name, age));
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return studentSet;
    }

    public Student selectStudent(Connection connection, int id) {
        Student student = null;
        // Step 1: Establishing a Connection
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_STUDENT_BY_ROLLNO);) {
            preparedStatement.setInt(1, id);
            // Step 3: Execute the query or update query
            ResultSet rs = preparedStatement.executeQuery();

            // Step 4: Process the ResultSet object.
            while (rs.next()) {
                String name = rs.getString("name");
                int age = rs.getInt("age");
                student = new Student(id, name, age);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return student;
    }

    private static Boolean lookUpStudentByRollno(Scanner scanner, Connection connection) {
        // getting roll number input from user
        System.out.println("*******Look up Student*******");
        System.out.print("Please enter the Roll Number of the student you want to look up: ");
        // validating input
        int rollno = validateRollnoInput(scanner);
        // check if student exists or not
        Student student = studentService.selectStudent(connection, rollno);
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

    public void insertUser(Connection connection, Student student) throws SQLException {
//        System.out.println(INSERT_USERS_SQL);
        // try-with-resource statement will auto close the connection.
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_STUDENT_SQL)) {
            preparedStatement.setLong(1, student.getRollno());
            preparedStatement.setString(2, student.getName());
            preparedStatement.setInt(3, student.getAge());
//            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    private static boolean addStudent(Scanner scanner, Connection connection) throws SQLException {
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
        Student student = studentService.selectStudent(connection, rollId);
        // check to see if it's already exist
        if (student != null) {
            System.out.printf("Student %s with Roll No. %d is already exist", student.getName(), student.getRollno());
            System.out.println();
            return false;
        }

        Student student1 = new Student(rollId, name, age);
        studentSet.add(student1);
        studentService.insertUser(connection, student1);
        System.out.printf("Successfully add student %s with Roll Number %d and age %d",
                student1.getName(), student1.getRollno(), student1.getAge());
        System.out.println();
        return true;
    }

    public boolean deleteStudent(Connection connection, int rollNo) throws SQLException {
        boolean rowDeleted;
        try (PreparedStatement statement = connection.prepareStatement(DELETE_STUDENT_SQL);) {
            statement.setInt(1, rollNo);
            rowDeleted = statement.executeUpdate() > 0;
        }
        return rowDeleted;
    }

    private static boolean deleteStudentByRollno(Scanner scanner, Connection connection) throws SQLException {
        System.out.println("*******Delete Student*******");
        System.out.print("Please enter the Roll Number of the student you want to delete: ");
        int rollno = validateRollnoInput(scanner);
        Student student = studentService.selectStudent(connection, rollno);
        if (student != null) {
            System.out.printf("Are you sure that you want to delete student %s with roll number %d? (Y/N): ",
                    student.getName(), student.getRollno());
            char confirmation = scanner.next().charAt(0);
            // checking to see if input is equal to y/Y
            if (confirmation == 'Y' || confirmation == 'y') {
                boolean isRemoved = studentSet.remove(student);
                studentService.deleteStudent(connection, rollno);
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

    // update student in the database with connection
    public boolean updateStudent(Connection connection, Student student) throws SQLException {
        boolean rowUpdated;
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_STUDENT_SQL);) {
            statement.setString(1, student.getName());
            statement.setInt(2, student.getAge());
            statement.setInt(3, student.getRollno());
            rowUpdated = statement.executeUpdate() > 0;
        }
        return rowUpdated;
    }


    private static Boolean updateStudentByRollno(Scanner scanner, Connection connection) throws SQLException {
        System.out.println("*******Update Student*******");
        System.out.print("Please enter the Roll Number of the student you want to update: ");
        int rollno = validateRollnoInput(scanner);
        scanner.nextLine();
        Student student = studentService.selectStudent(connection, rollno);
        if (student != null) {
            System.out.printf("Student with Roll Number %d is found, current name is %s" +
                    " and age is %d\n", student.getRollno(), student.getName(), student.getAge());
            // ask users for new inputs
            System.out.print("Please enter the new name: ");
            student.setName(validateNameInput(scanner));
            System.out.print("Please enter the new age: ");
            student.setAge(validateAgeInput(scanner));
            // update student in database
            studentService.updateStudent(connection, student);

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

    //Validating User input
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

    private void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }

    private static void printBannerSplash() {
        System.out.println();
        System.out.println("==========Welcome==========");
        System.out.println("Student Implementation with MYSQL via JDBC");
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

    private static void invalidInput() {
        System.out.println("Invalid choice, try again");
    }

    private static void footer() {
        System.out.println("\n==========Goodbye==========");
    }
}