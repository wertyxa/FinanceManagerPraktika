package com.finance.manager.FinanceManager.controllers;

import com.finance.manager.FinanceManager.models.Category;
import com.finance.manager.FinanceManager.models.Transaction;
import com.finance.manager.FinanceManager.repository.CategoryRepository;
import com.finance.manager.FinanceManager.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Controller
public class FinanceManagerController {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/admin")
    public String admin(Model model){

        return "admin";
    }


    @GetMapping("/admin/addCategory")
    public String addCategory(Model model){
        return "addCategory";
    }
    @PostMapping("admin/addCategory")
    public String addCategory( @RequestParam String name, @RequestParam String description, Model model){
        Category category = new Category(name,description);
        categoryRepository.save(category);

        return "addCategory";

    }
    @GetMapping("/admin/addTransaction")
    public String addTransactionView(Model model){
        Iterable<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "addTransaction";
    }
    @PostMapping("admin/addTransaction")
    public String addTransactionForm(@RequestParam String category,
                                     @RequestParam String type,
                                     @RequestParam Integer price,
                                     @RequestParam String description,
                                     @RequestParam String date,
                                     Model model) throws ParseException {
        System.out.println(category);
        System.out.println(type);
        System.out.println(price);
        System.out.println(description);
        System.out.println(date);
        Transaction transaction = new Transaction(category, type, price, description, date);
        transactionRepository.save(transaction);


        return "addTransaction";
    }

    @GetMapping("/admin/allTransaction")
    public String allTransaction(Model model){
        Iterable<Transaction> transactions = transactionRepository.findAll();
        model.addAttribute("transactions",transactions);

        return "allTransaction";
    }

    @GetMapping("/admin/allCategory")
    public String allCategories(Model model){
        model.addAttribute("categories",categoryRepository.findAll());

        return "allCategories";
    }

    @GetMapping("/admin/allTransaction/delete/{id}")
    public String deleteTransaction(@PathVariable(value = "id") long id, Model model){
        transactionRepository.deleteById(id);
        return "redirect:/admin/allTransaction";
    }
    @GetMapping("/admin/allCategory/delete/{id}")
    public String deleteCategory(@PathVariable(value = "id") long id, Model model){
        categoryRepository.deleteById(id);
        return "redirect:/admin/allCategory";
    }
    //Edit Category
    @GetMapping("/admin/allCategory/edit/{id}")
    public String editCategory(@PathVariable(value = "id") long id,
                               Model model) throws Exception {
        Optional<Category> category = categoryRepository.findById(id);
        ArrayList<Category> arrayList = new ArrayList<>();
        category.ifPresent(arrayList::add);
     model.addAttribute("categoris", arrayList);

        return "editCategory";
    }
    @PostMapping("/admin/allCategory/edit/{id}")
    public String editCategory(@PathVariable(value = "id") long id,
                               @RequestParam String name,
                               @RequestParam String description,
                               Model model) throws Exception {
        Category category = categoryRepository.findById(id).orElseThrow(Exception::new);
        category.setName(name);
        category.setDescription(description);
        categoryRepository.save(category);
        return "redirect:/admin/allCategory";
    }

//Edit Transaction

    @GetMapping("/admin/allTransaction/edit/{id}")
    public String editTransaction(@PathVariable(value = "id") long id,
                               Model model) throws Exception {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        ArrayList<Transaction> arrayList = new ArrayList<>();
        transaction.ifPresent(arrayList::add);
        model.addAttribute("transaction", arrayList);

        Iterable<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "editTransaction";
    }

    @PostMapping("/admin/allTransaction/edit/{id}")
    public String editTransaction(@PathVariable(value = "id") long id,
                                  @RequestParam String category,
                                  @RequestParam String type,
                                  @RequestParam double price,
                                  @RequestParam String description,
                                  @RequestParam String date,
                                  Model model) throws Exception {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(Exception::new);
                transaction.setCategory(category);
                transaction.setTypeTransaction(type);
                transaction.setPrice(price);
                transaction.setDescription(description);
                transaction.setDateString(date);
        transactionRepository.save(transaction);

        return "redirect:/admin/allTransaction";
    }
}
