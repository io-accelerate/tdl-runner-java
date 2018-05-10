package befaster;

import befaster.solutions.CHK.CheckoutSolution;
import befaster.solutions.FIZ.FizzBuzzSolution;
import befaster.solutions.HLO.HelloSolution;
import befaster.solutions.SUM.SumSolution;

import static befaster.runner.TypeConversion.asInt;

/**
 * This class maps an RPC event to a method call.
 * It converts the parameters into the right format.
 *
 * We have chosen to map events to instance methods
 * to allow for better test coverage computation.
 *
 * Mapping events to static methods might have also worked but
 * that would have resulted in uncovered default constructors.
 */
public class EntryPointMapping {
    private final SumSolution sumSolution;
    private final HelloSolution helloSolution;
    private final FizzBuzzSolution fizzBuzzSolution;
    private final CheckoutSolution checkoutSolution;

    EntryPointMapping() {
        sumSolution = new SumSolution();
        helloSolution = new HelloSolution();
        fizzBuzzSolution = new FizzBuzzSolution();
        checkoutSolution = new CheckoutSolution();
    }

    public Object sum(String ... p) {
        return sumSolution.compute(asInt(p[0]), asInt(p[1]));
    }

    public Object hello(String ... p) {
        return helloSolution.hello(p[0]);
    }

    public Object fizzBuzz(String ... p) {
        return fizzBuzzSolution.fizzBuzz(asInt(p[0]));
    }

    public Object checkout(String ... p) {
        return checkoutSolution.checkout(p[0]);
    }
}
