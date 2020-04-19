package com.finance.manager.FinanceManager.ReadWriteExelData;

import com.finance.manager.FinanceManager.FinanceManagerApplication;
import com.finance.manager.FinanceManager.models.Transaction;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class ExelData {
    @Value("${upload.path}")
    private static String uploadPath;

    public static File importDataToExelFile(Iterable<Transaction> transactions) throws IOException {


        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("AllTransaction");
        int rowCounter = 1;
        Row row = sheet.createRow(0);
        Cell cell1 =row.createCell(0);
        Cell cell2 =row.createCell(1);
        Cell cell3 =row.createCell(2);
        Cell cell4 =row.createCell(3);
        Cell cell5 =row.createCell(4);
        Cell cell6 =row.createCell(5);
        cell1.setCellValue("ID");
        cell2.setCellValue("Категорія");
        cell3.setCellValue("Дата");
        cell4.setCellValue("Опис");
        cell5.setCellValue("Ціна");
        cell6.setCellValue("Тип");
        for (Transaction  transaction:transactions) {


            Row rowT = sheet.createRow(rowCounter);
            Cell cellT1 =rowT.createCell(0);
            Cell cellT2 =rowT.createCell(1);
            Cell cellT3 =rowT.createCell(2);
            Cell cellT4 =rowT.createCell(3);
            Cell cellT5 =rowT.createCell(4);
            Cell cellT6 =rowT.createCell(5);
            cellT1.setCellValue(transaction.getId());
            cellT2.setCellValue(transaction.getCategory());
            cellT3.setCellValue(transaction.getDate());
            cellT4.setCellValue(transaction.getDescription());
            cellT5.setCellValue(transaction.getPrice());
            cellT6.setCellValue(transaction.getTypeTransaction());

            rowCounter++;
        }
        UUID uuid = UUID.randomUUID();
        File file = new File(uploadPath+ "/"+uuid+".xlsx");
        FileOutputStream outputStream = new FileOutputStream(file);
        workbook.write(outputStream);
        outputStream.close();
        return file;
    }

}
