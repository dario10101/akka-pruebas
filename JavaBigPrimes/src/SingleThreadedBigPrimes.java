import java.math.BigInteger;
import java.util.*;

public class SingleThreadedBigPrimes {

    public static void main(String[] args) {
        //runWithoutThreads();
        runWithThreads();
    }

    private static void runWithoutThreads(){
        System.out.println("run without threads: ");
        Long start = System.currentTimeMillis();

        SortedSet<BigInteger> primes = new TreeSet<>();

        while(primes.size() < 20){
            BigInteger bigInteger = new BigInteger(2000, new Random());
            primes.add(bigInteger.nextProbablePrime());
            System.out.println("Number " + primes.size() + " ok...");
        }

        Long end = System.currentTimeMillis();
        primes.stream().forEach(System.out::println);
        System.out.println("Time: " + (end - start) + " ms.");
    }

    public static void runWithThreads() {
        System.out.println("run with threads: ");
        Long start = System.currentTimeMillis();

        PrimesResults primesResults = new PrimesResults();
        Runnable task = new PrimeGenerator(primesResults);
        List<Thread> threads = new ArrayList<>();

        for(int i = 0; i < 20; i++){
            Thread thr = new Thread(task);
            threads.add(thr);
            thr.start();
        }

        threads.stream().forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Long end = System.currentTimeMillis();

        primesResults.print();
        System.out.println("Time: " + (end - start) + " ms.");
    }

}
