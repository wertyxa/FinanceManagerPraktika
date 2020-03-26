package com.finance.manager.FinanceManager.repository;

import com.finance.manager.FinanceManager.models.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Long> {}