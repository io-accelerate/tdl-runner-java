package io.accelerate;

import io.accelerate.client.queue.abstractions.ParamAccessor;
import io.accelerate.solutions.CHK.CheckoutSolution;
import io.accelerate.solutions.DMO.DemoRound1Solution;
import io.accelerate.solutions.DMO.DemoRound2Solution;
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
    private final DemoRound2Solution demoRound2Solution;

    EntryPointMapping() {
        sumSolution = new SumSolution();
        helloSolution = new HelloSolution();
        fizzBuzzSolution = new FizzBuzzSolution();
        checkoutSolution = new CheckoutSolution();
        demoRound1Solution = new DemoRound1Solution();
        demoRound2Solution = new DemoRound2Solution();
    }

    Object sum(List<ParamAccessor> p) {
        return sumSolution.compute(p.get(0).getAsInteger(), p.get(1).getAsInteger());
    }

    Object hello(List<ParamAccessor> p) {
        return helloSolution.hello(p.get(0).getAsString());
    }
    
    Object fizzBuzz(List<ParamAccessor> p) {
        return fizzBuzzSolution.fizzBuzz(p.get(0).getAsInteger());
    }

    Object checkout(List<ParamAccessor> p) {
        return checkoutSolution.checkout(p.get(0).getAsString());
    }

    Object increment(List<ParamAccessor> p) {
        return demoRound1Solution.increment(p.get(0).getAsInteger());
    }

    Object toUppercase(List<ParamAccessor> p) {
        return demoRound1Solution.toUppercase(p.get(0).getAsString());
    }

    Object letterToSanta(List<ParamAccessor> p) {
        return demoRound1Solution.letterToSanta();
    }

    Object countLines(List<ParamAccessor> p) {
        return demoRound1Solution.countLines(p.get(0).getAsString());
    }

    Object arraySum(List<ParamAccessor> p) {
        return demoRound2Solution.arraySum(p.get(0).getAsListOf(Integer.class));
    }

    Object intRange(List<ParamAccessor> p) {
        return demoRound2Solution.intRange(p.get(0).getAsInteger(), p.get(1).getAsInteger());
    }

    Object filterPass(List<ParamAccessor> p) {
        return demoRound2Solution.filterPass(p.get(0).getAsListOf(Integer.class), p.get(1).getAsInteger());
    }
}
