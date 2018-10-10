package io.egia.mqi.job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
class JobMeasure {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long jobMeasureId;
	private Long jobId;
	private Long measureId;
}
