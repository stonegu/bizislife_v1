package com.bizislife.core.hibernate.dao;

import java.util.*;

import com.bizislife.core.hibernate.pojo.*;
import com.bizislife.core.view.dto.*;

public interface AccountDao {
	
	public List<Account> findAccountsByIds(String accountIds);
	
	public List<Account> findAccountsByLoginName(String name);
	
	public List<AccountDto> findAccountsInOrg(Long orgId);

	public AccountDto getAccountById(Long accountId);
	
	public Account getAccountByUuid(String uuid);
	
	public Account getAccountPojoById(Long accountId);
	
	public AccountDto getAccountByUsernameAndPassword(String username, String password);
	
	public Accountprofile getAccountProfileByAccountId(Long accountId);
	
	public Long saveAccount(Account account);
	
	public Long saveAccountProfile(Accountprofile profile);
	
	public AccountDto transferToDto(Account account);
	
}
