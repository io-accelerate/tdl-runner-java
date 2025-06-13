package io.accelerate;

import io.accelerate.client.queue.abstractions.ParamAccessor;
import io.accelerate.solutions.AMZ.AmazingSolution;
import io.accelerate.solutions.CHK.CheckoutSolution;
import io.accelerate.solutions.RBT.RabbitHoleSolution;
import io.accelerate.solutions.DMO.*;
import io.accelerate.solutions.FIZ.FizzBuzzSolution;
import io.accelerate.solutions.HLO.HelloSolution;
import io.accelerate.solutions.SUM.SumSolution;
import io.accelerate.solutions.ULT.UltimateSolution;

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
    private final RabbitHoleSolution rabbitHoleSolution;
    private final AmazingSolution amazingSolution;
    private final UltimateSolution ultimateSolution;
    private final DemoRound1Solution demoRound1Solution;
    private final DemoRound2Solution demoRound2Solution;
    private final DemoRound3Solution demoRound3Solution;
    private final DemoRound4n5Solution demoRound4n5Solution;

    EntryPointMapping() {
        sumSolution = new SumSolution();
        helloSolution = new HelloSolution();
        fizzBuzzSolution = new FizzBuzzSolution();
        checkoutSolution = new CheckoutSolution();
        rabbitHoleSolution = new RabbitHoleSolution();
        amazingSolution = new AmazingSolution();
        ultimateSolution = new UltimateSolution();
        demoRound1Solution = new DemoRound1Solution();
        demoRound2Solution = new DemoRound2Solution();
        demoRound3Solution = new DemoRound3Solution();
        demoRound4n5Solution = new DemoRound4n5Solution();
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

    Object rabbitHole(List<ParamAccessor> p) {
        return rabbitHoleSolution.rabbitHole(
                p.get(0).getAsInteger(),
                p.get(1).getAsInteger(),
                p.get(2).getAsString(),
                p.get(3).getAsMapOf(String.class)
        );
    }

    Object amazingMaze(List<ParamAccessor> p) {
        return amazingSolution.amazingMaze(
                p.get(0).getAsInteger(),
                p.get(1).getAsInteger(),
                p.get(2).getAsMapOf(String.class)
        );
    }

    Object ultimateMaze(List<ParamAccessor> p) {
        return ultimateSolution.ultimateMaze(
                p.get(0).getAsInteger(),
                p.get(1).getAsInteger(),
                p.get(2).getAsMapOf(String.class)
        );
    }

    // Demo Round 1
    
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
    
    // Demo Round 2

    Object arraySum(List<ParamAccessor> p) {
        return demoRound2Solution.arraySum(p.get(0).getAsListOf(Integer.class));
    }

    Object intRange(List<ParamAccessor> p) {
        return demoRound2Solution.intRange(p.get(0).getAsInteger(), p.get(1).getAsInteger());
    }

    Object filterPass(List<ParamAccessor> p) {
        return demoRound2Solution.filterPass(p.get(0).getAsListOf(Integer.class), p.get(1).getAsInteger());
    }
    
    // Demo Round 3

    Object inventoryAdd(List<ParamAccessor> p) {
        return demoRound3Solution.inventoryAdd(p.getFirst().getAsObject(InventoryItem.class), p.get(1).getAsInteger());
    }

    Object inventorySize(List<ParamAccessor> p) {
        return demoRound3Solution.inventorySize();
    }

    Object inventoryGet(List<ParamAccessor> p) {
        return demoRound3Solution.inventoryGet(p.getFirst().getAsString());
    }

    // Demo Round 4 and 5

    Object waves(List<ParamAccessor> p) {
        return demoRound4n5Solution.waves(p.getFirst().getAsInteger());
    }
}
