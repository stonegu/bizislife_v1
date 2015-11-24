package com.bizislife.core.controller.component;

import java.util.ArrayList;
import java.util.List;

import com.bizislife.core.controller.component.PaginationNode.PaginationNodeType;

//pagination design: Not used!!! 
//<<  <  1  2  ... 9 10 11 12 13 14 15 ... 90 91   >  >>  goto page 3v   items per page 10v         showing 1-10 of 910
// << < ..9 (10) 11 12 .. > >>   goto page 3v   items per page 10v         showing 100-200 of 910


/*
 
 	pagination design: Google style
 	
 	example:
 	<< < (1) 2 3 4 5 6 7 8 9 10 > >>
 	<< < 1 2 3 4 5 (6) 7 8 9 10 > >>
 	<< < 4 5 6 7 8 (9) 10 11 12 13 > >>
 	
 
*/




public class Pagination {
	
	List<PaginationNode> paginationNodes;
	
	String viewAllUrl;
	String extraInfo;

	public Pagination() {
		super();
	}

	public Pagination(List<PaginationNode> paginationNodes) {
		super();
		this.paginationNodes = paginationNodes;
	}

	public String getViewAllUrl() {
		return viewAllUrl;
	}

	public void setViewAllUrl(String viewAllUrl) {
		this.viewAllUrl = viewAllUrl;
	}

	public String getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

	public List<PaginationNode> getPaginationNodes() {
		return paginationNodes;
	}

	public void setPaginationNodes(List<PaginationNode> paginationNodes) {
		this.paginationNodes = paginationNodes;
	}
	
	// get "<<" node
	public PaginationNode getGoFirstNode(){
		if(this.paginationNodes!=null && this.paginationNodes.size()>0){
			for(PaginationNode n : this.paginationNodes){
				if(n.getType().equals(PaginationNodeType.goFirst)){
					return n;
				}
			}
		}
		
		return null;
	}
	
	// get "<" node
	public PaginationNode getGoPreviousNode(){
		if(this.paginationNodes!=null && this.paginationNodes.size()>0){
			for(PaginationNode n : this.paginationNodes){
				if(n.getType().equals(PaginationNodeType.goPrevious)){
					return n;
				}
			}
		}
		return null;
	}
	
	// get ">" node
	public PaginationNode getGoNextNode(){
		if(this.paginationNodes!=null && this.paginationNodes.size()>0){
			for(PaginationNode n : this.paginationNodes){
				if(n.getType().equals(PaginationNodeType.goNext)){
					return n;
				}
			}
		}
		return null;
	}
	
	// get ">>" node
	public PaginationNode getGoLastNode(){
		if(this.paginationNodes!=null && this.paginationNodes.size()>0){
			for(PaginationNode n : this.paginationNodes){
				if(n.getType().equals(PaginationNodeType.goLast)){
					return n;
				}
			}
		}
		
		return null;
	}
	
	public PaginationNode getCurrentNode(){
		if(this.paginationNodes!=null && this.paginationNodes.size()>0){
			for(PaginationNode n : this.paginationNodes){
				if(n.isCurrentNode()){
					return n;
				}
			}
		}
		
		return null;
	}
	
	// return 1, 2, 3 ... , all numbers
	public List<PaginationNode> getPageNumberList(){
		
		if(this.paginationNodes!=null && this.paginationNodes.size()>0){
			List<PaginationNode> numberList = new ArrayList<PaginationNode>();
			for(PaginationNode n : this.paginationNodes){
				if(n.getType().equals(PaginationNodeType.number)){
					numberList.add(n);
				}
			}
			if(numberList.size()>0) return numberList;
		}
		
		return null;
	}
	
	public void addPaginationNode(PaginationNode node){
		if(node!=null){
			if(this.paginationNodes==null) this.paginationNodes = new ArrayList<PaginationNode>();
			this.paginationNodes.add(node);
		}
		
	}
	

	@Override
	public String toString() {
		return "Pagination [paginationNodes=" + paginationNodes + "]";
	}
	
	

}
