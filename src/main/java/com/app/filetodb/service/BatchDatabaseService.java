package com.app.filetodb.service;

import com.app.filetodb.component.ErrorHandler;
import jakarta.transaction.Synchronization;
import org.apache.commons.collections.bag.SynchronizedBag;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BooleanSupplier;

@Service
public class BatchDatabaseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ReportingService reportingService;

    @Autowired
    private ErrorHandler errorHandler;

    private static final int BATCH_SIZE = 1000;
    private static final int THREAD_POOL_SIZE = 5;

    //ExecutorService instance with fixed thread pool size for parallel batch entries.
    //private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    //Single ExecutorService instance for sequential batch entries.
    //private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void insertData(List<String[]> rows) {
        String insertQuery = "INSERT INTO csv_data (id, description, name, value_1, value_2, value_3, value_4, company, item, value_5) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        for (int i = 0; i < rows.size(); i += BATCH_SIZE) {
            List<String[]> batch = rows.subList(i, Math.min(i + BATCH_SIZE, rows.size()));

            if(!processBatch(batch, insertQuery)){
                System.out.println((i+BATCH_SIZE)/BATCH_SIZE +" batch processing failed");
            }else {
                System.out.println((i+BATCH_SIZE)/BATCH_SIZE +" batch processed");
            }

            // Submit batch processing to ExecutorService
//            int finalI = i;
//            executorService.submit(() -> {
//                if(!processBatch(batch, insertQuery)){
//                    System.out.println((finalI +BATCH_SIZE)/BATCH_SIZE +" batch processing failed");
//                }else {
//                    System.out.println((finalI +BATCH_SIZE)/BATCH_SIZE +" batch processed");
//                }
//            });

        }
//        executorService.shutdown();
//
//        while(!executorService.isTerminated()){}
//        System.out.println("Executor is terminated");
    }


    private boolean processBatch(List<String[]> batch, String insertQuery){
        try {
            for(String[] row: batch){
                try{
                    jdbcTemplate.execute(insertQuery, new PreparedStatementCallback() {
                        @Override
                        public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                            for(int k = 0; k <row.length; k++){
                                ps.setString(k+1, row[k]);
                            }
                            return ps.execute();
                        }
                    });
                    reportingService.recordSuccess();

                } catch (Exception e){
                    errorHandler.logError(row, new Exception("Insertion failed || "+e.getMessage()));
                    reportingService.recordFailure();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
