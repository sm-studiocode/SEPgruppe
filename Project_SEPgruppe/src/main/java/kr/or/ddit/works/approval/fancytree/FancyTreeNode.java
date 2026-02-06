package kr.or.ddit.works.approval.fancytree;

public interface FancyTreeNode<T> {
	
	public String getKey();
	public String getTitle();
	public boolean isFolder();
	public default boolean isLazy() {
		return isFolder();
	}
	public T getData();
}
