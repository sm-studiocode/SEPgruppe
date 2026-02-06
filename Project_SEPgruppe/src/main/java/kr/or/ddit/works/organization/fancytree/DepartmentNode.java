package kr.or.ddit.works.organization.fancytree;

import java.io.Serializable;

import kr.or.ddit.works.approval.fancytree.FancyTreeNode;
import kr.or.ddit.works.organization.vo.DepartmentVO;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DepartmentNode implements FancyTreeNode<DepartmentVO>, Serializable {

	private final DepartmentVO adaptee;

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return adaptee.getDeptCd();
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return adaptee.getDeptName();
	}

	@Override
	public boolean isFolder() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public DepartmentVO getData() {
		// TODO Auto-generated method stub
		return adaptee;
	}
}
