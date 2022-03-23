package com.engwili.arfe.client.web;

import com.engwili.arfe.dto.response.ReportSummary;
import com.engwili.arfe.service.ReportClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

@RestController
@RequiredArgsConstructor
@Log4j2
public class ReportController {

    private final ReportClientService reportClientService;
    private final ExecutorService executorService;

    @GetMapping(path = "/report", produces = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<ResponseEntity<ReportSummary>> getReport() {

        DeferredResult<ResponseEntity<ReportSummary>> output = new DeferredResult<>();
        executorService.submit(() -> {
            try {
                var s = reportClientService.getReportSummary();
                output.setResult(ResponseEntity.ok().body(s));
            } catch (ExecutionException | InterruptedException e) {
                log.info(e.getMessage());
                e.printStackTrace();
            }
        });
        return output;
    }
}