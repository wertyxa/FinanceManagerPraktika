package com.finance.manager.FinanceManager.controllers;

import com.finance.manager.FinanceManager.ReadWriteExelData.ExelData;
import com.finance.manager.FinanceManager.models.Category;
import com.finance.manager.FinanceManager.models.Transaction;
import com.finance.manager.FinanceManager.repository.CategoryRepository;
import com.finance.manager.FinanceManager.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Controller
public class FinanceManagerController {


    @Value("${upload.path}")
    private String uploadPath;


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

        return "redirect:/addCategory";

    }
    @GetMapping("/admin/addTransaction")
    public String addTransactionView(Model model){
        Iterable<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "redirect:/addTransaction";
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
    public String allTransaction(Model model) throws IOException {
        Iterable<Transaction> transactions = transactionRepository.findAll();
        model.addAttribute("transactions",transactions);
        //1 param - array Transaction
        //2 param - absolute path: C:/folder/
        //3 param - the file name without its extension: workbook
       // ExelData.importDataToExelFile(transactions, "C:/Finance/","All Transactions");
        return "allTransaction";
    }


    @RequestMapping("/admin/allTransaction/exelDownload")
    public void downloadExelResource(HttpServletRequest request, HttpServletResponse response
    ) throws IOException {

        File file = ExelData.importDataToExelFile(transactionRepository.findAll());

        if (file.exists()) {

            //get the mimetype
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            System.out.println(mimeType);
            if (mimeType == null) {
                //unknown mimetype so set the mimetype to application/octet-stream
                mimeType = "application/octet-stream";
                System.out.println(mimeType);

            }

            response.setContentType(mimeType);

            /**
             * In a regular HTTP response, the Content-Disposition response header is a
             * header indicating if the content is expected to be displayed inline in the
             * browser, that is, as a Web page or as part of a Web page, or as an
             * attachment, that is downloaded and saved locally.
             *
             */

            /**
             * Here we have mentioned it to show inline
             */
            response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

            //Here we have mentioned it to show as attachment
            // response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + file.getName() + "\""));

            response.setContentLength((int) file.length());

            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

            FileCopyUtils.copy(inputStream, response.getOutputStream());
            file.delete();
        }else {
            System.out.println(file.getPath());
        }
    }


    @GetMapping("/admin/allCategory")
    public String allCategories(Model model){
        model.addAttribute("categories",categoryRepository.findAll());

        return "allCategories";
    }

    @GetMapping("/admin/allTransaction/{id}/delete/")
    public String deleteTransaction(@PathVariable(value = "id") long id, Model model){
        transactionRepository.deleteById(id);
        return "redirect:/admin/allTransaction";
    }
    @GetMapping("/admin/allCategory/{id}/delete/")
    public String deleteCategory(@PathVariable(value = "id") long id, Model model){
        categoryRepository.deleteById(id);
        return "redirect:/admin/allCategory";
    }
    //Edit Category
    @GetMapping("/admin/allCategory/{id}/edit/")
    public String editCategory(@PathVariable(value = "id") long id,
                               Model model) throws Exception {
        Optional<Category> category = categoryRepository.findById(id);
        ArrayList<Category> arrayList = new ArrayList<>();
        category.ifPresent(arrayList::add);
     model.addAttribute("categoris", arrayList);

        return "editCategory";
    }
    @PostMapping("/admin/allCategory/{id}/edit/")
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

    @GetMapping("/admin/allTransaction/{id}/edit/")
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

    @PostMapping("/admin/allTransaction/{id}/edit/")
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

    @PostMapping("/admin/allTransaction/exelUpload")
    public String uploadFile(Model model,
                             @RequestParam MultipartFile file) throws IOException {
        if (file != null && !file.getOriginalFilename().isEmpty()){
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()){
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath+"/"+resultFileName));
        }
        return "redirect:/admin/allTransaction";
    }
    
}
