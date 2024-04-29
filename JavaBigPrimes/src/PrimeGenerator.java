import java.math.BigInteger;
import java.util.Random;

public class PrimeGenerator implements Runnable{

    private PrimesResults primesResults;

    public PrimeGenerator(PrimesResults primesResults){
        this.primesResults = primesResults;
    }

    @Override
    public void run() {
        BigInteger bigInteger = new BigInteger(2000, new Random());
        primesResults.addPrime(bigInteger.nextProbablePrime());
    }


    public PrimesResults getPrimesResults() {
        return primesResults;
    }
    public void setPrimesResults(PrimesResults primesResults) {
        this.primesResults = primesResults;
    }
}
