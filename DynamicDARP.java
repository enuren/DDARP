import java.util.Random;

import distributions.Distribution;
import distributions.Normal;
import distributions.Uniform;
import solver.NaiveSolver;
import solver.Solver;
import visualization.SolutionVisualizer;
import eventSimulator.DiscreteEventSimulator;
import instance.MyObjectiveFunction;
import instance.ObjectiveFunction;
import instance.Instance;
import instance.InstanceGenerator;



public class DynamicDARP {

	public static void main(String args[]){
		// Make the randomness behave the same each time the program is run, uncomment for random or change the number to fix another random sequence
		Instance.rand = new Random(1);
		
		System.out.println("Creating instance");
	
		// Get a single instance
		Instance inst = TestScenario.getNormdistScenarioA(1).get(0);
		
		// Print out the instance
		System.out.println(inst);
		
		// Now we need to create a solver... This should be replaced by one of your making
		System.out.println("Creating solver");
		Solver solver = new NaiveSolver();
		
		// Create your own objective function (For now, it is quite simple)
		ObjectiveFunction cf = new MyObjectiveFunction();
		
		// Create a discrete event simulation
		System.out.println("Creating simulator");
		DiscreteEventSimulator sim = new DiscreteEventSimulator(inst, solver, cf);

		// Run the simulation (and show us what is happening)
		System.out.println("Simulating");
		sim.simulate(true);
		
		// Show the final solution (with all labels)
		SolutionVisualizer.visualize(inst, sim.getSolution(),true,true, "Done",true);
		
		System.out.println("Ended the dynamic DARP program.");
	}
	
}
