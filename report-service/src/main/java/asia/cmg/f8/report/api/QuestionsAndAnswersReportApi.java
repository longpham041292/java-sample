package asia.cmg.f8.report.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.util.FileExportUtils;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.report.dto.CounterUsersAnsweredQuestion;
import asia.cmg.f8.report.entity.database.QuestionEntity;
import asia.cmg.f8.report.repository.QuestionRepository;
import asia.cmg.f8.report.service.QuestionsAndAnswersReportService;

@RestController
public class QuestionsAndAnswersReportApi {

	private static final String[] MEMBER_HEADER = {"QuestionKey", "QuestionContent", "CountAnsweredByPT", "CountAnsweredByEU"};
	
	private final String ANSWERED_QUESTIONS_EXPORT_FILENAME = "AnsweredQuestionsExport.xls";
	
	@Autowired
	QuestionsAndAnswersReportService questionAnswerService;
	
	@Autowired
	QuestionRepository questionRepo;
	
	@GetMapping(value = "/admin/v1/answered_question/export")
	@RequiredAdminRole
	public StreamingResponseBody exportAnsweredQuestions(final HttpServletResponse httpResponse) throws IOException {
		try {
			List<CounterUsersAnsweredQuestion> counterList = this.buildingAnsweredQuestionsData();
			
			return FileExportUtils.exportCSV(counterList, MEMBER_HEADER, ANSWERED_QUESTIONS_EXPORT_FILENAME, httpResponse);
		} catch (Exception e) {
			return null;
		}
	}
	
	@GetMapping(value = "/admin/v1/answered_question/report", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public ResponseEntity<Object> reportAnsweredQuestions(final HttpServletResponse httpResponse) throws IOException {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		
		try {
			List<CounterUsersAnsweredQuestion> counterList = this.buildingAnsweredQuestionsData();
			apiResponse.setData(counterList);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	private List<CounterUsersAnsweredQuestion> buildingAnsweredQuestionsData() {
		List<CounterUsersAnsweredQuestion> counterList = new ArrayList<CounterUsersAnsweredQuestion>();
		
		List<QuestionEntity> questions = questionRepo.findByLanguageAndUsedFor("pt", "en");
		if(questions.isEmpty()) {
			return counterList;
		}
		
		// Counting EU/PT answers questions
		questions.forEach(question -> {
			CounterUsersAnsweredQuestion counters = questionAnswerService.countmNumOfUserAnsweredQuestion(question);
			counterList.add(counters);
		});
		
		// Counting EU/PT completed OBW
		CounterUsersAnsweredQuestion countCompletedOBQ = new CounterUsersAnsweredQuestion();
		int countPTCompleted = questionAnswerService.countPTCompletedOBQ();
		int countEUCompleted = questionAnswerService.countEUCompletedOBQ(questions.size());
		countCompletedOBQ.setCountAnsweredByPT(countPTCompleted);
		countCompletedOBQ.setCountAnsweredByEU(countEUCompleted);
		countCompletedOBQ.setQuestionContent("No. Completed On-boarding Questions");
		counterList.add(countCompletedOBQ);
		
		return counterList;
	}
}
