package io.egia.mqi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.egia.mqi.domain.Measure;
import io.egia.mqi.domain.MeasureRepository;
import io.egia.mqi.domain.Version;
import io.egia.mqi.domain.VersionRepository;

/**
 * 
 * @author vango
 *
 *         The purpose of this class is to traverse the versions directory and
 *         apply all the necessary updates to bring the database up to the most
 *         recent version of the software. We assume that the installer will
 *         create the t_version table and also insert a record with the value of
 *         '0.0.0'. Only one record should ever exist in this table and it will
 *         reflect the current version of the database.
 * 
 */

@Component
public class DatabaseManager {

	private Logger log = LoggerFactory.getLogger(DatabaseManager.class);

	private String versionsDbDirectory;

	private String[] dbObjects = {"func", "table", "view", "proc", "data", "meas"};

	private String objectsDirecotry;

	@Autowired
	private SqlExecutor sqlExec;
	
	@Autowired
	private MeasureRepository measureRepository;

	@Autowired
	private VersionRepository versionRepository;

	public void setVersionsDirectory(String versionsDirectory) {
		this.versionsDbDirectory = versionsDirectory;
	}

	public void applyVersion(Version v) {

		log.info(String.format("Applying update: %s", v.getVersionId()));

		for (int i = 0; i < dbObjects.length; i++) {
			if (isMeasure(dbObjects[i])) {
				importMeasures(getDbObjectsDirectory(v, i));
			} else {
				applySqlScripts(getDbObjectsDirectory(v, i));
			}
		}

		versionRepository.updateVersion(v.getVersionId());
	}

	private boolean isMeasure(String dbObject) {
		return dbObject.equals("meas");
	}

	private String getDbObjectsDirectory(Version v, int i) {
		return versionsDbDirectory + File.separator + v.getVersionId() + File.separator + dbObjects[i];
	}

	public void importMeasures(String measuresDirecotry) {
		File measuresDirectory = new File(measuresDirecotry);
		
		for (final File f : measuresDirectory.listFiles()) {
			if (!f.isDirectory()) {
				Measure m = new Measure();
				m.setFileName(f.getName());
				m.setFileBytes(getMeasureFileBytes(measuresDirecotry, f).getBytes());
				measureRepository.saveAndFlush(m);
			}
		}
	}

    private String getMeasureFileBytes(String measuresDirecotry, File f) {
        return getFileContentAsString(measuresDirecotry + File.separator + f.getName()).toString();
    }

    public void applySqlScripts(String objectsDirecotry) {

		File sqlObjectsFile = new File(objectsDirecotry + File.separator + "file_list.txt");

		if (sqlObjectsFile.exists()) {

			List<String> tmpSqlFiles = new ArrayList<String>();

			try {
				Files.lines(Paths
						.get(sqlObjectsFile.toString()), StandardCharsets.UTF_8)
						.forEach(tmpSqlFiles::add);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (String s : tmpSqlFiles) {
				File sqlFile = new File(objectsDirecotry + File.separator + s);
				if (sqlFile.exists()) {
					log.info(String.format("Applying sql file: %s", sqlFile.toString()));
					sqlExec.execute(getFileContentAsString(sqlFile.toString()));
				}
			}
		}
	}
	
	private String getFileContentAsString(String path) {
	       
		StringBuilder sb = new StringBuilder();
       
		try {
			InputStream is = new FileInputStream(path);
	        BufferedReader buf = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	        
	        String line = buf.readLine();

	        while(line != null){
	        	sb.append(line.trim()).append("\n");
	            line = buf.readLine();
	        }  
	        
	        buf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}
}
