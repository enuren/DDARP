import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import distributions.Distribution;
import distributions.Normal;
import distributions.Singular;
import distributions.Uniform;
import eventSimulator.DiscreteEventSimulator;
import instance.Instance;
import instance.InstanceGenerator;
import instance.MyObjectiveFunction;
import instance.ObjectiveFunction;
import solver.NaiveSolver;
import solver.Solver;

public class TestScenario {

	public static void main(String[] args){
		// Cost function
		ObjectiveFunction costFunc = new MyObjectiveFunction();
		
		// Setup solvers and instances
		ArrayList<Instance> instances = new ArrayList<Instance>();
		instances.addAll(getNormdistScenarioA(1));
		
		System.out.println(instances.get(0));
		
		ArrayList<Solver> solvers = new ArrayList<Solver>();
		
		// Add your solvers here
		solvers.add(new NaiveSolver());
		
		runTests(costFunc, instances, solvers);
		
	}
	
	public static ArrayList<Instance> getNormdistScenarioA(int nTestInstances) {
		ArrayList<Instance> instances = new ArrayList<Instance>();
		Distribution nPreknown = new Uniform(100, 101);
		
		
		Distribution xCoordP = new Normal(25, 25);
		Distribution yCoordP = xCoordP;
		Distribution xCoordD = xCoordP;
		Distribution yCoordD = yCoordP;
		Distribution timeDist = new Uniform(0, 16);
		Distribution twLengthP = new Singular(0.5);
		Distribution twLengthD = new Singular(0.5);
		
		Distribution reactTimeP = new Singular(2.0);
		Distribution reactTimeD = new Singular(3.0);
		
		Distribution preknownReactTimeP = new Uniform(0.0, 14);
		Distribution preknownReactTimeD = new Uniform(0.0, 13);
		
		
		Distribution demandDist = new Uniform(1, 7); // 7 excluded
		
		double fractionPickupTWs = 0.5;
		
		// Setup instance generator
		InstanceGenerator instGen = new InstanceGenerator(xCoordP, yCoordP,
				xCoordD, yCoordD,
				twLengthP, twLengthD,
				demandDist,
				fractionPickupTWs);
		
		
		Distribution nRevealed = new Uniform(0.2,0.4);
		for(int i=0; i<nTestInstances; i++){
			int nPre = ((Double) nPreknown.sample() ).intValue();
			Double nRevD = nPre*nRevealed.sample();
			int nRev = nRevD.intValue();
			System.out.println("Preknown "+nPre);
			instances.add(instGen.generate(nPre, nRev, timeDist, reactTimeP, reactTimeD, 
					preknownReactTimeP, preknownReactTimeD));
			
			// generate 10% cancellations
			instances.get(instances.size()-1).generateCancellations(0.1);
		}
		
		return instances;
	}
	
	private static void runTests(ObjectiveFunction costFunc,
			ArrayList<Instance> instances, ArrayList<Solver> solvers) {
		double[][] costs = new double[solvers.size()][instances.size()];
		
		
		long[] times = new long[solvers.size()];
		int[] fails = new int[solvers.size()];
		
		for(int i=0; i<instances.size(); i++){
			DiscreteEventSimulator sim = new DiscreteEventSimulator(instances.get(i), null, costFunc);
			sim.setVerbose(false);
			System.out.println("\nTesting instance "+i+"/"+instances.size());
			for(int s=0; s<solvers.size(); s++){
				// Start timer
				System.out.print("\tSolver: "+solvers.get(s).toString());
				System.out.flush();
				long start = System.currentTimeMillis();

				//Solve
				sim.setSolver(solvers.get(s));
				//sim.simulate(false);
				//double cost = costFunc.getCost(sim.getSolution());
				double cost = syncRunSimulator(sim, costFunc);
				if(cost==Double.MAX_VALUE){
					fails[s]++;
					cost = 0;
				}
				costs[s][i] = cost;
				
				// End timer
				long time = System.currentTimeMillis()-start;
				times[s] += time;
				System.out.println(" "+time+" ms");
			}
		}
		
		System.out.println("\nAverage time usage:");
		
		for(int s=0; s<solvers.size(); s++){
			System.out.println(((int)Math.floor(times[s]))+" ms with "+fails[s]+" fails for "+solvers.get(s) );
		}
		
		writeToFile("testOutput.csv", solvers, costs);
		
		/*
		 * Print averages
		 */
		double[] bestCost = new double[instances.size()];
		for(int i=0; i<instances.size(); i++){
			double best = Double.MAX_VALUE;
			for(int s=0; s<solvers.size(); s++){
				if(costs[s][i]>0 && costs[s][i]<best)
					best = costs[s][i];
			}
			bestCost[i] = best;
		}
		
		for(int s=0; s<solvers.size(); s++){
			
			
			double avgCost = 0;
			for(int i=0; i<instances.size(); i++){
				avgCost += costs[s][i]/bestCost[i];
			}
			System.out.print(avgCost/(instances.size()-fails[s])+"\t");
			
			System.out.print((times[s]/instances.size())+" ms\t");
			
			System.out.println(solvers.get(s));
		}
		/*
		System.out.println("\n+=======================================================================");
		System.out.print("| Solver:  | ");
		for(int s=0; s<solvers.size(); s++){
			System.out.print(solvers.get(s));
			if(s<solvers.size()-1)
				System.out.print("\t| ");
		}
		System.out.println();
		
		System.out.print("| Average: | ");
		for(int s=0; s<solvers.size(); s++){
			double avgCost = 0;
			for(int i=0; i<instances.size(); i++){
				avgCost += costs[s][i];
			}
			System.out.print(avgCost/(instances.size()-fails[s]));
			if(s<solvers.size()-1)
				System.out.print("\t| ");
		}
		System.out.println("");
		System.out.println("+=======================================================================");
		*/
	}
	
	private static double syncRunSimulator(DiscreteEventSimulator sim, ObjectiveFunction costFunc){
		   ExecutorService executor = Executors.newFixedThreadPool(4);

		    Future<Double> future = executor.submit(sim);

		    executor.shutdown();            //        <-- reject all further submissions

		    double cost = Double.MAX_VALUE;
		    try {
		        cost = future.get(10, TimeUnit.SECONDS);  //     <-- wait 8 seconds to finish
		        if(sim.getInstance().isSolutionValid(sim.getSolution())!="" ){
		        	cost=Double.MAX_VALUE;
		        }
		    } catch (InterruptedException e) {    //     <-- possible error cases
		        System.out.println("job was interrupted");
		    } catch (ExecutionException e) {
		        System.out.println("caught exception: " + e.getCause());
		    } catch (TimeoutException e) {
		        future.cancel(true);              //     <-- interrupt the job
		        System.out.println("timeout");
		    }

		    // wait all unfinished tasks for 2 sec
		    
		    try {
		    	if(!executor.awaitTermination(2, TimeUnit.SECONDS)){
		    		// force them to quit by interrupting
		    		executor.shutdownNow();
		    	}
		    } catch (InterruptedException e) {    //     <-- possible error cases
		        System.out.println("job was interrupted 2");
		    }
	
		return cost;
	}
	
	private static void writeToFile(String filename, ArrayList<Solver> solvers, double costs[][]){
		try{
			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			
			for(int s=0; s<solvers.size(); s++){
				writer.print(solvers.get(s));
				if(s<solvers.size()-1)
					writer.print(",");
			}
			writer.println();
			
			for(int i=0; i<costs[0].length; i++){
				for(int s=0; s<solvers.size(); s++){
					writer.print(costs[s][i]);
					if(s<solvers.size()-1)
						writer.print(",");
				}
				writer.println("");
			}

			writer.close();
		} catch(Exception e){
			System.err.println("Failed to write to file:");
			e.printStackTrace();
		}
	}

}
