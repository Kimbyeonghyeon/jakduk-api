package com.jakduk.trigger;

import com.jakduk.model.db.Token;
import com.jakduk.repository.TokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

@EnableScheduling
@Slf4j
public class TokenTerminationTrigger {

	@Autowired
	private TokenRepository tokenRepository;

	private long span;

	public void init() {
		terminateToken();
	}

	@Scheduled(cron = "0 */5 * * * *")
	public void terminateToken() {
		// 기한 만료된 토큰 삭제
		Pageable request = new PageRequest(0, 100, new Sort(Sort.Direction.ASC, "createdTime"));
		Page<Token> page = tokenRepository.findAll(request);
		while (page.getTotalElements() > 0) {
			page.getContent().stream().filter(token -> token.getCreatedTime().getTime() + span <= System.currentTimeMillis()).forEach(token -> {
				tokenRepository.delete(token.getEmail());
			});
			request = request.next();
			page = tokenRepository.findAll(request);
		}
	}

	public void setSpan(long minutes) {
		span = TimeUnit.MINUTES.toMillis(Math.max(1, minutes));
	}
	public long getSpan() {
		return span;
	}
}
