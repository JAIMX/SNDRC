package bap.branching;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.jorlib.frameworks.columnGeneration.branchAndPrice.AbstractBranchCreator;
import org.jorlib.frameworks.columnGeneration.branchAndPrice.BAPNode;
import org.jorlib.frameworks.columnGeneration.util.MathProgrammingUtil;

import bap.branching.branchingDecisions.RoundServiceEdge;
import cg.Cycle;
import cg.SNDRCPricingProblem;
import model.SNDRC;

/**
 * Class which creates new branches in the branch-and-price tree. This
 * particular class branches on a service edge.More precisely, the class checks
 * whether there is a fractional service edge in the solution. We branch by identifying
 * service edge with fractional value closest to a threshold value and round it
 * down and up to the nearest integer.
 * @author Helen
 *
 */
public class BranchOnServiceEdge extends AbstractBranchCreator<SNDRC, Cycle, SNDRCPricingProblem>   {
	
	private double thresholdValue;
	private int branchEdgeIndex;
	double branchEdgeValue;
	SNDRCPricingProblem branchPricingProblem;
	
	public BranchOnServiceEdge(SNDRC modelData, List<SNDRCPricingProblem> pricingProblems,double thresholdValue) {
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
		
		for(SNDRCPricingProblem pricingProblem:pricingProblems) {
			double[] serviceEdgeCount=new double[dataModel.numServiceArc];
			
			for(Cycle cycle:solution) {
				if(cycle.associatedPricingProblem==pricingProblem) {
					
					Set<Integer> cycleEdgeIndexSet=cycle.edgeIndexSet;
					double value=cycle.value;
					for(int edgeIndex:cycleEdgeIndexSet) {
						if(dataModel.edgeSet.get(edgeIndex).edgeType==0) {
							serviceEdgeCount[edgeIndex]+=value;
						}
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
						branchPricingProblem=pricingProblem;
						branchEdgeIndex=edgeIndex;
						branchEdgeValue=currentEdgeValue;
						bestDifference=Math.abs(thresholdValue-decimalPart);
					}
				}
			}
			
			
		}
		
		
		
		
//		double[] serviceEdgeCount=new double[dataModel.numServiceArc];
//		for(Cycle cycle:solution) {
//			Set<Integer> cycleEdgeIndexSet=cycle.edgeIndexSet;
//			double value=cycle.value;
//			for(int edgeIndex:cycleEdgeIndexSet) {
//				if(dataModel.edgeSet.get(edgeIndex).edgeType==0) {
//					serviceEdgeCount[edgeIndex]+=value;
//				}
//			}
//		}
		
		
		
//		//Select a service edge whose value is  closest to threshold value
//		for(int edgeIndex=0;edgeIndex<dataModel.numServiceArc;edgeIndex++) {
//			double currentEdgeValue=serviceEdgeCount[edgeIndex];
//			if(MathProgrammingUtil.isFractional(currentEdgeValue)) {
//				isAllInteger=false;
//				double decimalPart=currentEdgeValue-Math.floor(currentEdgeValue);
//				if(Math.abs(thresholdValue-decimalPart)<bestDifference) {
//					branchEdgeIndex=edgeIndex;
//					branchEdgeValue=currentEdgeValue;
//					bestDifference=Math.abs(thresholdValue-decimalPart);
//				}
//			}
//		}
		
		return (!isAllInteger);
		
		
	}
	
	
	@Override
	protected List<BAPNode<SNDRC,Cycle>> getBranches(BAPNode<SNDRC,Cycle> parentNode){
	    

//	    if(parentNode.nodeID==19){                                                                                                                        
//	        System.out.println(branchEdgeValue);
//	        System.out.println("original Node: "+branchPricingProblem.originNodeO);
//	        System.out.println("capacity type: "+branchPricingProblem.capacityTypeS);
//	        System.out.println("edge index: "+branchEdgeIndex+" "+dataModel.edgeSet.get(branchEdgeIndex).start+"->"+dataModel.edgeSet.get(branchEdgeIndex).end);
//	    }
		//Branch 1:round  down to the nearest integer
//		RoundServiceEdge branchingDecision1=new RoundServiceEdge(0,branchEdgeIndex,branchEdgeValue);
		RoundServiceEdge branchingDecision1=new RoundServiceEdge(0,branchEdgeIndex,branchEdgeValue,branchPricingProblem);
		BAPNode<SNDRC,Cycle> node1=this.createBranch(parentNode, branchingDecision1, parentNode.getSolution(), parentNode.getInequalities());
		
		
		//Branch 1:round up to the nearest integer
//		RoundServiceEdge branchingDecision2=new RoundServiceEdge(1,branchEdgeIndex,branchEdgeValue);
		RoundServiceEdge branchingDecision2=new RoundServiceEdge(1,branchEdgeIndex,branchEdgeValue,branchPricingProblem);
		BAPNode<SNDRC,Cycle> node2=this.createBranch(parentNode, branchingDecision2, parentNode.getSolution(), parentNode.getInequalities());
		
//		return Arrays.asList(node2,node1);
		return Arrays.asList(node1,node2);
	}
	
	
	
}
