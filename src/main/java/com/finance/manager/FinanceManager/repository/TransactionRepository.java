package com.finance.manager.FinanceManager.repository;

import com.finance.manager.FinanceManager.models.Transaction;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {}
