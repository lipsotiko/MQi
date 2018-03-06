package io.egia.mqi.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.TestPropertySource;

import io.egia.mqi.StandaloneConfig;
import io.egia.mqi.domain.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes={StandaloneConfig.class})
@TestPropertySource(locations="classpath:application-dev.properties")
public class MeasureServiceTest {
	
	@Value("${server.port}")
	private String serverPort;
	
	@Autowired
	private ServerRepository serverRepository;
	
	@Autowired
	private JobRepository jobRepository;
	
	@Autowired
	private MeasureRepository measureRepository;
	
	@Autowired
	private JobMeasureRepository jobMeasureRepository;
	
	@Autowired
	private PatientRepository patientRepository;
	
	@Autowired
	private VisitRepository visitRepository;
	
	@Autowired
	private MeasureService measureService;
	
	@Value("${mqi.properties.server.version}")
	private String serverVersion;
	
	private List<Measure> measuresList;
	
	private List<Job> jobs;

	private String jobName = "jUnit Measure Service Test";
	
	private Long patientId;
	
	private Long jobId;
	
	private Long serverId;
	
	String serverName;
	
	InetAddress serverIp;
	
	@Before
	public void setUp() {

		//Clear any jobs that already exist
		jobs = jobRepository.findByJobName(jobName);
		
		if (jobs.size() > 0) {
			for (Job j : jobs) {
				jobMeasureRepository.delete(jobMeasureRepository.findByJobId(j.getJobId()));
				jobRepository.delete(j.getJobId());
			}
		}
		
		//Create a measure job
		Job job = new Job();
		job.setJobName(jobName);
		job.setProcessType("measure");
		job.setStatus("pending");
		jobRepository.saveAndFlush(job);
		
		//Retrieve the jobId that was created for the job
		jobId = jobRepository.findByJobName(jobName).get(0).getJobId();
		
		//Get a list of measures; measures are inserted during the MQI Initialization process
		measuresList = measureRepository.findAll();
		
		//Add each measure to the job
		for (Measure m : measuresList) {
			JobMeasure jobMeasure = new JobMeasure();
			jobMeasure.setJobId(jobId);
			jobMeasure.setMeasureId(m.getMeasureId());
			jobMeasureRepository.saveAndFlush(jobMeasure);
		}
		
		//Create then Retrieve the primary server id
		try {
			serverIp = InetAddress.getLocalHost();
			serverName = serverIp.getHostName();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Server s = new Server();
		s.setServerName(serverName);
		s.setServerPort(serverPort);
		s.setServerType("primary");
		s.setServerVersion(serverVersion);
		s.setChunkSize(100);
		serverRepository.saveAndFlush(s);
		serverId = serverRepository.findByServerType("primary").get(0).getServerId();
		
		//Create a chunk
		Chunk c = new Chunk();
		c.setRecordCnt(2L);
		c.setServerId(serverId);
		c.setChunkId(1L);
		
		//Add patient
		Patient p = new Patient();
		p.setFirstName("Evangelos");
		p.setMiddleName("Elias");
		p.setLastName("Poneres");
		p.setGender('m');
		p.setDateOfBirth(Date.valueOf("1988-04-28"));
		p.setChunk(c);
		patientRepository.saveAndFlush(p);
		
		//Add visits
		patientId = patientRepository.findAll().get(0).getPatientId();
		Visit v = new Visit();
		v.setPatientId(patientId);
		v.setDateOfService(Date.valueOf("2011-01-01"));
		v.setDenied(false);
		v.setIcdVersion(0);
		v.setDiag1("123.45678");
		visitRepository.saveAndFlush(v);
	}
	
	@Test
	public void processMeasures() {
		try {
			measureService.measureProcess();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@After
	public void validateAndTearDown() {
		//validate results
		assertEquals(1, measureService.getPatientCount().intValue());
	}
	
}
