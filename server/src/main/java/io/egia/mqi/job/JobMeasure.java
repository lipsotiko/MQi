package io.egia.mqi.job;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class JobMeasure {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long jobMeasureId;
	private Long jobId;
	private Long measureId;
}
