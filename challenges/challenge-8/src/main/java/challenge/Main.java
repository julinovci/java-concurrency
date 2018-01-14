package challenge;


import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class Main {
 
    public static void main(String[] args) {
        Tutor tutor = new Tutor();
        Student student = new Student(tutor);
        tutor.setStudent(student);
 
        Thread tutorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                tutor.studyTime();
            }
        });
 
        Thread studentThread = new Thread(new Runnable() {
            @Override
            public void run() {
                student.handInAssignment();
            }
        });
 
        tutorThread.start();
        studentThread.start();
    }
}
 
class Tutor {
    private Student student;
 
    boolean tutorArrived = false;
    public boolean hasTutorArrived() {
        return tutorArrived;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
 
    public void studyTime() {
        System.out.println("Tutor has arrived");
        tutorArrived = true;
        try {
            // wait for student to arrive and hand in assignment
            Thread.sleep(300);
        }
        catch (InterruptedException e) {
 
        }
        while (!student.isAssignmentHandedIn()) {
            continue;
        }
        student.startStudy();
        System.out.println("Tutor is studying with student");
    }
 
    public void getProgressReport() {
        // get progress report
        System.out.println("Tutor gave progress report");
    }
}
 
class Student {
 
    private Tutor tutor;

    boolean handedAssignment = false;

    public boolean isAssignmentHandedIn() {
        return handedAssignment;
    }
 
    Student(Tutor tutor) {
        this.tutor = tutor;
    }
 
    public void startStudy() {
        // study
        System.out.println("Student is studying");
    }
 
    public void handInAssignment() {
        while(!tutor.hasTutorArrived()) {
            continue;
        }
        tutor.getProgressReport();
        System.out.println("Student handed in assignment");
        handedAssignment = true;
    }
}


/// Another way:
class NewTutor {
    private Student student;

    public void setStudent(Student student) {
        this.student = student;
    }
 
    public void studyTime() {
        synchronized(this) {
            System.out.println("Tutor has arrived");
            synchronized(student) {
                try {
                    // wait for student to arrive and hand in assignment
                    this.wait();
                }
                catch (InterruptedException e) {
         
                }
                student.startStudy();
                System.out.println("Tutor is studying with student");

            }
        }
    }
 
    public void getProgressReport() {
        // get progress report
        System.out.println("Tutor gave progress report");
    }
}
 
class NewStudent {
 
    private Tutor tutor;
 
    NewStudent(Tutor tutor) {
        this.tutor = tutor;
    }
 
    public void startStudy() {
        // study
        System.out.println("Student is studying");
    }
 
    public void handInAssignment() {
        synchronized(tutor) {
            tutor.getProgressReport();
            synchronized(this) {
                System.out.println("Student handed in assignment");
                tutor.notifyAll();
            }
        }
    }
}
