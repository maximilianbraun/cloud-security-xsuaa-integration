package com.sap.cloud.security.token;

import com.sap.cloud.security.token.validation.MockTokenBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

public class SecurityContextTest {

	private static final Token TOKEN = new MockTokenBuilder().build();

	@Before
	public void setUp() {
		SecurityContext.clearToken();
	}

	@Test
	public void securityContext_initialTokenIsNull() {
		Token token = SecurityContext.getToken();

		assertThat(token).isNull();
	}

	@Test
	public void initTokenAndRetrieve() {
		SecurityContext.setToken(TOKEN);
		Token token = SecurityContext.getToken();

		assertThat(token).isEqualTo(TOKEN);
	}

	@Test
	public void clear_removesToken() {
		SecurityContext.setToken(TOKEN);
		SecurityContext.clearToken();
		Token token = SecurityContext.getToken();

		assertThat(token).isNull();
	}

	@Test
	public void tokenNotAvailableInDifferentThread() throws ExecutionException, InterruptedException {
		SecurityContext.setToken(TOKEN);

		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<Token> tokenInOtherThread = executor.submit(() -> SecurityContext.getToken());

		assertThat(tokenInOtherThread.get()).isNull();
	}

	@Test
	public void clearingTokenInDifferentThreadDoesNotAffectMainThread() throws ExecutionException, InterruptedException {
		SecurityContext.setToken(TOKEN);

		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(() -> SecurityContext.clearToken()).get(); //run and await other thread

		assertThat(SecurityContext.getToken()).isEqualTo(TOKEN);
	}

}