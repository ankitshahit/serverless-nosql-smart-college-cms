package io.college.cms.core.faq.service;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import io.college.cms.core.application.ConnectionService;
import io.college.cms.core.configuration.AppParams;
import io.college.cms.core.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class FaqBotService {
	public static final String QNA_MAKER_GENERATE_ANWSER = "https://westus.api.cognitive.microsoft.com/qnamaker/v2.0/knowledgebases/04bb6ca5-0758-4d24-a214-e01236fcac3b/generateAnswer";
	public static final String QUESTION_KEY = "question";
	public static final String ANSWERS_KEY = "answers";
	public static final String INDIVIDUAL_ANSWER_KEY = "answer";
	public static final String SCORE_KEY = "score";
	public static final String DEFAULT_BOT_MSG = "Sorry! I couldn't find relevant information. Try narrowing combination of question";
	private AppParams params;
	private ConnectionService connectionService;

	@Autowired
	public FaqBotService(AppParams params) {
		this.params = params;
	}

	@Autowired
	public void setConnectionService(ConnectionService connectionService) {
		this.connectionService = connectionService;
	}

	public Map<String, String> getQnaHeaders() {
		Map<String, String> map = new HashMap<>();
		map.put("Content-Type", "application/json");
		map.put("Ocp-Apim-Subscription-Key", params.getQnaMakerKey());
		return map;
	}

	public String getFirstAnswer(String value) throws ApplicationException {
		String escapedValue = HtmlUtils.htmlEscape(value);
		String answers = new String(DEFAULT_BOT_MSG);
		try {
			JSONObject requestBody = new JSONObject();
			requestBody.put(QUESTION_KEY, escapedValue);

			JSONObject responseBody = new JSONObject(
					connectionService.httpsPost(QNA_MAKER_GENERATE_ANWSER, getQnaHeaders(), requestBody.toString()));
			if (!responseBody.has(ANSWERS_KEY)) {
				return answers;
			}
			JSONArray array = (JSONArray) responseBody.get(ANSWERS_KEY);

			if (array == null) {
				return answers;
			}
			JSONObject firstAnswer = (JSONObject) array.get(0);
			if (firstAnswer == null || !firstAnswer.has(INDIVIDUAL_ANSWER_KEY)) {
				return answers;
			}
			answers = (new StringBuilder().append(" ").append(String.valueOf(firstAnswer.get(INDIVIDUAL_ANSWER_KEY)))
					.append(" <sub>(<small style=color:red>C-score: ").append(firstAnswer.get(SCORE_KEY))
					.append("</small>)</sub>").toString());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new ApplicationException(e);
		}
		return answers;
	}

}
