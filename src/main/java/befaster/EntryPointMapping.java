package befaster;

import befaster.solutions.ARRS.ArraySumSolution;
import befaster.solutions.CHK.CheckoutSolution;
import befaster.solutions.FIZ.FizzBuzzSolution;
import befaster.solutions.HLO.HelloSolution;
import befaster.solutions.IRNG.IntRangeSolution;
import befaster.solutions.SUM.SumSolution;
import com.google.gson.JsonElement;

import java.util.ArrayList;

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
class EntryPointMapping {
    private final SumSolution sumSolution;
    private final HelloSolution helloSolution;
    private final ArraySumSolution arraySumSolution;
    private final IntRangeSolution intRangeSolution;
    private final FizzBuzzSolution fizzBuzzSolution;
    private final CheckoutSolution checkoutSolution;

    EntryPointMapping() {
        sumSolution = new SumSolution();
        helloSolution = new HelloSolution();
        arraySumSolution = new ArraySumSolution();
        intRangeSolution = new IntRangeSolution();
        fizzBuzzSolution = new FizzBuzzSolution();
        checkoutSolution = new CheckoutSolution();
    }

    Object sum(JsonElement... p) {
        return sumSolution.compute(p[0].getAsInt(), p[1].getAsInt());
    }

    Object hello(JsonElement... p) {
        return helloSolution.hello(p[0].getAsString());
    }

    Object arraySum(JsonElement[] p) {
        ArrayList<Integer> intArray = new ArrayList<>();
        for (JsonElement jsonElement : p[0].getAsJsonArray()) {
            intArray.add(jsonElement.getAsInt());
        }
        return arraySumSolution.compute(intArray);
    }

    Object intRange(JsonElement... p) {
        return intRangeSolution.generate(p[0].getAsInt(), p[1].getAsInt());
    }

    Object fizzBuzz(JsonElement... p) {
        return fizzBuzzSolution.fizzBuzz(p[0].getAsInt());
    }

    Object checkout(JsonElement... p) {
        return checkoutSolution.checkout(p[0].getAsString());
    }
}
