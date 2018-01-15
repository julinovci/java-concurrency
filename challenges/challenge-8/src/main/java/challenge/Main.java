package challenge;


import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class Main {
 
    public static void main(String[] args) {
        Semaphore sem = new Semaphore(1);
        Tutor tutor = new Tutor(sem);
        Student student = new Student(sem, tutor);
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
    private Semaphore sem;

    boolean tutorArrived = false;
    public boolean hasTutorArrived() {
        return tutorArrived;
    }

    public Tutor(Semaphore sem) {
        this.sem = sem;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
 
    public void studyTime() {
        System.out.println("Tutor has arrived");
        try {
            sem.acquire();
        }
        catch (InterruptedException e) {}

        // wait for student to arrive and hand in assignment
        while (sem.availablePermits() != 1) {
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
    private Semaphore sem;
 
    Student(Semaphore sem, Tutor tutor) {
        this.tutor = tutor;
        this.sem = sem;
    }
 
    public void startStudy() {
        // study
        System.out.println("Student is studying");
    }
 
    public void handInAssignment() {
        while(sem.availablePermits() != 0) {
            continue;
        }
        tutor.getProgressReport();
        System.out.println("Student handed in assignment");
        sem.release();
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
