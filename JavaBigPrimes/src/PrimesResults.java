import java.math.BigInteger;
import java.util.SortedSet;
import java.util.TreeSet;

public class PrimesResults {

    private SortedSet<BigInteger> primes;

    public PrimesResults(){
        primes = new TreeSet<>();
    }

    public int getPrime(){
        synchronized (this) {
            return primes.size();
        }
    }
    public void addPrime(BigInteger prime){
        synchronized (this) {
            primes.add(prime);
        }
    }
    public void print(){
        synchronized (this) {
            primes.forEach(System.out::println);
        }
    }

}
