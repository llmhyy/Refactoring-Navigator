/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.bean;

import java.util.ArrayList;

import reflexactoring.diagram.bean.programmodel.ProgramReference;
import reflexactoring.diagram.bean.programmodel.UnitMemberWrapper;

/**
 * @author linyun
 *
 */
public class CloneInstance {
	
	private CloneSet set;
	
	private String fileName;
	private int startLineNumber;
	private int endLineNumber;
	
	private ArrayList<ProgramReference> coveringReferenceList = new ArrayList<>();
	private UnitMemberWrapper member;
	
	/**
	 * @param fileName
	 * @param startLineNumber
	 * @param endLineNumber
	 */
	public CloneInstance(String fileName, int startLineNumber, int endLineNumber) {
		super();
		this.fileName = fileName;
		this.startLineNumber = startLineNumber;
		this.endLineNumber = endLineNumber;
	}
	/**
	 * @param set
	 * @param fileName
	 * @param startLineNumber
	 * @param endLineNumber
	 */
	public CloneInstance(CloneSet set, String fileName, int startLineNumber,
			int endLineNumber) {
		super();
		this.set = set;
		this.fileName = fileName;
		this.startLineNumber = startLineNumber;
		this.endLineNumber = endLineNumber;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof CloneInstance){
			CloneInstance thatInstance = (CloneInstance)obj;
			return thatInstance.getFileName().equals(this.getFileName())
					&& thatInstance.getStartLineNumber() == this.getStartLineNumber()
					&& thatInstance.getEndLineNumber() == this.getEndLineNumber();
		}
		
		return false;
	}
	
	public int getLength(){
		return this.endLineNumber - this.startLineNumber + 1;
	}
	
	@Override
	public String toString(){
		String simpleName = fileName.substring(fileName.lastIndexOf("\\")+1, fileName.length());
		return simpleName + "[" + this.startLineNumber + "," + this.endLineNumber + "]";
	}
	
	/**
	 * @return the set
	 */
	public CloneSet getSet() {
		return set;
	}
	/**
	 * @param set the set to set
	 */
	public void setSet(CloneSet set) {
		this.set = set;
	}
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return the startLineNumber
	 */
	public int getStartLineNumber() {
		return startLineNumber;
	}
	/**
	 * @param startLineNumber the startLineNumber to set
	 */
	public void setStartLineNumber(int startLineNumber) {
		this.startLineNumber = startLineNumber;
	}
	/**
	 * @return the endLineNumber
	 */
	public int getEndLineNumber() {
		return endLineNumber;
	}
	/**
	 * @param endLineNumber the endLineNumber to set
	 */
	public void setEndLineNumber(int endLineNumber) {
		this.endLineNumber = endLineNumber;
	}
	/**
	 * @return the coveringReferenceList
	 */
	public ArrayList<ProgramReference> getCoveringReferenceList() {
		return coveringReferenceList;
	}
	/**
	 * @param coveringReferenceList the coveringReferenceList to set
	 */
	public void setCoveringReferenceList(
			ArrayList<ProgramReference> coveringReferenceList) {
		this.coveringReferenceList = coveringReferenceList;
	}
	/**
	 * @return the member
	 */
	public UnitMemberWrapper getMember() {
		return member;
	}
	/**
	 * @param member the member to set
	 */
	public void setMember(UnitMemberWrapper member) {
		this.member = member;
	}
	
	
}
