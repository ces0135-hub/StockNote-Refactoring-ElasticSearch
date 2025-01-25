package org.com.stocknote.domain.stock.service;

import org.com.stocknote.domain.stock.dto.response.StockInfoResponse;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.domain.stock.repository.StockRepository;
import org.com.stocknote.global.error.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;

@Service
public class StockDataService {
    @Autowired
    private StockRepository stockRepository;

    @Transactional
    public void saveStockDataFromCsv(String csvFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // CSV 파일의 각 줄에서 데이터를 읽어옴
                String[] values = line.split(",");
                if (values.length == 2) { // name, code
                    String name = values[0].trim();
                    String code = values[1].trim();

                    // Stock 엔티티 생성 및 데이터 저장
                    Stock stock = new Stock();
                    stock.setName(name);
                    stock.setCode(code);
                    stockRepository.save(stock);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to read the CSV file.");
        }
    }

    //주식종목 검색
    public StockInfoResponse findStock(String name) {
        return stockRepository.findByName(name)
                .map(StockInfoResponse::of)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.STOCK_NOT_FOUND.getMessage()));
    }


}
