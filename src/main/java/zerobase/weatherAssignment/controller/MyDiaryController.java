package zerobase.weatherAssignment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import zerobase.weatherAssignment.domain.Mydiary;
import zerobase.weatherAssignment.service.MyDiaryService;

import java.util.List;
import java.time.LocalDate;

@RestController
public class MyDiaryController {

    private final MyDiaryService myDiaryService;

    public MyDiaryController(MyDiaryService myDiaryService) {
        this.myDiaryService = myDiaryService;
    }

    @PostMapping("/create/diary") //성공
    void createDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "작성 하고싶은 기간의 날짜", example = "2024-05-30") LocalDate date, @RequestBody @Parameter(description = "일기 내용", example = "오늘은 코딩을 했다") String text) {
        myDiaryService.createMyDiary(date, text);
    }

    @Operation(summary  = "선택한 날짜의 모든 일기 데이터를 가져옵니다")
    @GetMapping("/read/diary")//성공
    List<Mydiary> readDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "조회할 기간의 날짜", example = "2024-05-30")LocalDate date){
        return myDiaryService.readMyDiary(date);
    }

    @Operation(summary  = "선택한 기간중의 모든 일기 데이터를 가져옵니다")
    @GetMapping("/read/diaries")//성공
    List<Mydiary> readDiaries(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "조회할 기간의 첫번째 날", example = "2024-05-30") LocalDate firstDate,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "조회할 기간의 마지막 날", example = "2024-06-01") LocalDate lastDate){
        return myDiaryService.readMyDiaries(firstDate,lastDate);
    }

    @Operation(summary  = "작성한 일기를 업데이트 합니다")
    @PutMapping("/update/diary")//성공
    void updateDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "업데이트 할 일기의 날짜", example = "2024-05-30")LocalDate date, @RequestBody String text){
         myDiaryService.updateMyDiary(date, text);
    }

    @Operation(summary  = "일기를 삭제합니다")
    @DeleteMapping("/delete/diary")//성공
    void deleteDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "삭제하고 싶은 일기의 날짜", example = "2024-05-30")LocalDate date){
        myDiaryService.deleteMyDiary(date);
    }

}
