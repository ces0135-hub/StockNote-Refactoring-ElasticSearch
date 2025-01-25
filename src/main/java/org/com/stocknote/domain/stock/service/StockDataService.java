package org.com.stocknote.domain.stock.service;
import com.opencsv.CSVReader;
import jakarta.annotation.PostConstruct;
import org.com.stocknote.domain.stock.dto.response.StockInfoResponse;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.domain.stock.repository.StockRepository;
import org.com.stocknote.global.error.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileReader;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class StockDataService {
    @Autowired
    private StockRepository stockRepository;

    @PostConstruct
    public void loadCsvData() {
        String filePath = "src/main/resources/info.csv"; // CSV 파일 경로
        try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
            List<String[]> records = csvReader.readAll();
            records.forEach(record -> {
                Stock stock = new Stock();
                stock.setName(record[0]);
                stock.setCode(record[1]);
                stockRepository.save(stock);
            });
            System.out.println("CSV 데이터가 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("CSV 데이터를 읽는 중 오류가 발생했습니다.");
        }
    }

    //주식종목 검색
    public StockInfoResponse findStock(String name) {
        return stockRepository.findByName(name)
                .map(StockInfoResponse::of)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.STOCK_NOT_FOUND.getMessage()));
    }


}
