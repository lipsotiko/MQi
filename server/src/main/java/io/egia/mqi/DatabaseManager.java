package io.egia.mqi;

import io.egia.mqi.measure.Measure;
import io.egia.mqi.measure.MeasureRepository;
import io.egia.mqi.version.Version;
import io.egia.mqi.version.VersionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author vango
 * <p>
 * The purpose of this class is to traverse the versions directory and
 * apply all the necessary updates to bring the database up to the most
 * recent version of the software. We assume that the installer will
 * create the version table and also insert a record with the value of
 * '0.0.0'. Only one record should ever exist in this table and it will
 * reflect the current version of the database.
 */

@Component
public class DatabaseManager {
    private Logger log = LoggerFactory.getLogger(DatabaseManager.class);
    private String versionsDbDirectory;
    private String[] dbObjects = {"func", "table", "view", "proc", "data", "meas"};
    private String objectsDirecotry;
    private SqlExecutor sqlExec;
    private MeasureRepository measureRepository;
    private VersionRepository versionRepository;

    public DatabaseManager(SqlExecutor sqlExec, MeasureRepository measureRepository, VersionRepository versionRepository) {
        this.sqlExec = sqlExec;
        this.measureRepository = measureRepository;
        this.versionRepository = versionRepository;
    }

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

    public void createVersionTable() {
        log.info("Creating version table");
        sqlExec.execute("drop table if exists version; " +
                "create table version (version_id varchar(10)); " +
                "insert into version (version_id) values ('0.0.0');");
    }

    public void dropVersionTable() {
        log.info("Drop the version table");
        sqlExec.execute("drop table if exists version;");
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

            while (line != null) {
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
