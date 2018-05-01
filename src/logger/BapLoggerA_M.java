package logger;





import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.jorlib.frameworks.columnGeneration.branchAndPrice.BAPNode;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.EventHandling.BAPListener;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.EventHandling.BranchEvent;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.EventHandling.FinishEvent;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.EventHandling.FinishProcessingNodeEvent;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.EventHandling.NodeIsFractionalEvent;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.EventHandling.NodeIsInfeasibleEvent;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.EventHandling.NodeIsIntegerEvent;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.EventHandling.ProcessingNextNodeEvent;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.EventHandling.PruneNodeEvent;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.EventHandling.StartEvent;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.EventHandling.TimeLimitExceededEvent;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.branchingDecisions.BranchingDecision;

import bap.BranchAndPriceA_M;
import bap.BranchAndPriceB;
import bap.BranchAndPriceB_M;
import bap.branching.branchingDecisions.RoundHoldingEdge;
import bap.branching.branchingDecisions.RoundLocalService;
import bap.branching.branchingDecisions.RoundLocalServiceForAllPricingProblems;
import bap.branching.branchingDecisions.RoundQ;
import bap.branching.branchingDecisions.RoundServiceEdge;
import bap.branching.branchingDecisions.RoundServiceEdgeForAllPricingProblems;
import bap.branching.branchingDecisions.RoundTimeService;
import bap.branching.branchingDecisions.RoundTimeServiceForAllPricingProblems;
import cg.Cycle;
import model.SNDRC;



public class BapLoggerA_M implements BAPListener{

    protected BufferedWriter writer;
    protected NumberFormat formatter;

    /** Branch-and-Price node ID of node currently being solved**/
    protected int bapNodeID;
    /** Parent node ID, -1 if root node **/
    protected int parentNodeID;
    /** Best integer solution **/
    protected int objectiveIncumbentSolution;
    /** Bound on the BAP node **/
    protected double nodeBound;
    /** What to do with the node, i.e prune (based on obj), Infeasible, Integer, Fractional, or Inconclusive if the nodeStatus could not be determined (e.g. due to time limit) **/
    protected NodeResultStatus nodeStatus;
    /** Number of nodes currently in the queue **/
    protected int nodesInQueue;

    //Colgen stats
    /** Number of column generation iterations **/
    protected int cgIterations;

    //Master problem
    /** Counts how much time is spent on solving master problems **/
    protected long timeSolvingMaster;
    /** Objective value of bap node **/
    protected double nodeValue;

    //Pricing Problem
    /** Counts how much time is spend on solving pricing problems **/
    protected long timeSolvingPricing;
    /** Total number of columns generated by the pricing problems **/
    protected int nrGeneratedColumns;
    protected double LB;
    protected int nrInequalities;
    protected String branchType;
    protected BranchAndPriceA_M bap;
    
    
    /**
     * Create a new logger which writes its output the the file specified
     * @param branchAndPrice Branch-and-Price instance for which this logger is created.
     * @param outputFile file to redirect the output to.
     */
    public BapLoggerA_M(BranchAndPriceA_M branchAndPrice, File outputFile){
        try {
            writer=new BufferedWriter(new FileWriter(outputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        formatter=new DecimalFormat("#0.00");
        branchAndPrice.addBranchAndPriceEventListener(this);
        bap=branchAndPrice;
    }
    
    
    
    
    
    
    /**
     * Write a single line of text to the output file
     * @param line line of text to be written
     */
    protected void writeLine(String line){
        try {
            writer.write(line);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    /**
     * Reset the values
     */
    protected void reset(){
        bapNodeID=-1;
        parentNodeID=-1;
        objectiveIncumbentSolution =-1;
        nodeBound =-1;
        cgIterations=0;
        timeSolvingMaster=0;
        nodeValue=-1;
        timeSolvingPricing=0;
        nrGeneratedColumns=0;
        nodesInQueue=-1;
        LB=0;
        nrInequalities=-1;
        branchType=null;
    }
    
    
    
    /**
     * Construct a single line in the log file, and write it to the output file
     */
    protected void constructAndWriteLine(){
//      this.writeLine(String.valueOf(bapNodeID) + "\t" + parentNodeID + "\t" + objectiveIncumbentSolution + "\t" + nodeBound  + "\t" + timeSolvingMaster + "\t" + timeSolvingPricing + "\t" + nodeStatus + "\t" + nodesInQueue + "\t" + LB+ "\t"+nrInequalities+"\t"+branchType);
        this.writeLine(String.valueOf(bapNodeID) + "\t" + parentNodeID + "\t" + objectiveIncumbentSolution + "\t" + nodeBound + "\t" + formatter.format(nodeValue) + "\t" + cgIterations + "\t" + timeSolvingMaster + "\t" + timeSolvingPricing + "\t" + nrGeneratedColumns + "\t" + nodeStatus + "\t" + nodesInQueue + "\t" + LB+ "\t"+nrInequalities+"\t"+branchType);
    }

    @Override
    public void startBAP(StartEvent startEvent) {
//        this.writeLine("BAPNodeID \t parentNodeID \t objectiveIncumbentSolution \t nodeBound \t t_master \t t_pricing  \t solutionStatus \t nodesInQueue \t LB \t nrInequalities \t branchType");
        this.writeLine("BAPNodeID \t parentNodeID \t objectiveIncumbentSolution \t nodeBound \t nodeValue \t cgIterations \t t_master \t t_pricing \t nrGenColumns \t solutionStatus \t nodesInQueue \t LB \t nrInequalities \t branchType");
    }

    @Override
    public void finishBAP(FinishEvent finishEvent) {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    

    @Override
    public void pruneNode(PruneNodeEvent pruneNodeEvent) {
        this.nodeStatus =NodeResultStatus.PRUNED;
        this.nodeBound =pruneNodeEvent.nodeBound;
        this.nrInequalities=pruneNodeEvent.node.getInequalities().size();
        this.constructAndWriteLine();
    }

    @Override
    public void nodeIsInfeasible(NodeIsInfeasibleEvent nodeIsInfeasibleEvent) {
        this.nodeStatus =NodeResultStatus.INFEASIBLE;
        this.nrInequalities=nodeIsInfeasibleEvent.node.getInequalities().size();
        this.constructAndWriteLine();
    }

    @Override
    public void nodeIsInteger(NodeIsIntegerEvent nodeIsIntegerEvent) {
        this.nodeStatus =NodeResultStatus.INTEGER;
        this.nrInequalities=nodeIsIntegerEvent.node.getInequalities().size();
        this.constructAndWriteLine();
    }

    @Override
    public void nodeIsFractional(NodeIsFractionalEvent nodeIsFractionalEvent) {
        this.nodeStatus =NodeResultStatus.FRACTIONAL;
        this.nrInequalities=nodeIsFractionalEvent.node.getInequalities().size();
        this.constructAndWriteLine();
    }
    
    
    @Override
    public void processNextNode(ProcessingNextNodeEvent processingNextNodeEvent) {
        this.reset();
        this.bapNodeID=processingNextNodeEvent.node.nodeID;
        this.parentNodeID=processingNextNodeEvent.node.getParentID();
        this.objectiveIncumbentSolution =processingNextNodeEvent.objectiveIncumbentSolution;
        this.nodesInQueue=processingNextNodeEvent.nodesInQueue;
        
        if(bap.getLowBoundQueue().size()>0) {
            BAPNode<SNDRC, Cycle> bapNode=(BAPNode<SNDRC, Cycle>) bap.getLowBoundQueue().peek();
            this.LB=bapNode.getBound();
        }else this.LB=0;
    }

    @Override
    public void finishedColumnGenerationForNode(FinishProcessingNodeEvent finishProcessingNodeEvent) {
        this.nodeBound = finishProcessingNodeEvent.nodeBound;
        this.nodeValue= finishProcessingNodeEvent.nodeValue;
        this.cgIterations= finishProcessingNodeEvent.numberOfCGIterations;
        this.timeSolvingMaster= finishProcessingNodeEvent.masterSolveTime;
        this.timeSolvingPricing= finishProcessingNodeEvent.pricingSolveTime;
        this.nrGeneratedColumns= finishProcessingNodeEvent.nrGeneratedColumns;
//        this.nrInequalities=finishProcessingNodeEvent.node.getInequalities().size();
//        this.LB=bap.getBound();
        BranchingDecision bd=finishProcessingNodeEvent.node.getBranchingDecision();
        if (bd instanceof RoundQ) {
            this.branchType="Q";
        }

        if (bd instanceof RoundServiceEdge) {
            this.branchType="ServiceEdge";
        }

        if (bd instanceof RoundServiceEdgeForAllPricingProblems) {
            this.branchType="ServiceEdge4All";
        }

        if (bd instanceof RoundLocalService) {
            this.branchType="localService";
        }
        
        if (bd instanceof RoundHoldingEdge) {
            this.branchType="holdingEdge";
        }
        
        if (bd instanceof RoundLocalServiceForAllPricingProblems) {
            this.branchType="localService4All";
        }
        
        if(bd instanceof RoundTimeService) {
        	this.branchType="TimeService";
        }
        
        if(bd instanceof RoundTimeServiceForAllPricingProblems) {
        	this.branchType="TimeService4All";
        }


    }

    @Override
    public void timeLimitExceeded(TimeLimitExceededEvent timeLimitExceededEvent){
        this.nodeStatus =NodeResultStatus.INCONCLUSIVE;
        this.constructAndWriteLine();
    }

    @Override
    public void branchCreated(BranchEvent branchEvent) {
        //Ignore this event, not needed by the logger.
    }

    
    protected enum NodeResultStatus{
        PRUNED, INFEASIBLE, FRACTIONAL, INTEGER, INCONCLUSIVE
    }
    
    
}

