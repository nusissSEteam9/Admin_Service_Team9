package nus.iss.se.team9.admin_service_team9.service;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import org.springframework.stereotype.Service;
import com.opencsv.CSVWriter;

@Service
public class CsvService {

    public <T> void generateCsv(List<T> data, String csvName) throws IOException {
        String csvFileName = csvName + ".csv";

        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFileName))) {
            if (!data.isEmpty()) {
                T firstEntity = data.getFirst();
                Field[] fields = firstEntity.getClass().getDeclaredFields();

                // 创建头部行
                String[] headers = new String[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    headers[i] = fields[i].getName();
                }
                writer.writeNext(headers);

                // 写入数据行
                for (T entity : data) {
                    String[] rowData = new String[fields.length];
                    for (int i = 0; i < fields.length; i++) {
                        fields[i].setAccessible(true);
                        try {
                            Object value = fields[i].get(entity);
                            rowData[i] = (value != null) ? value.toString() : "";
                        } catch (IllegalAccessException e) {
                            System.out.println(e.getMessage()); // 处理异常
                        }
                    }
                    writer.writeNext(rowData);
                }
            }
        }
    }
}

