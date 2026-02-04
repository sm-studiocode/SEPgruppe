package kr.or.ddit.works.admin.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.or.ddit.works.company.vo.CompanyVO;
import kr.or.ddit.works.mybatis.mappers.CompanyMapper;
import kr.or.ddit.works.mybatis.mappers.EmployeeMapper;
import kr.or.ddit.works.mybatis.mappers.SubscriptionMapper;
import kr.or.ddit.works.subscription.vo.SubscriptionsVO;

@Service
public class AdminPageServiceImpl implements AdminPageService{
	
	@Autowired
	private EmployeeMapper empMapper;
	
	@Autowired
	private CompanyMapper comMapper;
	
	@Autowired
	private SubscriptionMapper subMapper;

	@Override
	public Map<String, Object> getAdminpageDate(String companyNo) {
		
	    int countEmp = empMapper.countAllEmployees(companyNo, null);
		int countActiveEmp = empMapper.countActiveEmployees(companyNo);
	    CompanyVO company = comMapper.selectCompany(companyNo);

	    SubscriptionsVO subStart = null;
	    if (company != null) {
	        subStart = subMapper.selectSubscription(company.getContactId());
	    }
	    
        Map<String, Object> data = new HashMap<>();
        data.put("countEmp", countEmp);
        data.put("countActiveEmp", countActiveEmp);
        data.put("company", company);
        data.put("subStart", subStart);
        return data;

	}
	
}
