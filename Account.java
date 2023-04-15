// Alec Wolfe
// CS 454 HW 3
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
    private Lock lock;
    private Condition condition;
    private boolean prefWithdrawInProg;
    private int balance;

    public Account() {  
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
        this.prefWithdrawInProg = false;
        this.balance = 1000;
    }

  
    public void deposit(int k) { 
        lock.lock(); // aquire lock
        try {
            balance = balance + k; // add deposit to balance
        } 
        finally {
            lock.unlock(); // surrender lock
            condition.signalAll(); 
        }
    }


  
    public void withdraw(int k) {
        lock.lock(); 
        try {
            while (balance < k || prefWithdrawInProg) {
                condition.await(); // release lock until condition is met
            }
            balance = balance - k;
        }
        catch (InterruptedException e) {
        }
        finally {
            lock.unlock();
        }
    }


  
    public void preferredWithdraw(int k) {
        lock.lock(); // aquire lock
        try {
            prefWithdrawInProg = true; // set pref withdaw in progress
            while (balance<k) { // if balance is less than amount in account wait
                condition.await();
            }
            balance = balance -k; // set new balance
            prefWithdrawInProg = false; // done with withdraw
        } 
        catch (InterruptedException e) {
        }
        finally {
            lock.unlock(); // unlock
        }
    }


  // code from hw given
    public void transfer(int k, Account reserve) {
        lock.lock();
        try {
            reserve.withdraw(k);
            deposit(k);
        } finally {
            lock.unlock();
        }
    }
}
