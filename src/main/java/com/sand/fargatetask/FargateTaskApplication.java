package com.sand.fargatetask;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.AWSStepFunctionsClientBuilder;
import com.amazonaws.services.stepfunctions.model.SendTaskFailureRequest;
import com.amazonaws.services.stepfunctions.model.SendTaskSuccessRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class FargateTaskApplication implements CommandLineRunner {

	@Autowired
	private Environment env;

	public static Logger logger = LoggerFactory.getLogger(FargateTaskApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(FargateTaskApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String s3Bucket = env.getProperty("s3_bucket");
		String fail = env.getProperty("fail", "false");
		logger.info("Starting... s3Bucket = " + s3Bucket);
		try {
			Thread.sleep(10000);
		} catch (Exception e) {
			logger.error("Exception in sleeping", e);
		}
		logger.info("Done, sending success");
		sendSuccessOrFailure(Boolean.valueOf(fail));
		logger.info("Sent success");
	}

	private void sendSuccessOrFailure(Boolean failure) {
		String token = env.getProperty("taskToken");
		if(StringUtils.isEmpty(token)) {
			logger.error("Empty token");
			return;
		}
		ClientConfiguration clientConfiguration = new ClientConfiguration();
		clientConfiguration.setSocketTimeout((int) TimeUnit.SECONDS.toMillis(70));
		AWSStepFunctions client = AWSStepFunctionsClientBuilder.defaultClient();
				//.withRegion(Regions.US_EAST_1)
				//.withCredentials(new EnvironmentVariableCredentialsProvider())
				//.withClientConfiguration(clientConfiguration)
				//.build();

		if(!failure) {
			client.sendTaskSuccess(
					new SendTaskSuccessRequest().withOutput(
							"{\"God\" : \"Gautham\"}").withTaskToken(token));
		} else {
			client.sendTaskFailure(
					new SendTaskFailureRequest().withError(
							"{\"God\" : \"Gautham\"}"
					).withTaskToken(token)
			);
		}
	}
}
