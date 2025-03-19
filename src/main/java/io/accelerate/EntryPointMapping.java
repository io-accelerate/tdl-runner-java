package io.accelerate;

import io.accelerate.solutions.CHK.CheckoutSolution;
import io.accelerate.solutions.DMO.DemoRound1Solution;
import io.accelerate.solutions.FIZ.FizzBuzzSolution;
import io.accelerate.solutions.HLO.HelloSolution;
import io.accelerate.solutions.SUM.SumSolution;
import com.google.gson.JsonElement;

import java.util.List;

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
    private final FizzBuzzSolution fizzBuzzSolution;
    private final CheckoutSolution checkoutSolution;
    private final DemoRound1Solution demoRound1Solution;

    EntryPointMapping() {
        sumSolution = new SumSolution();
        helloSolution = new HelloSolution();
        fizzBuzzSolution = new FizzBuzzSolution();
        checkoutSolution = new CheckoutSolution();
        demoRound1Solution = new DemoRound1Solution();
    }

    Object sum(List<JsonElement> p) {
        return sumSolution.compute(p.get(0).getAsInt(), p.get(1).getAsInt());
    }

    Object hello(List<JsonElement> p) {
        return helloSolution.hello(p.get(0).getAsString());
    }
    
    Object fizzBuzz(List<JsonElement> p) {
        return fizzBuzzSolution.fizzBuzz(p.get(0).getAsInt());
    }

    Object checkout(List<JsonElement> p) {
        return checkoutSolution.checkout(p.get(0).getAsString());
    }

    Object increment(List<JsonElement> p) {
        return demoRound1Solution.increment(p.get(0).getAsInt());
    }

    Object toUppercase(List<JsonElement> p) {
        return demoRound1Solution.toUppercase(p.get(0).getAsString());
    }

    Object letterToSanta(List<JsonElement> p) {
        return demoRound1Solution.letterToSanta();
    }

    Object countLines(List<JsonElement> p) {
        return demoRound1Solution.countLines(p.get(0).getAsString());
    }
}
