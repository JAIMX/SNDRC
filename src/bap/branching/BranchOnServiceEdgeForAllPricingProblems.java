package bap.branching;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.jorlib.frameworks.columnGeneration.branchAndPrice.AbstractBranchCreator;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.BAPNode;
import org.jorlib.frameworks.columnGeneration.util.MathProgrammingUtil;

import bap.branching.branchingDecisions.RoundServiceEdge;
import bap.branching.branchingDecisions.RoundServiceEdgeForAllPricingProblems;
import cg.Cycle;
import cg.SNDRCPricingProblem;
import model.SNDRC;

public class BranchOnServiceEdgeForAllPricingProblems extends AbstractBranchCreator<SNDRC, Cycle, SNDRCPricingProblem>   {

	private double thresholdValue;
	private int branchEdgeIndex;
	double branchEdgeValue;
	
	public BranchOnServiceEdgeForAllPricingProblems(SNDRC modelData, List<SNDRCPricingProblem> pricingProblems,double thresholdValue) {
		super(modelData, pricingProblems);
		this.thresholdValue=thresholdValue;
	}
	
	@Override
	protected boolean canPerformBranching(List<Cycle> solution) {
		//Reset values
		branchEdgeIndex=-1;
		branchEdgeValue=0;
		boolean isAllInteger=true;
		double bestDifference=1;
		
		double[] serviceEdgeCount=new double[dataModel.numServiceArc];
		for(Cycle cycle:solution) {
			Set<Integer> cycleEdgeIndexSet=cycle.edgeIndexSet;
			double value=cycle.value;
			for(int edgeIndex:cycleEdgeIndexSet) {
				if(dataModel.edgeSet.get(edgeIndex).edgeType==0) {
					serviceEdgeCount[edgeIndex]+=value;
				}
			}
		}
		
		//Select a service edge whose value is  closest to threshold value
		for(int edgeIndex=0;edgeIndex<dataModel.numServiceArc;edgeIndex++) {
			double currentEdgeValue=serviceEdgeCount[edgeIndex];
			if(MathProgrammingUtil.isFractional(currentEdgeValue)) {
				isAllInteger=false;
				double decimalPart=currentEdgeValue-Math.floor(currentEdgeValue);
				if(Math.abs(thresholdValue-decimalPart)<bestDifference) {
					branchEdgeIndex=edgeIndex;
					branchEdgeValue=currentEdgeValue;
					bestDifference=Math.abs(thresholdValue-decimalPart);
				}
			}
		}
		
		return (!isAllInteger);
		
		
		
	}
	
	
	@Override
	protected List<BAPNode<SNDRC,Cycle>> getBranches(BAPNode<SNDRC,Cycle> parentNode){
		//Branch 1:round q down to the nearest integer
//		RoundServiceEdge branchingDecision1=new RoundServiceEdge(0,branchEdgeIndex,branchEdgeValue);
		RoundServiceEdgeForAllPricingProblems branchingDecision1=new RoundServiceEdgeForAllPricingProblems(0,branchEdgeIndex,branchEdgeValue);
		BAPNode<SNDRC,Cycle> node1=this.createBranch(parentNode, branchingDecision1, parentNode.getSolution(), parentNode.getInequalities());
		
		
		//Branch 1:round q down to the nearest integer
//		RoundServiceEdge branchingDecision2=new RoundServiceEdge(1,branchEdgeIndex,branchEdgeValue);
		RoundServiceEdgeForAllPricingProblems branchingDecision2=new RoundServiceEdgeForAllPricingProblems(1,branchEdgeIndex,branchEdgeValue);
		BAPNode<SNDRC,Cycle> node2=this.createBranch(parentNode, branchingDecision2, parentNode.getSolution(), parentNode.getInequalities());
		
		return Arrays.asList(node2,node1);
	}
}
