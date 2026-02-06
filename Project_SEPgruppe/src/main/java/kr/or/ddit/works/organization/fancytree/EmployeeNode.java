package kr.or.ddit.works.organization.fancytree;

import java.io.Serializable;

import kr.or.ddit.works.approval.fancytree.FancyTreeNode;
import kr.or.ddit.works.organization.vo.EmployeeVO;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmployeeNode implements FancyTreeNode<EmployeeVO>, Serializable {

	private final EmployeeVO adaptee;

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return adaptee.getEmpId();
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return adaptee.getEmpNm();
	}

	@Override
	public boolean isFolder() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EmployeeVO getData() {
		// TODO Auto-generated method stub
		return adaptee;
	}
}
